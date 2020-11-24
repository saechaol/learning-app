package com.saechaol.learningapp.model;

public class TaskDetails {

    public int taskId;
    public String subjectId;
    public String instructorId;
    public String title;
    public String description;
    public boolean isInProgress;
    public int progressMade;
    public String scheduleStartTime;
    public String scheduleEndTime;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int task) {
        this.taskId = task;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subject) {
        this.subjectId = subject;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructor) {
        this.instructorId = instructor;
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

    public boolean isInProgress() {
        return isInProgress;
    }

    public int getProgressMade() {
        return progressMade;
    }

    public void setProgressMade(int progress) {
        this.progressMade = progress;
    }

    public String getScheduleStartTime() {
        return scheduleStartTime;
    }

    public String getScheduleEndTime() {
        return scheduleEndTime;
    }

}
