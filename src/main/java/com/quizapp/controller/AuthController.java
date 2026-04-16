package com.quizapp.controller;

import com.quizapp.domain.UserRole;
import com.quizapp.dto.LoginRequest;
import com.quizapp.dto.StudentRegistrationRequest;
import com.quizapp.dto.TeacherRegistrationRequest;
import com.quizapp.service.RegistrationService;
import com.quizapp.service.auth.AuthService;
import com.quizapp.service.auth.SessionService;
import com.quizapp.service.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final SessionService sessionService;

    public AuthController(AuthService authService, RegistrationService registrationService, SessionService sessionService) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.sessionService = sessionService;
    }

    @ModelAttribute("loginRequest")
    public LoginRequest loginRequest() {
        return new LoginRequest();
    }

    @ModelAttribute("studentRegistrationRequest")
    public StudentRegistrationRequest studentRegistrationRequest() {
        return new StudentRegistrationRequest();
    }

    @ModelAttribute("teacherRegistrationRequest")
    public TeacherRegistrationRequest teacherRegistrationRequest() {
        return new TeacherRegistrationRequest();
    }

    @GetMapping("/")
    public String showAuthPage(HttpSession session, Model model) {
        SessionUser sessionUser = sessionService.getCurrentUser(session);
        if (sessionUser != null) {
            return "redirect:/home";
        }
        model.addAttribute("roles", UserRole.values());
        return "auth/index";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                        BindingResult bindingResult,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill in the login form correctly.");
            return "redirect:/";
        }

        try {
            SessionUser sessionUser = authService.login(loginRequest);
            sessionService.store(session, sessionUser);
            return "redirect:/home";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/register/student")
    public String registerStudent(@Valid @ModelAttribute("studentRegistrationRequest") StudentRegistrationRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please complete all required student registration fields.");
            return "redirect:/";
        }

        try {
            registrationService.registerStudent(request);
            redirectAttributes.addFlashAttribute("successMessage", "Student registration completed. Please login.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/register/teacher")
    public String registerTeacher(@Valid @ModelAttribute("teacherRegistrationRequest") TeacherRegistrationRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please complete all required teacher registration fields.");
            return "redirect:/";
        }

        try {
            registrationService.registerTeacher(request);
            redirectAttributes.addFlashAttribute("successMessage", "Teacher registration completed. Please login.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/home")
    public String home(HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser sessionUser = sessionService.getCurrentUser(session);
        if (sessionUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/";
        }
        return sessionUser.role() == UserRole.TEACHER ? "redirect:/teacher/home" : "redirect:/student/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        sessionService.clear(session);
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out.");
        return "redirect:/";
    }
}
