# Quiz Application

This project is a role based quiz application built with Java, Spring Boot, MySQL, HTML, and CSS.

## Database

- The project supports MySQL for the main app.
- The default local `mvn spring-boot:run` setup now uses H2 in-memory so the app starts even if MySQL is not installed yet.
- MySQL configuration is kept in `src/main/resources/application-mysql.properties`.
- To run with MySQL, activate the `mysql` profile.
- Production-only runtime settings are in `src/main/resources/application-prod.properties`.
- Tests also use H2 in-memory from `src/test/resources/application.properties`.
- Database credentials are no longer stored in source files.
- Use environment variables such as `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`.

## Secure Configuration

- Copy `.env.example` into your own local `.env` file if you want a private reference file.
- Do not commit `.env`; it is ignored by `.gitignore`.
- The project now supports secure database configuration through environment variables.

### Environment Variables

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DDL_AUTO`
- `PORT`

## Features

- Student and teacher registration with role selection.
- Login after registration with role based home pages.
- Students can view quizzes for only their section and course.
- Quiz state handling: `AVAILABLE`, `PENDING`, `ATTEMPTED`, and `MISSED`.
- Instant MCQ auto evaluation after submission.
- Student attempt history with previous scores.
- Teachers can create quizzes from the UI, assign them to a section and course, and monitor submissions.
- Teachers can see highest, lowest, and average scores per quiz.

## Assumptions

- A password field is included during registration because login would otherwise not be possible.
- A student belongs to one section and one registered course.
- A teacher may teach multiple sections and multiple courses.
- A quiz is assigned to one section and one course at a time.

## Design Pattern Requirements Covered

### GRASP

1. Controller:
   `AuthController`, `StudentController`, and `TeacherController` handle system events from the UI and delegate the domain work to services.

2. Creator:
   `TeacherQuizServiceImpl` creates `Quiz`, `Question`, and `AnswerOption` objects because it owns and aggregates the quiz creation flow.

### SOLID

1. SRP(Single Responsibility Principle):
   Authentication, registration, student quiz flow, teacher quiz flow, statistics, and session access are split into focused services.

2. DIP(Dependency Inversion Principle):
   Controllers depend on service interfaces such as `AuthService`, `StudentQuizService`, and `TeacherQuizService` instead of concrete classes.

### Creational Pattern

1. Factory Method:
   `UserRegistrationFactory` creates either `Student` or `Teacher` objects from registration input.

### Behavioral Pattern

1. Strategy:
   `QuizScoringStrategy` abstracts how quiz scores are calculated, and `McqQuizScoringStrategy` provides the current automatic correction logic.

## Run

1. Run locally without MySQL:
   `mvn spring-boot:run`
2. Or run with MySQL:
   `CREATE DATABASE quiz_app;`
3. Set environment variables:
   `export DB_URL=jdbc:mysql://localhost:3306/quiz_app`
   `export DB_USERNAME=your_mysql_username`
   `export DB_PASSWORD=your_mysql_password`
4. Start with MySQL profile:
   `mvn spring-boot:run -Dspring-boot.run.profiles=mysql`

## Deployment

To deploy without depending on `localhost`, host the app on a public server and provide database credentials through environment variables only.

### Recommended Runtime Profiles

- Local H2 demo:
  `mvn spring-boot:run`
- Local MySQL:
  `mvn spring-boot:run -Dspring-boot.run.profiles=mysql`
- Hosted production with MySQL:
  `java -jar target/quizapp-0.0.1-SNAPSHOT.jar --spring.profiles.active=mysql,prod`

### Docker

Build:
`docker build -t quizapp .`

Run with environment variables:
`docker run -p 8080:8080 -e DB_URL=jdbc:mysql://host:3306/quiz_app -e DB_USERNAME=your_mysql_username -e DB_PASSWORD=your_mysql_password -e PORT=8080 quizapp`
