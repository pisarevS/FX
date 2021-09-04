package com.sergey.pisarev.model

import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.input.Clipboard
import javafx.scene.input.ContextMenuEvent
import org.fxmisc.richtext.CodeArea

object ContextMenuCodeArea {

    fun installContextMenu(contextMenu: ContextMenu, codeAreaProgram: CodeArea) {
        val menuItemCopy = MenuItem("Copy")
        val menuItemPaste = MenuItem("Paste")
        val menuItemCut = MenuItem("Cut")
        menuItemCopy.onAction = EventHandler { codeAreaProgram.copy() }
        menuItemPaste.onAction = EventHandler { codeAreaProgram.paste() }
        menuItemCut.onAction = EventHandler { codeAreaProgram.cut() }
        contextMenu.items.addAll(menuItemCut, menuItemCopy, menuItemPaste)
        codeAreaProgram.onContextMenuRequested = EventHandler { event: ContextMenuEvent ->
            val clipboard = Clipboard.getSystemClipboard()
            val text = clipboard.string
            menuItemPaste.isDisable = text == null
            contextMenu.show(codeAreaProgram, event.screenX, event.screenY)
            event.consume()
        }
    }
}