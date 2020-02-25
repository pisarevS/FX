package com.sergey.pisarev.model;

import javafx.scene.input.DragEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;

public class File {

    public static java.io.File fileProgram;

    public static String getFileContent(DragEvent event) {
        List<java.io.File> file = event.getDragboard().getFiles();
        fileProgram = file.get(0);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream program = new FileInputStream(file.get(0));
            BufferedReader br = new BufferedReader(new InputStreamReader(program, UTF_8.name()));

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
        try {
            FileInputStream program = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(program, UTF_8.name()));

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
            writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<StringBuffer> getParameter(java.io.File path) {
        java.io.File folder = new java.io.File(path.getParent());
        java.io.File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (java.io.File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().contains("PAR")) {
                    return Program.getList(getFileContent(new java.io.File(listOfFile.getPath())));
                }
            }
        }
        return new ArrayList<>();
    }
}
