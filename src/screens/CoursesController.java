package screens;

import core.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import storagelayer.CourseBank;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;

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
        courseTable.getItems().add(newCourse);

        // TODO: later we’ll save to file with CourseBank here
        // try {
        //     courseBank.addCourse(newCourse);
        //     courseBank.saveCourses();
        // } catch (Exception e) {
        //     showError("Could not save course: " + e.getMessage());
        // }

        // clear fields
        courseIdField.clear();
        courseNameField.clear();
        creditsField.clear();
        dayField.clear();
        timeField.clear();
        roomField.clear();
    }


    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private CourseBank courseBank = new CourseBank();

    @FXML
    public void initialize() {
        // link columns to Course fields (getter names in Course.java)
        idCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
    }

        private void loadCoursesFromFile(){
            List<Course> courses = courseBank.loadCourses();
            ObservableList<Course> data = FXCollections.observableArrayList(courses);
            courseTable.setItems(data);
        }
}
