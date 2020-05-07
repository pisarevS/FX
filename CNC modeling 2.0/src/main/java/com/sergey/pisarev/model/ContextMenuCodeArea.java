package com.sergey.pisarev.model;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import org.fxmisc.richtext.CodeArea;

public class ContextMenuCodeArea {

    public static void installContextMenu(ContextMenu contextMenu, CodeArea codeAreaProgram){
        MenuItem menuItemCopy = new MenuItem("Copy");
        MenuItem menuItemPaste = new MenuItem("Paste");
        MenuItem menuItemCut = new MenuItem("Cut");
        menuItemCopy.setOnAction(event -> codeAreaProgram.copy());
        menuItemPaste.setOnAction(event -> codeAreaProgram.paste());
        menuItemCut.setOnAction(event -> codeAreaProgram.cut());
        contextMenu.getItems().addAll(menuItemCut, menuItemCopy, menuItemPaste);
        codeAreaProgram.setOnContextMenuRequested(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            String text = clipboard.getString();
            menuItemPaste.setDisable(text == null);
            contextMenu.show(codeAreaProgram, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }
}
