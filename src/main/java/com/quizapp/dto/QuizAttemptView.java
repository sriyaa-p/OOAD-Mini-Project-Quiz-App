package com.quizapp.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptView {

    private Long quizId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private List<QuizQuestionView> questions = new ArrayList<>();

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<QuizQuestionView> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestionView> questions) {
        this.questions = questions;
    }
}
