package MainApp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import Utils.FileUtil;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;

public class Controller implements Initializable {

    double sX = Screen.getPrimary().getOutputScaleX();
    double sY = Screen.getPrimary().getOutputScaleY();
    double oldStageX, oldStageY, oldScreenX, oldScreenY;
    int currentTab;// 1 Filed, 2 Player
    int currentStatus;
    int statusEditing;
    int playerNumber;
    final Node FolderIcon = new ImageView(new Image(getClass().getResourceAsStream("Folder.png")));
    final Node FileIcon = new ImageView(new Image(getClass().getResourceAsStream("file.png")));
    final Node CodeIcon = new ImageView(new Image(getClass().getResourceAsStream("Code.png")));

    Boolean isOpenFolder, isProject;
    File[] allFiles;
    File nowDir;
    File projectInfo, strategy;
    FieldRect<Integer> FieldSize;
    GraphicsContext fieldGraphicsContext, gridGraphicsContext;
    FileUtil projectFileUtil, strategyFileUtil;
    FieldDrawer fieldDrawer, gridDrawer;
    Position[] initPlayerPositions;

    // private Stage stage;

    @FXML
    private Pane MainPane, BkPane, FtPane;
    @FXML
    private Rectangle titleBarUp, titleBarDwn;
    @FXML
    private Label titleText;
    @FXML
    private Button CloseBtn;
    @FXML
    private Menu FileMenu, EditMenu, HelpMenu;
    @FXML
    private Accordion leftAccordion;
    @FXML
    private Pane Filed, Player, Project;
    @FXML
    private ImageView FiledImage;
    @FXML
    private TreeView<String> FileManager;
    @FXML
    private Canvas FiledCanvas, GridCanvas;
    @FXML
    private TextField TeamName, TeamNum, hostAddr, port;
    @FXML
    private Button beforePlayButton, playOnButton;
    @FXML
    private CheckBox showGrid;
    @FXML
    private ChoiceBox<String> statusSelector;

    @FXML
    private void closeWnd() {
        Platform.exit();

    }

    @FXML
    private void closeFolder() {
        if (checkCloseState()) {
            isOpenFolder = false;
            isProject = false;
            FileManager.setRoot(null);
            allFiles = null;
            nowDir = null;
            projectInfo = null;
            strategy = null;
            projectFileUtil = null;
            injectProjectInfo();
        }
    }

    @FXML
    private void TabFiled() {
        if (currentTab != 1) {
            Filed.setVisible(true);
            Player.setVisible(false);
            Project.setVisible(false);
            currentTab = 1;
        }
    }

    @FXML
    private void TabPlayer() {
        if (currentTab != 2) {
            Filed.setVisible(false);
            Player.setVisible(true);
            Project.setVisible(false);
            currentTab = 2;
        }
    }

    @FXML
    private void TabProject() {
        // if (isProject) {
        // if (currentTab != 3) {
        // Filed.setVisible(false);
        // Player.setVisible(false);
        // Project.setVisible(true);
        // currentTab = 3;
        // }
        // }
        if (currentTab != 3) {
            Filed.setVisible(false);
            Player.setVisible(false);
            Project.setVisible(true);
            currentTab = 3;
            // Analyzer a = new Analyzer(projectInfo);
            injectProjectInfo();
        }
    }

    private void injectProjectInfo() {
        if (projectFileUtil != null) {
            projectFileUtil.getProjectInfo();
            TeamName.setText(projectFileUtil.teamName);
            TeamNum.setText(String.valueOf(projectFileUtil.playerNumber));
            playerNumber = projectFileUtil.playerNumber;
            hostAddr.setText(projectFileUtil.host);
            port.setText(String.valueOf(projectFileUtil.port));
        } else {
            TeamName.setText("");
            TeamNum.setText(String.valueOf(""));
            hostAddr.setText("");
            port.setText("");
        }
    }

    @FXML
    private void OpenFolder() {
        if (isOpenFolder) {
            Alert askCloseFolder = new Alert(AlertType.CONFIRMATION, "是否关闭当前文件夹？");
            askCloseFolder.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    if (checkCloseState()) {
                        this.closeFolder();
                        this.OpenFolder();
                    }
                }
            });

        } else {
            DirectoryChooser fC = new DirectoryChooser();
            fC.setTitle("打开项目文件夹");
            nowDir = fC.showDialog(null);
            // System.out.println(nowDir);
            TreeItem<String> rootDir = new TreeItem<>(nowDir.getName(), FolderIcon);
            allFiles = nowDir.listFiles();
            showDirectory(nowDir, rootDir);
            FileManager.setRoot(rootDir);
            isOpenFolder = true;
            loadProject();
            if (currentTab == 3) {
                injectProjectInfo();
            }
        }
    }

    private void loadProject() {
        System.out.println(projectInfo);
        System.out.println(strategy);
        projectFileUtil = new FileUtil(projectInfo);
        strategyFileUtil = new FileUtil(strategy);
        injectProjectInfo();
    }

    private void showDirectory(File Dir, TreeItem<String> root) {
        // root.setExpanded(true);
        File[] files = Dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                TreeItem<String> SubDir = new TreeItem<>(file.getName(),
                        new ImageView(new Image(getClass().getResourceAsStream("Folder.png"))));
                showDirectory(file, SubDir);
                root.getChildren().add(SubDir);
            }
        }
        for (File file : files) {
            if (file.isFile()) {
                TreeItem<String> SubFile = new TreeItem<>(file.getName(),
                        new ImageView(new Image(getClass().getResourceAsStream("file.png"))));
                root.getChildren().add(SubFile);
                if (file.getName().equals("start.sh")) {
                    projectInfo = file;
                    isProject = true;
                }
                if (file.getName().equals("strategy.cc")) {
                    strategy = file;
                }
            }
        }
    }

    public String getFileExtension(File file) {
        String extension = "";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    @FXML
    private void setInfo() {
        if (projectFileUtil != null) {
            int i = Integer.parseInt(TeamNum.getText());
            if (i <= 11 && i >= 0) {
                projectFileUtil.setProjectInfo(TeamName.getText(), hostAddr.getText(),
                        Integer.parseInt(TeamNum.getText()),
                        Long.parseLong(port.getText()));
                new Alert(AlertType.INFORMATION, "工程信息已修改").show();
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("球员人数错误！");
                alert.setContentText("球员数量请设置在0~11人之间。:-)");
                alert.show();
            }
        }
    }

    @FXML
    private void beforePlayEdit() {
        if (isProject) {
            fieldGraphicsContext = null;
            strategyFileUtil.getStrategy();
            for (int i = 1; i <= playerNumber; i++) {
                int[] pos = strategyFileUtil.getStrategyBeamsPos(i);
                System.out.println(Arrays.toString(pos));
                fieldDrawer.drawRound(pos[0], pos[1], 8, Color.BLUE);
                
            }
        }
    }

    @FXML
    private void GridDisplay() {
        boolean status = showGrid.isSelected();
        if (status) {
            drawGrid(true);
        } else {
            drawGrid(false);
        }
    }

    private void drawGrid(boolean b) {

        if (b) {
            gridDrawer.drawLine(-15, 10, -15, -10, Color.AQUA, 2);
            gridDrawer.drawLine(-15, 10, 15, 10, Color.AQUA, 2);
            gridDrawer.drawLine(15, 10, 15, -10, Color.AQUA, 2);
            gridDrawer.drawLine(-15, -10, 15, -10, Color.AQUA, 2);
            for (int i = -14; i <= 14; i++) {
                if (i == 0) {
                    gridDrawer.drawLine(i, 10, i, -10, Color.AQUA, 4);
                }
                gridDrawer.drawLine(i, 10, i, -10, Color.AQUA, 1);
            }
            for (int i = -9; i <= 9; i++) {
                if (i == 0) {
                    gridDrawer.drawLine(-15, i, 15, i, Color.AQUA, 4);
                }
                gridDrawer.drawLine(-15, i, 15, i, Color.AQUA, 1);
            }
        } else {
            gridGraphicsContext.clearRect(0, 0, 1024, 704);
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        initWidgets();
        initCanvas();
        initSelector();
        loadIcon();
        isOpenFolder = false;
        isProject = false;
        currentTab = 1;
        // var stage = getStage();
    }

    // private Stage getStage() {
    // if (stage == null) {
    // stage = (Stage) MainPane.getScene().getWindow();
    // }
    // return stage;
    // }

    private void initSelector() {
        File status = new File("resources\\status.txt");
        List<String> statusList = new ArrayList<>();
        try {
            statusList = Files.readAllLines(status.toPath());
            for (String string : statusList) {
                statusSelector.getItems().add(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        statusSelector.getSelectionModel().selectedIndexProperty()
                .addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    switch (new_val.intValue()) {
                        case 0:
                            beforePlayEdit();
                        default:
                            System.out.println("Seleted " + new_val);
                    }
                });
    }

    private void initCanvas() {
        fieldGraphicsContext = FiledCanvas.getGraphicsContext2D();
        gridGraphicsContext = GridCanvas.getGraphicsContext2D();
        fieldDrawer = new FieldDrawer(fieldGraphicsContext);
        gridDrawer = new FieldDrawer(gridGraphicsContext);
        // fieldGraphicsContext.setFill(Color.ALICEBLUE);
        // fieldGraphicsContext.fillRect(0, 0, 512, 512);
    }

    private void loadIcon() {

    }

    private void initWidgets() {
        FieldSize = new FieldRect<Integer>();
        FieldSize.setWidth((int) (1088 / sX));
        FieldSize.setHeight((int) (752 / sY));
        FieldSize.setX((int) (264 / sX));
        FieldSize.setY((int) (70 / sY));

        MainPane.setPrefWidth(1600 / sX);
        MainPane.setPrefHeight(900 / sY);
        BkPane.setPrefWidth(1600 / sX);
        BkPane.setPrefHeight(900 / sY);
        FtPane.setPrefWidth(1600 / sX);
        FtPane.setPrefHeight(900 / sY);
        titleBarUp.setWidth(1600 / sX);
        titleBarDwn.setWidth(1600 / sX);
        titleText.setLayoutX((1600 * 0.5 / sX) - 64);
        CloseBtn.setLayoutX((1600 - 28) / sX);
        leftAccordion.setPrefWidth(240 / sX);
        leftAccordion.setPrefHeight(825 / sY);
        Filed.setPrefWidth(FieldSize.getWidth());
        Filed.setPrefHeight(FieldSize.getHeight());
        Filed.setLayoutX(FieldSize.getX());
        Filed.setLayoutY(FieldSize.getY());
        Player.setPrefWidth(FieldSize.getWidth());
        Player.setPrefHeight(FieldSize.getHeight());
        Player.setLayoutX(FieldSize.getX());
        Player.setLayoutY(FieldSize.getY());
        Player.setVisible(false);
        Project.setPrefWidth(FieldSize.getWidth());
        Project.setPrefHeight(FieldSize.getHeight());
        Project.setLayoutX(FieldSize.getX());
        Project.setLayoutY(FieldSize.getY());
        Project.setVisible(false);
        FiledImage.setFitWidth(FieldSize.getWidth());
        FiledImage.setFitHeight(FieldSize.getHeight());
        FiledCanvas.setLayoutX(24 / sX);
        FiledCanvas.setLayoutY(32 / sY);
        FiledCanvas.setWidth(FieldSize.getWidth());
        FiledCanvas.setHeight(FieldSize.getHeight());
        GridCanvas.setLayoutX(24 / sX);
        GridCanvas.setLayoutY(32 / sY);
        GridCanvas.setWidth(FieldSize.getWidth());
        GridCanvas.setHeight(FieldSize.getHeight());
    }

    private boolean checkCloseState() {
        return true;
    }

    class FieldDrawer {
        Position oSize = new Position(1024, 704);
        Position tSize = new Position(32, 22);
        GraphicsContext graphicsContext;

        FieldDrawer(GraphicsContext gc) {
            graphicsContext = gc;
        }

        private Position transPosition(double x, double y) {
            double X = 0, Y = 0;
            if (x < 0) {
                X = 16 - (Math.abs(x));
            }
            if (x >= 0) {
                X = 16 + x;
            }
            if (y < 0) {
                Y = 11 + (Math.abs(y));
            }
            if (y >= 0) {
                Y = 11 - y;
            }
            return new Position(X, Y);
        }

        public void drawLine(double x1, double y1, double x2, double y2, Paint color, double width) {
            Position position1 = transPosition(x1, y1);
            Position position2 = transPosition(x2, y2);
            Position nPosition1 = Positions.R2A(oSize, tSize, position1);
            Position nPosition2 = Positions.R2A(oSize, tSize, position2);
            graphicsContext.setStroke(color);
            graphicsContext.setLineWidth(width);
            graphicsContext.strokeLine(nPosition1.x, nPosition1.y, nPosition2.x, nPosition2.y);
        }

        public void drawRound(double x, double y, double radius, Paint color) {
            Position position = transPosition(x, y);
            Position nPosition = Positions.R2A(oSize, tSize, position);
            graphicsContext.setFill(color);
            graphicsContext.fillOval(nPosition.x - radius, nPosition.y - radius, radius * 2, radius * 2);
        }

    }

}
