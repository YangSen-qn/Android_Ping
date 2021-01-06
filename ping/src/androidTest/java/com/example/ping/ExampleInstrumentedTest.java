package com.example.ping;

import android.util.Log;

import com.ping.Ping;
import com.ping.PingOption;

import junit.framework.TestCase;

public class ExampleInstrumentedTest extends TestCase {

    public void test_Ping() {

        PingOption option = new PingOption();
        option.timeout = 3;
        option.count = 5;

        Ping ping = new Ping("upload.qiniu.com", option);
        ping = new Ping("119.167.237.230", option);

        ping.pingListener = new Ping.PingListener() {
            @Override
            public void pingDidStart(Ping ping, int dataBytesLength) {
                String string = String.format("PING %s(%s) %d bytes of data.", ping.address, ping.currentPingIP, dataBytesLength);
                Log.d("==", string);
            }

            @Override
            public void pingDidTimeout(Ping ping, int sequenceNumber) {
                Log.d("==", ping.currentPingIP + " icmp_seq=" + sequenceNumber + "timeout");
//                String string = String.format("PING %s(%s) %d bytes of data.", ping.address, ping.currentPingIP, dataBytesLength);
//                Log.d("==", string);
            }

            @Override
            public void pingReceivePacket(Ping ping, int sequenceNumber, float spendTime, int dataBytesLength) {
                String string = String.format("%d bytes from %s: icmp_seq=%d", dataBytesLength, ping.currentPingIP, sequenceNumber);
                Log.d("==", string);
            }

            @Override
            public void pingComplete(Ping ping, int sendPacketsCount, int receivedPacketsCount) {
                String string = String.format("%d packets transmitted, %d packets received, %.1f packet loss", sendPacketsCount, receivedPacketsCount, ((sendPacketsCount - receivedPacketsCount)*1.0 / sendPacketsCount));
                Log.d("==", string);
            }
        };

        ping.startPing();
    }
}
