package com.example.metis.util;

public class EncodeToHex {
    public static String toHexString(String input) {
        StringBuilder hexString = new StringBuilder();
        for (char c : input.toCharArray()) {
            hexString.append(String.format("%02x", (int) c));
        }
        return hexString.toString();
    }
}
