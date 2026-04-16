package com.quizapp.service.patterns.strategy;

import java.util.List;

public class ScoringResult {

    private final int score;
    private final int totalQuestions;
    private final List<QuestionEvaluation> evaluations;

    public ScoringResult(int score, int totalQuestions, List<QuestionEvaluation> evaluations) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.evaluations = evaluations;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public List<QuestionEvaluation> getEvaluations() {
        return evaluations;
    }
}
