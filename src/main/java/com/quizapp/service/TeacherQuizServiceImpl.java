package com.quizapp.service;

import com.quizapp.domain.AnswerOption;
import com.quizapp.domain.Course;
import com.quizapp.domain.Question;
import com.quizapp.domain.Quiz;
import com.quizapp.domain.QuizAttempt;
import com.quizapp.domain.Section;
import com.quizapp.domain.Teacher;
import com.quizapp.dto.QuizCreationRequest;
import com.quizapp.dto.QuizQuestionForm;
import com.quizapp.dto.QuizStatisticsView;
import com.quizapp.dto.TeacherQuizSummaryView;
import com.quizapp.dto.TeacherStudentResultView;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class TeacherQuizServiceImpl implements TeacherQuizService {

    private final TeacherRepository teacherRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final AcademicCatalogService academicCatalogService;
    private final QuizStatisticsService quizStatisticsService;

    public TeacherQuizServiceImpl(TeacherRepository teacherRepository,
                                  QuizRepository quizRepository,
                                  QuizAttemptRepository quizAttemptRepository,
                                  AcademicCatalogService academicCatalogService,
                                  QuizStatisticsService quizStatisticsService) {
        this.teacherRepository = teacherRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.academicCatalogService = academicCatalogService;
        this.quizStatisticsService = quizStatisticsService;
    }

    @Override
    @Transactional(readOnly = true)
    public QuizCreationRequest buildEmptyQuizForm() {
        QuizCreationRequest request = new QuizCreationRequest();
        request.getQuestions().add(new QuizQuestionForm());
        return request;
    }

    @Override
    public void createQuiz(Long teacherId, QuizCreationRequest request) {
        Teacher teacher = getTeacher(teacherId);
        validateQuizRequest(request, teacher);

        Section section = academicCatalogService.getSection(request.getSectionId());
        Course course = academicCatalogService.getCourse(request.getCourseId());

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle().trim());
        quiz.setDescription(request.getDescription());
        quiz.setAvailableFrom(request.getAvailableFrom());
        quiz.setDeadline(request.getDeadline());
        quiz.setTeacher(teacher);
        quiz.setSection(section);
        quiz.setCourse(course);

        int order = 1;
        for (QuizQuestionForm questionForm : request.getQuestions()) {
            Question question = new Question();
            question.setQuestionText(questionForm.getQuestionText().trim());
            question.setDisplayOrder(order++);
            question.addOption(buildOption("A", questionForm.getOptionA(), questionForm.getCorrectOptionLabel()));
            question.addOption(buildOption("B", questionForm.getOptionB(), questionForm.getCorrectOptionLabel()));
            question.addOption(buildOption("C", questionForm.getOptionC(), questionForm.getCorrectOptionLabel()));
            question.addOption(buildOption("D", questionForm.getOptionD(), questionForm.getCorrectOptionLabel()));
            quiz.addQuestion(question);
        }

        quizRepository.save(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherQuizSummaryView> getTeacherQuizzes(Long teacherId) {
        Teacher teacher = getTeacher(teacherId);
        List<Quiz> quizzes = quizRepository.findByTeacherOrderByDeadlineDesc(teacher);
        List<TeacherQuizSummaryView> summaryViews = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            List<QuizAttempt> attempts = quizAttemptRepository.findByQuizOrderBySubmittedAtDesc(quiz);
            QuizStatisticsView stats = quizStatisticsService.calculate(attempts);

            TeacherQuizSummaryView summaryView = new TeacherQuizSummaryView();
            summaryView.setQuizId(quiz.getId());
            summaryView.setTitle(quiz.getTitle());
            summaryView.setSectionName(quiz.getSection().getName());
            summaryView.setCourseName(quiz.getCourse().getName());
            summaryView.setAvailableFrom(quiz.getAvailableFrom());
            summaryView.setDeadline(quiz.getDeadline());
            summaryView.setSubmissionCount(stats.getSubmissionCount());
            summaryView.setHighestScore(stats.getHighestScore());
            summaryView.setLowestScore(stats.getLowestScore());
            summaryView.setAverageScore(stats.getAverageScore());
            summaryViews.add(summaryView);
        }
        return summaryViews;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> getTeacherSections(Long teacherId) {
        Teacher teacher = getTeacher(teacherId);
        return teacher.getSections().stream()
                .sorted(Comparator.comparing(Section::getName))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getTeacherCourses(Long teacherId) {
        Teacher teacher = getTeacher(teacherId);
        return teacher.getCourses().stream()
                .sorted(Comparator.comparing(Course::getName))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizStatisticsView getQuizStatistics(Long teacherId, Long quizId) {
        Quiz quiz = getOwnedQuiz(teacherId, quizId);
        return quizStatisticsService.calculate(quizAttemptRepository.findByQuizOrderBySubmittedAtDesc(quiz));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherStudentResultView> getQuizResults(Long teacherId, Long quizId) {
        Quiz quiz = getOwnedQuiz(teacherId, quizId);
        return quizAttemptRepository.findByQuizOrderBySubmittedAtDesc(quiz)
                .stream()
                .map(attempt -> {
                    TeacherStudentResultView row = new TeacherStudentResultView();
                    row.setStudentCode(attempt.getStudent().getUserCode());
                    row.setStudentName(attempt.getStudent().getName());
                    row.setScore(attempt.getScore());
                    row.setTotalQuestions(attempt.getTotalQuestions());
                    row.setSubmittedAt(attempt.getSubmittedAt());
                    return row;
                })
                .toList();
    }

    private Teacher getTeacher(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher account not found."));
    }

    private Quiz getOwnedQuiz(Long teacherId, Long quizId) {
        return quizRepository.findByIdAndTeacher_Id(quizId, teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found for this teacher."));
    }

    private void validateQuizRequest(QuizCreationRequest request, Teacher teacher) {
        if (request.getDeadline().isBefore(request.getAvailableFrom())) {
            throw new IllegalArgumentException("Deadline must be after the start time.");
        }
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Add at least one question.");
        }

        boolean teachesSection = teacher.getSections().stream().anyMatch(section -> section.getId().equals(request.getSectionId()));
        boolean teachesCourse = teacher.getCourses().stream().anyMatch(course -> course.getId().equals(request.getCourseId()));
        if (!teachesSection || !teachesCourse) {
            throw new IllegalArgumentException("You can only assign quizzes to your own sections and courses.");
        }
    }

    private AnswerOption buildOption(String label, String text, String correctOptionLabel) {
        AnswerOption option = new AnswerOption();
        option.setOptionLabel(label);
        option.setOptionText(text.trim());
        option.setCorrect(label.equalsIgnoreCase(correctOptionLabel.trim()));
        return option;
    }
}
