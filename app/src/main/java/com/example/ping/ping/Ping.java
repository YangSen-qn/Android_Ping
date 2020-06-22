package com.example.ping.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class Ping {

    public String currentPingIP;
    public final PingOption option;

    public Date startDate;
    public final String address;
    public PingListener pingListener;

    public Ping(String address, PingOption option){
        this.address = address;
        this.option = option;
    }

    public void startPing() {

        startDate = new Date();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(address);
        } catch (UnknownHostException ignore) {}

        if (inetAddress != null && inetAddress.getHostAddress() != null){
            currentPingIP = inetAddress.getHostAddress();
            ping();
        } else if(pingListener != null) {
           pingListener.pingComplete(this, 0 ,0);
        }
    }

    public void stopPing(){


    }

    private void ping(){
        BufferedReader in = null;
        try {
            String pingCommand = "ping" + " -c " + option.count + " -W " + option.timeout + " -s 80 " + currentPingIP;
            Process p = Runtime.getRuntime().exec(pingCommand);
            if (p == null) {
                return;
            }
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                parsePingLine(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {}
        }
    }

    private void parsePingLine(String line) {
        if (pingListener == null || line == null){
            return;
        }

        PingResultParser parser = new PingResultParser(line);
        if (parser.type == PingResultParser.Type.Unknown) {
            return;
        }

        switch (parser.type){
            case Start:{
                pingListener.pingDidStart(this, parser.sentPackageSize);
            }
            case Normal:{
                pingListener.pingReceivePacket(this, parser.sequenceNumber, parser.sentPackageSize);
            }
            case End:{
                pingListener.pingComplete(this, parser.sentPackageCount, parser.receivedPackageCount);
            }
            case Timeout:{
                pingListener.pingDidTimeout(this, parser.sequenceNumber);
            }
        }
    }

    public interface PingListener {
        void pingDidStart(Ping ping, int dataBytesLength);
        void pingDidTimeout(Ping ping, int sequenceNumber);
        void pingReceivePacket(Ping ping, int sequenceNumber, int dataBytesLength);
        void pingComplete(Ping ping, int sendPacketsCount, int receivedPacketsCount);
    }
}
