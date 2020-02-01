package com.sergey.pisarev.model;

import javafx.scene.input.DragEvent;

import java.io.*;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class File {

    public static String getFileContent( DragEvent event ) {
        StringBuilder sb = new StringBuilder();
        try {
            List<java.io.File> file=event.getDragboard().getFiles();
            FileInputStream program=new FileInputStream(file.get(0));
            BufferedReader br = new BufferedReader( new InputStreamReader(program, UTF_8.name()));

                String line;
                while(( line = br.readLine()) != null ) {
                    sb.append( line );
                    sb.append( '\n' );
                }

        }catch ( IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }

    public static String getFileContent( String path ) {
        StringBuilder sb = new StringBuilder();
        try {
            java.io.File file=new java.io.File(path);
            FileInputStream program=new FileInputStream(file);
            BufferedReader br = new BufferedReader( new InputStreamReader(program, UTF_8.name()));

            String line;
            while(( line = br.readLine()) != null ) {
                sb.append( line );
                sb.append( '\n' );
            }

        }catch ( IOException e){
            System.out.println(e.getMessage());
        }
        return sb.toString();
    }
}
