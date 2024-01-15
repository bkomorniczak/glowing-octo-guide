package com.psk.nlpmod;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class FlaskRequestHandler {
    private static class FlaskAIRequest {
        String question;

        FlaskAIRequest(String question) {
            this.question = question;
        }
    }
    public static boolean checkApiConnection() {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            // Add the path /state to the URL
            HttpGet request = new HttpGet(ApiConfig.getApiEndpointUrl() + "/state");

            HttpResponse response = httpClient.execute(request);
            return response.getStatusLine().getStatusCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getAIResponse(String question) throws IOException {
        FlaskAIRequest flaskAIRequest = new FlaskAIRequest(question);
        String data = new Gson().toJson(flaskAIRequest);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            // Use the API endpoint URL from ApiConfig
            HttpPost request = new HttpPost(ApiConfig.getApiEndpointUrl());
            StringEntity params = new StringEntity(data, "UTF-8");
            request.addHeader("Content-Type", "application/json");

            // Use the token from SecurityConfig
            request.addHeader(SecurityConfig.getSecurityTokenKey(), SecurityConfig.getSecurityTokenValue());
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        }
    }
}
