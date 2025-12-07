package main;



import core.Student;
import core.Course;
import core.Enrollment;

import storagelayer.StudentStore;
import storagelayer.CourseBank;
import storagelayer.EnrollmentStore;

import logicEngine.EnrollmentEngine;

import java.util.List;


public class EnrollmentEngineTest {
    public static void main(String[] args) {
        StudentStore studentStore = new StudentStore();
        CourseBank courseStore = new CourseBank();
        EnrollmentStore enrollmentStore = new EnrollmentStore();
        EnrollmentEngine engine = new EnrollmentEngine();

        List<Student> students = studentStore.getStudents();
        List<Course> courses = courseStore.loadCourses();
        List<Enrollment> enrollments = enrollmentStore.loadEnrollments(students, courses);

        if (students.isEmpty() || courses.isEmpty()) {
            System.out.println("No students or courses found. Run those tests first.");
            return;
        }

        Student s = students.get(0);
        Course c = courses.get(0);

        System.out.println("Trying to enroll " + s.getName() + " on " + c.getCourseName());
        Enrollment result1 = engine.enrollStudent(s, c, enrollments);

        if (result1 == null) {
            System.out.println("Already enrolled (first attempt).");
        } else {
            System.out.println("Enrollment successful: " + result1);
        }

        System.out.println("Trying to enroll the SAME student on the SAME course again...");
        Enrollment result2 = engine.enrollStudent(s, c, enrollments);

        if (result2 == null) {
            System.out.println("Duplicate prevented correctly ✅");
        } else {
            System.out.println("ERROR: duplicate enrollment allowed ❌");
        }
    }

}


