package com.pd;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RequestHandler {
    private static class OpenAIRequest {
        String prompt;

        OpenAIRequest(String prompt) {
            this.prompt = prompt;
        }
    }

    public static String getAIResponse(String prompt) throws IOException {
        OpenAIRequest openAIRequest = new OpenAIRequest(prompt);
        String data = new Gson().toJson(openAIRequest);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("http://localhost:5000/ask");
            StringEntity params = new StringEntity(data, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("X-CSRFToken", "2467dd71760c6ddce7e912c932933e9a56f077553d696255c7a40b0f202e74c0");

            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        }
    }
}
