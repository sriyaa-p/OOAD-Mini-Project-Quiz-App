package com.quizapp.service.auth;

import com.quizapp.domain.UserRole;
import jakarta.servlet.http.HttpSession;

public interface SessionService {

    void store(HttpSession session, SessionUser sessionUser);

    SessionUser getCurrentUser(HttpSession session);

    SessionUser requireRole(HttpSession session, UserRole role);

    void clear(HttpSession session);
}
