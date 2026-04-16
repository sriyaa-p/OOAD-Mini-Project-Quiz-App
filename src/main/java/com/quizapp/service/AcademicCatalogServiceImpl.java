package com.quizapp.service;

import com.quizapp.domain.Course;
import com.quizapp.domain.Section;
import com.quizapp.repository.CourseRepository;
import com.quizapp.repository.SectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AcademicCatalogServiceImpl implements AcademicCatalogService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public AcademicCatalogServiceImpl(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Section findOrCreateSection(String sectionName) {
        String normalized = normalize(sectionName);
        return sectionRepository.findByNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Section section = new Section();
                    section.setName(normalized);
                    return sectionRepository.save(section);
                });
    }

    @Override
    public Course findOrCreateCourse(String courseName) {
        String normalized = normalize(courseName);
        return courseRepository.findByNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Course course = new Course();
                    course.setName(normalized);
                    return courseRepository.save(course);
                });
    }

    @Override
    public Set<Section> resolveSections(String sectionsInput) {
        return Arrays.stream(sectionsInput.split(","))
                .map(this::normalize)
                .filter(value -> !value.isBlank())
                .map(this::findOrCreateSection)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<Course> resolveCourses(String coursesInput) {
        return Arrays.stream(coursesInput.split(","))
                .map(this::normalize)
                .filter(value -> !value.isBlank())
                .map(this::findOrCreateCourse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Section getSection(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> getSectionsByIds(Set<Long> ids) {
        return sectionRepository.findAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getCoursesByIds(Set<Long> ids) {
        return courseRepository.findAllById(ids);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
