package com.quizapp.service.auth;

import com.quizapp.dto.LoginRequest;

public interface AuthService {

    SessionUser login(LoginRequest request);
}
