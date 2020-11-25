package com.sergey.pisarev.model;

import javafx.scene.input.DragEvent;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;

public class File {

    public static java.io.File fileProgram;
    private static String encoding = String.valueOf(UTF_8);

    public static String getFileContent(DragEvent event) {

        List<java.io.File> file = event.getDragboard().getFiles();
        fileProgram = file.get(0);
        StringBuilder sb = new StringBuilder();
        checkEncoding(file.get(0));

        try {
            FileInputStream program = new FileInputStream(file.get(0));
            BufferedReader br = new BufferedReader(new InputStreamReader(program,encoding));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            program.close();
        } catch (IOException e) {
            out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static String getFileContent(java.io.File file) {
        StringBuilder sb = new StringBuilder();
        checkEncoding(file);

        try {
            FileInputStream program = new FileInputStream(file);

            BufferedReader br = new BufferedReader(new InputStreamReader(program, encoding));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            program.close();
        } catch (IOException e) {
            out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static void setFileContent(java.io.File file, String text) {
        Writer writer;
        try {
            writer = Files.newBufferedWriter(file.toPath(), Charset.forName(encoding));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<StringBuffer> getParameter(java.io.File path) {
        java.io.File folder = new java.io.File(path.getParent());
        java.io.File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (java.io.File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().contains("PAR")) {
                    String text = getFileContent(listOfFile);
                    return Arrays.stream(text.split("\n"))
                            .map(StringBuffer::new)
                            .collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }

    private static String guessEncoding(InputStream is){
        CharacterDetector characterDetector=CharacterDetector.getInstance();
        String encoding="";
        try {
            encoding=characterDetector.detect(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoding ;
    }

    private static void checkEncoding(java.io.File file){
        try {
            FileInputStream text = new FileInputStream(file);
            encoding=guessEncoding(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
