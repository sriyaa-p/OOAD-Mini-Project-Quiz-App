package com.quizapp.controller;

import com.quizapp.domain.UserRole;
import com.quizapp.dto.QuizCreationRequest;
import com.quizapp.dto.QuizQuestionForm;
import com.quizapp.service.TeacherQuizService;
import com.quizapp.service.auth.SessionService;
import com.quizapp.service.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TeacherController {

    private final TeacherQuizService teacherQuizService;
    private final SessionService sessionService;

    public TeacherController(TeacherQuizService teacherQuizService, SessionService sessionService) {
        this.teacherQuizService = teacherQuizService;
        this.sessionService = sessionService;
    }

    @GetMapping("/teacher/home")
    public String home(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.TEACHER);
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("quizzes", teacherQuizService.getTeacherQuizzes(sessionUser.userId()));
            return "teacher/home";
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/teacher/quizzes/new")
    public String createQuizForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.TEACHER);
            model.addAttribute("sessionUser", sessionUser);
            if (!model.containsAttribute("quizForm")) {
                model.addAttribute("quizForm", teacherQuizService.buildEmptyQuizForm());
            }
            populateTeacherContext(model, sessionUser.userId());
            return "teacher/create-quiz";
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/teacher/quizzes/new")
    public String createQuiz(@Valid @ModelAttribute("quizForm") QuizCreationRequest quizForm,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.TEACHER);

            if (quizForm.getQuestions() == null || quizForm.getQuestions().isEmpty()) {
                quizForm.getQuestions().add(new QuizQuestionForm());
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("sessionUser", sessionUser);
                populateTeacherContext(model, sessionUser.userId());
                return "teacher/create-quiz";
            }

            teacherQuizService.createQuiz(sessionUser.userId(), quizForm);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz assigned successfully.");
            return "redirect:/teacher/home";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/teacher/quizzes/new";
        }
    }

    @GetMapping("/teacher/quizzes/{quizId}/results")
    public String quizResults(@PathVariable Long quizId,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            SessionUser sessionUser = sessionService.requireRole(session, UserRole.TEACHER);
            model.addAttribute("sessionUser", sessionUser);
            model.addAttribute("statistics", teacherQuizService.getQuizStatistics(sessionUser.userId(), quizId));
            model.addAttribute("results", teacherQuizService.getQuizResults(sessionUser.userId(), quizId));
            return "teacher/results";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/teacher/home";
        }
    }

    private void populateTeacherContext(Model model, Long teacherId) {
        model.addAttribute("sections", teacherQuizService.getTeacherSections(teacherId));
        model.addAttribute("courses", teacherQuizService.getTeacherCourses(teacherId));
    }
}
