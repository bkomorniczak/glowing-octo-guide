package com.psk.nlpmod;

import java.io.*;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiConfig {
    private static final Logger LOGGER = Logger.getLogger(ApiConfig.class.getName());
    public static final String DEFAULT_API_ENDPOINT_URL = "http://localhost:5000/ask";
    private static String apiEndpointUrl = DEFAULT_API_ENDPOINT_URL;
    private static final Path configFile = Paths.get("config/apiConfig.txt");

    public static void resetApiEndpointUrl() {
        apiEndpointUrl = DEFAULT_API_ENDPOINT_URL;
        saveConfig();
    }
    public static void setApiEndpointUrl(String url) {
        apiEndpointUrl = url;
        saveConfig();
    }

    public static String getApiEndpointUrl() {
        return apiEndpointUrl;
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(configFile.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
                writer.write(apiEndpointUrl);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while saving configuration", e);
        }
    }

    public static void loadConfig() {
        if (Files.exists(configFile)) {
            try (BufferedReader reader = Files.newBufferedReader(configFile)) {
                apiEndpointUrl = reader.readLine();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error while loading configuration", e);
            }
        }
    }
}

