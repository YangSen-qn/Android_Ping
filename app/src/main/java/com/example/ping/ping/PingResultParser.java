package com.example.ping.ping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingResultParser {

    public enum Type{
        Unknown,
        Start,
        Normal,
        Timeout,
        End
    }

    public Type type = Type.Unknown;

    public int sentPackageCount;
    public int receivedPackageCount;
    public String ip;
    public int ttl;
    public int time;
    public int sentPackageSize;
    public int sequenceNumber;

    private final String result;

    public PingResultParser(String result){
        this.result = result;

        if (isNormalPing()){
            this.ip = getPingIP();
            this.sequenceNumber = getPingSequenceNumber();
            this.sentPackageSize = getPingPackageSize();
            this.ttl = getPingTTL();
            this.time = getPingTime();
        } else if (isPingStart()) {
            this.ip = getPingIP();
            this.sentPackageSize = getStartPingPackageSize();
        } else if (isPingEnd()) {
            this.sentPackageCount = getPingTotalSentPackageCount();
            this.receivedPackageCount = getPingTotalReceivedPackageCount();
        } else if (isTimeout()){
            this.ip = getPingIP();
            this.sequenceNumber = getPingSequenceNumber();
        }
    }

    private boolean isNormalPing(){
        return result.contains("bytes from");
    }

    private boolean isTimeout(){
        return result.contains("timeout");
    }

    private boolean isPingStart(){
        return result.startsWith("PING");
    }

    private boolean isPingEnd(){
        return result.contains("packets transmitted");
    }

    private int getStartPingPackageSize(){
        Pattern pattern = Pattern.compile("\\d+(?=(\\(\\d+\\))? bytes of)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String sizeString = null;
        try {
            sizeString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (sizeString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(sizeString);
        }
    }

    private int getPingPackageSize(){
        Pattern pattern = Pattern.compile("(?<=icmp_seq=)\\d+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String sizeString = null;
        try {
            sizeString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (sizeString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(sizeString);
        }
    }

    private String getPingIP(){
        Pattern pattern = Pattern.compile("((\\d{0,3}\\.){3}\\d{0,3}|([A-Fa-f0-9]{1,4}::?){1,7}[A-Fa-f0-9]{1,4})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String ip = null;
        try {
            ip = matcher.group();
        } catch (IllegalStateException ignore){}
        return ip;
    }

    private int getPingSequenceNumber(){
        Pattern pattern = Pattern.compile("(?<= icmp_seq=)\\d+ ", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String numString = null;
        try {
            numString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (numString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(numString);
        }
    }

    private int getPingTTL(){
        Pattern pattern = Pattern.compile("(?<= ttl=)\\d+ ", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String ttlString = null;
        try {
            ttlString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (ttlString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(ttlString);
        }
    }

    private int getPingTime(){
        Pattern pattern = Pattern.compile("(?<= time=)(\\d+(\\.\\d+)? )", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String timeString = null;
        try {
            timeString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (timeString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(timeString);
        }
    }

    private int getPingTotalSentPackageCount(){
        Pattern pattern = Pattern.compile("(\\d+)(?= packets transmitted)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String countString = null;
        try {
            countString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (countString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(countString);
        }
    }

    private int getPingTotalReceivedPackageCount(){
        Pattern pattern = Pattern.compile("(\\d+)(?= received,)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(result);
        String countString = null;
        try {
            countString = matcher.group();
        } catch (IllegalStateException ignore){}

        if (countString.length() == 0){
            return -1;
        } else {
            return Integer.parseInt(countString);
        }
    }
}