package com.quizapp.service;

import com.quizapp.domain.Course;
import com.quizapp.domain.Section;
import com.quizapp.dto.QuizCreationRequest;
import com.quizapp.dto.QuizStatisticsView;
import com.quizapp.dto.TeacherQuizSummaryView;
import com.quizapp.dto.TeacherStudentResultView;

import java.util.List;

public interface TeacherQuizService {

    QuizCreationRequest buildEmptyQuizForm();

    void createQuiz(Long teacherId, QuizCreationRequest request);

    List<TeacherQuizSummaryView> getTeacherQuizzes(Long teacherId);

    List<Section> getTeacherSections(Long teacherId);

    List<Course> getTeacherCourses(Long teacherId);

    QuizStatisticsView getQuizStatistics(Long teacherId, Long quizId);

    List<TeacherStudentResultView> getQuizResults(Long teacherId, Long quizId);
}
