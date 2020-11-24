package com.saechaol.learningapp.model;

import com.sinch.gson.annotations.Expose;
import com.sinch.gson.annotations.SerializedName;

/**
 * Provides support for retrieving and updating enrollment data
 */
public class StudentEnrollmentPostData {

    @SerializedName("subject_id")
    @Expose
    private String subjectId;

    @SerializedName("student_id")
    @Expose
    private String studentId;

    @SerializedName("instructor_id")
    @Expose
    private String instructorId;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String id) {
        this.subjectId = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String id) {
        this.studentId = id;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

}
