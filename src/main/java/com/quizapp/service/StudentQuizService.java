package com.quizapp.service;

import com.quizapp.dto.QuizAttemptView;
import com.quizapp.dto.QuizCardView;
import com.quizapp.dto.QuizSubmissionRequest;
import com.quizapp.dto.QuizSubmissionResultView;
import com.quizapp.dto.StudentAttemptHistoryView;

import java.util.List;

public interface StudentQuizService {

    List<QuizCardView> getAvailableQuizzes(Long studentId);

    List<StudentAttemptHistoryView> getAttemptHistory(Long studentId);

    QuizAttemptView getQuizForAttempt(Long studentId, Long quizId);

    QuizSubmissionResultView submitQuiz(Long studentId, Long quizId, QuizSubmissionRequest request);
}
