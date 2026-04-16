package com.quizapp.dto;

import java.util.HashMap;
import java.util.Map;

public class QuizSubmissionRequest {

    private Map<Long, Long> selectedOptions = new HashMap<>();

    public Map<Long, Long> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Map<Long, Long> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
