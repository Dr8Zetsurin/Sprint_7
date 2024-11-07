package org.example.utils;

public class DataGenerator {
    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        return result.toString();
    }

    public static String generateLogin() {
        return "courier" + generateRandomString(10);
    }

    public static String generatePassword() {
        return "pass" + generateRandomString(10);
    }

    public static String generateFirstName() {
        return "name" + generateRandomString(10);
    }
} 