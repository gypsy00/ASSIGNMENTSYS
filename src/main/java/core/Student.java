package core;

public class Student extends Users {
    private final String studentNumber;
    private final int yearOfStudy;

    public Student(String id, String name, String studentNumber, String email, int yearOfStudy) {
        super(id, name, email);
        this.studentNumber = studentNumber;
        this.yearOfStudy = yearOfStudy;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

}


