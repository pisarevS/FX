package com.sergey.pisarev.model;

import javafx.scene.input.DragEvent;

import java.io.*;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class File {

    public static java.io.File fileProgram;
    public static java.io.File fileParameter;

    public static String getFileContent( DragEvent event,String key ) {
        List<java.io.File> file=event.getDragboard().getFiles();
        if(key.equals("program")){
            fileProgram=file.get(0);
        }else if(key.equals("parameter")){
            fileParameter=file.get(0);
        }
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream program=new FileInputStream(file.get(0));
            BufferedReader br = new BufferedReader( new InputStreamReader(program, UTF_8.name()));

                String line;
                while(( line = br.readLine()) != null ) {
                    sb.append( line );
                    sb.append( '\n' );
                }
                program.close();
        }catch ( IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static String getFileContent( java.io.File file ) {

        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream program=new FileInputStream(file);
            BufferedReader br = new BufferedReader( new InputStreamReader(program, UTF_8.name()));

            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }
            program.close();
        }catch ( IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static void setFileContent(java.io.File file, String text ) {
        Writer writer=null;
        try {
            writer=new BufferedWriter(new FileWriter(file));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(text.getBytes());
            fileOutputStream.close();
        }catch ( IOException e){
            System.out.println(e.getMessage());
        }*/
    }
}
