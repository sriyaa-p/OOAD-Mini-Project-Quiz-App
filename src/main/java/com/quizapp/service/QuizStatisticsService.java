package com.quizapp.service;

import com.quizapp.domain.QuizAttempt;
import com.quizapp.dto.QuizStatisticsView;

import java.util.List;

public interface QuizStatisticsService {

    QuizStatisticsView calculate(List<QuizAttempt> attempts);
}
