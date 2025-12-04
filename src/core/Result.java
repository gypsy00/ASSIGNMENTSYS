package core;

public class Result {

    //fields
    private String resultId;
    private Student student;
    private Course course;
    private int grade;

    //constructors
    public Result(String resultId, Student student, Course course, int grade) {
        this.resultId = resultId;
        this.student = student;
        this.course = course;
        this.grade = grade;
    }

    //getters
    public String getResultId() {
        return resultId;
    }
    public Student getStudent() {
        return student;
    }
    public Course getCourse() {
        return course;
    }
    public int getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return student.getName() + "-" + course.getCourseName() + ":" + grade;
    }

}
