package storagelayer;

import core.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentStore {

    // field for students store
    private static final String FILE_NAME = "students.txt";

    private final List<Student> students = new ArrayList<>();

    public StudentStore() {
        loadFromFile();
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public void addStudent(Student student) {
        students.add(student);
        saveToFile();
    }

    public void updateStudent(Student oldStudent, Student updatedStudent) {
        int index = students.indexOf(oldStudent);
        if (index >= 0) {
            students.set(index, updatedStudent);
            saveToFile();
        }
    }

    public void removeStudent(Student student) {
        students.remove(student);
        saveToFile();
    }

    public Student findById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

    // Method to save students list to file

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.println(
                        student.getId() + "," +
                                student.getName() + "," +
                                student.getEmail() + "," +
                                student.getStudentNumber() + "," +
                                student.getYearOfStudy()
                );
            }
        } catch (IOException e) {
            System.out.print("Error saving students " + e.getMessage());
        }
    }

    private void loadFromFile() {
        students.clear();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No students.txt file yet, starting empty ");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length != 5) continue;

                String id = parts[0];
                String name = parts[1];
                String email = parts[2];
                String studentNumber = parts[3];
                int yearOfStudy;
                try {
                    yearOfStudy = Integer.parseInt(parts[4]);
                } catch (NumberFormatException nfe) {
                    yearOfStudy = 0;
                }

                Student s = new Student(id, name, email, studentNumber, yearOfStudy);
                students.add(s);
            }
        } catch (IOException e) {
            System.out.print("Error loading students " + e.getMessage());
        }
    }
}









