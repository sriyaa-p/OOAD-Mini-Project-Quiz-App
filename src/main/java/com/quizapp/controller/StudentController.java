package com.quizapp.controller;

import com.quizapp.domain.UserRole;
import com.quizapp.dto.QuizSubmissionRequest;
import com.quizapp.dto.QuizSubmissionResultView;
import com.quizapp.service.StudentQuizService;
import com.quizapp.service.auth.SessionService;
import com.quizapp.service.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private final StudentQuizService studentQuizService;
    private final SessionService sessionService;

    public StudentController(StudentQuizService studentQuizService, SessionService sessionService) {
        this.studentQuizService = studentQuizService;
        this.sessionService = sessionService;
    }

    @GetMapping("/student/home")
    public String home(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.STUDENT);
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("quizzes", studentQuizService.getAvailableQuizzes(sessionUser.userId()));
            model.addAttribute("history", studentQuizService.getAttemptHistory(sessionUser.userId()));
            return "student/home";
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/student/quiz/{quizId}")
    public String attemptQuiz(@PathVariable Long quizId,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.STUDENT);
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("quiz", studentQuizService.getQuizForAttempt(sessionUser.userId(), quizId));
            model.addAttribute("submissionRequest", new QuizSubmissionRequest());
            return "student/attempt";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/home";
        }
    }

    @PostMapping("/student/quiz/{quizId}/submit")
    public String submitQuiz(@PathVariable Long quizId,
                             @ModelAttribute QuizSubmissionRequest submissionRequest,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.STUDENT);
            QuizSubmissionResultView result = studentQuizService.submitQuiz(sessionUser.userId(), quizId, submissionRequest);
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("result", result);
            return "student/result";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/home";
        }
    }
}
