package com.quizapp.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionView {

    private Long questionId;
    private String questionText;
    private Integer displayOrder;
    private List<QuizOptionView> options = new ArrayList<>();

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<QuizOptionView> getOptions() {
        return options;
    }

    public void setOptions(List<QuizOptionView> options) {
        this.options = options;
    }
}
