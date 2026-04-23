# Quiz Application

A role-based quiz application built with Java, Spring Boot, Spring MVC, Spring Data JPA, Thymeleaf, MySQL/H2, HTML, and CSS.

The application has two main user interfaces:

- Student interface: students can register, log in, view quizzes assigned to their section and course, attempt available quizzes, receive instant scores, and view attempt history.
- Teacher interface: teachers can register, log in, create quizzes for their own sections and courses, monitor submissions, and view score statistics.

## Features

- Student and teacher registration with role selection.
- Login after registration with role-based redirection.
- Students see only quizzes assigned to their own section and course.
- Quiz state handling: `AVAILABLE`, `PENDING`, `ATTEMPTED`, and `MISSED`.
- MCQ quiz attempt flow with required option selection.
- Instant automatic scoring after submission.
- Student attempt history with previous scores.
- Teachers can create quizzes from the UI and assign them to a section and course.
- Teachers can view submission count, highest score, lowest score, average score, and individual student results.
- Local H2 profile for easy startup and MySQL profile for persistent deployment.

## Technology Stack

- Java 17+
- Spring Boot
- Spring MVC
- Spring Data JPA
- Thymeleaf
- MySQL for persistent runtime storage
- H2 for local/demo/test execution
- Maven

## MVC Architecture

This project uses MVC because the application has a clear separation between user interaction, business rules, and stored data. Students and teachers perform actions through web pages, but the rules for quiz availability, quiz ownership, registration, authentication, scoring, and statistics should not live inside those pages. MVC keeps those responsibilities separated and easier to test, maintain, and extend.

### Model

The Model represents the application data and domain relationships.

- `AppUser`, `Student`, and `Teacher`: represent registered users and their roles.
- `Section` and `Course`: represent the academic grouping used to assign quizzes.
- `Quiz`, `Question`, and `AnswerOption`: represent a complete quiz structure.
- `QuizAttempt` and `AttemptAnswer`: represent a student's submitted quiz and selected answers.
- `QuizStatus`: represents the current state of a quiz for a student.
- Repository classes such as `QuizRepository`, `StudentRepository`, `TeacherRepository`, and `QuizAttemptRepository` provide database access for these model objects.

### View

The View is implemented with Thymeleaf templates. These files render data prepared by controllers and services.

- `templates/auth/index.html`: login, student registration, and teacher registration page.
- `templates/student/home.html`: student dashboard showing assigned quizzes and attempt history.
- `templates/student/attempt.html`: quiz attempt screen.
- `templates/student/result.html`: instant result page after submission.
- `templates/teacher/home.html`: teacher dashboard with quiz summaries and statistics.
- `templates/teacher/create-quiz.html`: teacher quiz creation form.
- `templates/teacher/results.html`: detailed quiz result table for a teacher.

### Controller

Controllers receive HTTP requests, validate the logged-in role, call services, place data into the `Model`, and return the correct Thymeleaf view.

- `AuthController`: handles login, logout, role-based home redirection, and registration endpoints.
- `StudentController`: handles student dashboard, quiz attempt page, and quiz submission.
- `TeacherController`: handles teacher dashboard, quiz creation, and quiz result pages.

### Service

Services contain business logic. This keeps controllers thin and prevents HTML pages from containing domain rules.

- `AuthServiceImpl`: verifies login credentials and selected role.
- `SessionServiceImpl`: stores, reads, clears, and validates the logged-in user session.
- `RegistrationServiceImpl`: coordinates student and teacher registration.
- `AcademicCatalogServiceImpl`: creates or resolves sections and courses.
- `StudentQuizServiceImpl`: controls quiz visibility, quiz status, authorized quiz attempt, submission, and attempt history.
- `TeacherQuizServiceImpl`: controls quiz creation, teacher ownership checks, section/course access, quiz summaries, and result views.
- `QuizStatisticsServiceImpl`: calculates submission count, highest score, lowest score, and average score.

## Why MVC Is Used

MVC is used because the system has two different role-based interfaces that share the same domain data but require different workflows.

- Separation of concerns: controllers handle requests, services handle business rules, repositories handle persistence, and templates handle display.
- Maintainability: student features can be changed mainly in `StudentController`, `StudentQuizServiceImpl`, and student templates without disturbing teacher quiz creation logic.
- Reusability: shared services such as `SessionServiceImpl`, `AcademicCatalogServiceImpl`, and `QuizStatisticsServiceImpl` are reusable across controllers.
- Testability: service classes can be tested without rendering web pages.
- Security and correctness: role checks are centralized through `SessionServiceImpl.requireRole(...)`, reducing repeated authorization logic.
- Extensibility: new quiz types, scoring rules, or dashboard views can be added by extending service interfaces and view models.

## Activity-to-Class Mapping

| Activity | Controller | Service / Pattern Class | Repository / Model | View |
| --- | --- | --- | --- | --- |
| Show login and registration page | `AuthController.showAuthPage` | `SessionServiceImpl.getCurrentUser` | `UserRole` | `auth/index.html` |
| Student registration | `AuthController.registerStudent` | `RegistrationServiceImpl.registerStudent`, `UserRegistrationFactoryImpl.createStudent`, `AcademicCatalogServiceImpl` | `StudentRepository`, `Student`, `Section`, `Course` | `auth/index.html` |
| Teacher registration | `AuthController.registerTeacher` | `RegistrationServiceImpl.registerTeacher`, `UserRegistrationFactoryImpl.createTeacher`, `AcademicCatalogServiceImpl` | `TeacherRepository`, `Teacher`, `Section`, `Course` | `auth/index.html` |
| Login | `AuthController.login` | `AuthServiceImpl.login`, `SessionServiceImpl.store` | `AppUserRepository`, `AppUser` | Redirects to `/home` |
| Role-based routing | `AuthController.home` | `SessionServiceImpl.getCurrentUser` | `SessionUser`, `UserRole` | Redirects to student or teacher home |
| Student dashboard | `StudentController.home` | `StudentQuizServiceImpl.getAvailableQuizzes`, `StudentQuizServiceImpl.getAttemptHistory` | `QuizRepository`, `QuizAttemptRepository`, `Student` | `student/home.html` |
| Student opens quiz attempt | `StudentController.attemptQuiz` | `StudentQuizServiceImpl.getQuizForAttempt` | `Quiz`, `Question`, `AnswerOption` | `student/attempt.html` |
| Student submits quiz | `StudentController.submitQuiz` | `StudentQuizServiceImpl.submitQuiz`, `McqQuizScoringStrategy.score` | `QuizAttempt`, `AttemptAnswer`, `QuizAttemptRepository` | `student/result.html` |
| Teacher dashboard | `TeacherController.home` | `TeacherQuizServiceImpl.getTeacherQuizzes`, `QuizStatisticsServiceImpl.calculate` | `QuizRepository`, `QuizAttemptRepository`, `Teacher` | `teacher/home.html` |
| Teacher opens quiz form | `TeacherController.createQuizForm` | `TeacherQuizServiceImpl.buildEmptyQuizForm`, `getTeacherSections`, `getTeacherCourses` | `Teacher`, `Section`, `Course` | `teacher/create-quiz.html` |
| Teacher creates quiz | `TeacherController.createQuiz` | `TeacherQuizServiceImpl.createQuiz` | `Quiz`, `Question`, `AnswerOption`, `QuizRepository` | Redirects to `teacher/home.html` |
| Teacher views results | `TeacherController.quizResults` | `TeacherQuizServiceImpl.getQuizStatistics`, `TeacherQuizServiceImpl.getQuizResults` | `QuizAttemptRepository`, `QuizAttempt` | `teacher/results.html` |
| Logout | `AuthController.logout` | `SessionServiceImpl.clear` | HTTP session | Redirects to `auth/index.html` |

## Design Principles and Patterns

### GRASP Principles

#### Controller

Used in:

- `AuthController`
- `StudentController`
- `TeacherController`

Why:

These classes receive system events from the web interface. For example, submitting a quiz is received by `StudentController.submitQuiz(...)`, and creating a quiz is received by `TeacherController.createQuiz(...)`. The controllers do not directly calculate scores or save complex objects; they delegate to services.

#### Information Expert

Used in:

- `StudentQuizServiceImpl.resolveStatus(...)`
- `StudentQuizServiceImpl.getAuthorizedQuiz(...)`
- `TeacherQuizServiceImpl.getOwnedQuiz(...)`
- `QuizStatisticsServiceImpl.calculate(...)`

Why:

The class that has the required data performs the related decision. `StudentQuizServiceImpl` knows the student, quiz, deadline, start time, and attempts, so it decides whether a quiz is `AVAILABLE`, `PENDING`, `ATTEMPTED`, or `MISSED`. `QuizStatisticsServiceImpl` receives all attempts, so it calculates score statistics.

#### Creator

Used in:

- `TeacherQuizServiceImpl.createQuiz(...)`
- `TeacherQuizServiceImpl.buildOption(...)`
- `StudentQuizServiceImpl.submitQuiz(...)`
- `UserRegistrationFactoryImpl.createStudent(...)`
- `UserRegistrationFactoryImpl.createTeacher(...)`

Why:

The class that coordinates the creation flow creates the related objects. `TeacherQuizServiceImpl.createQuiz(...)` creates `Quiz`, `Question`, and `AnswerOption` objects because that service owns the quiz creation activity. `StudentQuizServiceImpl.submitQuiz(...)` creates `QuizAttempt` and `AttemptAnswer` objects because that service owns the quiz submission activity.

#### Low Coupling

Used in:

- Controllers depending on interfaces such as `StudentQuizService`, `TeacherQuizService`, `AuthService`, `RegistrationService`, and `SessionService`.
- Services depending on repository interfaces such as `QuizRepository`, `StudentRepository`, and `TeacherRepository`.

Why:

Controllers do not know database details, and views do not know business logic. This reduces the impact of change. For example, changing the scoring strategy does not require changing `StudentController`.

#### High Cohesion

Used in:

- `AuthServiceImpl`: only authentication.
- `SessionServiceImpl`: only session management.
- `RegistrationServiceImpl`: only registration coordination.
- `StudentQuizServiceImpl`: only student quiz workflow.
- `TeacherQuizServiceImpl`: only teacher quiz workflow.
- `QuizStatisticsServiceImpl`: only statistics calculation.

Why:

Each class has a focused purpose. This makes the application easier to understand and avoids one large service class doing every task.

#### Protected Variations

Used in:

- `QuizScoringStrategy` interface
- `McqQuizScoringStrategy` implementation
- Service interfaces such as `StudentQuizService`, `TeacherQuizService`, and `RegistrationService`

Why:

The application is protected from future variation. If another scoring type is added later, a new scoring strategy can be introduced without rewriting the student submission controller.

### SOLID Principles

#### Single Responsibility Principle

Used in:

- `AuthServiceImpl` for login validation.
- `RegistrationServiceImpl` for registration.
- `AcademicCatalogServiceImpl` for sections and courses.
- `StudentQuizServiceImpl` for student quiz behavior.
- `TeacherQuizServiceImpl` for teacher quiz behavior.
- `QuizStatisticsServiceImpl` for score statistics.

Why:

Each class has only one main reason to change. For example, changes to teacher result statistics are isolated from student quiz attempt logic.

#### Open/Closed Principle

Used in:

- `QuizScoringStrategy`
- `McqQuizScoringStrategy`

Why:

The scoring behavior is open for extension. A future `NegativeMarkingScoringStrategy` or `TimedQuizScoringStrategy` can be added without changing `StudentController.submitQuiz(...)`.

#### Liskov Substitution Principle

Used in:

- `Student` and `Teacher` extending `AppUser`
- `StudentQuizServiceImpl` implementing `StudentQuizService`
- `TeacherQuizServiceImpl` implementing `TeacherQuizService`
- `McqQuizScoringStrategy` implementing `QuizScoringStrategy`

Why:

Code can use the parent abstraction safely. For example, authentication can load an `AppUser` and then check the `UserRole`, while specific student and teacher data remains in child classes.

#### Interface Segregation Principle

Used in:

- `StudentQuizService`
- `TeacherQuizService`
- `AuthService`
- `RegistrationService`
- `SessionService`
- `QuizStatisticsService`
- `AcademicCatalogService`

Why:

Controllers depend only on the operations they need. `StudentController` does not depend on teacher quiz creation methods, and `TeacherController` does not depend on student submission methods.

#### Dependency Inversion Principle

Used in:

- `StudentController` depending on `StudentQuizService` and `SessionService`.
- `TeacherController` depending on `TeacherQuizService` and `SessionService`.
- `AuthController` depending on `AuthService`, `RegistrationService`, and `SessionService`.
- `StudentQuizServiceImpl` depending on `QuizScoringStrategy`.

Why:

High-level classes depend on abstractions instead of concrete implementations. This improves testability and allows implementation changes without changing controller code.

## Design Patterns Used

### Factory Pattern

Used in:

- `UserRegistrationFactory`
- `UserRegistrationFactoryImpl`
- Called by `RegistrationServiceImpl.registerStudent(...)`
- Called by `RegistrationServiceImpl.registerTeacher(...)`

Purpose:

The factory creates the correct user object for the selected role. Student and teacher registration require different domain objects and relationships. A student has one section and one course, while a teacher can have multiple sections and courses.

Why it is used:

User creation is separated from registration coordination. `RegistrationServiceImpl` validates uniqueness and prepares related objects, while `UserRegistrationFactoryImpl` builds the actual `Student` or `Teacher` object.

### Strategy Pattern

Used in:

- `QuizScoringStrategy`
- `McqQuizScoringStrategy`
- Called by `StudentQuizServiceImpl.submitQuiz(...)`

Purpose:

The strategy calculates quiz scores. The current strategy checks selected MCQ options against correct options and returns a `ScoringResult`.

Why it is used:

Scoring rules can change independently from the quiz submission flow. The submission service saves the attempt, but the strategy decides how marks are calculated.

### Repository Pattern

Used in:

- `AppUserRepository`
- `StudentRepository`
- `TeacherRepository`
- `QuizRepository`
- `QuizAttemptRepository`
- `SectionRepository`
- `CourseRepository`
- `AnswerOptionRepository`

Purpose:

Repositories hide database access behind simple method calls.

Why it is used:

Service classes can ask for data using meaningful methods such as `findByTeacherOrderByDeadlineDesc(...)` or `findByQuizAndStudent(...)` instead of writing SQL in controllers or views.

### DTO / View Model Pattern

Used in:

- Request DTOs: `LoginRequest`, `StudentRegistrationRequest`, `TeacherRegistrationRequest`, `QuizCreationRequest`, `QuizSubmissionRequest`
- View DTOs: `QuizCardView`, `QuizAttemptView`, `QuizQuestionView`, `QuizOptionView`, `QuizSubmissionResultView`, `StudentAttemptHistoryView`, `TeacherQuizSummaryView`, `TeacherStudentResultView`, `QuizStatisticsView`

Purpose:

DTOs carry only the data needed for a specific form or page.

Why it is used:

Templates do not need to directly expose full entity objects. This keeps views simpler and avoids leaking internal domain structure into the UI.

## Student Interface Principles

The student interface is designed for safe, focused quiz taking.

Used in:

- `StudentController`
- `StudentQuizServiceImpl`
- `student/home.html`
- `student/attempt.html`
- `student/result.html`

Principles:

- Role-based access: `SessionServiceImpl.requireRole(session, UserRole.STUDENT)` ensures only students can access student pages.
- Least privilege: `StudentQuizServiceImpl.getAuthorizedQuiz(...)` ensures a student can only open quizzes assigned to their own section and course.
- Clear status feedback: `StudentQuizServiceImpl.resolveStatus(...)` returns `AVAILABLE`, `PENDING`, `ATTEMPTED`, or `MISSED`, and `student/home.html` shows matching badges and disabled buttons.
- Simplicity: the student dashboard separates current quizzes from attempt history.
- Error prevention: `student/attempt.html` uses required radio buttons so each question must have an answer before submission.
- Immediate feedback: `StudentQuizServiceImpl.submitQuiz(...)` returns `QuizSubmissionResultView`, which is shown on `student/result.html`.
- Data minimization: student views use DTOs like `QuizCardView` and `QuizAttemptView`, not raw database entities.

Why these principles are used:

Students need a simple and controlled flow. They should see only relevant quizzes, should not attempt expired or unauthorized quizzes, and should receive immediate confirmation of their score.

## Teacher Interface Principles

The teacher interface is designed for quiz management, ownership control, and performance monitoring.

Used in:

- `TeacherController`
- `TeacherQuizServiceImpl`
- `QuizStatisticsServiceImpl`
- `teacher/home.html`
- `teacher/create-quiz.html`
- `teacher/results.html`

Principles:

- Role-based access: `SessionServiceImpl.requireRole(session, UserRole.TEACHER)` ensures only teachers can access teacher pages.
- Ownership validation: `TeacherQuizServiceImpl.getOwnedQuiz(...)` ensures teachers can view results only for their own quizzes.
- Assignment control: `TeacherQuizServiceImpl.validateQuizRequest(...)` ensures teachers can assign quizzes only to their own sections and courses.
- Data-driven dashboard: `TeacherQuizServiceImpl.getTeacherQuizzes(...)` combines quiz information with statistics for the teacher dashboard.
- Focused creation flow: `teacher/create-quiz.html` groups quiz metadata, section, course, schedule, description, questions, options, and correct answer selection.
- Performance visibility: `QuizStatisticsServiceImpl.calculate(...)` provides submission count, highest score, lowest score, and average score.
- Detailed review: `TeacherQuizServiceImpl.getQuizResults(...)` returns individual student scores for `teacher/results.html`.

Why these principles are used:

Teachers need more management power than students, so the interface emphasizes creation, assignment, monitoring, and result analysis. Ownership checks are important because a teacher should not manage quizzes created by another teacher.

## Database

- The project supports MySQL for the main app.
- The default local `mvn spring-boot:run` setup uses H2 in-memory so the app starts even if MySQL is not installed yet.
- MySQL configuration is kept in `src/main/resources/application-mysql.properties`.
- To run with MySQL, activate the `mysql` profile.
- Production-only runtime settings are in `src/main/resources/application-prod.properties`.
- Tests also use H2 in-memory from `src/test/resources/application.properties`.
- Database credentials are not stored in source files.
- Use environment variables such as `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.

## Secure Configuration

- Copy `.env.example` into your own local `.env` file if you want a private reference file.
- Do not commit `.env`; it is ignored by `.gitignore`.
- The project supports secure database configuration through environment variables.

### Environment Variables

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DDL_AUTO`
- `PORT`

## Assumptions

- A password field is included during registration because login would otherwise not be possible.
- A student belongs to one section and one registered course.
- A teacher may teach multiple sections and multiple courses.
- A quiz is assigned to one section and one course at a time.
- Current automatic evaluation is for MCQ questions.

## Run

1. Run locally without MySQL:

   ```bash
   mvn spring-boot:run
   ```

2. Or run with MySQL:

   ```sql
   CREATE DATABASE quiz_app;
   ```

3. Set environment variables:

   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/quiz_app
   export DB_USERNAME=your_mysql_username
   export DB_PASSWORD=your_mysql_password
   ```

4. Start with MySQL profile:

   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

## Deployment

To deploy without depending on `localhost`, host the app on a public server and provide database credentials through environment variables only.

### Recommended Runtime Profiles

- Local H2 demo:

  ```bash
  mvn spring-boot:run
  ```

- Local MySQL:

  ```bash
  mvn spring-boot:run -Dspring-boot.run.profiles=mysql
  ```

- Hosted production with MySQL:

  ```bash
  java -jar target/quizapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql,prod
  ```

### Docker

Build:

```bash
docker build -t quizapp .
```

Run with environment variables:

```bash
docker run -p 8080:8080 -e DB_URL=jdbc:mysql://host:3306/quiz_app -e DB_USERNAME=your_mysql_username -e DB_PASSWORD=your_mysql_password -e PORT=8080 quizapp
```
