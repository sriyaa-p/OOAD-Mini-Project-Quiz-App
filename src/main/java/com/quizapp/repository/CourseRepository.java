package com.quizapp.repository;

import com.quizapp.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByNameIgnoreCase(String name);
}
