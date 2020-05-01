package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.Callback;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Program implements Runnable {

    private List<StringBuffer> programList;
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
    private String[] gCodes = {"G0", "G00", "G1", "G01", "G2", "G02", "G3", "G03", "G17", "G18","G41","G42"};

    public Program(String program, Map<String, String> variablesList, Callback callback) {
        this.program = program;
        this.variablesList = variablesList;
        this.callback = callback;
        initLists();
    }

    private void initLists() {

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
        replaceParameterVariables(variablesList);
        data.setProgramList(Arrays.stream(program.split("\n")).map(StringBuffer::new).collect(Collectors.toList()));

        programList = Arrays.stream(program.split("\n"))
                .map(StringBuffer::new)
                .peek(this::removeLockedFrame)
                .peek(this::removeIgnore)
                .peek(this::addDefVariables)
                .peek(this::addRVariables)
                .peek(this::initVariables)
                .peek(this::replaceProgramVariables)
                .collect(Collectors.toList());

        gotoF(programList);
        addFrameList();
        callback.callingBack(data);
    }

    private void addFrameList() {
        String CR = "CR";
        String RND = "RND";
        String IC = "=IC";
        StringBuffer strFrame;
        boolean isHorizontalAxis = false;
        boolean isVerticalAxis = false;
        double tempHorizontal = 650;
        double tempVertical = 250;
        double tempCR = 0;
        double tempRND = 0;
        boolean isCR = false;
        boolean isRND = false;
        boolean isRadius = false;
        selectCoordinateSystem(programList);
        for (int i = 0; i < programList.size(); i++) {
            strFrame = programList.get(i);
            Frame frame = new Frame();
            try {
                if (containsGCode(strFrame)) {
                    List<String> gCode = searchGCog(strFrame.toString());
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
                if (contains(strFrame, horizontalAxis + IC)) {
                    tempHorizontal = tempHorizontal + incrementSearch(strFrame, horizontalAxis + IC);
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
                if (contains(strFrame, verticalAxis + IC)) {
                    tempVertical = tempVertical + incrementSearch(strFrame, verticalAxis + IC);
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
            try {
                if (contains(strFrame, CR) && isRadius) {
                    tempCR = coordinateSearch(strFrame, CR);
                    if (tempCR != FIBO) {
                        isCR = true;
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }
            try {
                if (contains(strFrame, RND)&&isHorizontalAxis||contains(strFrame, RND)&&isVerticalAxis) {
                    tempRND = coordinateSearch(strFrame, RND);
                    if (tempRND != FIBO) {
                        isRND = true;
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
            if (isRND) {
                frame.setX(tempHorizontal);
                frame.setZ(tempVertical);
                frame.setRnd(tempRND);
                frame.setRND(true);
                frame.setAxisContains(true);
                frame.setId(i);
                frameList.add(frame);
                isHorizontalAxis = false;
                isVerticalAxis = false;
                isRND = false;
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

    private void removeLockedFrame(StringBuffer frame) {
        if (frame.toString().contains(";")) frame.delete(frame.indexOf(";"), frame.length());
    }

    private void removeIgnore(StringBuffer frame) {
        listIgnore.forEach(ignore -> {
            if (frame.toString().contains(ignore)) frame.delete(0, frame.length());
        });
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

    private void replaceProgramVariables(StringBuffer frame) {
        variablesList.forEach((key, value) -> {
            if (frame.toString().contains(key)) {
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
                String str = frame.toString().replace(key, value1);
                frame.replace(0, frame.length(), str);
            }
        });
    }

    private void gotoF(List<StringBuffer> programList) {
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

    private void addDefVariables(StringBuffer frame) {
        for (String def : defs) {
            if (frame.toString().contains(def)) {
                frame.delete(0, frame.indexOf(def) + def.length());
                String[] arrStr = frame.toString().split(",");
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

    private void addRVariables(StringBuffer frame) {
        Pattern pattern = Pattern.compile("R(\\d+)" + "=");
        Matcher matcher = pattern.matcher(frame);
        while (matcher.find()) {
            variablesList.put(matcher.group().replace("=", ""), "");
        }
    }

    private void initVariables(StringBuffer frame) {
        for (String def : defs) {
            if (frame.indexOf(def) == -1) {
                variablesList.forEach((key, value) -> {
                    if (frame.toString().contains(key + "=")) {
                        String[] arrStr = frame.toString().split(" ");
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
        replaceParameterVariables(variablesList);
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
        if (isSymbol(temp.toString())&&temp.indexOf("=")==0) {
            if (temp.indexOf("=")!=-1) {
                int index = temp.indexOf("=");
                temp.replace(index, index + 1, "");
            }
            return Expression.calculate(temp.toString());
        } else if (!isSymbol(temp.toString())||temp.indexOf("-")==0) {
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

    private void selectCoordinateSystem(List<StringBuffer> programList) {
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
        if (text.contains("=")) return true;
        if (text.contains("+")) return true;
        if (text.contains("-")) return true;
        if (text.contains("*")) return true;
        if (text.contains("/")) return true;
        if (text.contains("(")) return true;
        return text.contains(")");
    }

    private boolean activatedRadius(List<String> gCode) {
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
