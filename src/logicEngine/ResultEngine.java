package logicEngine;

import core.Course;
import core.Result;
import core.Student;
import storagelayer.ResultStore;

import java.util.List;

public class ResultEngine {
    // fields
    private ResultStore resultStore;

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

    // method to add or update a result; gradeText should be an integer string (0-100)
    public Result addOrUpdateResult(
            Student student,
            Course course,
            String gradeText,
            List<Result> currentResults) {

        // 1. validate grade
        if (!isValidGrade(gradeText)) {
            return null; // invalid grade
        }

        int grade = Integer.parseInt(gradeText.trim());

        // 2. check if result already exists for this student+course
        for (int i = 0; i < currentResults.size(); i++) {
            Result r = currentResults.get(i);
            if (r.getStudent().getId().equals(student.getId())
                    && r.getCourse().getCourseId().equals(course.getCourseId())) {

                // replace existing Result with updated grade
                Result updated = new Result(r.getResultId(), student, course, grade);
                currentResults.set(i, updated);

                resultStore.saveResults(currentResults);
                return updated;
            }
        }

        // 3. create new Result if none exists
        String newId = "R" + (currentResults.size() + 1);
        Result newResult = new Result(newId, student, course, grade);
        currentResults.add(newResult);

        resultStore.saveResults(currentResults);

        return newResult;
    }
}









