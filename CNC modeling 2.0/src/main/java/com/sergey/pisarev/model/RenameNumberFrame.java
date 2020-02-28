package com.sergey.pisarev.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RenameNumberFrame {

    public static String rename(String text, int firstNumber, int iterNumber) {
        List<StringBuffer> list =  Arrays.stream(text.split("\n"))
                .map(StringBuffer::new)
                .collect(Collectors.toList());

        Pattern closedFrame = Pattern.compile(";[^\n]*");
        Pattern numberFrame = Pattern.compile("N(\\d+)");
        int iter = 0;

        for (int i = 0; i < list.size(); i++) {
            String tempNumberFrame = null;
            Matcher matcherCloseFrame = closedFrame.matcher(list.get(i));

            if (matcherCloseFrame.find()) {
                String substring = list.get(i).substring(0, matcherCloseFrame.start());
                Matcher matcherNumberFrame = numberFrame.matcher(substring);
                if (matcherNumberFrame.find()) {
                    tempNumberFrame = list.get(i).substring(matcherNumberFrame.start(), matcherNumberFrame.end());
                    iter++;
                }
            } else {
                Matcher matcherNumberFrame = numberFrame.matcher(list.get(i));
                if (matcherNumberFrame.find()) {
                    tempNumberFrame = list.get(i).substring(matcherNumberFrame.start(), matcherNumberFrame.end());
                    iter++;
                } else {
                    if (!list.get(i).toString().equals("")) {
                        iter++;
                        int result = (firstNumber - iterNumber) + iter * iterNumber;
                        String strFrame = "N" + result + " ";
                        list.get(i).replace(0, 0, strFrame);
                    }
                }
            }
            if (tempNumberFrame != null) {
                int start = list.get(i).indexOf(tempNumberFrame);
                int result = (firstNumber - iterNumber) + iter * iterNumber;
                int end = start + tempNumberFrame.length();
                String strFrame = "N" + result;
                list.get(i).replace(start, end, strFrame);
            }
        }
        StringBuilder result = new StringBuilder();
        for (StringBuffer stringBuffer : list) {
            result.append(stringBuffer).append("\n");
        }
        return result.toString();
    }
}
