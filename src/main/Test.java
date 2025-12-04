package main;

import core.Course;
import storagelayer.CourseBank;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        CourseBank store = new CourseBank();

        List<Course> courses = new ArrayList<>();

        courses.add(new Course("CS101", "Programming 1", 30,
                "Monday", "10-12", "Room A"));

        courses.add(new Course("CS102", "Databases", 30,
                "Tuesday", "14-16", "Room B"));

        courses.add(new Course("CS103", "Web Dev", 15,
                "Friday", "09-11", "Room C"));

        store.saveCourse(courses);
        System.out.println("Courses Saved");

        List<Course> loaded = store.loadCourses();
        System.out.println("Courses Loaded");
        for (Course course : loaded) {
            System.out.println(course);
        }
    }
}
