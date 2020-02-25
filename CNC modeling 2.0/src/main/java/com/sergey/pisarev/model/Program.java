package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.Callback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class Program implements Runnable {

    private ArrayList<StringBuffer> programList;
    private String[] defs = {"DEF REAL", "DEF INT"};
    private MyData data = new MyData();
    private Callback callback;
    private int x;
    private int u;
    private String horizontalAxis;
    private String verticalAxis;
    private String program;
    private ArrayList<String> listIgnore;
    private Map<String, String> variablesList;
    private ArrayList<Frame> frameList;
    private Map<Integer, String> errorListMap;
    private final float FIBO = 1123581220;
    private String[] gCodes = {"G0", "G00", "G1", "G01", "G2", "G02", "G3", "G03", "G17", "G18"};

    public Program(String program, Map<String, String> variablesList, Callback callback) {
        this.program = program;
        this.variablesList = variablesList;
        this.callback = callback;
        initLists();
    }

    private void initLists() {
        programList = new ArrayList<>();
        listIgnore = new ArrayList<>();
        frameList = new ArrayList<>();
        errorListMap = new HashMap<>();
        //ЛПО
        listIgnore.add("G58 X=0 Z=N_CHUCK_HEIGHT_Z_S1[N_CHUCK_JAWS]");
        listIgnore.add("G59 X=N_WP_ZP_X_S1 Z=N_WP_ZP_Z_S1");
        listIgnore.add("G59 X=N_WP_ZP_X_S1");
        listIgnore.add("G59 X=N_WP_ZP_X_S1 Z=N_WP_ZP_Z_S1");
        listIgnore.add("G58 X=0 Z=N_CHUCK_HEIGHT_Z_S2[N_CHUCK_JAWS]");
        listIgnore.add("G59 X=N_WP_ZP_X_S2 Z=N_WP_ZP_Z_S2");
        listIgnore.add("G58 U=0 W=N_CHUCK_HEIGHT_W_S1[N_CHUCK_JAWS]");
        listIgnore.add("G59 U=N_WP_ZP_U_S1 W=N_WP_ZP_W_S1");
        listIgnore.add("G58 U=0 W=N_CHUCK_HEIGHT_W_S2[N_CHUCK_JAWS]");
        listIgnore.add("G59 U=N_WP_ZP_U_S2 W=N_WP_ZP_W_S2");
        //ЛПО2
        listIgnore.add("N_ZERO_O(54,X1,0,\"TR\")");
        listIgnore.add("N_ZERO_O(54,Z1,CHUCK_HEIGHT_Z1_S1[0],\"TR\")");
        listIgnore.add("N_ZERO_O(54,X1,WP_ZP_X1_S1,\"FI\")");
        listIgnore.add("N_ZERO_O(54,Z1,WP_ZP_Z1_S1,\"FI\")");

        listIgnore.add("N_ZERO_O(54,X1,0,\"TR\")");
        listIgnore.add("N_ZERO_O(54,Z1,CHUCK_HEIGHT_Z1_S2[0],\"TR\")");
        listIgnore.add("N_ZERO_O(54,X1,WP_ZP_X1_S2,\"FI\")");
        listIgnore.add("N_ZERO_O(54,Z1,WP_ZP_Z1_S2,\"FI\")");

        listIgnore.add("N_ZERO_O(54,X2,0,\"TR\")");
        listIgnore.add("N_ZERO_O(54,Z2,CHUCK_HEIGHT_Z2_S1[0],\"TR\")");
        listIgnore.add("N_ZERO_O(54,X2,WP_ZP_X2_S1,\"FI\")");
        listIgnore.add("N_ZERO_O(54,Z2,WP_ZP_Z2_S1,\"FI\")");

        listIgnore.add("N_ZERO_O(54,X2,0,\"TR\")");
        listIgnore.add("N_ZERO_O(54,Z2,CHUCK_HEIGHT_Z2_S2[0],\"TR\")");
        listIgnore.add("N_ZERO_O(54,X2,WP_ZP_X2_S2,\"FI\")");
        listIgnore.add("N_ZERO_O(54,Z2,WP_ZP_Z2_S2,\"FI\")");

        variablesList.put("N_GANTRYPOS_X", "650");
        variablesList.put("N_GANTRYPOS_Z", "250");
        variablesList.put("N_GANTRYPOS_U", "650");
        variablesList.put("N_GANTRYPOS_W", "250");
        variablesList.put("$P_TOOLR", "16");
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
        initVariables(programList);
        replaceParameterVariables(variablesList);
        replaceProgramVariables(programList);
        addFrameList();
        callback.callingBack(data);
    }

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

    public static ArrayList<StringBuffer> getList(String program) {
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

    private void replaceParameterVariables(Map<String, String> variablesList) {
        variablesList.forEach((key, value1) -> {
            String value = value1;
            for (String keys : variablesList.keySet()) {
                if (value.contains(keys)) {
                    value = value.replace(keys, variablesList.get(keys));
                    variablesList.put(key, value);
                }
            }
        });
    }

    private void replaceProgramVariables(ArrayList<StringBuffer> programList) {
        variablesList.forEach((key, value) -> {
            for (StringBuffer stringBuffer : programList) {
                if (stringBuffer.toString().contains(key)) {
                    String value1 = value;
                    if (isSymbol(value1)) {
                        double newValve = 0;
                        try {
                            newValve = Expression.calculate(value1);
                        } catch (EmptyStackException e) {
                            e.printStackTrace();
                        }
                        value1 = String.valueOf(newValve);
                    }
                    String str = stringBuffer.toString().replace(key, value1);
                    stringBuffer.replace(0, stringBuffer.length(), str);
                }
            }
        });
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
        programList.forEach(valve -> {
            listIgnore.forEach(ignore -> {
                if (valve.toString().contains(ignore)) {
                    valve.delete(0, valve.length());
                }
            });
        });
    }

    private void removeLockedFrame(ArrayList<StringBuffer> programList) {
        programList.forEach(valve -> {
            if (valve.toString().contains(";")) {
                valve.delete(valve.indexOf(";"), valve.length());
            }
        });
    }

    private boolean containsDef(ArrayList<StringBuffer> programList) {
        for (String def : defs) {
            for (StringBuffer stringBuffer : programList) {
                if (stringBuffer.toString().contains(def))
                    return true;
            }
        }
        return false;
    }

    private void initVariables(ArrayList<StringBuffer> programList) {
        programList.forEach(valve -> {
            if (!valve.toString().contains(defs[0]) && !valve.toString().contains(defs[1])) {
                variablesList.forEach((key, value) -> {
                    if (valve.toString().contains(key + "=")) {
                        String[] arrStr = valve.toString().split(" ");
                        for (String str : arrStr) {
                            if (str.contains("=")) {
                                String[] arrVar = str.split("=");
                                variablesList.put(arrVar[0].replace(" ", ""), arrVar[1].replace(" ", ""));
                            }
                        }
                    }
                });
            }
        });
    }

    private void searchDef(ArrayList<StringBuffer> programList) {
        for (String def : defs) {
            programList.forEach(valve -> {
                if (valve.toString().contains(def)) {
                    valve.delete(0, valve.indexOf(def) + def.length());
                    String[] arrStr = valve.toString().split(",");
                    for (String str : arrStr) {
                        if (str.contains("=")) {
                            String[] arrVar = str.split("=");
                            variablesList.put(arrVar[0].replace(" ", ""), arrVar[1].replace(" ", ""));
                        } else {
                            variablesList.put(str.replace(" ", ""), "");
                        }
                    }
                }
            });
        }
    }

    private boolean isGCode(String g) {
        for (String gCode : gCodes) {
            if (g.equals(gCode)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String> searchGCog(String frame) {
        ArrayList<String> gCodeList = new ArrayList<>();
        StringBuilder g = new StringBuilder("G");
        if (frame.contains("G")) {
            for (int i = 0; i < frame.length(); i++) {
                char c = frame.charAt(i);
                if (c == 'G') {
                    for (int j = i + 1; j < frame.length(); j++) {
                        char t = frame.charAt(j);
                        if (isDigit(t)) {
                            g.append(t);
                        } else {
                            if (isGCode(g.toString()))
                                gCodeList.add(g.toString());
                            break;
                        }
                    }
                    g = new StringBuilder("G");
                }
            }
        }
        return gCodeList;
    }

    private float coordinateSearch(StringBuffer frame, String axis) throws Exception {
        StringBuilder temp = new StringBuilder();
        for (int i = frame.indexOf(axis) + axis.length(); i < frame.length(); i++) {
            if (readUp(frame.charAt(i))) {
                temp.append(frame.charAt(i));
            } else {
                break;
            }
        }
        if (temp.toString().contains("=")) {
            int index = temp.indexOf("=");
            temp.replace(index, index + 1, "");
        }
        if (isSymbol(temp.toString())) {
            return Expression.calculate(temp.toString());
        } else if (!isSymbol(temp.toString())) {
            return Float.parseFloat(temp.toString());
        }
        return FIBO;
    }

    private float incrementSearch(StringBuffer frame, String axis) throws Exception {
        StringBuilder temp = new StringBuilder();
        int n = frame.indexOf(axis);

        if (frame.charAt(n + axis.length()) == '(') {
            for (int i = n + axis.length(); i < frame.length(); i++) {
                if (readUp(frame.charAt(i))) {
                    temp.append(frame.charAt(i));
                } else {
                    break;
                }
            }
            return Expression.calculate(temp.toString());
        }
        return Float.parseFloat(temp.toString());
    }

    private boolean containsAxis(StringBuffer frame, String axis) {
        if (contains(frame, axis)) {
            int n = frame.indexOf(axis) + 1;
            char c = frame.charAt(n);
            switch (c) {
                case '-':
                case '=':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    return true;
            }
        }
        return false;
    }

    private void selectCoordinateSystem(ArrayList<StringBuffer> programList) {
        programList.forEach(valve -> {
            if (valve.toString().contains("X"))
                x++;
            if (valve.toString().contains("U"))
                u++;
            if (x > u) {
                horizontalAxis = "X";
                verticalAxis = "Z";
            } else {
                horizontalAxis = "U";
                verticalAxis = "W";
            }
        });
    }

    private boolean readUp(char input) {
        switch (input) {
            case 'C':
            case 'X':
            case 'G':
            case 'M':
            case 'F':
            case 'W':
            case 'Z':
            case 'D':
            case 'S':
            case 'A':
            case 'U':
            case 'L':
            case 'O':
            case 'H':
            case 'R':
                return false;
        }
        return true;
    }

    private boolean contains(StringBuffer sb, String findString) {
        return sb.indexOf(findString) > -1;
    }

    private boolean containsGCode(StringBuffer sb) {
        for (String g : gCodes) {
            if (sb.indexOf(g) > -1) return true;
        }
        return false;
    }

    private boolean isDigit(char input) {
        switch (input) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
        }
        return false;
    }

    private boolean isSymbol(String text) {
        if (text.contains("+")) return true;
        if (text.contains("-")) return true;
        if (text.contains("*")) return true;
        if (text.contains("/")) return true;
        if (text.contains("(")) return true;
        return text.contains(")");
    }

    private boolean activatedRadius(ArrayList<String> gCode) {
        for (String code : gCode) {
            switch (code) {
                case "G2":
                case "G02":
                case "G3":
                case "G03":
                    return true;
            }
        }
        return false;
    }
}
