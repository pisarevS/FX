package com.sergey.pisarev.model

import org.fxmisc.richtext.CodeArea
import java.util.Objects
import com.sergey.pisarev.controller.MainController
import org.reactfx.Subscription
import org.fxmisc.richtext.model.PlainTextChange
import com.sergey.pisarev.model.StyleText
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.layout.AnchorPane
import java.util.Arrays
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import java.time.Duration
import java.util.regex.Pattern

object StyleText {
    fun setStyle(codeArea: CodeArea) {
        codeArea.stylesheets.add(Objects.requireNonNull(MainController::class.java.classLoader.getResource("g_code_keywords.css")).toExternalForm())
        val cleanupWhenNoLongerNeedIt = codeArea // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges() // do not emit an event until 1 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(100)) // run the following code block when previous stream emits an event
                .subscribe { ignore: List<PlainTextChange?>? -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.text)) }
    }

    fun setListener(codeArea: CodeArea, anchorPaneProgram: AnchorPane) {
        val fontSize = intArrayOf(16)
        anchorPaneProgram.addEventHandler(KeyEvent.KEY_PRESSED) { event: KeyEvent? ->
            val ctrlPlus: KeyCombination = KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN)
            val ctrlMimus: KeyCombination = KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN)
            val ctrlDefault: KeyCombination = KeyCodeCombination(KeyCode.DIGIT0, KeyCombination.CONTROL_DOWN)
            if (ctrlPlus.match(event)) {
                if (fontSize[0] < 30) {
                    fontSize[0]++
                    codeArea.style = "-fx-font-size: " + fontSize[0] + "px"
                }
            }
            if (ctrlMimus.match(event)) {
                if (fontSize[0] > 10) {
                    fontSize[0]--
                    codeArea.style = "-fx-font-size: " + fontSize[0] + "px"
                }
            }
            if (ctrlDefault.match(event)) {
                fontSize[0] = 16
                codeArea.style = "-fx-font-size: " + fontSize[0] + "px"
            }
        }
    }
    
    fun setStyleRefresh(codeArea: CodeArea) {
        codeArea.stylesheets.add(Objects.requireNonNull(MainController::class.java.classLoader.getResource("g_code_keywords.css")).toExternalForm())
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.text))
    }

    val KEYWORDS = Arrays.asList("DIAM_KANAV", "BORE_DIAM", "WHEEL_UNMACHINED",
            "WHEEL_MACHINED", "SYM_FACTOR", "TREAD_HEIGHT_S1",
            "TREAD_HEIGHT_S2", "GLOBAL_ALLOWANCE", "TREAD_ALLOWANCE",
            "TREAD_DIAM", "VYLET_ST", "STUPICA_VNUT", "STUPICA_NAR",
            "DISK_VNUT", "DISK_NAR", "YABLOKO_VNUT", "YABLOKO_NAR",
            "WHEEL_HEIGHT", "N_GANTRYPOS_X", "N_GANTRYPOS_Z", "N_WHEEL_UNMACHINED",
            "N_WHEEL_MACHINED", "N_SYM_FACTOR", "UGOL", "Y_0", "Z_0", "START_SHNEK", "SHIRINA_GREB")
    val WAITM = Arrays.asList("N_WAITM", "SETM")
    val LIMS = Arrays.asList("LIMS", "E_TCARR")
    private val AXIS = arrayOf(
            "X", "Z", "U", "W", "CR", "F", "RND"
    )
    private val KEYWORD_PATTERN = "\\b(" + java.lang.String.join("|", KEYWORDS) + ")\\b"
    private val WAITM_PATTERN = "\\b(" + java.lang.String.join("|", WAITM) + ")\\b"
    private val LIMS_PATTERN = "\\b(" + java.lang.String.join("|", LIMS) + ")\\b"
    private const val R_PARAMETER = "R(\\d+)"
    private val AXIS_PATTERN = "\\b(" + java.lang.String.join("|", *AXIS) + ")\\b"
    private const val GCODE_PATTERN = "G(\\d+)"
    private const val MCODE_PATTERN = "M(\\d+)"
    private const val FIGURES_PATTERN = "(?:[^\\w_]|^|\\b)(\\d+)"
    private const val PAREN_PATTERN = "\\(|\\)"
    private const val BRACE_PATTERN = "\\{|\\}"
    private const val NUMBER_FRAME_PATTERN = "N(\\d+)"
    private const val SEMICOLON_PATTERN = "\\+|\\-|\\*|\\/|\\="
    private const val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
    private const val COMMENT_PATTERN = ";[^\n]*"
    private val PATTERN = Pattern.compile(
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
    )

    private fun computeHighlighting(text: String): StyleSpans<Collection<String?>> {
        val matcher = PATTERN.matcher(text)
        var lastKwEnd = 0
        val spansBuilder = StyleSpansBuilder<Collection<String?>>()
        while (matcher.find()) {
            val styleClass = (if (matcher.group("KEYWORD") != null) "keyword" else if (matcher.group("WAITM") != null) "waitm" else if (matcher.group("LIMS") != null) "lims" else if (matcher.group("RPARAMETER") != null) "rparameter" else if (matcher.group("PAREN") != null) "paren" else if (matcher.group("BRACE") != null) "brace" else if (matcher.group("NUMBERFRAME") != null) "number_frame" else if (matcher.group("SEMICOLON") != null) "semicolon" else if (matcher.group("STRING") != null) "string" else if (matcher.group("COMMENT") != null) "comment" else if (matcher.group("AXIS") != null) "axis" else if (matcher.group("GCODE") != null) "g_code" else if (matcher.group("MCODE") != null) "m_code" else if (matcher.group("FIGURES") != null) "figures" else null)!! /* never happens */
            spansBuilder.add(emptyList<String>(), matcher.start() - lastKwEnd)
            spansBuilder.add(setOf<String?>(styleClass), matcher.end() - matcher.start())
            lastKwEnd = matcher.end()
        }
        spansBuilder.add(emptyList<String>(), text.length - lastKwEnd)
        return spansBuilder.create()
    }
}