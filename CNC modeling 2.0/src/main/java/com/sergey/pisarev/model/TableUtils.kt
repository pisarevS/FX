package com.sergey.pisarev.model

import java.text.NumberFormat
import org.fxmisc.richtext.CodeArea
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.control.TableView
import javafx.scene.input.*
import javafx.scene.layout.Pane
import org.fxmisc.richtext.model.TwoDimensional
import java.lang.StringBuilder
import java.text.ParseException
import java.util.Arrays
import java.util.stream.Collectors
import java.util.StringTokenizer

class TableUtils {
    /**
     * Copy/Paste keyboard event handler.
     * The handler uses the keyEvent's source for the clipboard data. The source must be of type TableView.
     */
    class TableKeyEventHandler : EventHandler<KeyEvent> {
        var copyKeyCodeCompination = KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY)
        var pasteKeyCodeCompination = KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY)
        override fun handle(keyEvent: KeyEvent) {
            if (copyKeyCodeCompination.match(keyEvent)) {
                if (keyEvent.source is TableView<*>) {
                    println("copy")
                    // copy to clipboard
                    copySelectionToClipboard(keyEvent.source as TableView<*>)
                    // event is handled, consume it
                    keyEvent.consume()
                }
            } else if (pasteKeyCodeCompination.match(keyEvent)) {
                if (keyEvent.source is TableView<*>) {
                    // copy to clipboard
                    pasteFromClipboard(keyEvent.source as TableView<*>)
                    // event is handled, consume it
                    keyEvent.consume()
                }
            }
        }
    }

    fun zoom(pane: Pane) {
        pane.onScroll = EventHandler { event ->
            var zoomFactor = 1.05
            val deltaY = event.deltaY
            if (deltaY < 0) {
                zoomFactor = 0.95
            }
            pane.scaleX = pane.scaleX * zoomFactor
            pane.scaleY = pane.scaleY * zoomFactor
            event.consume()
        }
    }

    companion object {
        private val numberFormatter = NumberFormat.getNumberInstance()

        /**
         * Install the keyboard handler:
         * + CTRL + C = copy to clipboard
         * + CTRL + V = paste to clipboard
         *
         * @param codeArea
         */
        @JvmStatic
        fun installKeyHandler(codeArea: CodeArea) {
            codeArea.onKeyPressed = TableKeyEventHandler()
            codeArea.addEventHandler(KeyEvent.KEY_PRESSED) { event: KeyEvent ->
                val copyRowKey = KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN)
                val moveRowUpKey = KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
                val moveRowDownKey = KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
                if (copyRowKey.match(event)) {
                    val row = codeArea.offsetToPosition(codeArea.caretPosition, TwoDimensional.Bias.Forward).major
                    val text = codeArea.text
                    val textRow = Arrays.stream(text.split("\n".toRegex()).toTypedArray())
                            .collect(Collectors.toList())[row]
                    val position = codeArea.caretPosition
                    codeArea.insertText(row, 0, """
     $textRow
     
     """.trimIndent())
                    codeArea.moveTo(position + textRow.length + 1)
                    event.consume()
                }
                if (moveRowUpKey.match(event)) {
                    val row = codeArea.offsetToPosition(codeArea.caretPosition, TwoDimensional.Bias.Forward).major
                    val text = codeArea.text
                    val list = Arrays.stream(text.split("\n".toRegex()).toTypedArray())
                            .collect(Collectors.toList())
                    val position = codeArea.caretPosition
                    codeArea.insertText(row - 1, 0, """
     ${list[row]}
     
     """.trimIndent())
                    codeArea.replaceText(row + 1, 0, row + 1, list[row].length + 1, "")
                    codeArea.moveTo(position - list[row - 1].length - 1)
                }
                if (moveRowDownKey.match(event)) {
                    val row = codeArea.offsetToPosition(codeArea.caretPosition, TwoDimensional.Bias.Forward).major
                    val text = codeArea.text
                    val list = Arrays.stream(text.split("\n".toRegex()).toTypedArray())
                            .collect(Collectors.toList())
                    val position = codeArea.caretPosition
                    codeArea.insertText(row + 2, 0, """
     ${list[row]}
     
     """.trimIndent())
                    codeArea.replaceText(row, 0, row, list[row].length + 1, "")
                    codeArea.moveTo(position + list[row + 1].length + 1)
                }
            }
        }

        /**
         * Get table selection and copy it to the clipboard.
         *
         * @param table
         */
        fun copySelectionToClipboard(table: TableView<*>) {
            val plainBuffer = StringBuilder()
            val htmlBuffer = StringBuilder()
            val positionList = table.selectionModel.selectedCells
            var prevRow = -1
            htmlBuffer.append("<html>\n<body>\n<table>\n")
            htmlBuffer.append(" <tr>\n")
            for (position in positionList) {
                val viewRow = position.row
                val viewCol = position.column

                // determine whether we advance in a row (tab) or a column
                // (newline).
                if (prevRow == viewRow) {
                    plainBuffer.append('\t')
                } else if (prevRow != -1) {
                    plainBuffer.append('\n')
                    htmlBuffer.append(" </tr>\n <tr>\n")
                }

                // create string from cell
                var text = ""

                // null-check: provide empty string for nulls
                when (val observableValue = table.getVisibleLeafColumn(viewCol).getCellObservableValue(viewRow) as Any) { // table position gives the view index => we need to operate on the view columns
                    is DoubleProperty -> { // TODO: handle boolean etc
                        text = numberFormatter.format(observableValue.get())
                    }
                    is IntegerProperty -> {
                        text = numberFormatter.format(observableValue.get().toLong())
                    }
                    is StringProperty -> {
                        text = observableValue.get()
                    }
                    else -> {
                        println("Unsupported observable value: $observableValue")
                    }
                }

                // add new item to clipboard
                plainBuffer.append(text)
                htmlBuffer.append("  <td>$text</td>\n")

                // remember previous
                prevRow = viewRow
            }
            htmlBuffer.append(" </tr>\n")
            htmlBuffer.append("</table>\n</body>\n</html>")

            // create clipboard content
            val clipboardContent = ClipboardContent()
            clipboardContent.putString(plainBuffer.toString())
            clipboardContent.putHtml(htmlBuffer.toString())
            println("ascii:\n$plainBuffer\n\nhtml:\n$htmlBuffer")

            // set clipboard content
            Clipboard.getSystemClipboard().setContent(clipboardContent)
        }

        fun pasteFromClipboard(table: TableView<*>) {

            // abort if there's not cell selected to start with
            if (table.selectionModel.selectedCells.size == 0) {
                return
            }

            // get the cell position to start with
            val pasteCellPosition = table.selectionModel.selectedCells[0]
            println("Pasting into cell $pasteCellPosition")
            val pasteString = Clipboard.getSystemClipboard().string
            println(pasteString)
            var rowClipboard = -1
            val rowTokenizer = StringTokenizer(pasteString, "\n")
            while (rowTokenizer.hasMoreTokens()) {
                rowClipboard++
                val rowString = rowTokenizer.nextToken()
                val columnTokenizer = StringTokenizer(rowString, "\t")
                var colClipboard = -1
                while (columnTokenizer.hasMoreTokens()) {
                    colClipboard++

                    // get next cell data from clipboard
                    val clipboardCellContent = columnTokenizer.nextToken()

                    // calculate the position in the table cell
                    val rowTable = pasteCellPosition.row + rowClipboard
                    val colTable = pasteCellPosition.column + colClipboard

                    // skip if we reached the end of the table
                    if (rowTable >= table.items.size) {
                        continue
                    }
                    if (colTable >= table.columns.size) {
                        continue
                    }

                    // System.out.println( rowClipboard + "/" + colClipboard + ": " + cell);

                    // get cell
                    val tableColumn = table.getVisibleLeafColumn(colTable) // table position gives the view index => we need to operate on the view columns
                    val observableValue = tableColumn.getCellObservableValue(rowTable)
                    println("$rowTable/$colTable: $observableValue")

                    // TODO: handle boolean, etc
                    if (observableValue is DoubleProperty) {
                        try {
                            val value = numberFormatter.parse(clipboardCellContent).toDouble()
                            observableValue.set(value)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    } else if (observableValue is IntegerProperty) {
                        try {
                            val value = NumberFormat.getInstance().parse(clipboardCellContent).toInt()
                            observableValue.set(value)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    } else if (observableValue is StringProperty) {
                        observableValue.set(clipboardCellContent)
                    } else {
                        println("Unsupported observable value: $observableValue")
                    }
                    println("$rowTable/$colTable")
                }
            }
        }
    }
}