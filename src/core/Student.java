package core;

public class Student extends Users {

    //Fields
    private String studentNumber;
    private int yearOfStudy;

    // Constructor to set all fields (calls Users constructor)
    public Student(String id, String name, String email, String studentNumber, int yearOfStudy) {
        super(id, name, email);
        this.studentNumber = studentNumber;this.yearOfStudy = yearOfStudy;
    }

    // Getter for studentNumber
    public String getStudentNumber() {
        return studentNumber;
    }

    // Getter for yearOfStudy
    public int getYearOfStudy() {
        return yearOfStudy;
    }
}


