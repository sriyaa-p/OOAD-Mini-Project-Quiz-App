package com.quizapp.service.patterns.factory;

import com.quizapp.domain.Course;
import com.quizapp.domain.Section;
import com.quizapp.domain.Student;
import com.quizapp.domain.Teacher;
import com.quizapp.domain.UserRole;
import com.quizapp.dto.StudentRegistrationRequest;
import com.quizapp.dto.TeacherRegistrationRequest;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserRegistrationFactoryImpl implements UserRegistrationFactory {

    @Override
    public Student createStudent(StudentRegistrationRequest request, Section section, Course course, String passwordHash) {
        Student student = new Student();
        student.setUserCode(request.getUserCode().trim());
        student.setName(request.getName().trim());
        student.setEmail(request.getEmail().trim().toLowerCase());
        student.setPasswordHash(passwordHash);
        student.setRole(UserRole.STUDENT);
        student.setSection(section);
        student.setCourse(course);
        return student;
    }

    @Override
    public Teacher createTeacher(TeacherRegistrationRequest request, Set<Section> sections, Set<Course> courses, String passwordHash) {
        Teacher teacher = new Teacher();
        teacher.setUserCode(request.getUserCode().trim());
        teacher.setName(request.getName().trim());
        teacher.setEmail(request.getEmail().trim().toLowerCase());
        teacher.setPasswordHash(passwordHash);
        teacher.setRole(UserRole.TEACHER);
        teacher.setSections(sections);
        teacher.setCourses(courses);
        return teacher;
    }
}
