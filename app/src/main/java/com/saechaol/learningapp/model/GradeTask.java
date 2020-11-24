package com.saechaol.learningapp.model;

import java.io.Serializable;

/**
 * Provides support for grading tasks
 */
public class GradeTask implements Serializable {

    public String taskId;
    public String subjectId;
    public String title;
    public String description;
    public String scheduleStartTime;
    public String scheduleEndTime;
    public String studentId;
    public String instructorGrade;

    public String getTaskId() {
        return taskId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScheduleStartTime() {
        return scheduleStartTime;
    }

    public String getScheduleEndTime() {
        return scheduleEndTime;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getInstructorGrade() {
        return instructorGrade;
    }
    
}
