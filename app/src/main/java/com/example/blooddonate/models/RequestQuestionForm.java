package com.example.blooddonate.models;

public class RequestQuestionForm {

    private String question;
    private boolean isQualified;

    // Constructor

    public RequestQuestionForm() {}
    public RequestQuestionForm(String question, boolean isQualified) {
        this.question = question;
        this.isQualified = isQualified;
    }

    // Getter for question
    public String getQuestion() {
        return question;
    }

    // Setter for question
    public void setQuestion(String question) {
        this.question = question;
    }

    // Getter for isQualified
    public boolean isQualified() {
        return isQualified;
    }

    // Setter for isQualified
    public void setQualified(boolean qualified) {
        isQualified = qualified;
    }
}
