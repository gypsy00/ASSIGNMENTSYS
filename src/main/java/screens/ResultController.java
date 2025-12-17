package screens;

import core.Course;
import core.Result;
import core.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import storagelayer.CourseBank;
import storagelayer.ResultStore;
import storagelayer.StudentStore;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultController implements Initializable {

    // ===== Table =====
    @FXML private TableView<Result> courseTable;
    @FXML private TableColumn<Result, String> studentIdCol;
    @FXML private TableColumn<Result, String> studentNameCol;
    @FXML private TableColumn<Result, String> courseIdCol;
    @FXML private TableColumn<Result, String> courseNameCol;
    @FXML private TableColumn<Result, String> gradeCol;

    // ===== Inputs =====
    @FXML private TextField searchField;
    @FXML private TextField studentIdField;
    @FXML private TextField studentNameField;
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField gradeField;

    // ===== Stores =====
    private final StudentStore studentStore = new StudentStore();
    private final CourseBank courseBank = new CourseBank();
    private final ResultStore resultStore = new ResultStore(studentStore, courseBank); // ✅ FIX

    // ===== Data =====
    private final ObservableList<Result> masterData = FXCollections.observableArrayList();
    private FilteredList<Result> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupRowSelection();
        loadResults();
        setupSearch();
        setupStudentAutoFill();
    }

    private void setupTableColumns() {
        studentIdCol.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getStudent().getId()))
        );

        studentNameCol.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getStudent().getName()))
        );

        courseIdCol.setCellValueFactory(data ->
                new SimpleStringProperty(safe(data.getValue().getCourse().getCourseId()))
        );

        courseNameCol.setCellValueFactory(cellData -> {
            Result r = cellData.getValue();
            String name = "";

            if (r != null && r.getCourse() != null) {
                name = r.getCourse().getCourseName();
            }
            return new ReadOnlyStringWrapper(name);
        });


        gradeCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getGrade()))
        );

        courseIdField.textProperty().addListener((observable, oldText, newText) -> {
            loadCourseName(newText);
        });
    }

    private void setupRowSelection() {
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel == null) return;

            studentIdField.setText(sel.getStudent().getId());
            studentNameField.setText(sel.getStudent().getName());
            courseIdField.setText(sel.getCourse().getCourseId());
            gradeField.setText(String.valueOf(sel.getGrade()));
        });
    }

    private void setupStudentAutoFill() {
        studentIdField.textProperty().addListener((obs, oldV, newV) -> {
            Student s = studentStore.findById(newV);
            if (s != null) studentNameField.setText(s.getName());
            else studentNameField.clear();
        });
    }

    private void loadResults() {
        masterData.setAll(resultStore.getResults());
        filteredData = new FilteredList<>(masterData, r -> true);
        courseTable.setItems(filteredData);
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String q = (newVal == null) ? "" : newVal.trim().toLowerCase();

            filteredData.setPredicate(result -> {
                if (q.isEmpty()) return true;

                String sid = safe(result.getStudent().getId()).toLowerCase();
                String sname = safe(result.getStudent().getName()).toLowerCase();
                String cid = safe(result.getCourse().getCourseId()).toLowerCase();
                String grade = String.valueOf(result.getGrade()).toLowerCase();

                return sid.contains(q) || sname.contains(q) || cid.contains(q) || grade.contains(q);
            });
        });
    }

    // ===== Button Actions =====

    @FXML
    private void onAddGradeClicked() {
        String studentId = studentIdField.getText().trim();
        String courseId = courseIdField.getText().trim();

        Student student = studentStore.findById(studentId);
        Course course = courseBank.findById(courseId);

        if (student == null || course == null) {
            showError("Invalid Student ID or Course ID.");
            return;
        }

        int grade;
        try {
            grade = Integer.parseInt(gradeField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Grade must be a number.");
            return;
        }

        String resultId = generateResultId();
        Result newResult = new Result(resultId, student, course, grade);

        resultStore.addResult(newResult);
        masterData.add(newResult);

        clearFields();
    }

    @FXML
    private void onUpdateGradeClicked() {
        Result selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a result to update.");
            return;
        }

        Student student = studentStore.findById(studentIdField.getText().trim());
        Course course = courseBank.findById(courseIdField.getText().trim());

        if (student == null || course == null) {
            showError("Invalid Student ID or Course ID.");
            return;
        }

        int grade;
        try {
            grade = Integer.parseInt(gradeField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Grade must be a number.");
            return;
        }

        Result updated = new Result(selected.getResultId(), student, course, grade);

        resultStore.updateResult(selected, updated);

        int index = masterData.indexOf(selected);
        if (index >= 0) masterData.set(index, updated);

        courseTable.refresh();
        clearFields();
    }

    @FXML
    private void onDeleteGradeClicked() {
        Result selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a result to delete.");
            return;
        }

        resultStore.removeResult(selected);
        masterData.remove(selected);

        clearFields();
    }

    private void loadCourseName(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            courseNameField.clear();
            return;
        }

        Course course = MainApp.getCoursebank().findById(courseId.trim());

        if (course != null) {
            courseNameField.setText(course.getCourseName());
        }else{
            courseNameField.clear();
        }
    }

    // ===== Helpers =====

    private void clearFields() {
        studentIdField.clear();
        studentNameField.clear();
        courseIdField.clear();
        gradeField.clear();
        courseTable.getSelectionModel().clearSelection();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String generateResultId() {
        int max = 0;
        for (Result r : resultStore.getResults()) {
            String id = r.getResultId();
            if (id != null && id.startsWith("R")) {
                try {
                    int n = Integer.parseInt(id.substring(1));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return "R" + (max + 1);
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    // ===== Navigation (keep your existing ones if already working) =====
    @FXML
    private void handleGoToEnrollments(ActionEvent event) {
        MainApp.switchScene(event, "EnrollmentView.fxml");
    }

    @FXML
    private void handleGoToStudents(ActionEvent event) {
        MainApp.switchScene(event, "Students.fxml");
    }
}
