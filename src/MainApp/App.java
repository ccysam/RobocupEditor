package MainApp;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    double oldStageX, oldStageY, oldScreenX, oldScreenY;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent MainPane = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
        Scene s1Scene = new Scene(MainPane);
        
        DragUtil.addDragListener(primaryStage, MainPane.lookup("#titleBarUp"));
        DragUtil.addDragListener(primaryStage, MainPane.lookup("#titleText"));

        s1Scene.setFill(null);
        primaryStage.setTitle("RobocupEditor");
        primaryStage.setScene(s1Scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

}

class DragUtil {
    public static void addDragListener(Stage stage, Node root) {
        new DragListener(stage).enableDrag(root);
    }
}

class DragListener implements EventHandler<MouseEvent> {

    private double xOffset = 0;
    private double yOffset = 0;
    private final Stage stage;

    public DragListener(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void handle(MouseEvent event) {
        event.consume();
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            stage.setX(event.getScreenX() - xOffset);
            if (event.getScreenY() - yOffset < 0) {
                stage.setY(0);
            } else {
                stage.setY(event.getScreenY() - yOffset);
            }
        }
    }

    public void enableDrag(Node node) {
        node.setOnMousePressed(this);
        node.setOnMouseDragged(this);
    }
}