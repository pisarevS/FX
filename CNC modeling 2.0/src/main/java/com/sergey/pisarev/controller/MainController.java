package com.sergey.pisarev.controller;

import com.sergey.pisarev.interfaces.IController;
import com.sergey.pisarev.interfaces.PresenterImpl;
import com.sergey.pisarev.model.*;
import com.sergey.pisarev.presenter.Presenter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.TwoDimensional;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class MainController implements IController {

    private PresenterImpl presenter;
    private int countClick = 2;
    private boolean isDownSingleBlock = false;
    private boolean isCycleStart = false;
    private ContextMenu contextMenu;
    private final CodeArea codeAreaProgram = new CodeArea();
    public static MainController mainController;
    public static Stage STAGE;
    private ResizableCanvas visualizerCanvas;

    @FXML
    Text textZooming = new Text();

    @FXML
    Text textCoordinateX = new Text();

    @FXML
    Text textCoordinateZ = new Text();

    @FXML
    Text textFrameCoordinateX = new Text();

    @FXML
    Text textFrameCoordinateZ = new Text();

    @FXML
    Text textFrame = new Text();

    @FXML
    StackPane paneCanvas = new StackPane();

    @FXML
    AnchorPane anchorPaneProgram = new AnchorPane();

    @FXML
    Button buttonStart = new Button();

    @FXML
    Button buttonCycleStart = new Button();

    @FXML
    Button buttonSingleBlock = new Button();

    @FXML
    Button buttonReset = new Button();

    @FXML
    SplitPane splitPane=new SplitPane();

    public String getTextCodeArea() {
        return codeAreaProgram.getText();
    }

    public void setTextCodeArea(String text) {
        codeAreaProgram.clear();
        codeAreaProgram.appendText(text);
    }

    @FXML
    public void initialize() {
        mainController = this;
        visualizerCanvas = new ResizableCanvas();
        paneCanvas.setStyle("-fx-background-color: #F5F5F5");
        paneCanvas.getChildren().add(visualizerCanvas);
        codeAreaProgram.addEventHandler(KeyEvent.KEY_RELEASED, codeAreaChangeCaretListener());
        presenter = new Presenter(this, visualizerCanvas.getGraphicsContext2D());
        StyleText.setStyle(codeAreaProgram);
        codeAreaProgram.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaProgram));
        StackPane stackPaneProgram = new StackPane(new VirtualizedScrollPane<>(codeAreaProgram));
        AnchorPane.setTopAnchor(stackPaneProgram, 0.0);
        AnchorPane.setBottomAnchor(stackPaneProgram, 0.0);
        AnchorPane.setLeftAnchor(stackPaneProgram, 0.0);
        AnchorPane.setRightAnchor(stackPaneProgram, 0.0);
        anchorPaneProgram.getChildren().add(stackPaneProgram);
        buttonStart.setTextFill(Color.BLACK);
        buttonCycleStart.setTextFill(Color.BLACK);
        buttonSingleBlock.setTextFill(Color.BLACK);
        buttonReset.setTextFill(Color.BLACK);
        contextMenu = new ContextMenu();
        ContextMenuCodeArea.installContextMenu(contextMenu,codeAreaProgram);
        TableUtils.installKeyHandler(codeAreaProgram);
        setOnChangesText(codeAreaProgram);

        visualizerCanvas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2){
                if(visualizerCanvas.getHeight()-textFrameCoordinateX.getLayoutY()<=visualizerCanvas.getHeight()-event.getY()&&
                        visualizerCanvas.getHeight()-textFrameCoordinateX.getLayoutY()+textFrameCoordinateX.getStrokeMiterLimit()>=visualizerCanvas.getHeight()-event.getY()&&
                        event.getX()>=textFrameCoordinateX.getLayoutX()&&event.getX()<=textFrameCoordinateX.getLayoutX()+textFrameCoordinateX.getWrappingWidth()){
                  setClipboardContent(textFrameCoordinateX.getText().replace("X=",""));
                }else if(visualizerCanvas.getHeight()-textFrameCoordinateZ.getLayoutY()<=visualizerCanvas.getHeight()-event.getY()&&
                        visualizerCanvas.getHeight()-textFrameCoordinateZ.getLayoutY()+textFrameCoordinateZ.getStrokeMiterLimit()>=visualizerCanvas.getHeight()-event.getY()&&
                        event.getX()>=textFrameCoordinateZ.getLayoutX()&&event.getX()<=textFrameCoordinateZ.getLayoutX()+textFrameCoordinateZ.getWrappingWidth()){
                    setClipboardContent(textFrameCoordinateZ.getText().replace("Z=",""));
                }else {
                    presenter.onMouseClickedCanvas(event);
                }
            }
        });

        visualizerCanvas.setOnMouseMoved(event -> {
            presenter.onMouseMovedCanvas(event);
        });

        visualizerCanvas.setOnScroll((ScrollEvent event) -> {
            presenter.handleZooming(event);
        });

        visualizerCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            presenter.handleMousePressed(event);
        });

        visualizerCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            presenter.handleMouseDragged(event);
        });

        visualizerCanvas.widthProperty().addListener(observable -> presenter.initSystemCoordinate( visualizerCanvas.getWidth(),visualizerCanvas.getHeight()));
        visualizerCanvas.heightProperty().addListener(observable -> presenter.initSystemCoordinate(visualizerCanvas.getWidth(),visualizerCanvas.getHeight()));

        exit();
    }

    private void setClipboardContent(String content){
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        clipboardContent.putHtml(content);
        System.out.println(content);
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }

    private EventHandler<InputEvent> codeAreaChangeCaretListener() {
        return event -> {
            CodeArea codeArea = (CodeArea) event.getSource();
            textFrame.setText("");
            textFrameCoordinateX.setText("");
            textFrameCoordinateZ.setText("");
            presenter.getCaretPosition(codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor());
        };
    }

    @FXML
    public void handleDragOverProgram(DragEvent event) {
        if (event.getDragboard().hasFiles())
            event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    public void handleDragProgram(DragEvent event) {
        presenter.openDragProgram(event);
        STAGE.setTitle(File.fileProgram.toString());
        textFrame.setText("");
    }

    @FXML
    public void onMouseClickedProgram(Event event) {
        textFrame.setText("");
        textFrameCoordinateX.setText("");
        textFrameCoordinateZ.setText("");
        presenter.getCaretPosition(codeAreaProgram.offsetToPosition(codeAreaProgram.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor());
        contextMenu.hide();
    }

    @FXML
    public void onStart(ActionEvent actionEvent) {
        buttonReset.setDisable(false);
        buttonStart.setDisable(true);
        buttonCycleStart.setDisable(true);
        buttonSingleBlock.setDisable(true);
        presenter.onStart(codeAreaProgram.getText());
    }

    @FXML
    public void onCycleStart(ActionEvent actionEvent) {
        buttonReset.setDisable(false);
        buttonStart.setDisable(true);
        buttonCycleStart.setDisable(true);
        buttonSingleBlock.setDisable(false);
        isCycleStart = true;
        if (isDownSingleBlock) {
            buttonCycleStart.setDisable(false);
        }
        presenter.onCycleStart(codeAreaProgram.getText());
    }

    @FXML
    public void onSingleBlock(ActionEvent actionEvent) {
        countClick++;
        if (countClick % 2 == 0) {
            isDownSingleBlock = false;
            buttonSingleBlock.setStyle("-fx-background-color: ");
            buttonCycleStart.setDisable(true);
            presenter.onSingleBlock(false);
        } else {
            isDownSingleBlock = true;
            buttonSingleBlock.setStyle("-fx-background-color: yellow");
            buttonCycleStart.setDisable(false);
            buttonStart.setDisable(true);
            presenter.onSingleBlock(true);
        }
    }

    @FXML
    public void onReset(ActionEvent actionEvent) {
        textFrame.setText("");
        textFrameCoordinateX.setText("");
        textFrameCoordinateZ.setText("");
        buttonReset.setDisable(true);
        buttonStart.setDisable(false);
        buttonCycleStart.setDisable(false);
        buttonSingleBlock.setDisable(false);
        buttonSingleBlock.setStyle("-fx-background-color: ");
        countClick = 2;
        presenter.onReset();
        if (isCycleStart) {
            StyleText.setStyleRefresh(codeAreaProgram);
        }
    }

    @Override
    public void onReset() {
        buttonCycleStart.setDisable(true);
        buttonSingleBlock.setStyle("-fx-background-color: ");
        buttonSingleBlock.setDisable(true);
    }

    @Override
    public void showProgram(String text) {
        codeAreaProgram.clear();
        codeAreaProgram.appendText(text);
        buttonStart.setDisable(false);
        buttonCycleStart.setDisable(false);
        buttonSingleBlock.setDisable(false);
        buttonReset.setDisable(true);
    }

    @Override
    public void showFrame(String frame) {
        textFrame.setText(frame);
    }

    @Override
    public void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setContentText(error);
        Platform.runLater(alert::showAndWait);
    }

    @Override
    public void setZooming(double zooming) {
        textZooming.setText((int) zooming + "%");
    }

    @Override
    public void getCoordinateCanvas(double x, double z) {
        double scale = Math.pow(10, 2);
        x = Math.round(x * scale) / scale;
        z = Math.round(z * scale) / scale;
        textCoordinateX.setText("X " + x);
        textCoordinateZ.setText("Z " + z);
    }

    @Override
    public void getCoordinateFrame(double x, double z) {
        double scale = Math.pow(10, 3);
        x = Math.round(x * scale) / scale;
        z = Math.round(z * scale) / scale;
        textFrameCoordinateX.setText("X=" + x);
        textFrameCoordinateZ.setText("Z=" + z);
    }

    @Override
    public void showCaretBoxOnCycleStart(int number, StringBuffer frame) {
        textFrame.setText(frame.toString());
        showCaretBox(number, frame);
    }

    @Override
    public void showCaretBoxOnCanvasClick(int number, StringBuffer frame) {
        textFrame.setText(frame.toString());
        showCaretBox(number, frame);
    }

    @FXML
    public void menuSaveProgram(ActionEvent actionEvent) {
        if (File.fileProgram != null) {
            File.setFileContent(File.fileProgram, codeAreaProgram.getText());
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setAlertType(AlertType.INFORMATION);
            alert.setContentText("File saved!");
            alert.showAndWait();
        }
    }

    @FXML
    public void menuRenameFrameNumbers(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("rename_frame_number.fxml")));
            Stage stage = new Stage();
            RenameFrameNumbersController.STAGE = stage;
            stage.setScene(new Scene(root, 328, 250));
            stage.setTitle("Rename Frames");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void menuAbout(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("about.fxml")));
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 400, 250));
            stage.setTitle("About CNC modeling 2.0");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void menuQuit(ActionEvent actionEvent) {
        saveChanges();
        Platform.exit();
    }

    private void setOnChangesText(CodeArea codeAreaProgram) {
        codeAreaProgram
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(1))
                .subscribe(ignore -> presenter.setOnChangesTextProgram(codeAreaProgram.getText()));
    }

    private void alertSaveChanges(java.io.File file, String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setAlertType(AlertType.INFORMATION);
        alert.setContentText("Do you want to save file changes?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.setOnAction(event -> {
            File.setFileContent(file, text);
        });
        alert.showAndWait();
    }

    private void saveChanges() {
        if (File.fileProgram != null) {
            String programFile = File.getFileContent(File.fileProgram);
            if (!programFile.equals(codeAreaProgram.getText())) {
                alertSaveChanges(File.fileProgram, codeAreaProgram.getText());
            }
        }
    }

    private void showCaretBox(int number, StringBuffer frame) {
        int end;
        try {
            end = codeAreaProgram.position(number + 1, 0).toOffset() - 1;
        } catch (Exception e) {
            end = codeAreaProgram.getLength();
        }
        codeAreaProgram.moveTo(end - frame.length());
        if (!codeAreaProgram.getCaretBounds().isPresent()) {
            codeAreaProgram.requestFollowCaret();
            codeAreaProgram.requestFocus();
            codeAreaProgram.scrollYBy(1000);
        }
        codeAreaProgram.getCaretBounds();
    }

    private void exit() {
        STAGE.setOnCloseRequest(event -> {
            saveChanges();
            Platform.exit();
        });
    }
}
