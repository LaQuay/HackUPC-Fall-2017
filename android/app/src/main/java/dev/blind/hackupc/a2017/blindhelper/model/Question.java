package dev.blind.hackupc.a2017.blindhelper.model;

public class Question {
    private String questionText;

    public Question(String text) {
        this.questionText = text;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
