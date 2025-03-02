package org.example.mdintech.utils;

import org.apache.commons.validator.routines.RegexValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordVerification {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private PasswordVerification() {
    }

    public static boolean isStrongPassword(String password) {
        RegexValidator validator = new RegexValidator("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
        return validator.isValid(password);
    }

    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public static boolean comparePasswords(String password1, String password2) {
        return password1.equals(password2);
    }
}
