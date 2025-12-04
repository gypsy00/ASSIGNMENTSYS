package logicEngine;

import core.Course;
import core.Enrollment;
import core.Student;
import storagelayer.EnrollmentStore;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentEngine {
    // field for store
    private EnrollmentStore enrollmentStore;

    public EnrollmentEngine() {
        this.enrollmentStore = new EnrollmentStore();
    }

    // method to check if student is already on course and enroll if not
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
