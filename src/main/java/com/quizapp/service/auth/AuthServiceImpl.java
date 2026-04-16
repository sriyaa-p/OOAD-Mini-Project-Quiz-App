package com.quizapp.service.auth;

import com.quizapp.domain.AppUser;
import com.quizapp.dto.LoginRequest;
import com.quizapp.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SessionUser login(LoginRequest request) {
        AppUser user = appUserRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("No account found for this email."));

        if (user.getRole() != request.getRole()) {
            throw new IllegalArgumentException("Selected role does not match the registered account.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password.");
        }

        return new SessionUser(user.getId(), user.getName(), user.getRole());
    }
}
