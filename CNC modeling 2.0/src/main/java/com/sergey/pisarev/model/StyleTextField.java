package com.sergey.pisarev.model;

import com.sergey.pisarev.controller.Controller;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleTextField {


    public void setStyle(CodeArea codeArea){
        codeArea.getStylesheets().add(Objects.requireNonNull(Controller.class.getClassLoader().getResource("java-keywords.css")).toExternalForm());
        Subscription cleanupWhenNoLongerNeedIt = codeArea
                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))
                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
    }

    private static final String[] KEYWORDS = new String[] {
            "BORE_DIAM", "WHEEL_UNMACHINED", "WHEEL_MACHINED", "SYM_FACTOR", "TREAD_HEIGHT_S1",
            "TREAD_HEIGHT_S2", "GLOBAL_ALLOWANCE", "TREAD_ALLOWANCE", "TREAD_DIAM", "VYLET_ST",
            "STUPICA_VNUT", "STUPICA_NAR", "DISK_VNUT", "DISK_NAR",
            "YABLOKO_VNUT", "YABLOKO_NAR", "WHEEL_HEIGHT", "N_GANTRYPOS_X", "N_GANTRYPOS_Z",
            "N_WHEEL_UNMACHINED", "N_WHEEL_MACHINED", "N_SYM_FACTOR"
    };

    private static final String[] AXIS = new String[] {
            "X", "Z", "U" ,"W", "CR"
    };

    private static final String[] GCODE= new String[] {
            "G0", "G00", "G1" ,"G01", "G2","G02","G3","G03","G4",
            "G04","G40","G41","G42","G54","G55","G56","G57","G58","G60",
            "G64","G640","G90","G91","G95","G96","G97","G603","G641","G153"
    };


    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String AXIS_PATTERN = "\\b(" + String.join("|", AXIS) + ")\\b";
    private static final String GCODE_PATTERN = "\\b(" + String.join("|", GCODE) + ")\\b";
    private static final String FIGURES_PATTERN = "(?:[^\\w_]|^|\\b)(\\d+)";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\+|\\-|\\*|\\/";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = ";[^\n]*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<AXIS>" + AXIS_PATTERN + ")"
                    + "|(?<GCODE>" + GCODE_PATTERN + ")"
                    + "|(?<FIGURES>" + FIGURES_PATTERN + ")"
    );


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            matcher.group("AXIS") != null ? "axis" :
                                                                                 matcher.group("GCODE") != null ? "g_code" :
                                                                                      matcher.group("FIGURES") != null ? "figures" :
                                                                                          null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
