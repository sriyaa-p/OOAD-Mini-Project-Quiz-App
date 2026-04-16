package com.quizapp.repository;

import com.quizapp.domain.Quiz;
import com.quizapp.domain.QuizAttempt;
import com.quizapp.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByQuizAndStudent(Quiz quiz, Student student);

    boolean existsByQuizAndStudent(Quiz quiz, Student student);

    List<QuizAttempt> findByStudentOrderBySubmittedAtDesc(Student student);

    List<QuizAttempt> findByQuizOrderBySubmittedAtDesc(Quiz quiz);
}
