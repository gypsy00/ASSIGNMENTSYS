package core;

public class Enrollment {
    // fields
    private String enrollmentId;
    private Student student;
    private Course course;

    // constructor
    public Enrollment(String enrollmentId, Student student, Course course) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
    }

    // getters
    public String getEnrollmentId() {
        return enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return student.getName() + " enrolled on " + course.getCourseName();
    }

}
