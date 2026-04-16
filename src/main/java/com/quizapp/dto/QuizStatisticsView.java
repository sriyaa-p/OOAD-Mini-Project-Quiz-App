package com.quizapp.dto;

public class QuizStatisticsView {

    private long submissionCount;
    private Integer highestScore;
    private Integer lowestScore;
    private Double averageScore;

    public long getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(long submissionCount) {
        this.submissionCount = submissionCount;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Integer getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(Integer lowestScore) {
        this.lowestScore = lowestScore;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }
}
