package logicEngine;

import core.Course;
import core.Result;
import core.Student;
import storagelayer.ResultStore;

import java.util.List;

public class ResultEngine {

    private final ResultStore resultStore;

    public ResultEngine(ResultStore resultStore) {
        this.resultStore = resultStore;
    }

    private boolean isValidGrade(String gradeText) {
        if (gradeText == null) return false;
        gradeText = gradeText.trim();
        try {
            int value = Integer.parseInt(gradeText);
            return value >= 0 && value <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Finds an existing Result for the same student + course (if any)
    private Result findByStudentAndCourse(Student student, Course course) {
        List<Result> results = resultStore.getResults();
        for (Result r : results) {
            if (r == null || r.getStudent() == null || r.getCourse() == null) continue;

            boolean sameStudent = r.getStudent().getId().equals(student.getId());
            boolean sameCourse = r.getCourse().getCourseId().equals(course.getCourseId());

            if (sameStudent && sameCourse) {
                return r;
            }
        }
        return null;
    }

    // Creates a new ID like R1, R2, R3... based on what's already in the store
    private String generateNextResultId() {
        int max = 0;
        for (Result r : resultStore.getResults()) {
            if (r == null || r.getResultId() == null) continue;
            String id = r.getResultId().trim(); // e.g. "R12"
            if (id.startsWith("R")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return "R" + (max + 1);
    }

    /**
     * Adds a new result OR updates existing one (same student+course).
     * Returns the created/updated Result, or null if grade invalid.
     */
    public Result addOrUpdateResult(Student student, Course course, String gradeText) {

        if (student == null || course == null) return null;

        // 1) validate grade
        if (!isValidGrade(gradeText)) return null;

        int grade = Integer.parseInt(gradeText.trim());

        // 2) check if existing result for this student+course
        Result existing = findByStudentAndCourse(student, course);

        if (existing != null) {
            // update existing
            Result updated = new Result(existing.getResultId(), student, course, grade);

            // ✅ matches your ResultStore method name
            resultStore.updateResult(existing, updated);

            return updated;
        }

        // 3) otherwise add new
        String newId = generateNextResultId();
        Result newResult = new Result(newId, student, course, grade);

        // ✅ matches your ResultStore method name
        resultStore.addResult(newResult);

        return newResult;
    }

    // Optional helper if you need delete functionality from UI later:
    public void removeResult(Result result) {
        if (result == null) return;
        resultStore.removeResult(result);
    }
}









