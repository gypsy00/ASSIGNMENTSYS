package screens;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import storagelayer.StudentStore;   // adjust this if your package/name is different
import core.Student;               // adjust to your real Student class

import java.io.IOException;

public class StudentsController {

    // ----- Table -----
    @FXML
    private TableView<Student> studentTable;


    @FXML
    private TableColumn<Student, String> idCol;

    @FXML
    private TableColumn<Student, String> nameCol;

    @FXML
    private TableColumn<Student, String> emailCol;

    @FXML
    private TableColumn<Student, String> studentNumberCol;

    @FXML
    private TableColumn<Student, Integer> yearCol;

    // ----- Form fields -----
    @FXML
    private TextField studentIdField;

    @FXML
    private TextField studentNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField studentNumberField;

    @FXML
    private TextField yearField;


    // data storage
    private final ObservableList<Student> masterData = FXCollections.observableArrayList();
    private StudentStore studentStore = new StudentStore();



    public void initialize() {


        // link columns to Student getters (adjust names to match your Student class)
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentNumberCol.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("yearOfStudy"));

        loadStudentsFromFile();


        // when a row is selected, show it in the text fields
        // when a row is selected, show it in the text fields
        studentTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    if (newSel != null) {
                        studentIdField.setText(newSel.getId());
                        studentNameField.setText(newSel.getName());
                        // using majorField to show student number
                        studentNumberField.setText(newSel.getStudentNumber());
                        emailField.setText(newSel.getEmail());
                        yearField.setText(String.valueOf(newSel.getYearOfStudy()));
                    }
                });
    }

    private void loadStudentsFromFile() {
        masterData.clear();
        masterData.addAll(studentStore.getStudents()); // adjust to your method name
        studentTable.setItems(masterData);
    }

    // ----- Buttons -----

    @FXML
    private void onAddStudentClicked(ActionEvent event) {
        String id = studentIdField.getText().trim();
        String name = studentNameField.getText().trim();
        String email = emailField.getText().trim();
        String studentNumber = studentNumberField.getText().trim();
        String yearText = yearField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || studentNumber.isEmpty() ||yearText.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            showError("Year must be a number.");
            return;
        }

        Student s = new Student(id, name, email,  studentNumber, year); // adjust ctor if needed

        try {
            studentStore.addStudent(s);     // adjust method names to your StudentStore
            loadStudentsFromFile();
            clearFields();
        } catch (Exception e) {
            showError("Could not save students: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateStudentClicked(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a student to update.");
            return;
        }

        if (!confirm("Update Student", "Are you sure you want to update this student?")) {
            return;
        }

        String id = studentIdField.getText().trim();
        String name = studentNameField.getText().trim();
        String email = emailField.getText().trim();
        String studentNumber = studentNumberField.getText().trim();
        String yearText = yearField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || studentNumber.isEmpty() ||yearText.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            showError("Year must be a number.");
            return;
        }

        Student updated = new Student(id, name, email, studentNumber, year); // adjust ctor

        try {
            studentStore.updateStudent(selected, updated);   // adjust method names
            loadStudentsFromFile();
            studentTable.getSelectionModel().select(updated);
        } catch (Exception e) {
            showError("Could not update students: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteStudentClicked(ActionEvent event) {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a student to delete.");
            return;
        }

        if (!confirm("Delete Student", "Are you sure you want to delete this student?")) {
            return;
        }

        try {
            studentStore.removeStudent(selected);  // adjust method names
            loadStudentsFromFile();
            clearFields();
        } catch (Exception e) {
            showError("Could not delete student: " + e.getMessage());
        }
    }

    //age navigation
    @FXML
    private void handleGoToCourses(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CoursesView.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----- Helpers -----

    private void clearFields() {
        studentIdField.clear();
        studentNameField.clear();
        emailField.clear();
        studentNumberField.clear();
        yearField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType ok = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(ok, cancel);

        return alert.showAndWait().orElse(cancel) == ok;
    }
}