package com.sergey.pisarev.model;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenameFrameNumbers {

    public static String rename(String text, int firstNumber, int iterNumber) {
        StringBuffer program=new StringBuffer();
        AtomicInteger iterator = new AtomicInteger();
        Arrays.stream(text.split("\n"))
                .map(StringBuffer::new)
                .peek(p->{
                    Pattern closedFrame = Pattern.compile(";[^\n]*");
                    Pattern numberFrame = Pattern.compile("N(\\d+)");
                    Matcher matcherNumberFrame;
                    Matcher matcherCloseFrame;
                    String tempNumberFrame = null;
                    matcherCloseFrame = closedFrame.matcher(p);
                    if (matcherCloseFrame.find()) {
                        String substring = p.substring(0, matcherCloseFrame.start());
                        matcherNumberFrame = numberFrame.matcher(substring);
                        if (matcherNumberFrame.find()) {
                            tempNumberFrame = p.substring(matcherNumberFrame.start(), matcherNumberFrame.end());
                            iterator.getAndIncrement();
                        }else if (!substring.isEmpty()) {
                            iterator.getAndIncrement();
                            int result = (firstNumber - iterNumber) + iterator.get() * iterNumber;
                            String strFrame = "N" + result + " ";
                            p.replace(0, 0, strFrame);
                        }
                    } else {
                        matcherNumberFrame = numberFrame.matcher(p);
                        if (matcherNumberFrame.find()) {
                            tempNumberFrame = p.substring(matcherNumberFrame.start(), matcherNumberFrame.end());
                            iterator.getAndIncrement();
                        } else if (!p.toString().equals("")) {
                            iterator.getAndIncrement();
                            int result = (firstNumber - iterNumber) + iterator.get() * iterNumber;
                            String strFrame = "N" + result + " ";
                            p.replace(0, 0, strFrame);
                        }
                    }

                    if (tempNumberFrame != null) {
                        int start = p.indexOf(tempNumberFrame);
                        int result = (firstNumber - iterNumber) + iterator.get() * iterNumber;
                        int end = start + tempNumberFrame.length();
                        String strFrame = "N" + result;
                        p.replace(start, end, strFrame);
                    }
                })
                .peek(p->program.append(p).append("\n"))
                .toArray();


        return program.toString();
    }
}
