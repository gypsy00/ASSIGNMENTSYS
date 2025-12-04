package storagelayer;

import core.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentStore {

    // field for students store
    private final String FILE_NAME = "students.txt";

    // Method to save students list to file
    public void saveStudents(List<Student> students) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Student student : students) {
                writer.println(
                        student.getId() + "," +
                                student.getName() + "," +
                                student.getStudentNumber() + "," +
                                student.getEmail() + "," +
                                student.getYearOfStudy()
                );
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    // Method to load students from file
    public List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return list; // return empty list if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue; // skip malformed lines

                String id = parts[0];
                String name = parts[1];
                String studentNumber = parts[2];
                String email = parts[3];
                int yearOfStudy;
                try {
                    yearOfStudy = Integer.parseInt(parts[4]);
                } catch (NumberFormatException nfe) {
                    yearOfStudy = 0;
                }

                Student student = new Student(id, name, studentNumber, email, yearOfStudy);
                list.add(student);
            }
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }

        return list;
    }
}
