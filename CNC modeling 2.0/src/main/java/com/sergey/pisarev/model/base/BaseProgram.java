package com.sergey.pisarev.model.base;

import com.sergey.pisarev.model.Expression;
import com.sergey.pisarev.model.Frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseProgram {

    private int x;
    private int u;
    protected String horizontalAxis;
    protected String verticalAxis;
    protected String program;
    protected ArrayList<String> listIgnore;
    protected Map<String, String> variablesList;
    protected ArrayList<Frame> frameList;
    protected Map<Integer, String> errorListMap;
    protected final float FIBO = 1123581220;
    private String[] gCodes = {"G0", "G00", "G1", "G01", "G2", "G02", "G3", "G03", "G17", "G18"};

    protected BaseProgram(String program, Map<String, String> variablesList) {
        this.program = program;
        this.variablesList =  variablesList;
        initLists();
    }

    protected abstract void readParameterVariables(ArrayList<StringBuffer> parameterList);

    protected abstract void replaceProgramVariables(ArrayList<StringBuffer> programList) throws Exception;

    protected abstract void replaceParameterVariables(Map<String, String> variablesList);

    protected abstract void addFrameList();

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

    private boolean isGCode(String g) {
        for (String gCode : gCodes) {
            if (g.equals(gCode)) {
                return true;
            }
        }
        return false;
    }

    protected ArrayList<String> searchGCog(String frame) {
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

    protected float coordinateSearch(StringBuffer frame, String axis) throws Exception {
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

    protected float incrementSearch(StringBuffer frame, String axis) throws Exception {
        Expression expression = new Expression();
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
            return expression.calculate(temp.toString());
        }
        return Float.parseFloat(temp.toString());
    }

    protected boolean containsAxis(StringBuffer frame, String axis) {
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

    protected void selectCoordinateSystem(ArrayList<StringBuffer> programList) {
        for (int i = 0; i < programList.size(); i++) {
            if (programList.get(i).toString().contains("X"))
                x++;
            if (programList.get(i).toString().contains("U"))
                u++;
            if (x > u) {
                horizontalAxis = "X";
                verticalAxis = "Z";
            } else {
                horizontalAxis = "U";
                verticalAxis = "W";
            }
        }
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

    protected boolean contains(StringBuffer sb, String findString) {
        return sb.indexOf(findString) > -1;
    }

    protected boolean containsGCode(StringBuffer sb) {
        if (sb.indexOf("G17") > -1)
            return true;
        if (sb.indexOf("G18") > -1)
            return true;
        if (sb.indexOf("G0") > -1)
            return true;
        if (sb.indexOf("G00") > -1)
            return true;
        if (sb.indexOf("G1") > -1)
            return true;
        if (sb.indexOf("G01") > -1)
            return true;
        if (sb.indexOf("G2") > -1)
            return true;
        if (sb.indexOf("G02") > -1)
            return true;
        if (sb.indexOf("G3") > -1)
            return true;
        return sb.indexOf("G03") > -1;
    }

    protected boolean isDigit(char input) {
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

    protected boolean isSymbol(String text) {
        if (text.contains("+")) return true;
        if (text.contains("-")) return true;
        if (text.contains("*")) return true;
        if (text.contains("/")) return true;
        if (text.contains("(")) return true;
        return text.contains(")");
    }

    protected boolean activatedRadius(ArrayList<String> gCode) {
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
