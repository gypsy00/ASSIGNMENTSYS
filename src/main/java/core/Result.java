package core;

public class Result {
    private final String resultId;
    private final Student student;
    private final Course course;
    private final int grade;

    public Result(String resultId, Student student, Course course, int grade) {
        this.resultId = resultId;
        this.student = student;
        this.course = course;
        this.grade = grade;
    }

    public String getResultId() { return resultId; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public int getGrade() { return grade; }
}

