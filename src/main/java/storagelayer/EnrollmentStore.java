package storagelayer;

import core.Course;
import core.Enrollment;
import core.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentStore {

    // Single file location (ONE source of truth)
    private static final Path FILE_PATH = Paths.get("enrollments.txt");

    private final StudentStore studentStore;
    private final CourseBank courseBank;

    // In-memory enrollments list
    private final List<Enrollment> enrollments = new ArrayList<>();

    public EnrollmentStore(StudentStore studentStore, CourseBank courseBank) {
        this.studentStore = studentStore;
        this.courseBank = courseBank;
        loadFromFile();
    }

    /** Returns a copy so callers can't mutate internal list directly */
    public List<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
        saveToFile();
    }

    public Enrollment findById(String enrollmentId) {
        for (Enrollment e : enrollments) {
            if (e.getEnrollmentId() != null && e.getEnrollmentId().equals(enrollmentId)) {
                return e;
            }
        }
        return null;
    }

    public void updateEnrollment(Enrollment oldEnrollment, Enrollment newEnrollment) {
        int index = enrollments.indexOf(oldEnrollment);
        if (index >= 0) {
            enrollments.set(index, newEnrollment);
            saveToFile();
        }
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollments.remove(enrollment);
        saveToFile();
    }

    /** Load enrollments from file into memory */
    private void loadFromFile() {
        enrollments.clear();

        if (!Files.exists(FILE_PATH)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Expected format: enrollmentId,studentId,courseId
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                String enrollmentId = parts[0].trim();
                String studentId = parts[1].trim();
                String courseId = parts[2].trim();

                Student student = studentStore.findById(studentId);
                Course course = courseBank.findById(courseId);

                // ✅ Correct logic: skip if either one is missing
                if (student == null || course == null) {
                    continue;
                }

                enrollments.add(new Enrollment(enrollmentId, student, course));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Save ALL in-memory enrollments to file (prevents "file cleared" bug) */
    private void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            for (Enrollment e : enrollments) {
                // Defensive: skip broken objects
                if (e == null || e.getStudent() == null || e.getCourse() == null) continue;

                writer.write(
                        e.getEnrollmentId() + "," +
                                e.getStudent().getId() + "," +
                                e.getCourse().getCourseId()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
