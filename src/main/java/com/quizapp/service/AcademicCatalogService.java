package com.quizapp.service;

import com.quizapp.domain.Course;
import com.quizapp.domain.Section;

import java.util.List;
import java.util.Set;

public interface AcademicCatalogService {

    Section findOrCreateSection(String sectionName);

    Course findOrCreateCourse(String courseName);

    Set<Section> resolveSections(String sectionsInput);

    Set<Course> resolveCourses(String coursesInput);

    Section getSection(Long id);

    Course getCourse(Long id);

    List<Section> getSectionsByIds(Set<Long> ids);

    List<Course> getCoursesByIds(Set<Long> ids);
}
