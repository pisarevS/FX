package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.Callback;
import com.sergey.pisarev.model.base.BaseProgram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class Program extends BaseProgram implements Runnable {

    private ArrayList<StringBuffer> programList;
    private ArrayList<StringBuffer> parameterList;
    private String[] defs = {"DEF REAL", "DEF INT"};
    private String offn = "OFFN=";
    private MyData data = new MyData();
    private Callback callback;

    public Program(String program, String parameter, Callback callback) {
        super(program, parameter);
        programList = new ArrayList<>();
        parameterList = new ArrayList<>();
        this.callback = callback;
    }

    @Override
    public void run() {
        programList.addAll(getList(program));
        data.setProgramList(programList);
        removeIgnore(programList);
        removeLockedFrame(programList);
        gotoF(programList);
        if (containsDef(programList))
            searchDef(programList);
        parameterList.addAll(getList(parameter));
        readParameterVariables(parameterList);
        initVariables(programList);
        replaceParameterVariables(variablesList);
        replaceProgramVariables(programList);
        addFrameList();
        callback.callingBack(data);
    }

    @Override
    protected void addFrameList() {
        selectCoordinateSystem(programList);
        StringBuffer strFrame;
        boolean isHorizontalAxis = false;
        boolean isVerticalAxis = false;
        float tempHorizontal = 650;
        float tempVertical = 250;
        float tempCR = 0;
        boolean isCR = false;
        boolean isRadius = false;
        for (int i = 0; i < programList.size(); i++) {
            strFrame = programList.get(i);
            Frame frame = new Frame();

            try {
                if (containsGCode(strFrame)) {
                    ArrayList<String> gCode = searchGCog(strFrame.toString());
                    isRadius = activatedRadius(gCode);
                    frame.setGCode(gCode);
                    frame.setId(i);
                    frame.setX(tempHorizontal);
                    frame.setZ(tempVertical);
                    frameList.add(frame);
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }

            try {
                if (contains(strFrame, horizontalAxis + "=IC")) {
                    tempHorizontal = tempHorizontal + incrementSearch(strFrame, horizontalAxis + "=IC");
                    isHorizontalAxis = true;
                } else if (containsAxis(strFrame, horizontalAxis)) {
                    tempHorizontal = coordinateSearch(strFrame, horizontalAxis);
                    if (tempHorizontal != FIBO) {
                        isHorizontalAxis = true;
                    } else {
                        errorListMap.put(i, strFrame.toString());
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }

            try {
                if (contains(strFrame, verticalAxis + "=IC")) {
                    tempVertical = tempVertical + incrementSearch(strFrame, verticalAxis + "=IC");
                    isVerticalAxis = true;
                } else if (containsAxis(strFrame, verticalAxis)) {
                    tempVertical = coordinateSearch(strFrame, verticalAxis);
                    if (tempVertical != FIBO) {
                        isVerticalAxis = true;
                    } else {
                        errorListMap.put(i, strFrame.toString());
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }

            String radiusCR = "CR=";
            try {
                if (contains(strFrame, radiusCR) && isRadius) {
                    tempCR = coordinateSearch(strFrame, radiusCR);
                    if (tempCR != FIBO) {
                        isCR = true;
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }

            if (isCR) {
                frame.setX(tempHorizontal);
                frame.setZ(tempVertical);
                frame.setCr(tempCR);
                frame.setIsCR(true);
                frame.setAxisContains(true);
                frame.setId(i);
                frameList.add(frame);
                isHorizontalAxis = false;
                isVerticalAxis = false;
                isCR = false;
            }

            if (isHorizontalAxis || isVerticalAxis) {
                frame.setX(tempHorizontal);
                frame.setZ(tempVertical);
                frame.setAxisContains(true);
                frame.setId(i);
                frameList.add(frame);
                isHorizontalAxis = false;
                isVerticalAxis = false;
            }
        }
        data.setErrorListMap(errorListMap);
        Set<Frame> s = new LinkedHashSet<>(frameList);
        frameList.clear();
        frameList.addAll(s);
        data.setFrameList(frameList);
    }

    @Override
    protected ArrayList<StringBuffer> getList(String program) {
        ArrayList<StringBuffer> arrayList = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new StringReader(program));
            String line;
            while ((line = br.readLine()) != null) {
                arrayList.add(new StringBuffer(line));
            }
            br.close();
        } catch (IOException ignored) {

        }
        return arrayList;
    }

    @Override
    protected void readParameterVariables(ArrayList<StringBuffer> parameterList) {
        for (StringBuffer stringBuffer : parameterList) {
            if (stringBuffer.toString().contains(";")) {
                stringBuffer.delete(stringBuffer.indexOf(";"), stringBuffer.length());
            }
            if (stringBuffer.toString().contains("=")) {
                int key = 0;
                for (int j = stringBuffer.indexOf("=") - 1; j >= 0; j--) {
                    char c = stringBuffer.charAt(j);
                    if (c == ' ') {
                        key = j;
                        break;
                    }
                }
                variablesList.put(
                        stringBuffer.substring(key, stringBuffer.indexOf("=")).replace(" ", "")
                        , stringBuffer.substring(stringBuffer.indexOf("=") + 1, stringBuffer.length()).replace(" ", ""));
            }
        }
    }

    @Override
    protected void replaceParameterVariables(Map<String, String> variablesList) {
        for (Map.Entry entry : variablesList.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            for (String keys : variablesList.keySet()) {
                if (value.contains(keys)) {
                    value = value.replace(keys, variablesList.get(keys));
                    variablesList.put(key, value);
                }
            }
        }
    }

    @Override
    protected void replaceProgramVariables(ArrayList<StringBuffer> programList) {
        for (Map.Entry entry : variablesList.entrySet()) {
            for (StringBuffer stringBuffer : programList) {

                if (stringBuffer.toString().contains(entry.getKey().toString())) {

                    String key = entry.getKey().toString();
                    String value = entry.getValue().toString();

                    if (isSymbol(value)) {
                        double newValve = 0;
                        try {
                            newValve = Expression.calculate(value);
                        } catch (EmptyStackException e) {
                            e.printStackTrace();
                        }
                        value = String.valueOf(newValve);
                    }

                    String str = stringBuffer.toString().replace(key, value);
                    stringBuffer.replace(0, stringBuffer.length(), str);
                }
            }
        }
    }

    private void gotoF(ArrayList<StringBuffer> programList) {
        String label;
        String gotoF = "GOTOF";
        for (int i = 0; i < programList.size(); i++) {
            if (programList.get(i).toString().contains(gotoF)) {
                label = programList.get(i).substring(programList.get(i).indexOf(gotoF) + gotoF.length(), programList.get(i).length()).replace(" ", "");
                for (int j = i + 1; j < programList.size(); j++) {
                    if (!programList.get(j).toString().contains(label + ":")) {
                        programList.get(j).delete(0, programList.get(j).length());
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void removeIgnore(ArrayList<StringBuffer> programList) {
        for (StringBuffer stringBuffer : programList) {
            for (String s : listIgnore) {
                if (stringBuffer.toString().contains(s)) {
                    stringBuffer.delete(0, stringBuffer.length());
                }
            }
        }
    }

    private void removeLockedFrame(ArrayList<StringBuffer> programList) {
        for (StringBuffer stringBuffer : programList) {
            if (stringBuffer.toString().contains(";")) {
                stringBuffer.delete(stringBuffer.indexOf(";"), stringBuffer.length());
            }
        }

    }

    private boolean containsDef(ArrayList<StringBuffer> programList) {
        for (StringBuffer stringBuffer : programList) {
            for (String def : defs) {
                if (stringBuffer.toString().contains(def))
                    return true;
            }
        }
        return false;
    }

    private void initVariables(ArrayList<StringBuffer> programList) {
        for (StringBuffer stringBuffer : programList) {
            if (!stringBuffer.toString().contains(defs[0]) && !stringBuffer.toString().contains(defs[1])) {
                variablesList.forEach((key, value) -> {
                    if (stringBuffer.toString().contains(key + "=")) {
                        String[] arrStr = stringBuffer.toString().split(" ");
                        for (String str : arrStr) {
                            if (str.contains("=")) {
                                String[] arrVar = str.split("=");
                                variablesList.put(arrVar[0].replace(" ", ""), arrVar[1].replace(" ", ""));
                            }
                        }
                    }
                });
            }
        }
    }

    private void searchDef(ArrayList<StringBuffer> programList) {
        for (StringBuffer stringBuffer : programList) {
            for (String def : defs) {
                if (stringBuffer.toString().contains(def)) {
                    stringBuffer.delete(0, stringBuffer.indexOf(def) + def.length());
                    String[] arrStr = stringBuffer.toString().split(",");
                    for (String str : arrStr) {
                        if (str.contains("=")) {
                            String[] arrVar = str.split("=");
                            variablesList.put(arrVar[0].replace(" ", ""), arrVar[1].replace(" ", ""));
                        } else {
                            variablesList.put(str.replace(" ", ""), "");
                        }
                    }
                }
            }
        }
    }


   /* private float searchOffn(StringBuffer frame) {
        Expression expression = new Expression();
        int n = frame.indexOf(offn);
        String temp = frame.substring(n + offn.length(), frame.length());
        try {
            return Expression.calculate(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
