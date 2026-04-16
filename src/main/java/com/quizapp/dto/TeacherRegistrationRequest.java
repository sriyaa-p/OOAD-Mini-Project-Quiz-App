package com.quizapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class TeacherRegistrationRequest {

    @NotBlank
    private String userCode;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String sectionsInput;

    @NotBlank
    private String coursesInput;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSectionsInput() {
        return sectionsInput;
    }

    public void setSectionsInput(String sectionsInput) {
        this.sectionsInput = sectionsInput;
    }

    public String getCoursesInput() {
        return coursesInput;
    }

    public void setCoursesInput(String coursesInput) {
        this.coursesInput = coursesInput;
    }
}
