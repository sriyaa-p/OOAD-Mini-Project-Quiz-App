package com.quizapp.service.patterns.strategy;

import com.quizapp.domain.AnswerOption;
import com.quizapp.domain.Question;

public class QuestionEvaluation {

    private final Question question;
    private final AnswerOption selectedOption;
    private final boolean correct;

    public QuestionEvaluation(Question question, AnswerOption selectedOption, boolean correct) {
        this.question = question;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public AnswerOption getSelectedOption() {
        return selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }
}
