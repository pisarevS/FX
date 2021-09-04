package com.sergey.pisarev.controller

import com.sergey.pisarev.model.RenameFrameNumbers.rename
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TextField
import javafx.stage.Stage

class RenameFrameNumbersController {
    @FXML
    var textFieldFirstNumber = TextField()

    @FXML
    var textFieldStep = TextField()
    @FXML
    fun buttonOnClickOk(actionEvent: ActionEvent?) {
        if (textFieldFirstNumber.text.isNotEmpty() && textFieldStep.text.isNotEmpty() && MainController.mainController!!.textCodeArea!!.isNotEmpty()) {
            MainController.mainController!!.textCodeArea = rename(MainController.mainController?.textCodeArea, textFieldFirstNumber.text.toInt(), textFieldStep.text.toInt())
            STAGE!!.close()
        }
    }

    @FXML
    fun buttonOnClickCancel(actionEvent: ActionEvent?) {
        STAGE!!.close()
    }

    companion object {
        @JvmField
        var STAGE: Stage? = null
    }
}