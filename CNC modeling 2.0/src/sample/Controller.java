package sample;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    private CodeArea codeAreaProgram=new CodeArea();
    private CodeArea codeAreaParameter=new CodeArea();


    @FXML
    AnchorPane anchorPaneProgram=new AnchorPane();

    @FXML
    AnchorPane anchorPaneParameter=new AnchorPane();


    private static final String[] KEYWORDS = new String[] {
            "BORE_DIAM", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\+";
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
                                                                            null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void initialize(){
        codeAreaProgram.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaProgram));
        StackPane stackPaneProgram = new StackPane(new VirtualizedScrollPane<>(codeAreaProgram));
        AnchorPane.setTopAnchor(stackPaneProgram,0.0);
        AnchorPane.setBottomAnchor(stackPaneProgram,0.0);
        AnchorPane.setLeftAnchor(stackPaneProgram,0.0);
        AnchorPane.setRightAnchor(stackPaneProgram,0.0);
        anchorPaneProgram.getChildren().add(stackPaneProgram);
        codeAreaProgram.getStylesheets().add(Controller.class.getResource("java-keywords.css").toExternalForm());


        Subscription cleanupWhenNoLongerNeedIt = codeAreaProgram

                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> codeAreaProgram.setStyleSpans(0, computeHighlighting(codeAreaProgram.getText())));



        codeAreaParameter.setParagraphGraphicFactory(LineNumberFactory.get(codeAreaParameter));
        StackPane stackPaneParameter = new StackPane(new VirtualizedScrollPane<>(codeAreaParameter));
        AnchorPane.setTopAnchor(stackPaneParameter,0.0);
        AnchorPane.setBottomAnchor(stackPaneParameter,0.0);
        AnchorPane.setLeftAnchor(stackPaneParameter,0.0);
        AnchorPane.setRightAnchor(stackPaneParameter,0.0);
        anchorPaneParameter.getChildren().add(stackPaneParameter);
    }

    @FXML
    public void handleDragOverProgram(DragEvent event){
        if(event.getDragboard().hasFiles())
        event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    public void handleDragOverParameter(DragEvent event){
        if(event.getDragboard().hasFiles())
            event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    public void handleDragProgram(DragEvent event){
        String text= sample.File.getFileContent(event);
        codeAreaProgram.clear();
        codeAreaProgram.appendText(text);


    }

    @FXML
    public void handleDragParameter(DragEvent event){
        String text= sample.File.getFileContent(event);
        codeAreaParameter.clear();
        codeAreaParameter.appendText(text);
    }

    @FXML
    public void onMouseClickedProgram(Event event){

    }
}
