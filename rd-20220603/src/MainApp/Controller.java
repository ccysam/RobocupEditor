package MainApp;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class Controller {
    @FXML
    private void closeWnd(){
        Platform.exit();
    }
}
