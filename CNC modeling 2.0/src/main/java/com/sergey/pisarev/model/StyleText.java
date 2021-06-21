package com.sergey.pisarev.model;

import com.sergey.pisarev.controller.MainController;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleText {

    public static void setStyle(CodeArea codeArea) {
        codeArea.getStylesheets().add(Objects.requireNonNull(MainController.class.getClassLoader().getResource("g_code_keywords.css")).toExternalForm());
        Subscription cleanupWhenNoLongerNeedIt = codeArea
                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 1 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(100))
                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
    }

    public static void setListener(CodeArea codeArea, AnchorPane anchorPaneProgram){
        final int[] fontSize = {16};
        anchorPaneProgram.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCombination ctrlPlus = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_ANY);
            KeyCombination ctrlMimus = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_ANY);
            KeyCombination ctrlDefault = new KeyCodeCombination(KeyCode.MULTIPLY, KeyCombination.CONTROL_ANY);
            if (ctrlPlus.match(event)) {
                if (fontSize[0] < 30) {
                    fontSize[0]++;
                    codeArea.setStyle("-fx-font-size: " + fontSize[0] + "px");
                }
            }
            if (ctrlMimus.match(event)) {
                if (fontSize[0] > 11) {
                    fontSize[0]--;
                    codeArea.setStyle("-fx-font-size: " + fontSize[0] + "px");
                }
            }
            if (ctrlDefault.match(event)) {
                fontSize[0] = 16;
                codeArea.setStyle("-fx-font-size: " + fontSize[0] + "px");

            }
        });
    }

    public static void setStyleRefresh(CodeArea codeArea) {
        codeArea.getStylesheets().add(Objects.requireNonNull(MainController.class.getClassLoader().getResource("g_code_keywords.css")).toExternalForm());
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
    }

    public static final List<String> KEYWORDS = Arrays.asList("DIAM_KANAV", "BORE_DIAM", "WHEEL_UNMACHINED",
            "WHEEL_MACHINED", "SYM_FACTOR", "TREAD_HEIGHT_S1",
            "TREAD_HEIGHT_S2", "GLOBAL_ALLOWANCE", "TREAD_ALLOWANCE",
            "TREAD_DIAM", "VYLET_ST", "STUPICA_VNUT", "STUPICA_NAR",
            "DISK_VNUT", "DISK_NAR", "YABLOKO_VNUT", "YABLOKO_NAR",
            "WHEEL_HEIGHT", "N_GANTRYPOS_X", "N_GANTRYPOS_Z", "N_WHEEL_UNMACHINED",
            "N_WHEEL_MACHINED", "N_SYM_FACTOR", "UGOL", "Y_0", "Z_0", "START_SHNEK", "SHIRINA_GREB");

    public static final List<String> WAITM = Arrays.asList("N_WAITM", "SETM");

    public static final List<String> LIMS = Arrays.asList("LIMS", "E_TCARR");

    private static final String[] AXIS = new String[]{
            "X", "Z", "U", "W", "CR", "F", "RND"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String WAITM_PATTERN = "\\b(" + String.join("|", WAITM) + ")\\b";
    private static final String LIMS_PATTERN = "\\b(" + String.join("|", LIMS) + ")\\b";
    private static final String R_PARAMETER = "R(\\d+)";
    private static final String AXIS_PATTERN = "\\b(" + String.join("|", AXIS) + ")\\b";
    private static final String GCODE_PATTERN = "G(\\d+)";
    private static final String MCODE_PATTERN = "M(\\d+)";
    private static final String FIGURES_PATTERN = "(?:[^\\w_]|^|\\b)(\\d+)";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String NUMBER_FRAME_PATTERN = "N(\\d+)";
    private static final String SEMICOLON_PATTERN = "\\+|\\-|\\*|\\/|\\=";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = ";[^\n]*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<WAITM>" + WAITM_PATTERN + ")"
                    + "|(?<LIMS>" + LIMS_PATTERN + ")"
                    + "|(?<RPARAMETER>" + R_PARAMETER + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<NUMBERFRAME>" + NUMBER_FRAME_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<AXIS>" + AXIS_PATTERN + ")"
                    + "|(?<GCODE>" + GCODE_PATTERN + ")"
                    + "|(?<MCODE>" + MCODE_PATTERN + ")"
                    + "|(?<FIGURES>" + FIGURES_PATTERN + ")"
    );


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("WAITM") != null ? "waitm" :
                                    matcher.group("LIMS") != null ? "lims" :
                                            matcher.group("RPARAMETER") != null ? "rparameter" :
                                                    matcher.group("PAREN") != null ? "paren" :
                                                            matcher.group("BRACE") != null ? "brace" :
                                                                    matcher.group("NUMBERFRAME") != null ? "number_frame" :
                                                                            matcher.group("SEMICOLON") != null ? "semicolon" :
                                                                                    matcher.group("STRING") != null ? "string" :
                                                                                            matcher.group("COMMENT") != null ? "comment" :
                                                                                                    matcher.group("AXIS") != null ? "axis" :
                                                                                                            matcher.group("GCODE") != null ? "g_code" :
                                                                                                                    matcher.group("MCODE") != null ? "m_code" :
                                                                                                                            matcher.group("FIGURES") != null ? "figures" :
                                                                                                                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
