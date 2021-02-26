package com.sergey.pisarev.model;

import javafx.scene.input.DragEvent;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MyFile {

    public static File filePath;
    private static String encoding = String.valueOf(UTF_8);

    public static String getFileContent(DragEvent event) {
        List<File> file = event.getDragboard().getFiles();
        filePath = file.get(0);
        StringBuilder sb = new StringBuilder();
        checkEncoding(file.get(0));
        try {
            Files.lines(file.get(0).toPath(), Charset.forName(encoding))
                    .peek(line -> sb.append(line).append('\n'))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String getFileContent() {
        StringBuilder sb = new StringBuilder();
        checkEncoding(filePath);
        try {
            Files.lines(filePath.toPath(), Charset.forName(encoding))
                    .peek(line -> sb.append(line).append('\n'))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static List<StringBuilder> getFileContent(File file) {
        List<StringBuilder> listFrame = new ArrayList<>();
        checkEncoding(file);
        try {
            listFrame = Files.lines(file.toPath(), Charset.forName(encoding))
                    .map(StringBuilder::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listFrame;
    }

    public static void setFileContent(File file, String text) {
        try {
            writer(file, text, Charset.forName(encoding));
        } catch (IOException e) {
            try {
                writer(file, text, UTF_8);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private static void writer(File file, String text, Charset cs) throws IOException {
        Writer writer = Files.newBufferedWriter(file.toPath(), cs);
        writer.write(text);
        writer.close();
    }

    public static List<StringBuilder> getParameter(File path) {
        File folder = new File(path.getParent());
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().contains("PAR")) {
                    return getFileContent(listOfFile);
                }
            }
        }
        return new ArrayList<>();
    }

    private static String guessEncoding(InputStream is) {
        CharacterDetector characterDetector = CharacterDetector.getInstance();
        String encoding = "";
        try {
            encoding = characterDetector.detect(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoding;
    }

    private static void checkEncoding(File file) {
        if (file != null) {
            try {
                FileInputStream text = new FileInputStream(file);
                encoding = guessEncoding(text);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
