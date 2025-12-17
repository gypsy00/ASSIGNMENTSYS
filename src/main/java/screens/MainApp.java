package screens;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import storagelayer.CourseBank;
import storagelayer.EnrollmentStore;
import storagelayer.ResultStore;
import storagelayer.StudentStore;


public class MainApp extends Application {

    private static final StudentStore studentStore = new StudentStore();
    private static final CourseBank coursebank = new CourseBank();
    private static final EnrollmentStore enrollmentStore = new EnrollmentStore(studentStore, coursebank);
    private static final ResultStore resultStore = new ResultStore(studentStore, coursebank);

    public static StudentStore getStudentStore() {
        return studentStore;
    }

    public static CourseBank getCoursebank() {
        return coursebank;
    }

    public static EnrollmentStore getEnrollmentStore() {
        return enrollmentStore;
    }

    public static ResultStore getResultStore() {
        return resultStore;
    }

    public static void switchScene(ActionEvent event, String fxml) {
        try{
            System.out.println("Switching to : /views/" + fxml);
            Parent root = FXMLLoader.load(
                    MainApp.class.getResource("/views/" + fxml)
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (Exception e) {
            System.out.println("FAiled to load: /views/" + fxml);
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/CoursesView.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Course Registration System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}