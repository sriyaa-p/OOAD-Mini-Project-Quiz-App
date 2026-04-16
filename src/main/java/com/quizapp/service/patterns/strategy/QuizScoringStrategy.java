package com.quizapp.service.patterns.strategy;

import com.quizapp.domain.Quiz;

import java.util.Map;

public interface QuizScoringStrategy {

    ScoringResult score(Quiz quiz, Map<Long, Long> selectedOptionsByQuestionId);
}
