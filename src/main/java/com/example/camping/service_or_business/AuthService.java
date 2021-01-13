package com.example.camping.service_or_business;

import com.example.camping.model.User;

public interface AuthService {
    User getCurrentUser();
    String getCurrentUserId();
    User signUpUser(String username, String password, String repeatedPassword);
}
