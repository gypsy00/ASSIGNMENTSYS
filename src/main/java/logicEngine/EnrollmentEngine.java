package logicEngine;

import core.Course;
import core.Enrollment;
import core.Student;
import storagelayer.EnrollmentStore;
import storagelayer.StudentStore;
import storagelayer.CourseBank;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import java.util.List;

public class EnrollmentEngine {

    private final EnrollmentStore enrollmentStore;
    private static final int MAX_CREDITS_PER_STUDENT = 70;

    public EnrollmentEngine(StudentStore studentStore, CourseBank courseBank) {
        this.enrollmentStore = new EnrollmentStore(studentStore, courseBank);
    }

    public Enrollment enrollStudent(Student student, Course course, List<Enrollment> currentEnrollments) {
        if (student == null || course == null) return null;

        //Duplicate check
        for (Enrollment enrollment : currentEnrollments) {
            if (enrollment.getStudent().getId().equals(student.getId()) &&
                    enrollment.getCourse().getCourseId().equals(course.getCourseId())) {
                return null;
            }
        }

        //credit limit check
        int currentCredits = calculateTotalCreditsForStudent(student, currentEnrollments);
        if (currentCredits + course.getCredits() > MAX_CREDITS_PER_STUDENT) {
            return null;
        }

        //schedule check
        if (hasScheduleClash(student, course, currentEnrollments)) {
            return null;
        }

        String enrollmentId = "E" + (currentEnrollments.size() + 1);
        Enrollment newEnrollment = new Enrollment(enrollmentId, student, course);
        enrollmentStore.addEnrollment(newEnrollment);
        return newEnrollment;
    }

    //sum of credits for all courses student is enrolled to
    private int calculateTotalCreditsForStudent(Student student, List<Enrollment> currentEnrollments) {
        int total = 0;
        for (Enrollment e : currentEnrollments) {
            if (e.getStudent().getId().equals(student.getId())) {
                total += e.getCourse().getCredits();
            }
        }
        return total;
    }

    private boolean hasScheduleClash(Student student, Course newCourse, List<Enrollment> currentEnrollments) {
        for (Enrollment e : currentEnrollments) {
            if (!e.getStudent().getId().equals(student.getId())) {
                continue;
            }

            Course existing = e.getCourse();

            if (existing.getDay().equalsIgnoreCase(newCourse.getDay()) && timeOverlaps(existing, newCourse)) {
                return true;
            }
        }
        return false;
    }

    private boolean timeOverlaps(Course a, Course b) {
        TimeRange ra = parseRange(a.getTime());
        TimeRange rb = parseRange(b.getTime());
        if (ra == null || rb == null) return false;
        return ra.start.isBefore(rb.end) && rb.start.isBefore(ra.end);
    }

    private TimeRange parseRange(String timeStr) {
        if (timeStr == null) return null;

        String cleaned = timeStr.replace("", "");
        String[] parts = cleaned.split("-");
        if (parts.length != 2) return null;

        LocalTime start = parseTime(parts[0]);
        LocalTime end = parseTime(parts[1]);
        if (start == null || end == null) return null;

        return new TimeRange(start, end);
    }

    private LocalTime parseTime(String s) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm");
            return LocalTime.parse(s, fmt);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static class TimeRange {
        final LocalTime start;
        final LocalTime end;

        TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }
}



