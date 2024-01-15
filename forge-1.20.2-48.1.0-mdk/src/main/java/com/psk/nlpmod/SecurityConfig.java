package com.psk.nlpmod;

import java.io.*;
import java.nio.file.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SecurityConfig {
    private static final Path tokenFile = Paths.get("config/securityToken.txt");
    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    private static final String DEFAULT_CSRF_TOKEN_KEY = "X-CSRFToken";
    private static final String DEFAULT_CSRF_TOKEN_VALUE = "2467dd71760c6ddce7e912c932933e9a56f077553d696255c7a40b0f202e74c0";
    private static String securityTokenKey = DEFAULT_CSRF_TOKEN_KEY;
    private static String securityTokenValue = DEFAULT_CSRF_TOKEN_VALUE;

    public static boolean isTokenSet() {
        return !securityTokenKey.isEmpty() && !securityTokenValue.isEmpty();
    }

    public static void resetSecurityToken() {
        securityTokenKey = DEFAULT_CSRF_TOKEN_KEY;
        securityTokenValue = DEFAULT_CSRF_TOKEN_VALUE;
        saveToken();
    }

    public static void setSecurityToken(String key, String value) {
        securityTokenKey = key;
        securityTokenValue = value;
        saveToken();
    }

    public static String getSecurityTokenKey() {
        return securityTokenKey;
    }

    public static String getSecurityTokenValue() {
        return securityTokenValue;
    }

    private static void saveToken() {
        try {
            Files.createDirectories(tokenFile.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(tokenFile)) {
                writer.write(securityTokenKey + "\n" + securityTokenValue);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred while saving the security token to a file.", e);
        }
    }
}
