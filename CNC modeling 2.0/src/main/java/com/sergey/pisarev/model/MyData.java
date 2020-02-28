package com.sergey.pisarev.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyData {

    private List<StringBuffer> programList = new ArrayList<>();

    private Map<Integer, String> errorListMap = new LinkedHashMap<>();

    private List<Frame> frameList = new ArrayList<>();

    public MyData() {
    }

    public Map<Integer, String> getErrorListMap() {
        return errorListMap;
    }

    public void setErrorListMap(Map<Integer, String> errorListMap) {
        this.errorListMap = errorListMap;
    }

    public List<Frame> getFrameList() {
        return frameList;
    }

    public void setFrameList(ArrayList<Frame> frameList) {
        this.frameList = frameList;
    }

    public List<StringBuffer> getProgramList() {
        return programList;
    }

    public void setProgramList(List<StringBuffer> programList) {
        this.programList = programList;
    }
}
