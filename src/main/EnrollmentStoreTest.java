package main;


import core.Student;
import core.Course;
import core.Enrollment;

import storagelayer.StudentStore;
import storagelayer.CourseBank;
import storagelayer.EnrollmentStore;

import java.util.ArrayList;
import java.util.List;


public class EnrollmentStoreTest {
    public static void main(String[] args) {
        StudentStore studentStore = new StudentStore();
        CourseBank courseStore = new CourseBank();
        EnrollmentStore enrollmentStore = new EnrollmentStore();

        List<Student> students = studentStore.loadStudents();
        List<Course> courses = courseStore.loadCourses();

        if (students.isEmpty() || courses.isEmpty()) {
            System.out.println("No students or courses found. Run the Student/Course tests first.");
            return;
        }

        List<Enrollment> enrollments = new ArrayList<>();

        enrollments.add(new Enrollment("E1", students.get(0), courses.get(0)));

        if (students.size() > 1 && courses.size() > 1) {
            enrollments.add(new Enrollment("E2", students.get(1), courses.get(1)));
        }

        enrollmentStore.saveEnrollments(enrollments);
        System.out.println("Enrollments saved.");

        List<Enrollment> loaded = enrollmentStore.loadEnrollments(students, courses);

        System.out.println("Enrollments loaded:");
        for (Enrollment e : loaded) {
            System.out.println(e);
        }
    }
}
