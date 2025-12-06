package storagelayer;

//imports
import core.Course;

import java.io.BufferedReader
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CourseBank {

    // course store
    private final String FILE_NAME = "courses.txt";

    // saving courses to file
    public void saveCourse(List<Course> courses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Course course : courses) {
                writer.println(
                        course.getCourseId() + "," +
                                course.getCourseName() + "," +
                                course.getCredits() + "," +
                                course.getDay() + "," +
                                course.getTime() + "," +
                                course.getRoom()
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving courses: " + e.getMessage());
        }
    }

    // load courses from file
    public List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return courses; // return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue; // skip malformed lines

                String courseId = parts[0];
                String courseName = parts[1];
                int credits;
                try {
                    credits = Integer.parseInt(parts[2]);
                } catch (NumberFormatException nfe) {
                    credits = 0;
                }
                String day = parts[3];
                String time = parts[4];
                String room = parts[5];

                Course course = new Course(courseId, courseName, credits, day, time, room);
                courses.add(course);
            }
        } catch (IOException e) {
            System.out.println("Error loading courses: " + e.getMessage());
        }

        return courses;
    }
}
