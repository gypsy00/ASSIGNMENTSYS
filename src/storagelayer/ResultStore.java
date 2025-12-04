package storagelayer;

//imports
import core.Course;
import core.Result;
import core.Student;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResultStore {

    //result store
    private final String FILE_NAME = "results.txt";

    //saving results to file
    public void saveResults(List<Result> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Result result : results) {
                writer.println(
                        result.getResultId() + "," +
                                result.getStudent().getId() + "," +
                                result.getCourse().getCourseId() + "," +
                                result.getGrade()
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving results: " + e.getMessage());
        }
    }


    //list of known students and courses to match ids correctly and load results
    public List<Result> loadResults(List<Student> students, List<Course> courses) {
        List<Result> results = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return results;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue; // skip malformed lines

                String resultId = parts[0];
                String studentId = parts[1];
                String courseId = parts[2];
                int grade;
                try {
                    grade = Integer.parseInt(parts[3]);
                } catch (NumberFormatException nfe) {
                    grade = 0;
                }

                Student student = findStudentById(students, studentId);
                Course course = findCourseById(courses, courseId);

                if (student != null && course != null) {
                    results.add(new Result(resultId, student, course, grade));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading results: " + e.getMessage());
        }

        return results;
    }


    //helper search methods
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
