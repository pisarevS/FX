package com.sergey.pisarev.model;

import com.sergey.pisarev.interfaces.Callback;
import com.sergey.pisarev.model.base.BaseDraw;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Program extends BaseDraw implements Runnable {

    private MyData data;
    private final Callback callback;
    private int x;
    private int u;
    private String horizontalAxis;
    private String verticalAxis;
    private final String program;
    private List<StringBuffer> programList;
    private ArrayList<String> listIgnore;
    private final Map<String, String> variablesList;
    private ArrayList<Frame> frameList;
    private Map<Integer, String> errorListMap;
    private final float FIBO = 1123581220;
    private final String[] defs = {"DEF REAL", "DEF INT"};
    private final String[] gCodes = {"G0", "G00", "G1", "G01", "G2", "G02", "G3", "G03", "G17", "G18", "G41", "G42","G40"};

    public Program(String program, Map<String, String> variablesList, Callback callback) {
        super();
        this.program = program;
        this.variablesList = variablesList;
        this.callback = callback;
        initLists();
    }

    private void initLists() {
        data = new MyData();
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

        variablesList.put("N_GANTRYPOS_X", String.valueOf(Constant.N_GANTRYPOS_X));
        variablesList.put("N_GANTRYPOS_Z", String.valueOf(Constant.N_GANTRYPOS_Z));
        variablesList.put("N_GANTRYPOS_U", String.valueOf(Constant.N_GANTRYPOS_X));
        variablesList.put("N_GANTRYPOS_W", String.valueOf(Constant.N_GANTRYPOS_Z));
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
                .peek(this::readDefVariables)
                .peek(this::readRVariables)
                .peek(this::initVariables)
                .peek(this::replaceProgramVariables)
                .collect(Collectors.toList());

        gotoF(programList);
        addFrameList();
        callback.callingBack(data);
    }

    private void addFrameList() {
        final String CR = "CR";
        final String RND = "RND";
        final String IC = "=IC";
        final String OFFN = "OFFN";
        final String TOOL = "T";
        final String DIAMON = "DIAMON";
        final String DIAMOF = "DIAMOF";
        final String HOME = "HOME";
        boolean isHorizontalAxis = false;
        boolean isVerticalAxis = false;
        double tempHorizontal = new Point().getX();
        double tempVertical = new Point().getZ();
        double tempCR = 0;
        double tempRND = 0;
        double tempOFFN = 0;
        String tempTOOL="";
        boolean isCR = false;
        boolean isRND = false;
        boolean isOFFN = false;
        boolean isTOOL = false;
        boolean isRadius = false;
        boolean isDiamon = false;
        StringBuffer strFrame;
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
                if (contains(strFrame, RND) && isHorizontalAxis && !contains(strFrame, CR) || contains(strFrame, RND) && isVerticalAxis && !contains(strFrame, CR)) {
                    tempRND = coordinateSearch(strFrame, RND);
                    if (tempRND != FIBO) {
                        isRND = true;
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }
            try {
                if (contains(strFrame, OFFN)) {
                    tempOFFN = coordinateSearch(strFrame, OFFN);
                    if (tempOFFN != FIBO) {
                        isOFFN = true;
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }
            try {
                if(contains(strFrame,DIAMON)||contains(strFrame,DIAMOF)){
                    if (contains(strFrame, DIAMON)) {
                        isDiamon=true;
                    }
                    if (contains(strFrame, DIAMOF)) {
                        isDiamon=false;
                    }
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }
            try {
                if (contains(strFrame, HOME)) {
                    isVerticalAxis = true;
                    isHorizontalAxis = true;
                    frame.setHome(true);
                    tempHorizontal=Constant.N_GANTRYPOS_X;
                    tempVertical=Constant.N_GANTRYPOS_Z;
                }
            } catch (Exception e) {
                errorListMap.put(i, strFrame.toString());
            }

            if (containsTool(strFrame)) {
                frame.setId(i);
                frame.setDiamon(isDiamon);
                tempTOOL=readTool(strFrame);
                isTOOL=true;
                frame.setTool(tempTOOL);
                frame.setTool(isTOOL);
                frame.setX(new Point().getX());
                frame.setZ(new Point().getZ());
                frame.setAxisContains(true);
                frameList.add(frame);
            }
            if (isCR) {
                frame.setId(i);
                frame.setDiamon(isDiamon);
                frame.setX(tempHorizontal);
                frame.setZ(tempVertical);
                frame.setOffn(tempOFFN);
                frame.setOffn(isOFFN);
                frame.setCr(tempCR);
                frame.setIsCR(true);
                frame.setAxisContains(true);
                frameList.add(frame);
                isHorizontalAxis = false;
                isVerticalAxis = false;
                isCR = false;
            }
            if (isRND) {
                frame.setId(i);
                frame.setDiamon(isDiamon);
                frame.setX(tempHorizontal);
                frame.setZ(tempVertical);
                frame.setOffn(tempOFFN);
                frame.setOffn(isOFFN);
                frame.setRnd(tempRND);
                frame.setRND(true);
                frame.setAxisContains(true);
                frameList.add(frame);
                isHorizontalAxis = false;
                isVerticalAxis = false;
                isRND = false;
            }
            if (isHorizontalAxis || isVerticalAxis) {
                frame.setId(i);
                frame.setDiamon(isDiamon);
                frame.setX(tempHorizontal);
                frame.setZ(tempVertical);
                frame.setOffn(tempOFFN);
                frame.setOffn(isOFFN);
                frame.setAxisContains(true);
                frameList.add(frame);
                isHorizontalAxis = false;
                isVerticalAxis = false;
            }
        }
        data.setErrorListMap(errorListMap);
        Set<Frame> s = new LinkedHashSet<>(frameList);
        frameList.clear();
        frameList.addAll(s);
        correctionForOffn(frameList);
        correctionForDiamon(frameList);
        data.setFrameList(frameList);
    }

    private void correctionForDiamon(List<Frame> frameList) {
        for (Frame frame : frameList) {
            if (frame.getDiamon() && frame.isAxisContains()) {
                if (frame.getTool() == null && !frame.isHome())
                    frame.setX(frame.getX() / 2);
            }
        }
    }

    private String readTool(StringBuffer strFrame) {
        for(String tool:toolsMap.keySet()){
            if(strFrame.indexOf(tool)>-1) return tool;
        }
        return "";
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

    private void readDefVariables(StringBuffer frame) {
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

    private void readRVariables(StringBuffer frame) {
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
        if (isSymbol(temp.toString()) && temp.indexOf("=") == 0) {
            if (temp.indexOf("=") != -1) {
                int index = temp.indexOf("=");
                temp.replace(index, index + 1, "");
            }
            return Expression.calculate(temp.toString());
        } else if (!isSymbol(temp.toString()) || temp.indexOf("-") == 0) {
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



    private boolean containsTool(StringBuffer sb) {
        for (String tool : toolsMap.keySet()) {
            if (sb.indexOf(tool) > -1) return true;
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
                case "G01":
                case "G1":
                case "G0":
                case "G00":
                    return false;
            }
        }
        return false;
    }
}
