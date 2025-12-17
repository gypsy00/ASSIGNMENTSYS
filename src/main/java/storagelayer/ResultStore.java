package storagelayer;

import core.Course;
import core.Result;
import core.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ResultStore {

    private static final Path FILE_PATH = Paths.get("results.txt");

    private final StudentStore studentStore;
    private final CourseBank courseBank;

    private final List<Result> results = new ArrayList<>();

    public ResultStore(StudentStore studentStore, CourseBank courseBank) {
        this.studentStore = studentStore;
        this.courseBank = courseBank;
        loadFromFile();
    }

    public List<Result> getResults() {
        return new ArrayList<>(results);
    }

    public void addResult(Result result) {
        results.add(result);
        saveToFile();
    }

    public void updateResult(Result oldResult, Result newResult) {
        int index = results.indexOf(oldResult);
        if (index >= 0) {
            results.set(index, newResult);
            saveToFile();
        }
    }

    public void removeResult(Result result) {
        results.remove(result);
        saveToFile();
    }

    public Result findById(String resultId) {
        for (Result r : results) {
            if (r.getResultId() != null && r.getResultId().equals(resultId)) return r;
        }
        return null;
    }

    private void loadFromFile() {
        results.clear();
        if (!Files.exists(FILE_PATH)) return;

        try (BufferedReader reader = Files.newBufferedReader(FILE_PATH)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // resultId,studentId,courseId,grade
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String resultId  = parts[0].trim();
                String studentId = parts[1].trim();
                String courseId  = parts[2].trim();
                String gradeStr  = parts[3].trim();

                int grade;
                try {
                    grade = Integer.parseInt(gradeStr);
                } catch (NumberFormatException ex) {
                    continue;
                }

                Student student = studentStore.findById(studentId);
                Course course = courseBank.findById(courseId);

                if (student == null || course == null) continue;

                results.add(new Result(resultId, student, course, grade));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(FILE_PATH)) {
            for (Result r : results) {
                if (r == null || r.getStudent() == null || r.getCourse() == null) continue;

                writer.write(
                        r.getResultId() + "," +
                                r.getStudent().getId() + "," +
                                r.getCourse().getCourseId() + "," +
                                r.getGrade()
                );
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
