package com.example.authservice.utils;

import com.example.authservice.model.UserInfoDto;

public class ValidationUtil {
    private static final String PASSWORD_REGEX =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    public static void validateUserAttributes(UserInfoDto userInfoDto) {
        if (userInfoDto == null) {
            throw new IllegalArgumentException("User info cannot be null");
        }

        String username = userInfoDto.getUsername();
        String password = userInfoDto.getPassword();

        if (username == null) {
            throw new IllegalArgumentException("Invalid username");
        }

        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters long, contain uppercase, lowercase, digit, and special character"
            );
        }
    }
}
