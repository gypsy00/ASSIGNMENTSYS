package storagelayer;

//imports
import core.Course;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CourseBank {

    // course store
    private static final String FILE_NAME = "courses.txt";

    private List<Course> courses = new ArrayList<>();

    public CourseBank() {
        loadFromFile();
    }

    public List<Course> getAllCourses() {
        return courses;
    }

    public List<Course> loadCourses() {
        loadFromFile();
        return new ArrayList<>(courses);
    }

    public void addCourse(Course course) {
        courses.add(course);
        saveToFile();
    }

    public void saveCourse(List<Course> newCourses) {
        courses.clear();
        courses.addAll(newCourses);
        saveToFile();
    }

    public void removeCourse(Course course) {
        courses.remove(course);
        saveToFile();
    }

    public void updateCourse(Course oldCourse, Course updatedCourse) {
        int index = courses.indexOf(oldCourse);
        if (index >=0) {
            courses. set(index, updatedCourse);
            saveToFile();
        }
    }

    //loading courses from file
    public void loadFromFile() {
        courses.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6) continue;

                String id = parts[0];
                String name = parts[1];
                int credits = Integer.parseInt(parts[2]);
                String day = parts[3];
                String time = parts[4];
                String room = parts[5];

                courses.add(new Course(id, name, credits, day, time, room));
            }
        } catch (IOException e) {
            System.err.println("Could not load courses:" + e.getMessage());
        }
    }

    //saving courses to file
    private void saveToFile() {
        File file = new File(FILE_NAME);
        System.out.println("Saving courses to " + file.getAbsolutePath());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Course c : courses) {
                String line = String.join(",",
                        c.getCourseId(),
                        c.getCourseName(),
                        String.valueOf(c.getCredits()),
                        c.getDay(),
                        c.getTime(),
                        c.getRoom()
                );
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Could not save courses:" + e.getMessage());
        }
    }
}
