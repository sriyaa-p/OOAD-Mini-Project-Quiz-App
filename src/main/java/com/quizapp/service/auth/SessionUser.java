package com.quizapp.service.auth;

import com.quizapp.domain.UserRole;

public record SessionUser(Long userId, String name, UserRole role) {
}
