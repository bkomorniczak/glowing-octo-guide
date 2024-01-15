package com.psk.nlpmod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class FlaskResponseHandler {

    private static final Gson gson = new Gson();

    public static String parseFlaskResponse(String jsonResponse) {
        try {
            JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);
            return responseObj.has("answer") ? responseObj.get("answer").getAsString() : "Error";
        } catch (Exception e) {
            return "Error while processing response: " + e.getMessage();
        }
    }
}
