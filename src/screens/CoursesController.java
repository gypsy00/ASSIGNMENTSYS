package screens;

import core.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import storagelayer.CourseBank;
import java.util.List;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import java.util.Optional;


import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

public class CoursesController {


//Fields for table
    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, String> idCol;

    @FXML
    private TableColumn<Course, String> nameCol;

    @FXML
    private TableColumn<Course, Integer> creditsCol;

    @FXML
    private TableColumn<Course, String> dayCol;

    @FXML
    private TableColumn<Course, String> timeCol;

    @FXML
    private TableColumn<Course, String> roomCol;

    //Fields for form
    @FXML
    private TextField courseIdField;
    @FXML
    private TextField courseNameField;
    @FXML
    private TextField creditsField;
    @FXML
    private TextField dayField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField roomField;

    //firlds for update and delete button
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    //fields for search
    @FXML
    private TextField searchField;

    private ObservableList<Course> masterData = FXCollections.observableArrayList();
    private FilteredList<Course> filteredData;

    //add button method
    @FXML
    private void onAddCourseClicked(ActionEvent event) {
        String id = courseIdField.getText().trim();
        String name = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        String day = dayField.getText().trim();
        String time = timeField.getText().trim();
        String room = roomField.getText().trim();

        // validation
        if (id.isEmpty() || name.isEmpty() || creditsText.isEmpty()
                || day.isEmpty() || time.isEmpty() || room.isEmpty()) {
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

        // create Course object (adapt constructor if needed)
        Course newCourse = new Course(id, name, credits, day, time, room);

        // update table UI
        masterData.add(newCourse);
        courseTable.getItems().add(newCourse);



        try {
            courseBank.addCourse(newCourse);

            loadCoursesFromFile();

            // clear fields
            courseIdField.clear();
            courseNameField.clear();
            creditsField.clear();
            dayField.clear();
            timeField.clear();
            roomField.clear();
        }catch (Exception e){
            showError("Could not save courses: " + e.getMessage());
        }
    }




    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirm (String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }


    //delete button method
    @FXML
    private void onDeleteCourseClicked(ActionEvent event) {
        Course selected = courseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a course to delete.");
            return;
        }

        // Ask for confirmation
        boolean ok = confirm(
                "Delete Course",
                "Are you sure you want to delete course " + selected.getCourseId() + "?"
        );

        if (!ok) {
            return; // user clicked No
        }

        // 1) Remove from CourseBank (and file)
        masterData.remove(selected);
        courseBank.removeCourse(selected);

        // 2) Remove from table
        courseTable.getItems().remove(selected);

        // 3) Clear fields (optional)
        courseIdField.clear();
        courseNameField.clear();
        creditsField.clear();
        dayField.clear();
        timeField.clear();
        roomField.clear();
    }


    //update button method
    @FXML
    private void onUpdateCourseClicked(ActionEvent event) {
        Course selected = courseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a course to update.");
            return;
        }

        // Read values from the form
        String id    = courseIdField.getText().trim();
        String name  = courseNameField.getText().trim();
        String creditsText = creditsField.getText().trim();
        String day   = dayField.getText().trim();
        String time  = timeField.getText().trim();
        String room  = roomField.getText().trim();

        // Basic validation
        if (id.isEmpty() || name.isEmpty() || creditsText.isEmpty()
                || day.isEmpty() || time.isEmpty() || room.isEmpty()) {
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

        // Ask for confirmation before changing anything
        boolean ok = confirm(
                "Update Course",
                "Are you sure you want to update course " + selected.getCourseId() + "?"
        );

        if (!ok) {
            return; // user cancelled
        }

        // Build updated Course object
        Course updatedCourse = new Course(id, name, credits, day, time, room);

        // 1) Update in CourseBank (and file)
        int indexInMaster = masterData.indexOf(selected);
        if (indexInMaster >= 0) {
            masterData.set(indexInMaster, updatedCourse);
        }
        courseBank.updateCourse(selected, updatedCourse);

        // 2) Update in table
        int index = courseTable.getItems().indexOf(selected);
        courseTable.getItems().set(index, updatedCourse);
        courseTable.getSelectionModel().select(updatedCourse);
    }

    private CourseBank courseBank = new CourseBank();


    //initialize method
    @FXML
    public void initialize() {
        // 1) Column setup (keep what you already had)
        idCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        // 2) Load courses from CourseBank into masterData
        List<Course> courses = courseBank.loadCourses();
        masterData.setAll(courses);

        // 3) Create filtered list wrapping masterData
        filteredData = new FilteredList<>(masterData, c -> true);

        // 4) Sorted list so table sorting still works
        SortedList<Course> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(courseTable.comparatorProperty());

        // 5) Connect to table
        courseTable.setItems(sortedData);

        // 6) Live search
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String filter = (newValue == null) ? "" : newValue.trim().toLowerCase();

            if (filter.isEmpty()) {
                // show all
                filteredData.setPredicate(c -> true);
            } else {
                filteredData.setPredicate(c -> {
                    if (c == null) return false;

                    String id   = c.getCourseId().toLowerCase();
                    String name = c.getCourseName().toLowerCase();

                    return id.contains(filter) || name.contains(filter);
                });
            }
        });

        searchField.requestFocus();

        // 7) Your existing selection listener (copy your old code here)
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldCourse, selectedCourse) -> {
                    if (selectedCourse != null) {
                        courseIdField.setText(selectedCourse.getCourseId());
                        courseNameField.setText(selectedCourse.getCourseName());
                        creditsField.setText(String.valueOf(selectedCourse.getCredits()));
                        dayField.setText(selectedCourse.getDay());
                        timeField.setText(selectedCourse.getTime());
                        roomField.setText(selectedCourse.getRoom());
                    }
                }
        );
    }

        private void loadCoursesFromFile(){
        //get courses from CourseBank
            List<Course> courses = courseBank.loadCourses();
            ObservableList<Course> data = FXCollections.observableArrayList(courses);
            courseTable.setItems(data);
        }
}
