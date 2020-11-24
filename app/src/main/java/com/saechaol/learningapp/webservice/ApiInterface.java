package com.saechaol.learningapp.webservice;

import com.saechaol.learningapp.model.AdminDetails;
import com.saechaol.learningapp.model.GradeTask;
import com.saechaol.learningapp.model.InstructorDetails;
import com.saechaol.learningapp.model.RegisterUsers;
import com.saechaol.learningapp.model.ScheduleDetailPostData;
import com.saechaol.learningapp.model.StudentDetails;
import com.saechaol.learningapp.model.StudentEnrollmentPostData;
import com.saechaol.learningapp.model.SubjectDetails;
import com.saechaol.learningapp.model.TaskDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Provides base API for the application
 */
public interface ApiInterface {

    @GET("api/Register/GetRegisterAuth")
    Call<List<RegisterUsers>> authenticate(@Query("userName") String userName, @Query("password") String password);

    @GET("api/Admin/GetAdminByUserName")
    Call<List<AdminDetails>> getAdminInfo(@Query("userName") String userName);


    @GET("api/Student/GetStudentByUserName")
    Call<List<StudentDetails>> getStudentInfo(@Query("userName") String userName);


    @GET("api/Instructor/GetInstructorByUserName")
    Call<List<InstructorDetails>> getInstructorInfo(@Query("userName") String userName);


    @GET("api/Admin/GetAllAdmin")
    Call<List<AdminDetails>> getAdmins();

    @POST("api/DeleteAdmin/PostAdminRmv")
    Call<String> removeAdmin(@Query("idAdminRmv") String adminUserName);

    @POST("api/Register/PostAddAdmin")
    Call<AdminDetails> addAdmin(@Query("adminUserName") String adminUserName, @Query("adminPassword") String adminPassword, @Query("adminFirsName") String adminFirsName, @Query("adminLastName") String adminLastName, @Query("adminTelephone") String adminTelephone, @Query("adminAddress") String adminAddress, @Query("adminAliasMailId") String adminAliasMailId, @Query("adminEmailId") String adminEmailId, @Query("adminSkypeId") String adminSkypeId);

    @POST("api/Admin/PostAdminUpdate")
    Call<String> updateAdmin(@Body AdminDetails userDetails);


    @GET("api/Instructor/GetAllInstructor")
    Call<List<InstructorDetails>> getInstructors();

    @POST("api/DeleteInstructor/PostInstructorRmv")
    Call<String> removeInstructor(@Query("idInstructorRmv") String idInstructorRmv);

    @POST("api/Register/PostAddInstructor")
    Call<InstructorDetails> addInstructor(@Query("instUserName") String userName, @Query("instPassword") String password, @Query("instFirsName") String instFirsName, @Query("instLastName") String instLastName, @Query("instTelephone") String instTelephone, @Query("instAddress") String instAddress, @Query("instAliasMailId") String instAliasMailId, @Query("instEmailId") String instEmailId, @Query("instSkypeId") String instSkypeId);

    @POST("api/Instructor/PostInstructorUpdate/")
    Call<String> updateInstructor(@Body InstructorDetails userDetails);


    @GET("api/Student/GetAllStudent")
    Call<List<StudentDetails>> getStudents();

    @GET("api/Subject/GetSubjectByStudent")
    Call<ArrayList<SubjectDetails>> getSubjectForStudent(@Query("idStudent") String idStudent);

    @POST("api/DeleteStudent/PostStudentRmv")
    Call<String> removeStudent(@Query("idStudentRmv") String idInstructorRmv);

    @POST("api/Register/PostAddStudent")
    Call<StudentDetails> addStudent(@Query("userName") String userName, @Query("password") String password, @Query("firsName") String instFirsName, @Query("lastName") String instLastName, @Query("telephone") String instTelephone, @Query("address") String instAddress, @Query("aliasMailId") String instAliasMailId, @Query("emailId") String instEmailId, @Query("skypeId") String instSkypeId);

    @POST("api/Student/PostStudentUpdate/")
    Call<String> updateStudent(@Body StudentDetails userDetails);

    @POST("api/Register/PostRegisterPassUpdate")
    Call<String> changePassword(@Query("userName") String userName, @Query("password") String password);


    @GET("api/Subject/GetAllSubject")
    Call<List<SubjectDetails>> getAllSubject();

    @GET("api/Subject/GetAllSubjectWithTask")
    Call<List<SubjectDetails>> getAllSubjectWithTask(@Query("flag") String subjectId);

    @POST("api/Subject/PostSubject")
    Call<SubjectDetails> addSubject(@Body SubjectDetails subjectDetails);

    @POST("api/SubjectUpdate/PostSubjectUpdate")
    Call<String> updateSubject(@Body SubjectDetails subjectDetails);


    @POST("api/SubjectRmv/PostSubjectRmv")
    Call<String> removeSubject(@Query("subject_id") String idSubject);

    @GET("api/DeEnrollStudent/GetDeEnrollBySubject")
    Call<List<StudentDetails>> getDeEnrollBySubject(@Query("idSubject") String subjectId);


    @GET("api/EnrollStudent/GetEnrollBySubject")
    Call<List<StudentDetails>> getEnrollBySubject(@Query("idSubject") String subjectId);

    @POST("api/EnrollStudent/PostEnrollStudent")
    Call<StudentEnrollmentPostData> enrollBySubject(@Body StudentEnrollmentPostData listStudentData);


    @POST("api/DeEnrollStudent/PostDeEnroll")
    Call<StudentEnrollmentPostData> deEnrollBySubject(@Body StudentEnrollmentPostData listStudentData);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("api/EnrollStudent/GetSubjectByStd")
    Call<ArrayList<SubjectDetails>> getEnrolledSubjectForStudent(@Query("idStudent") String idStudent);

    @POST("api/Tasks/PostTask/")
    Call<String> addSchedule(@Body ScheduleDetailPostData details);


    @POST("api/ScheduleRmv/PostTaskRmv")
    Call<String> removeTasks(@Query("subject_id") String subjectId);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("api/Tasks/PostTaskUpdate")
    Call<String> updateTaskData(@Query("idTask") String idTask, @Query("topic") String topic, @Query("description") String desc);


    @GET("api/UserTasks/GetTasksByUser")
    Call<List<TaskDetails>> getTaskByUser(@Query("userId") String userName, @Query("userType") String userType);


    @GET("api/Tasks/GetTasksBySubject")
    Call<List<TaskDetails>> getTasksBySubject(@Query("subjectId") String subjectId);

    @GET("api/Tasks/GetStudentByTask")
    Call<List<GradeTask>> getGrades(@Query("task") String task, @Query("subjectid") String subjId);

    @GET("api/Tasks/GetTasksByStudent")
    Call<List<TaskDetails>> getListTaskForStudent(@Query("subject") String subject, @Query("studentId") String studentId);


    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("api/Tasks/PostGradeUpdate")
    Call<String> updateGrade(@Query("task_id") String taskId, @Query("student_id") String student_id, @Query("grade") String grade);

    @GET("api/Tasks/GetTasksByStudent")
    Call<List<GradeTask>> getGradesForStudent(@Query("studentId") String studentId, @Query("subject") String subject);

}
