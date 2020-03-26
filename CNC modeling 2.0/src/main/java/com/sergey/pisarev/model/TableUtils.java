package com.sergey.pisarev.model;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.TwoDimensional;

public class TableUtils {

    private static NumberFormat numberFormatter = NumberFormat.getNumberInstance();


    /**
     * Install the keyboard handler:
     *   + CTRL + C = copy to clipboard
     *   + CTRL + V = paste to clipboard
     * @param codeArea
     */
    public static void installKeyHandler(CodeArea codeArea) {

        codeArea.setOnKeyPressed(new TableKeyEventHandler());

        codeArea.addEventHandler(KeyEvent.KEY_PRESSED,event -> {
            KeyCodeCombination copyRowKey = new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN);
            KeyCodeCombination moveRowUpKey = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
            KeyCodeCombination moveRowDownKey = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN);
            if (copyRowKey.match(event)) {
                int row = codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor();
                String text = codeArea.getText();
                String textRow = Arrays.stream(text.split("\n"))
                        .collect(Collectors.toList())
                        .get(row);
                int position = codeArea.getCaretPosition();
                codeArea.insertText(row, 0, textRow + "\n");
                codeArea.moveTo(position + textRow.length() + 1);
                event.consume();
            }
            if (moveRowUpKey.match(event)) {
                int row = codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor();
                String text = codeArea.getText();
                List<String>list = Arrays.stream(text.split("\n"))
                        .collect(Collectors.toList());
                int position = codeArea.getCaretPosition();
                codeArea.insertText(row - 1, 0, list.get(row) + "\n");
                codeArea.replaceText(row+1,0,row+1,list.get(row).length()+1,"");
                codeArea.moveTo(position - list.get(row-1).length()-1 );
            }
            if (moveRowDownKey.match(event)) {
                int row = codeArea.offsetToPosition(codeArea.getCaretPosition(), TwoDimensional.Bias.Forward).getMajor();
                String text = codeArea.getText();
                List<String>list = Arrays.stream(text.split("\n"))
                        .collect(Collectors.toList());
                int position = codeArea.getCaretPosition();
                codeArea.insertText(row + 2, 0, list.get(row) + "\n");
                codeArea.replaceText(row,0,row,list.get(row).length()+1,"");
                codeArea.moveTo(position + list.get(row+1).length()+1 );
            }
        });
    }

    /**
     * Copy/Paste keyboard event handler.
     * The handler uses the keyEvent's source for the clipboard data. The source must be of type TableView.
     */
    public static class TableKeyEventHandler implements EventHandler<KeyEvent> {

        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        KeyCodeCombination pasteKeyCodeCompination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);

        public void handle(final KeyEvent keyEvent) {

            if (copyKeyCodeCompination.match(keyEvent)) {

                if( keyEvent.getSource() instanceof TableView) {
                    System.out.println("copy");
                    // copy to clipboard
                    copySelectionToClipboard( (TableView<?>) keyEvent.getSource());

                    // event is handled, consume it
                    keyEvent.consume();

                }

            }
            else if (pasteKeyCodeCompination.match(keyEvent)) {

                if( keyEvent.getSource() instanceof TableView) {

                    // copy to clipboard
                    pasteFromClipboard( (TableView<?>) keyEvent.getSource());

                    // event is handled, consume it
                    keyEvent.consume();
                }
            }
        }
    }

    /**
     * Get table selection and copy it to the clipboard.
     * @param table
     */
    public static void copySelectionToClipboard(TableView<?> table) {

        StringBuilder plainBuffer = new StringBuilder();
        StringBuilder htmlBuffer = new StringBuilder();

        ObservableList<TablePosition> positionList = table.getSelectionModel().getSelectedCells();

        int prevRow = -1;

        htmlBuffer.append( "<html>\n<body>\n<table>\n");

        htmlBuffer.append( " <tr>\n");

        for (TablePosition position : positionList) {

            int viewRow = position.getRow();
            int viewCol = position.getColumn();

            // determine whether we advance in a row (tab) or a column
            // (newline).
            if (prevRow == viewRow) {

                plainBuffer.append('\t');

            } else if (prevRow != -1) {

                plainBuffer.append('\n');
                htmlBuffer.append( " </tr>\n <tr>\n");
            }

            // create string from cell
            String text = "";

            Object observableValue = (Object) table.getVisibleLeafColumn(viewCol).getCellObservableValue( viewRow); // table position gives the view index => we need to operate on the view columns

            // null-check: provide empty string for nulls
            if (observableValue == null) {
                text = "";
            }
            else if( observableValue instanceof DoubleProperty) { // TODO: handle boolean etc

                text = numberFormatter.format( ((DoubleProperty) observableValue).get());

            }
            else if( observableValue instanceof IntegerProperty) {

                text = numberFormatter.format( ((IntegerProperty) observableValue).get());

            }
            else if( observableValue instanceof StringProperty) {

                text = ((StringProperty) observableValue).get();

            }
            else {
                System.out.println("Unsupported observable value: " + observableValue);
            }

            // add new item to clipboard
            plainBuffer.append(text);
            htmlBuffer.append( "  <td>" + text + "</td>\n");

            // remember previous
            prevRow = viewRow;
        }

        htmlBuffer.append( " </tr>\n");
        htmlBuffer.append( "</table>\n</body>\n</html>");

        // create clipboard content
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(plainBuffer.toString());
        clipboardContent.putHtml(htmlBuffer.toString());

        System.out.println("ascii:\n" + plainBuffer.toString() + "\n\nhtml:\n" + htmlBuffer.toString());

        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);


    }

    public static void pasteFromClipboard( TableView<?> table) {

        // abort if there's not cell selected to start with
        if( table.getSelectionModel().getSelectedCells().size() == 0) {
            return;
        }

        // get the cell position to start with
        TablePosition pasteCellPosition = table.getSelectionModel().getSelectedCells().get(0);

        System.out.println("Pasting into cell " + pasteCellPosition);

        String pasteString = Clipboard.getSystemClipboard().getString();

        System.out.println(pasteString);

        int rowClipboard = -1;

        StringTokenizer rowTokenizer = new StringTokenizer( pasteString, "\n");
        while( rowTokenizer.hasMoreTokens()) {

            rowClipboard++;

            String rowString = rowTokenizer.nextToken();

            StringTokenizer columnTokenizer = new StringTokenizer( rowString, "\t");

            int colClipboard = -1;

            while( columnTokenizer.hasMoreTokens()) {

                colClipboard++;

                // get next cell data from clipboard
                String clipboardCellContent = columnTokenizer.nextToken();

                // calculate the position in the table cell
                int rowTable = pasteCellPosition.getRow() + rowClipboard;
                int colTable = pasteCellPosition.getColumn() + colClipboard;

                // skip if we reached the end of the table
                if( rowTable >= table.getItems().size()) {
                    continue;
                }
                if( colTable >= table.getColumns().size()) {
                    continue;
                }

                // System.out.println( rowClipboard + "/" + colClipboard + ": " + cell);

                // get cell
                TableColumn tableColumn = table.getVisibleLeafColumn(colTable);  // table position gives the view index => we need to operate on the view columns
                ObservableValue observableValue = tableColumn.getCellObservableValue(rowTable);

                System.out.println( rowTable + "/" + colTable + ": " +observableValue);

                // TODO: handle boolean, etc
                if( observableValue instanceof DoubleProperty) {

                    try {

                        double value = numberFormatter.parse(clipboardCellContent).doubleValue();
                        ((DoubleProperty) observableValue).set(value);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else if( observableValue instanceof IntegerProperty) {

                    try {

                        int value = NumberFormat.getInstance().parse(clipboardCellContent).intValue();
                        ((IntegerProperty) observableValue).set(value);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else if( observableValue instanceof StringProperty) {

                    ((StringProperty) observableValue).set(clipboardCellContent);

                } else {

                    System.out.println("Unsupported observable value: " + observableValue);

                }

                System.out.println(rowTable + "/" + colTable);
            }

        }

    }

}