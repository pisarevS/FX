package com.sergey.pisarev.controller

import com.sergey.pisarev.model.StyleText.setStyle
import com.sergey.pisarev.model.StyleText.setListener
import com.sergey.pisarev.model.ContextMenuCodeArea.installContextMenu
import com.sergey.pisarev.model.TableUtils.Companion.installKeyHandler
import com.sergey.pisarev.model.StyleText.setStyleRefresh
import com.sergey.pisarev.interfaces.IController
import com.sergey.pisarev.interfaces.PresenterImpl
import org.fxmisc.richtext.CodeArea
import com.sergey.pisarev.model.ResizableCanvas
import com.sergey.pisarev.presenter.Presenter
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.model.TwoDimensional
import com.sergey.pisarev.model.MyFile
import java.lang.StringBuffer
import java.util.Objects
import javafx.application.Platform
import javafx.beans.Observable
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import java.io.IOException
import java.lang.Exception
import java.time.Duration
import kotlin.math.pow
import kotlin.math.roundToInt

class MainController : IController {
    private var presenter: PresenterImpl? = null
    private var countClick = 2
    private var isDownSingleBlock = false
    private var isCycleStart = false
    private var contextMenu: ContextMenu? = null
    private val codeAreaProgram = CodeArea()
    private var visualizerCanvas: ResizableCanvas? = null

    @FXML
    var textZooming = Text()

    @FXML
    var textCoordinateX = Text()

    @FXML
    var textCoordinateZ = Text()

    @FXML
    var textFrameCoordinateX = Text()

    @FXML
    var textFrameCoordinateZ = Text()

    @FXML
    var textFrame = Text()

    @FXML
    var paneCanvas = StackPane()

    @FXML
    var anchorPaneProgram = AnchorPane()

    @FXML
    var buttonStart = Button()

    @FXML
    var buttonCycleStart = Button()

    @FXML
    var buttonSingleBlock = Button()

    @FXML
    var buttonReset = Button()

    @FXML
    var splitPane = SplitPane()

    @FXML
    var checkBoxToolRadius = CheckBox()
    var textCodeArea: String?
        get() = codeAreaProgram.text
        set(text) {
            codeAreaProgram.clear()
            codeAreaProgram.appendText(text)
        }

    @FXML
    fun initialize() {
        mainController = this
        visualizerCanvas = ResizableCanvas()
        paneCanvas.style = "-fx-background-color: #F5F5F5"
        paneCanvas.children.add(visualizerCanvas)
        codeAreaProgram.addEventHandler(KeyEvent.KEY_RELEASED, codeAreaChangeCaretListener())
        presenter = Presenter(this, visualizerCanvas!!.graphicsContext2D)
        setStyle(codeAreaProgram)
        setListener(codeAreaProgram, anchorPaneProgram)
        codeAreaProgram.paragraphGraphicFactory = LineNumberFactory.get(codeAreaProgram)
        val stackPaneProgram = StackPane(VirtualizedScrollPane(codeAreaProgram))
        AnchorPane.setTopAnchor(stackPaneProgram, 0.0)
        AnchorPane.setBottomAnchor(stackPaneProgram, 0.0)
        AnchorPane.setLeftAnchor(stackPaneProgram, 0.0)
        AnchorPane.setRightAnchor(stackPaneProgram, 0.0)
        anchorPaneProgram.children.add(stackPaneProgram)
        buttonStart.textFill = Color.BLACK
        buttonCycleStart.textFill = Color.BLACK
        buttonSingleBlock.textFill = Color.BLACK
        buttonReset.textFill = Color.BLACK
        contextMenu = ContextMenu()
        installContextMenu(contextMenu!!, codeAreaProgram)
        installKeyHandler(codeAreaProgram)
        setOnChangesText(codeAreaProgram)
        visualizerCanvas!!.onMouseClicked = EventHandler { event: MouseEvent ->
            if (event.clickCount == 2) {
                if (visualizerCanvas!!.height - textFrameCoordinateX.layoutY <= visualizerCanvas!!.height - event.y && visualizerCanvas!!.height - textFrameCoordinateX.layoutY + textFrameCoordinateX.strokeMiterLimit >= visualizerCanvas!!.height - event.y && event.x >= textFrameCoordinateX.layoutX && event.x <= textFrameCoordinateX.layoutX + textFrameCoordinateX.wrappingWidth) {
                    setClipboardContent(textFrameCoordinateX.text.replace("X=", ""))
                } else if (visualizerCanvas!!.height - textFrameCoordinateZ.layoutY <= visualizerCanvas!!.height - event.y && visualizerCanvas!!.height - textFrameCoordinateZ.layoutY + textFrameCoordinateZ.strokeMiterLimit >= visualizerCanvas!!.height - event.y && event.x >= textFrameCoordinateZ.layoutX && event.x <= textFrameCoordinateZ.layoutX + textFrameCoordinateZ.wrappingWidth) {
                    setClipboardContent(textFrameCoordinateZ.text.replace("Z=", ""))
                } else presenter!!.onMouseClickedCanvas(event)
            }
        }
        visualizerCanvas!!.onMouseMoved = EventHandler { event: MouseEvent? -> presenter!!.onMouseMovedCanvas(event) }
        visualizerCanvas!!.onScroll = EventHandler { event: ScrollEvent? -> presenter!!.handleZooming(event) }
        visualizerCanvas!!.addEventHandler(MouseEvent.MOUSE_PRESSED) { event: MouseEvent? -> presenter!!.handleMousePressed(event) }
        visualizerCanvas!!.addEventHandler(MouseEvent.MOUSE_DRAGGED) { event: MouseEvent? -> presenter!!.handleMouseDragged(event) }
        visualizerCanvas!!.widthProperty().addListener { observable: Observable? -> presenter!!.initSystemCoordinate(visualizerCanvas!!.width, visualizerCanvas!!.height) }
        visualizerCanvas!!.heightProperty().addListener { observable: Observable? -> presenter!!.initSystemCoordinate(visualizerCanvas!!.width, visualizerCanvas!!.height) }
        exit()
    }

    private fun setClipboardContent(content: String) {
        val clipboardContent = ClipboardContent()
        clipboardContent.putString(content)
        clipboardContent.putHtml(content)
        Clipboard.getSystemClipboard().setContent(clipboardContent)
    }

    private fun codeAreaChangeCaretListener(): EventHandler<InputEvent> {
        return EventHandler { event: InputEvent ->
            val codeArea = event.source as CodeArea
            textFrame.text = ""
            textFrameCoordinateX.text = ""
            textFrameCoordinateZ.text = ""
            presenter!!.getCaretPosition(codeArea.offsetToPosition(codeArea.caretPosition, TwoDimensional.Bias.Forward).major)
        }
    }

    @FXML
    fun handleDragOverProgram(event: DragEvent) {
        if (event.dragboard.hasFiles()) event.acceptTransferModes(*TransferMode.ANY)
    }

    @FXML
    fun handleDragProgram(event: DragEvent?) {
        presenter!!.openDragProgram(event)
        STAGE!!.title = MyFile.filePath.toString()
        reset()
    }

    @FXML
    fun onMouseClickedProgram(event: Event?) {
        textFrame.text = ""
        textFrameCoordinateX.text = ""
        textFrameCoordinateZ.text = ""
        presenter!!.getCaretPosition(codeAreaProgram.offsetToPosition(codeAreaProgram.caretPosition, TwoDimensional.Bias.Forward).major)
        contextMenu!!.hide()
    }

    @FXML
    fun onStart(actionEvent: ActionEvent?) {
        buttonReset.isDisable = false
        buttonStart.isDisable = true
        buttonCycleStart.isDisable = true
        buttonSingleBlock.isDisable = true
        presenter!!.onStart(codeAreaProgram.text)
    }

    @FXML
    fun onCycleStart(actionEvent: ActionEvent?) {
        buttonReset.isDisable = false
        buttonStart.isDisable = true
        buttonCycleStart.isDisable = true
        buttonSingleBlock.isDisable = false
        isCycleStart = true
        if (isDownSingleBlock) buttonCycleStart.isDisable = false
        presenter!!.onCycleStart(codeAreaProgram.text, checkBoxToolRadius.isSelected)
    }

    @FXML
    fun onSingleBlock(actionEvent: ActionEvent?) {
        countClick++
        if (countClick % 2 == 0) {
            isDownSingleBlock = false
            buttonSingleBlock.style = "-fx-background-color:white "
            buttonCycleStart.isDisable = true
            presenter!!.onSingleBlock(false)
        } else {
            isDownSingleBlock = true
            buttonSingleBlock.style = "-fx-background-color: yellow"
            buttonCycleStart.isDisable = false
            buttonStart.isDisable = true
            presenter!!.onSingleBlock(true)
        }
    }

    @FXML
    fun onReset(actionEvent: ActionEvent?) {
        reset()
    }

    private fun reset() {
        textFrame.text = ""
        textFrameCoordinateX.text = ""
        textFrameCoordinateZ.text = ""
        buttonReset.isDisable = true
        buttonStart.isDisable = false
        buttonCycleStart.isDisable = false
        buttonSingleBlock.isDisable = false
        buttonSingleBlock.style = "-fx-background-color: white"
        countClick = 2
        presenter!!.onReset()
        if (isCycleStart) setStyleRefresh(codeAreaProgram)
    }

    override fun onStop() {
        buttonCycleStart.isDisable = true
        buttonSingleBlock.style = "-fx-background-color: white"
        buttonSingleBlock.isDisable = true
    }

    override fun showProgram(text: String?) {
        codeAreaProgram.clear()
        codeAreaProgram.appendText(text)
        buttonStart.isDisable = false
        buttonCycleStart.isDisable = false
        buttonSingleBlock.isDisable = false
        buttonReset.isDisable = true
    }

    override fun showFrame(frame: String?) {
        textFrame.text = frame
    }

    override fun showError(error: String?) {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Error"
        alert.alertType = Alert.AlertType.WARNING
        alert.contentText = error
        Platform.runLater { alert.showAndWait() }
    }

    override fun setZooming(zooming: Double) {
        textZooming.text = "${zooming.roundToInt()}%"
    }

    override fun getCoordinateCanvas(x: Double, z: Double) {
        var x = x
        var z = z
        val scale = 10.0.pow(2.0)
        x = (x * scale).roundToInt() / scale
        z = (z * scale).roundToInt() / scale
        textCoordinateX.text = "X $x"
        textCoordinateZ.text = "Z $z"
    }

    override fun getCoordinateFrame(x: Double, z: Double) {
        var x = x
        var z = z
        val scale = 10.0.pow(3.0)
        x = (x * scale).roundToInt() / scale
        z = (z * scale).roundToInt() / scale
        textFrameCoordinateX.text = "X=$x"
        textFrameCoordinateZ.text = "Z=$z"
    }

    override fun showCaretBoxOnCycleStart(number: Int, frame: StringBuffer?) {
        textFrame.text = frame.toString()
        showCaretBox(number, frame!!)
    }

    override fun showCaretBoxOnCanvasClick(number: Int, frame: StringBuffer?) {
        textFrame.text = frame.toString()
        showCaretBox(number, frame!!)
    }

    override fun showSaveAlert() {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.alertType = Alert.AlertType.INFORMATION
        alert.contentText = "Do you want to save file changes?"
        alert.buttonTypes.clear()
        alert.buttonTypes.addAll(ButtonType.YES, ButtonType.NO)
        val yesButton = alert.dialogPane.lookupButton(ButtonType.YES) as Button
        yesButton.onAction = EventHandler { presenter!!.saveProgram(codeAreaProgram.text) }
        alert.showAndWait()
    }

    @FXML
    fun menuSaveProgram(actionEvent: ActionEvent?) {
        presenter!!.saveProgram(codeAreaProgram.text)
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.alertType = Alert.AlertType.INFORMATION
        alert.contentText = "File saved!"
        alert.showAndWait()
    }

    @FXML
    fun menuRenameFrameNumbers(actionEvent: ActionEvent?) {
        try {
            val root = FXMLLoader.load<Parent>(Objects.requireNonNull(javaClass.classLoader.getResource("rename_frame_number.fxml")))
            val stage = Stage()
            RenameFrameNumbersController.STAGE = stage
            stage.scene = Scene(root, 328.0, 250.0)
            stage.title = "Rename Frames"
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.initStyle(StageStyle.UTILITY)
            stage.show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun menuAbout(actionEvent: ActionEvent?) {
        try {
            val root = FXMLLoader.load<Parent>(Objects.requireNonNull(javaClass.classLoader.getResource("about.fxml")))
            val stage = Stage()
            stage.scene = Scene(root, 400.0, 250.0)
            stage.title = "About CNC modeling 2.0"
            stage.initModality(Modality.APPLICATION_MODAL)
            stage.initStyle(StageStyle.UTILITY)
            stage.show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @FXML
    fun menuQuit(actionEvent: ActionEvent?) {
        presenter!!.checkChangesProgram(codeAreaProgram.text)
        Platform.exit()
    }

    private fun setOnChangesText(codeAreaProgram: CodeArea) {
        codeAreaProgram
                .multiPlainChanges()
                .successionEnds(Duration.ofMillis(1))
                .subscribe { presenter!!.setOnChangesTextProgram(codeAreaProgram.text) }
    }

    private fun showCaretBox(number: Int, frame: StringBuffer) {
        val end: Int = try {
            codeAreaProgram.position(number + 1, 0).toOffset() - 1
        } catch (e: Exception) {
            codeAreaProgram.length
        }
        codeAreaProgram.moveTo(end - frame.length)
        if (!codeAreaProgram.caretBounds.isPresent) {
            codeAreaProgram.requestFollowCaret()
            codeAreaProgram.requestFocus()
            codeAreaProgram.scrollYBy(1000.0)
        }
        codeAreaProgram.caretBounds
    }

    private fun exit() {
        STAGE!!.onCloseRequest = EventHandler {
            presenter!!.checkChangesProgram(codeAreaProgram.text)
            Platform.exit()
        }
    }

    @FXML
    fun menuConvertAviaProgram(actionEvent: ActionEvent?) {
        presenter!!.convertAviaProgram(codeAreaProgram.text)
    }

    @FXML
    fun menuConvertTheProgramToG17(actionEvent: ActionEvent?) {
        presenter!!.convertTheProgramToG17(codeAreaProgram.text)
    }

    @FXML
    fun menuConvertTheProgramToG18(actionEvent: ActionEvent?) {
        presenter!!.convertTheProgramToG18(codeAreaProgram.text)
    }

    companion object {
        var mainController: MainController? = null
        @JvmField
        var STAGE: Stage? = null
    }
}