package screens;

import core.Course;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
        import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import storagelayer.CourseBank;

import java.util.Optional;

public class CoursesController {

    // ===== Table =====
    @FXML private TableView<Course> courseTable;

    @FXML private TableColumn<Course, String> idCol;
    @FXML private TableColumn<Course, String> nameCol;
    @FXML private TableColumn<Course, Integer> creditsCol;
    @FXML private TableColumn<Course, String> dayCol;
    @FXML private TableColumn<Course, String> timeCol;
    @FXML private TableColumn<Course, String> roomCol;

    // ===== Form fields =====
    @FXML private TextField courseIdField;
    @FXML private TextField courseNameField;
    @FXML private TextField creditsField;
    @FXML private TextField dayField;
    @FXML private TextField timeField;
    @FXML private TextField roomField;

    // ===== Search =====
    @FXML private TextField searchField;

    // ===== Data =====
    private final CourseBank courseBank = new CourseBank();

    private final ObservableList<Course> masterData = FXCollections.observableArrayList();
    private FilteredList<Course> filteredData;

    @FXML
    public void initialize() {

        // 1) Column bindings (must match Course getter names)
        idCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        // 2) Load once from file into CourseBank internal list, then into masterData
        courseBank.loadFromFile();
        masterData.setAll(courseBank.getAllCourses());

        // 3) Wrap with FilteredList + SortedList
        filteredData = new FilteredList<>(masterData, c -> true);

        SortedList<Course> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(courseTable.comparatorProperty());

        courseTable.setItems(sortedData);

        // 4) Live search
        searchField.textProperty().addListener(this::onSearchChanged);

        // 5) When selecting a row, show values in the form
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
            }
        });
    }

    // ======================
    // ADD COURSE
    // ======================
    @FXML
    private void onAddCourseClicked(ActionEvent event) {

        String id = courseIdField.getText().trim();
        String name = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        String day = dayField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || creditsText.isEmpty() || day.isEmpty() || time.isEmpty() || room.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
        } catch (NumberFormatException e) {
            showError("Credits must be a number.");
            return;
        }

        // prevent duplicate courseId
        if (courseBank.findById(id) != null) {
            showError("Course ID already exists: " + id);
            return;
        }

        Course newCourse = new Course(id, name, credits, day, time, room);

        try {
            // 1) Save into CourseBank (which saves to file internally)
            courseBank.addCourse(newCourse);

            // 2) Update UI list (DO NOT reload file here)
            masterData.setAll(courseBank.getAllCourses());

            clearForm();
            courseTable.getSelectionModel().clearSelection();

        } catch (Exception e) {
            showError("Could not save course: " + e.getMessage());
        }
    }

    // ======================
    // DELETE COURSE
    // ======================
    @FXML
    private void onDeleteCourseClicked(ActionEvent event) {

        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a course to delete.");
            return;
        }

        boolean ok = confirm("Delete Course", "Delete course " + selected.getCourseId() + "?");
        if (!ok) return;

        try {
            courseBank.removeCourse(selected);
            masterData.setAll(courseBank.getAllCourses());
            clearForm();
            courseTable.getSelectionModel().clearSelection();
        } catch (Exception e) {
            showError("Could not delete course: " + e.getMessage());
        }
    }

    // ======================
    // UPDATE COURSE
    // ======================
    @FXML
    private void onUpdateCourseClicked(ActionEvent event) {

        Course selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a course to update.");
            return;
        }

        String id = courseIdField.getText().trim();
        String name = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        String day = dayField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || creditsText.isEmpty() || day.isEmpty() || time.isEmpty() || room.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsText);
        } catch (NumberFormatException e) {
            showError("Credits must be a number.");
            return;
        }

        // If ID changed, ensure not colliding
        Course existing = courseBank.findById(id);
        if (existing != null && existing != selected) {
            showError("Another course already uses ID: " + id);
            return;
        }

        boolean ok = confirm("Update Course", "Update course " + selected.getCourseId() + "?");
        if (!ok) return;

        Course updated = new Course(id, name, credits, day, time, room);

        try {
            courseBank.updateCourse(selected, updated);
            masterData.setAll(courseBank.getAllCourses());

            // reselect updated item
            courseTable.getSelectionModel().select(updated);

        } catch (Exception e) {
            showError("Could not update course: " + e.getMessage());
        }
    }

    // ======================
    // NAVIGATION
    // ======================
    @FXML
    private void handleGoToEnrollments(ActionEvent event) {
        goTo(event, "/views/EnrollmentView.fxml");
    }

    @FXML
    private void handleGoToStudents(ActionEvent event) {
        goTo(event, "/views/Students.fxml");
    }

    @FXML
    private void handleGoToResults(ActionEvent event) {
        MainApp.switchScene(event, "ResultsView.fxml");
    }

    private void goTo(ActionEvent event, String fxmlPath) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not open page: " + e.getMessage());
        }
    }

    // ======================
    // HELPERS
    // ======================
    private void onSearchChanged(ObservableValue<? extends String> obs, String oldVal, String newVal) {
        String filter = (newVal == null) ? "" : newVal.trim().toLowerCase();

        if (filter.isEmpty()) {
            filteredData.setPredicate(c -> true);
            return;
        }

        filteredData.setPredicate(c -> {
            if (c == null) return false;
            return c.getCourseId().toLowerCase().contains(filter)
                    || c.getCourseName().toLowerCase().contains(filter);
        });
    }

    private void populateForm(Course c) {
        courseIdField.setText(c.getCourseId());
        courseNameField.setText(c.getCourseName());
        creditsField.setText(String.valueOf(c.getCredits()));
        dayField.setText(c.getDay());
        timeField.setText(c.getTime());
        roomField.setText(c.getRoom());
    }

    private void clearForm() {
        courseIdField.clear();
        courseNameField.clear();
        creditsField.clear();
        dayField.clear();
        timeField.clear();
        roomField.clear();
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

        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yes;
    }
}
