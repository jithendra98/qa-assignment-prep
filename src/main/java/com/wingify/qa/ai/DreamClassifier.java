package com.wingify.qa.ai;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Classifies dreams as "Good" or "Bad" using OpenAI API.
 * Falls back to keyword matching if no API key is set.
 */
public class DreamClassifier {

    private static final Logger logger = LogManager.getLogger(DreamClassifier.class);

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final String apiKey;
    private final String model;
    private final OkHttpClient httpClient;
    private final Gson gson;

    // constructor with custom model
    public DreamClassifier(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    // default constructor - uses gpt-3.5-turbo
    public DreamClassifier(String apiKey) {
        this(apiKey, "gpt-3.5-turbo");
    }

    /**
     * Classify a dream as Good or Bad.
     * Uses OpenAI if API key is available, otherwise falls back to keyword matching.
     */
    public String classifyDream(String dreamName) {
        logger.info("Classifying dream: '{}'", dreamName);

        if (isApiKeyConfigured()) {
            try {
                return classifyWithOpenAI(dreamName);
            } catch (Exception e) {
                logger.warn("OpenAI API call failed ({}), using keyword fallback", e.getMessage());
                return classifyWithKeywords(dreamName);
            }
        } else {
            logger.info("No API key found, using keyword-based fallback classifier");
            return classifyWithKeywords(dreamName);
        }
    }

    // calls the OpenAI chat completions endpoint
    private String classifyWithOpenAI(String dreamName) throws IOException {
        String systemPrompt = "You are a dream classifier. Given a dream name or description, "
                + "classify it as exactly \"Good\" or \"Bad\" based on its sentiment and nature. "
                + "Respond with ONLY the single word: Good or Bad. No other text.";

        String userPrompt = "Classify this dream: \"" + dreamName + "\"";

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("temperature", 0.0);
        requestBody.addProperty("max_tokens", 10);

        JsonArray messages = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);
        messages.add(userMessage);

        requestBody.add("messages", messages);

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), JSON_MEDIA_TYPE))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("OpenAI API error " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            String classification = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString()
                    .trim();

            if (classification.toLowerCase().contains("good")) {
                classification = "Good";
            } else if (classification.toLowerCase().contains("bad")) {
                classification = "Bad";
            }

            logger.info("OpenAI classified '{}' as: {}", dreamName, classification);
            return classification;
        }
    }

    /**
     * Simple keyword-based fallback classifier.
     * Checks for positive/negative words in the dream name.
     */
    // TODO: could improve this with a proper ML model later
    public String classifyWithKeywords(String dreamName) {
        if (dreamName == null || dreamName.trim().isEmpty()) {
            return "Good";
        }
        String lower = dreamName.toLowerCase().trim();

        // negative words - things associated with bad dreams
        String[] negativeTokens = {
                "monster", "chase", "lost", "maze", "late", "exam", "fail",
                "nightmare", "fall", "trap", "dark", "die", "death", "ghost",
                "snake", "drown", "blood", "fire", "attack", "alone", "scary",
                "fear", "panic", "stress", "cry", "pain", "spider", "war"
        };

        // positive words - happy/adventurous dream themes
        String[] positiveTokens = {
                "flying", "fly", "mountain", "win", "lottery", "swim", "dolphin",
                "adventure", "travel", "magic", "happy", "love", "sun", "peace",
                "success", "joy", "cloud", "star", "friend", "smile", "hero",
                "gold", "gift", "party", "paradise", "wonder", "soar", "calm"
        };

        int negScore = 0;
        int posScore = 0;

        for (String token : negativeTokens) {
            if (lower.contains(token)) {
                negScore += 2;
            }
        }

        for (String token : positiveTokens) {
            if (lower.contains(token)) {
                posScore += 2;
            }
        }

        String result = (negScore > posScore) ? "Bad" : "Good";
        logger.info("Keyword classifier: '{}' -> {} (pos={}, neg={})",
                dreamName, result, posScore, negScore);
        return result;
    }

    // check if we have a valid API key
    public boolean isApiKeyConfigured() {
        return apiKey != null && !apiKey.isEmpty() && !apiKey.equals("${OPENAI_API_KEY}");
    }

    // returns which mode we're using
    public String getEngineMode() {
        return isApiKeyConfigured()
                ? "OpenAI (" + model + ")"
                : "Keyword fallback (offline)";
    }
}
