package com.quizapp.repository;

import com.quizapp.domain.Course;
import com.quizapp.domain.Quiz;
import com.quizapp.domain.Section;
import com.quizapp.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findBySectionAndCourseOrderByAvailableFromDesc(Section section, Course course);

    List<Quiz> findByTeacherOrderByDeadlineDesc(Teacher teacher);

    Optional<Quiz> findByIdAndTeacher_Id(Long quizId, Long teacherId);
}
