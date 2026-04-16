package com.quizapp.service.patterns.factory;

import com.quizapp.domain.Course;
import com.quizapp.domain.Section;
import com.quizapp.domain.Student;
import com.quizapp.domain.Teacher;
import com.quizapp.dto.StudentRegistrationRequest;
import com.quizapp.dto.TeacherRegistrationRequest;

import java.util.Set;

public interface UserRegistrationFactory {

    Student createStudent(StudentRegistrationRequest request, Section section, Course course, String passwordHash);

    Teacher createTeacher(TeacherRegistrationRequest request, Set<Section> sections, Set<Course> courses, String passwordHash);
}
