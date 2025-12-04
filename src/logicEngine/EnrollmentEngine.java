package logicEngine;

import core.Course;
import core.Enrollment;
import core.Student;
import storagelayer.EnrollmentStore;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentEngine {

    //timetable checker
    private boolean hasScheduleClash(Student student, Course newCourse, List<Enrollment> currentEnrollments) {
        for (Enrollment e : currentEnrollments) {
            if (!e.getStudent().getId().equals(student. getId())){
                continue;
            }

            Course existing = e.getCourse();

            if (existing.getDay(). equalsIgnoreCase(newCourse.getDay()) && existing.getTime().equals(newCourse.getTime())){
                return true;
            }
        }
        return false;
    }

    //sum of credits for all courses student is enrolled to
    private int calculateTotalCreditsForStudent(Student student, List<Enrollment> currentEnrollments) {
        int total = 0;
        for (Enrollment e : currentEnrollments) {
            if (e.getStudent(). getId().equals(student.getId())) {
                total+= e.getCourse().getCredits();
            }
        }
        return total;
    }
    // field for store
    private EnrollmentStore enrollmentStore;

    //credit limit
    private static final int MAX_CREDITS_PER_STUDENT = 70;


    public EnrollmentEngine() {
        this.enrollmentStore = new EnrollmentStore();
    }

    // method to check if student is already on course and enroll if not & extended to check credit and schedule clashes
    public Enrollment enrollStudent(
            Student student,
            Course course,
            List<Enrollment> currentEnrollments) {

        // check for duplicates
        for (Enrollment e : currentEnrollments) {
            if (e.getStudent().getId().equals(student.getId()) &&
                    e.getCourse().getCourseId().equals(course.getCourseId())) {
                // if student is already enrolled return null
                return null;
            }
        }

        //credit limit check
        int currentCredits = calculateTotalCreditsForStudent(student, currentEnrollments);
        if (currentCredits + course.getCredits() >  MAX_CREDITS_PER_STUDENT) {
            return null;
        }

        //schedule check
        if (hasScheduleClash(student, course, currentEnrollments)) {
            return null;
        }

        // create new enrollment id
        String newId = "E" + (currentEnrollments.size() + 1);
        Enrollment newEnrollment = new Enrollment(newId, student, course);

        // add to list
        currentEnrollments.add(newEnrollment);

        // save to file
        enrollmentStore.saveEnrollments(currentEnrollments);

        // return new enrollment
        return newEnrollment;
    }
}
