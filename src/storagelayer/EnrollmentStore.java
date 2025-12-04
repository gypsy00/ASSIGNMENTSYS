package storagelayer;

import core.Course;
import core.Enrollment;
import core.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentStore {

    // enrollment store
    private final String FILE_NAME = "enrollments.txt";

    // method to write enrollments to store
    public void saveEnrollments(List<Enrollment> enrollments) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Enrollment enrollment : enrollments) {
                writer.println(
                        enrollment.getEnrollmentId() + "," +
                                enrollment.getStudent().getId() + "," +
                                enrollment.getCourse().getCourseId()
                );
            }
        } catch (IOException ex) {
            System.out.println("Error saving enrollments: " + ex.getMessage());
        }
    }

    // method to load enrollments
    public List<Enrollment> loadEnrollments(List<Student> students, List<Course> courses) {
        List<Enrollment> list = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return list; // return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 3) continue; // skip malformed lines

                String enrollmentId = parts[0];
                String studentId = parts[1];
                String courseId = parts[2];

                Student student = findStudentById(students, studentId);
                Course course = findCourseById(courses, courseId);

                if (student != null && course != null) {
                    list.add(new Enrollment(enrollmentId, student, course));
                }
            }
        } catch (IOException ex) {
            System.out.println("Error loading enrollments: " + ex.getMessage());
        }

        return list;
    }

    // Helper methods
    private Student findStudentById(List<Student> students, String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    private Course findCourseById(List<Course> courses, String id) {
        for (Course c : courses) {
            if (c.getCourseId().equals(id)) {
                return c;
            }
        }
        return null;
    }
}
