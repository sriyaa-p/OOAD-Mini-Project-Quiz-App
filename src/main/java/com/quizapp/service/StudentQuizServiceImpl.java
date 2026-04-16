package com.quizapp.service;

import com.quizapp.domain.AttemptAnswer;
import com.quizapp.domain.Quiz;
import com.quizapp.domain.QuizAttempt;
import com.quizapp.domain.QuizStatus;
import com.quizapp.domain.Student;
import com.quizapp.dto.QuizAttemptView;
import com.quizapp.dto.QuizCardView;
import com.quizapp.dto.QuizOptionView;
import com.quizapp.dto.QuizQuestionView;
import com.quizapp.dto.QuizSubmissionRequest;
import com.quizapp.dto.QuizSubmissionResultView;
import com.quizapp.dto.StudentAttemptHistoryView;
import com.quizapp.repository.QuizAttemptRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.StudentRepository;
import com.quizapp.service.patterns.strategy.QuestionEvaluation;
import com.quizapp.service.patterns.strategy.QuizScoringStrategy;
import com.quizapp.service.patterns.strategy.ScoringResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class StudentQuizServiceImpl implements StudentQuizService {

    private final StudentRepository studentRepository;
    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizScoringStrategy quizScoringStrategy;

    public StudentQuizServiceImpl(StudentRepository studentRepository,
                                  QuizRepository quizRepository,
                                  QuizAttemptRepository quizAttemptRepository,
                                  QuizScoringStrategy quizScoringStrategy) {
        this.studentRepository = studentRepository;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizScoringStrategy = quizScoringStrategy;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizCardView> getAvailableQuizzes(Long studentId) {
        Student student = getStudent(studentId);
        List<Quiz> quizzes = quizRepository.findBySectionAndCourseOrderByAvailableFromDesc(student.getSection(), student.getCourse());
        List<QuizCardView> cards = new ArrayList<>();

        for (Quiz quiz : quizzes) {
            QuizCardView card = new QuizCardView();
            card.setQuizId(quiz.getId());
            card.setTitle(quiz.getTitle());
            card.setSectionName(quiz.getSection().getName());
            card.setCourseName(quiz.getCourse().getName());
            card.setAvailableFrom(quiz.getAvailableFrom());
            card.setDeadline(quiz.getDeadline());

            quizAttemptRepository.findByQuizAndStudent(quiz, student).ifPresent(attempt -> card.setScore(attempt.getScore()));
            card.setStatus(resolveStatus(quiz, student));
            cards.add(card);
        }

        return cards;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentAttemptHistoryView> getAttemptHistory(Long studentId) {
        Student student = getStudent(studentId);
        return quizAttemptRepository.findByStudentOrderBySubmittedAtDesc(student)
                .stream()
                .map(attempt -> {
                    StudentAttemptHistoryView item = new StudentAttemptHistoryView();
                    item.setAttemptId(attempt.getId());
                    item.setQuizTitle(attempt.getQuiz().getTitle());
                    item.setScore(attempt.getScore());
                    item.setTotalQuestions(attempt.getTotalQuestions());
                    item.setSubmittedAt(attempt.getSubmittedAt());
                    return item;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptView getQuizForAttempt(Long studentId, Long quizId) {
        Student student = getStudent(studentId);
        Quiz quiz = getAuthorizedQuiz(student, quizId);

        if (resolveStatus(quiz, student) != QuizStatus.AVAILABLE) {
            throw new IllegalArgumentException("This quiz is not available for attempt.");
        }

        QuizAttemptView view = new QuizAttemptView();
        view.setQuizId(quiz.getId());
        view.setTitle(quiz.getTitle());
        view.setDescription(quiz.getDescription());
        view.setDeadline(quiz.getDeadline());

        List<QuizQuestionView> questions = quiz.getQuestions()
                .stream()
                .sorted(Comparator.comparing(question -> question.getDisplayOrder() == null ? 0 : question.getDisplayOrder()))
                .map(question -> {
                    QuizQuestionView questionView = new QuizQuestionView();
                    questionView.setQuestionId(question.getId());
                    questionView.setQuestionText(question.getQuestionText());
                    questionView.setDisplayOrder(question.getDisplayOrder());
                    questionView.setOptions(question.getOptions().stream().map(option -> {
                        QuizOptionView optionView = new QuizOptionView();
                        optionView.setOptionId(option.getId());
                        optionView.setLabel(option.getOptionLabel());
                        optionView.setText(option.getOptionText());
                        return optionView;
                    }).toList());
                    return questionView;
                })
                .toList();

        view.setQuestions(questions);
        return view;
    }

    @Override
    public QuizSubmissionResultView submitQuiz(Long studentId, Long quizId, QuizSubmissionRequest request) {
        Student student = getStudent(studentId);
        Quiz quiz = getAuthorizedQuiz(student, quizId);

        if (resolveStatus(quiz, student) != QuizStatus.AVAILABLE) {
            throw new IllegalArgumentException("This quiz can no longer be submitted.");
        }

        ScoringResult scoringResult = quizScoringStrategy.score(quiz, request.getSelectedOptions());

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setScore(scoringResult.getScore());
        attempt.setTotalQuestions(scoringResult.getTotalQuestions());

        for (QuestionEvaluation evaluation : scoringResult.getEvaluations()) {
            AttemptAnswer attemptAnswer = new AttemptAnswer();
            attemptAnswer.setQuestion(evaluation.getQuestion());
            attemptAnswer.setSelectedOption(evaluation.getSelectedOption());
            attemptAnswer.setCorrect(evaluation.isCorrect());
            attempt.addAttemptAnswer(attemptAnswer);
        }

        quizAttemptRepository.save(attempt);

        QuizSubmissionResultView resultView = new QuizSubmissionResultView();
        resultView.setQuizTitle(quiz.getTitle());
        resultView.setScore(scoringResult.getScore());
        resultView.setTotalQuestions(scoringResult.getTotalQuestions());
        return resultView;
    }

    private QuizStatus resolveStatus(Quiz quiz, Student student) {
        if (quizAttemptRepository.existsByQuizAndStudent(quiz, student)) {
            return QuizStatus.ATTEMPTED;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(quiz.getAvailableFrom())) {
            return QuizStatus.PENDING;
        }
        if (now.isAfter(quiz.getDeadline())) {
            return QuizStatus.MISSED;
        }
        return QuizStatus.AVAILABLE;
    }

    private Student getStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student account not found."));
    }

    private Quiz getAuthorizedQuiz(Student student, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found."));

        boolean sameSection = quiz.getSection().getId().equals(student.getSection().getId());
        boolean sameCourse = quiz.getCourse().getId().equals(student.getCourse().getId());
        if (!sameSection || !sameCourse) {
            throw new IllegalArgumentException("This quiz is not assigned to your section or course.");
        }
        return quiz;
    }
}
