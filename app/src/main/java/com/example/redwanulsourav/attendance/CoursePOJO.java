package com.example.redwanulsourav.attendance;

public class CoursePOJO {
    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    private String courseName;
    private String courseNumber;
    CoursePOJO(){

    }

    public CoursePOJO(String courseName, String courseNumber) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
    }
}
