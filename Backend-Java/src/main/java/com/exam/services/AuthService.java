package com.exam.services;

import com.exam.dto.AuthResponse;
import com.exam.dto.LoginRequest;
import com.exam.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
