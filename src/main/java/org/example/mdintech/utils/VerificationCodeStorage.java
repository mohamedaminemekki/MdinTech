package org.example.mdintech.utils;

import java.util.HashMap;
import java.util.Map;

public class VerificationCodeStorage {
    private static final Map<String, String> verificationCodes = new HashMap<>();

    public static void store(String email, String code) {
        verificationCodes.put(email, code);
    }

    public static String getCode(String email) {
        return verificationCodes.get(email);
    }

    public static void remove(String email) {
        verificationCodes.remove(email);
    }
}
