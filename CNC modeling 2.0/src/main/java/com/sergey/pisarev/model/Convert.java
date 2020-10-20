package com.sergey.pisarev.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Convert {

    private String y="",z="",angle="";

    public String convertAviaProgram(String aviaProgram){
        List<StringBuffer> programList=new ArrayList<>();
        List<StringBuffer> list;
        String y="",z="",angle="";

        list = Arrays.stream(aviaProgram.split("\n"))
                .map(StringBuffer::new)
                .collect(Collectors.toList());

        for (StringBuffer frame:list) {
            if(frame.indexOf("E_TCARR")!=-1){
                searchPlane(frame);
                y=this.y;
                z=this.z;
                angle=this.angle;
                break;
            }
        }

        int count = (int) list.stream().filter(frame -> frame.indexOf("E_TCARR") != -1).count();
        int countCheck=1;
        for (StringBuffer frame:list){
            if(frame.indexOf("E_TCARR")!=-1&&count!=countCheck){
                countCheck++;
                if(frame.indexOf(y)!=-1){
                    frame.replace(frame.indexOf(y),frame.indexOf(y)+y.length(),"Y_0");
                }
                if(frame.indexOf(z)!=-1){
                    frame.replace(frame.indexOf(z),frame.indexOf(z)+z.length(),"Z_0");
                }
                if(frame.indexOf(angle)!=-1) {
                    frame.replace(frame.indexOf(angle), frame.indexOf(angle) + angle.length(), "UGOL");
                }
            }else if(frame.indexOf("E_TCARR")!=-1&&count==countCheck) {
                searchPlane(frame);
                if(frame.indexOf(this.y)!=-1){
                    frame.replace(frame.indexOf(this.y),frame.indexOf(this.y)+y.length(),"0");
                }
                if(frame.indexOf(this.z)!=-1){
                    frame.replace(frame.indexOf(this.z),frame.indexOf(this.z)+z.length(),"0");
                }
                if(frame.indexOf(this.angle)!=-1) {
                    frame.replace(frame.indexOf(this.angle), frame.indexOf(this.angle) + this.angle.length(), "0");
                }
            }
        }

        countCheck=0;
        for (StringBuffer frame:list) {
            if(frame.indexOf("E_TCARR") != -1) countCheck++;
            if(frame.indexOf("E_HEAD")!=-1){
                programList.add(new StringBuffer("EXTERN START_SHNEK(INT)"));
                programList.add(new StringBuffer("DEF REAL UGOL,Y_0,Z_0"));
                programList.add(frame);
                programList.add(new StringBuffer(";**********************************"));
                programList.add(new StringBuffer("UGOL="+angle+"            ;Угол сверления"));
                programList.add(new StringBuffer("Y_0="+y+"         ;Координата заходной точки по Y"));
                programList.add(new StringBuffer("Z_0="+z+"         ;Координата заходной точки по Z"));
                programList.add(new StringBuffer(";**********************************"));
                programList.add(new StringBuffer("START_SHNEK(10) ;Запускает транспортер каждое n колесо"));
            }else if(frame.indexOf("E_TCARR") != -1&&frame.indexOf("UGOL") == -1&&count!=countCheck){
                //удаляем лишнее кадры
            } else if(frame.indexOf("IF(R0") != -1){
                //удаляем лишнее кадры
            }else if(frame.indexOf("M50") != -1){
                //удаляем лишнее кадры
            }else if(frame.indexOf("ENDIF") != -1){
                //удаляем лишнее кадры
            }else if(frame.indexOf("R0=R0") != -1){
                //удаляем лишнее кадры
            }else if(frame.toString().equals("")){
                //удаляем лишнее кадры
            }
            else programList.add(frame);
        }
        StringBuilder program=new StringBuilder();
        programList.forEach(frame-> program.append(frame.toString()).append("\n"));
        return program.toString();
    }

    private void  searchPlane(StringBuffer frame){
        char[] chArray = frame.toString().toCharArray();
        int n=0;
        StringBuilder number;
        for(int i=0;i<chArray.length;i++){
            if(chArray[i]==','){
                n++;
                switch (n){
                    case 6:
                        number= new StringBuilder();
                        for(int j=i+1;j<chArray.length;j++){
                            if(chArray[j]!=','){
                                number.append(chArray[j]);
                            }else break;
                        }
                        y=number.toString();
                        break;
                    case 7:
                        number= new StringBuilder();
                        for(int j=i+1;j<chArray.length;j++){
                            if(chArray[j]!=','){
                                number.append(chArray[j]);
                            }else break;
                        }
                        z=number.toString();
                        break;
                    case 9:
                        number= new StringBuilder();
                        for(int j=i+1;j<chArray.length;j++){
                            if(chArray[j]!=','){
                                number.append(chArray[j]);
                            }else break;
                        }
                        angle=number.toString();
                        break;
                }
            }
        }
    }
}
