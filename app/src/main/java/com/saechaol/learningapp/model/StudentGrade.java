package com.saechaol.learningapp.model;

/**
 * Provides a base object for storing student grade data. Supports
 * data retrieval and mutation.
 */
public class StudentGrade {

    String taskId;
    String topic;
    String grade;

    public StudentGrade(String taskId, String topic, String grade) {
        this.taskId = taskId;
        this.topic = topic;
        this.grade = grade;
    }

    public String getTopic() {
        return topic;
    }

    public String getGrade() {
        return grade;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

}
