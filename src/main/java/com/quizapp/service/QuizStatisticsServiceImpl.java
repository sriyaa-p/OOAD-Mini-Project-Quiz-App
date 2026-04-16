package com.quizapp.service;

import com.quizapp.domain.QuizAttempt;
import com.quizapp.dto.QuizStatisticsView;
import org.springframework.stereotype.Service;

import java.util.IntSummaryStatistics;
import java.util.List;

@Service
public class QuizStatisticsServiceImpl implements QuizStatisticsService {

    @Override
    public QuizStatisticsView calculate(List<QuizAttempt> attempts) {
        QuizStatisticsView statisticsView = new QuizStatisticsView();
        statisticsView.setSubmissionCount(attempts.size());

        if (attempts.isEmpty()) {
            statisticsView.setHighestScore(0);
            statisticsView.setLowestScore(0);
            statisticsView.setAverageScore(0.0);
            return statisticsView;
        }

        IntSummaryStatistics stats = attempts.stream()
                .mapToInt(QuizAttempt::getScore)
                .summaryStatistics();

        statisticsView.setHighestScore(stats.getMax());
        statisticsView.setLowestScore(stats.getMin());
        statisticsView.setAverageScore(stats.getAverage());
        return statisticsView;
    }
}
