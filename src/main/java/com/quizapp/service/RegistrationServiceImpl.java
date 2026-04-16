package com.quizapp.service;

import com.quizapp.domain.Course;
import com.quizapp.domain.Section;
import com.quizapp.domain.Student;
import com.quizapp.domain.Teacher;
import com.quizapp.dto.StudentRegistrationRequest;
import com.quizapp.dto.TeacherRegistrationRequest;
import com.quizapp.repository.AppUserRepository;
import com.quizapp.repository.StudentRepository;
import com.quizapp.repository.TeacherRepository;
import com.quizapp.service.patterns.factory.UserRegistrationFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {

    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicCatalogService academicCatalogService;
    private final UserRegistrationFactory userRegistrationFactory;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(AppUserRepository appUserRepository,
                                   StudentRepository studentRepository,
                                   TeacherRepository teacherRepository,
                                   AcademicCatalogService academicCatalogService,
                                   UserRegistrationFactory userRegistrationFactory,
                                   PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.academicCatalogService = academicCatalogService;
        this.userRegistrationFactory = userRegistrationFactory;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerStudent(StudentRegistrationRequest request) {
        validateUniqueUser(request.getEmail(), request.getUserCode());
        Section section = academicCatalogService.findOrCreateSection(request.getSectionName());
        Course course = academicCatalogService.findOrCreateCourse(request.getCourseName());
        String passwordHash = passwordEncoder.encode(request.getPassword());
        Student student = userRegistrationFactory.createStudent(request, section, course, passwordHash);
        studentRepository.save(student);
    }

    @Override
    public void registerTeacher(TeacherRegistrationRequest request) {
        validateUniqueUser(request.getEmail(), request.getUserCode());
        Set<Section> sections = academicCatalogService.resolveSections(request.getSectionsInput());
        Set<Course> courses = academicCatalogService.resolveCourses(request.getCoursesInput());
        String passwordHash = passwordEncoder.encode(request.getPassword());
        Teacher teacher = userRegistrationFactory.createTeacher(request, sections, courses, passwordHash);
        teacherRepository.save(teacher);
    }

    private void validateUniqueUser(String email, String userCode) {
        if (appUserRepository.existsByEmail(email.trim().toLowerCase())) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (appUserRepository.existsByUserCode(userCode.trim())) {
            throw new IllegalArgumentException("ID is already registered.");
        }
    }
}
