package com.sergey.pisarev.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Convert {

    private String y = "", z = "", angle = "";
    private final String UGOL="UGOL";
    private final String Y_0="Y_0";
    private final String Z_0="Z_0";
    private final String E_TCARR ="E_TCARR";
    private final String E_HEAD ="E_HEAD";

    public String convertAviaProgram(String aviaProgram) {
        List<StringBuffer> programList = new ArrayList<>();
        List<StringBuffer> list;
        AtomicReference<String> atomicY = new AtomicReference<>("");
        AtomicReference<String> atomicZ = new AtomicReference<>("");
        AtomicReference<String> atomicAngle = new AtomicReference<>("");

        list = Arrays.stream(aviaProgram.split("\n"))
                .filter(frame -> !frame.contains("IF(R0"))
                .filter(frame -> !frame.contains("M50"))
                .filter(frame -> !frame.contains("ENDIF"))
                .filter(frame -> !frame.contains("R0=R0"))
                .filter(frame -> !frame.equals(""))
                .map(StringBuffer::new)
                .collect(Collectors.toList());

        list.stream()
                .filter(frame -> frame.indexOf(E_TCARR) != -1)
                .findFirst()
                .ifPresent(frame -> {
                    searchCoordinate(frame);
                    atomicY.set(y);
                    atomicZ.set(z);
                    atomicAngle.set(angle);
                });

        int count = (int) list.stream()
                .filter(frame -> frame.indexOf(E_TCARR) != -1)
                .count();

        int countCheck = 1;
        for (StringBuffer frame : list) {
            if (frame.indexOf(E_TCARR) != -1 && count != countCheck) {
                countCheck++;
                if (frame.indexOf(atomicY.get()) != -1) {
                    frame.replace(frame.indexOf(atomicY.get()), frame.indexOf(atomicY.get()) + atomicY.get().length(), Y_0);
                }
                if (frame.indexOf(atomicZ.get()) != -1) {
                    frame.replace(frame.indexOf(atomicZ.get()), frame.indexOf(atomicZ.get()) + atomicZ.get().length(), Z_0);
                }
                if (frame.indexOf(atomicAngle.get()) != -1) {
                    frame.replace(frame.indexOf(atomicAngle.get()), frame.indexOf(atomicAngle.get()) + atomicAngle.get().length(), UGOL);
                }
            } else if (frame.indexOf(E_TCARR) != -1 && count == countCheck) {
                searchCoordinate(frame);
                if (frame.indexOf(y) != -1) {
                    frame.replace(frame.indexOf(y), frame.indexOf(y) + y.length(), "0");
                }
                if (frame.indexOf(z) != -1) {
                    frame.replace(frame.indexOf(z), frame.indexOf(z) + z.length(), "0");
                }
                if (frame.indexOf(angle) != -1) {
                    frame.replace(frame.indexOf(angle), frame.indexOf(angle) + angle.length(), "0");
                }
            }
        }

        StringBuffer tempAngle=new StringBuffer(UGOL+"=           ;Угол сверления");
        StringBuffer tempY=new StringBuffer(Y_0+"=            ;Координата заходной точки по Y");
        StringBuffer tempZ=new StringBuffer(Z_0+"=            ;Координата заходной точки по Z");

        countCheck = 0;
        for (StringBuffer frame : list) {
            if (frame.indexOf(E_TCARR) != -1) countCheck++;
            if (frame.indexOf(E_HEAD) != -1) {
                programList.add(new StringBuffer("EXTERN START_SHNEK(INT)"));
                programList.add(new StringBuffer("DEF REAL "+UGOL+","+Y_0+","+Z_0));
                programList.add(frame);
                programList.add(new StringBuffer(";**********************************************"));
                programList.add(new StringBuffer(new StringBuffer(tempAngle.replace(UGOL.length()+1,UGOL.length()+1+atomicAngle.get().length(), atomicAngle.get()))));
                programList.add(new StringBuffer(new StringBuffer(tempY.replace(Y_0.length()+1,Y_0.length()+1+atomicY.get().length(), atomicY.get()))));
                programList.add(new StringBuffer(new StringBuffer(tempZ.replace(Y_0.length()+1,Y_0.length()+1+atomicZ.get().length(), atomicZ.get()))));
                programList.add(new StringBuffer(";**********************************************"));
                programList.add(new StringBuffer("START_SHNEK(10) ;Запускает транспортер каждое n колесо"));
            } else if (frame.indexOf(E_TCARR) != -1 && frame.indexOf(UGOL) == -1 && count != countCheck) {
                //удаляем лишнее кадры
            } else programList.add(frame);
        }
        StringBuffer program = new StringBuffer();
        programList.forEach(frame -> program.append(frame.toString()).append("\n"));
        return program.toString();
    }

    private void searchCoordinate(StringBuffer frame) {
        char[] chArray = frame.toString().toCharArray();
        int n = 0;
        StringBuilder number;
        for (int i = 0; i < chArray.length; i++) {
            if (chArray[i] == ',') {
                n++;
                switch (n) {
                    case 6:
                        number = new StringBuilder();
                        for (int j = i + 1; j < chArray.length; j++) {
                            if (chArray[j] != ',') {
                                number.append(chArray[j]);
                            } else break;
                        }
                        y = number.toString();
                        break;
                    case 7:
                        number = new StringBuilder();
                        for (int j = i + 1; j < chArray.length; j++) {
                            if (chArray[j] != ',') {
                                number.append(chArray[j]);
                            } else break;
                        }
                        z = number.toString();
                        break;
                    case 9:
                        number = new StringBuilder();
                        for (int j = i + 1; j < chArray.length; j++) {
                            if (chArray[j] != ',') {
                                number.append(chArray[j]);
                            } else break;
                        }
                        angle = number.toString();
                        break;
                }
            }
        }
    }
}
