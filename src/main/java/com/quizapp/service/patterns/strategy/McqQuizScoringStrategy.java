package com.quizapp.service.patterns.strategy;

import com.quizapp.domain.AnswerOption;
import com.quizapp.domain.Question;
import com.quizapp.domain.Quiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class McqQuizScoringStrategy implements QuizScoringStrategy {

    @Override
    public ScoringResult score(Quiz quiz, Map<Long, Long> selectedOptionsByQuestionId) {
        int totalScore = 0;
        List<QuestionEvaluation> evaluations = new ArrayList<>();

        for (Question question : quiz.getQuestions()) {
            Long selectedOptionId = selectedOptionsByQuestionId.get(question.getId());
            AnswerOption selectedOption = question.getOptions()
                    .stream()
                    .filter(option -> option.getId().equals(selectedOptionId))
                    .findFirst()
                    .orElse(null);

            boolean correct = selectedOption != null && selectedOption.isCorrect();
            if (correct) {
                totalScore++;
            }
            evaluations.add(new QuestionEvaluation(question, selectedOption, correct));
        }

        return new ScoringResult(totalScore, quiz.getQuestions().size(), evaluations);
    }
}
