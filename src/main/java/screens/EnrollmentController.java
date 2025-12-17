package screens;

import core.Course;
import core.Enrollment;
import core.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import storagelayer.CourseBank;
import storagelayer.EnrollmentStore;
import storagelayer.StudentStore;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import java.io.IOException;

public class EnrollmentController {

    @FXML
    private TableView<Enrollment> enrollmentTable;

    @FXML
    private TableColumn<Enrollment, String> enrollmentIdCol;

    @FXML
    private TableColumn<Enrollment, String> studentIdCol;

    @FXML
    private TableColumn<Enrollment, String> studentNameCol;

    @FXML
    private TableColumn<Enrollment, String> courseNameCol;

    @FXML
    private TableColumn<Enrollment, String> courseIdCol;


    @FXML
    private TextField studentIdField;

    @FXML
    private TextField studentNameField;

    @FXML
    private TextField courseIdField;

    @FXML
    private TextField courseNameField;

    @FXML
    private TextField enrollmentIdField;

    @FXML
    private TextField searchField;


    private FilteredList<Enrollment> filteredData;

    private final ObservableList<Enrollment> masterData = FXCollections.observableArrayList();


    private final StudentStore studentStore = MainApp.getStudentStore();
    private final CourseBank courseBank = MainApp.getCoursebank();
    private final EnrollmentStore enrollmentStore = MainApp.getEnrollmentStore();



    @FXML
    public void initialize(){

        studentIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            Student student = studentStore.findById(newValue);
            if (student != null){
                studentNameField.setText(student.getName());
            }else {
                studentNameField.clear();
            }
        });

        courseIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            Course course = courseBank.findById(newValue);
            if (course != null){
                courseNameField.setText(course.getCourseName());
            }else  {
                courseNameField.clear();
            }
        });



        enrollmentIdCol.setCellValueFactory(new PropertyValueFactory<>("enrollmentId"));

        studentIdCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getStudent().getId())
                );

        studentNameCol.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getStudent().getStudentNumber()));


        courseIdCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCourse().getCourseId())
        );

        courseNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCourse().getCourseName()));



        filteredData = new FilteredList<>(masterData, p -> true);
        enrollmentTable.setItems(filteredData);

        loadEnrollments();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String q = newValue == null  ? "" :  newValue.toLowerCase().trim();

            filteredData.setPredicate(enrollment -> {
                if (q.isEmpty()) return true;
                String enrollmentId = safe(enrollment.getEnrollmentId());
                String studentId = safe(enrollment.getStudent().getId());
                String studentName = safe(enrollment.getStudent().getName());
                String courseId = safe(enrollment.getCourse().getCourseId());
                String courseName = safe(enrollment.getCourse().getCourseName());

                return enrollmentId.contains(q)
                        || studentId.contains(q)
                        || studentName.contains(q)
                        || courseId.contains(q)
                        || courseName.contains(q);


            });

        });



    }

    private String safe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private void loadEnrollments(){
        masterData.setAll(enrollmentStore.getEnrollments());
    }

    @FXML
    private void handleGoToStudents(ActionEvent event) {
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Students.fxml"));
            Scene scene = new Scene(loader.load());

            stage.setTitle("Students");
            stage.setScene(scene);
            stage.show();
        }catch (IOException e) {
            e.printStackTrace();
            showError("Could not open students page" + e.getMessage());
        }
    }

    @FXML
    private void onUpdateEnrollmentClicked(ActionEvent event) {
        Enrollment selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            showError("Select an Enrollment to update.");
            return;
        }

        String studentId = studentIdField.getText();
        String courseId = courseIdField.getText();

        if( studentId.isEmpty() || courseId.isEmpty()){
            showError("All fields are required.");
            return;
        }

        Student student = studentStore.findById(studentId);
        Course course = courseBank.findById(courseId);

        if (student == null || course == null) {
            showError("Inavalid student or course id.");
            return;
        }

        String enrollmentId = selected.getEnrollmentId();

        Enrollment updated = new Enrollment(enrollmentId, student, course);
        enrollmentStore.updateEnrollment(selected, updated);

        loadEnrollments();
        enrollmentTable.getSelectionModel().select(updated);
    }

    @FXML
    private void onDeleteEnrollmentClicked(ActionEvent event) {
        Enrollment selected = enrollmentTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            showError("Select an Enrollment to delete.");
            return;
        }
        enrollmentStore.removeEnrollment(selected);
        loadEnrollments();
        clearFields();
    }

    @FXML
    private void onAddEnrollmentClicked(){
        String studentId = studentIdField.getText();
        String courseId = courseIdField.getText();

        Student student = studentStore.findById(studentId);
        Course course = courseBank.findById(courseId);

        if (student == null || course == null){
            showError ("Invalid Student Id or Course ID");
            return;
        }


        String enrollmentId = generateEnrollmentId();

        Enrollment newEnrollment = new Enrollment(generateEnrollmentId(), student, course);
        enrollmentStore.addEnrollment(newEnrollment);
        masterData.add(newEnrollment);

        searchField.clear();
        enrollmentTable.refresh();
        clearFields();
    }

    private void clearFields(){
        enrollmentIdField.clear();
        studentIdField.clear();
        courseIdField.clear();
    }

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateEnrollmentId(){
        int max = 0;
        for (Enrollment e : enrollmentStore.getEnrollments()){
            String id = e.getEnrollmentId();

            if (id != null && id.startsWith("E")) {
                try{
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max) max = n;
                }catch (NumberFormatException ignored){}
            }
        }
        return "E" + (max + 1);
    }

}
