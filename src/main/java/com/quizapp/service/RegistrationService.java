package com.quizapp.service;

import com.quizapp.dto.StudentRegistrationRequest;
import com.quizapp.dto.TeacherRegistrationRequest;

public interface RegistrationService {

    void registerStudent(StudentRegistrationRequest request);

    void registerTeacher(TeacherRegistrationRequest request);
}
