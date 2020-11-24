package com.saechaol.learningapp.model;

/**
 * Provides support for retrieving and updating course details
 */
public class SubjectDetails {

    public String subjectId;
    public String title;
    public String description;
    public String isVideoEnabled;
    public String isAudioEnabled;
    public String startDate;
    public String endDate;
    public String instructorId;
    public String startTime;
    public String endTime;
    public String mailAlias;
    public int duration;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String id) {
        this.subjectId = id;
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

    public String getIsVideoEnabled() {
        return isVideoEnabled;
    }

    public void setIsVideoEnabled(String video) {
        isVideoEnabled = video;
    }

    public String getIsAudioEnabled() {
        return isAudioEnabled;
    }

    public void setIsAudioEnabled(String audio) {
        this.isAudioEnabled = audio;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String start) {
        this.startDate = start;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String end) {
        this.endDate = end;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructor) {
        this.instructorId = instructor;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String start) {
        this.startTime = start;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String end) {
        this.endTime = end;
    }

    public String getMailAlias() {
        return mailAlias;
    }

    public void setMailAlias(String mail) {
        this.mailAlias = mail;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
