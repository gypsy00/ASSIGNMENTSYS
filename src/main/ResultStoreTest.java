package main;

import core.Course;
import core.Result;
import core.Student;
import storagelayer.CourseBank;
import storagelayer.ResultStore;
import storagelayer.StudentStore;

import java.util.ArrayList;
import java.util.List;

public class ResultStoreTest {
    public static void main(String[] args) {
        StudentStore studentStore = new StudentStore();
        CourseBank courseBank = new CourseBank();
        ResultStore resultStore = new ResultStore();

        List<Student> students = studentStore.getStudents();
        List<Course> courses = courseBank.loadCourses();

        if (students.isEmpty() || courses.isEmpty()) {
            System.out.println("Missing students or courses. Run those tests first.");
            return;
        }

        List<Result> results = new ArrayList<>();

        results.add(new Result("R1", students.get(0), courses.get(0), 85));

        if (students.size() > 1 && courses.size() > 1) {
            results.add(new Result("R2", students.get(1), courses.get(1), 72));
        }

        resultStore.saveResults(results);
        System.out.println("Results saved.");

        List<Result> loaded = resultStore.loadResults(students, courses);

        System.out.println("Results loaded:");
        for (Result r : loaded) {
            System.out.println(r);
        }
    }
}