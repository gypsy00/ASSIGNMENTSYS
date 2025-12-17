package core;

public class Course {

    //Fields
    private String courseId;
    private String courseName;
    private int credits;
    private String day;
    private String time;
    private String room;


    //Constructors
    public Course(String courseId, String courseName, int credits,
                  String day, String time, String room) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.day = day;
        this.time = time;
        this.room = room;
    }

    //getters
    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getRoom() {
        return room;
    }
    @Override
    public String toString() {
        return courseId + " - " + courseName;
    }
}




