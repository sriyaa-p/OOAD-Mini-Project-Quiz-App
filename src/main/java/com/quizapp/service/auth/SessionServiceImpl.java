package com.quizapp.service.auth;

import com.quizapp.domain.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {

    private static final String SESSION_USER_KEY = "sessionUser";

    @Override
    public void store(HttpSession session, SessionUser sessionUser) {
        session.setAttribute(SESSION_USER_KEY, sessionUser);
    }

    @Override
    public SessionUser getCurrentUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SESSION_USER_KEY);
    }

    @Override
    public SessionUser requireRole(HttpSession session, UserRole role) {
        SessionUser sessionUser = getCurrentUser(session);
        if (sessionUser == null) {
            throw new IllegalStateException("Please login first.");
        }
        if (sessionUser.role() != role) {
            throw new IllegalStateException("You are not allowed to access this page.");
        }
        return sessionUser;
    }

    @Override
    public void clear(HttpSession session) {
        session.invalidate();
    }
}
