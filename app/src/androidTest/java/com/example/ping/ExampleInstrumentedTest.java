package com.example.ping;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.ping.ping.Ping;
import com.example.ping.ping.PingOption;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleInstrumentedTest extends TestCase {

    public void testPing() {

        CountDownLatch simple = new CountDownLatch(1);

        PingOption option = new PingOption();
        option.timeout = 3;
        option.count = 5;

        Ping ping = new Ping("upload.qiniu.com", option);
        ping = new Ping("119.167.237.230", option);

        ping.pingListener = new Ping.PingListener() {
            @Override
            public void pingDidStart(Ping ping, int dataBytesLength) {
                Log.d("==", "PING " + ping.address + "(" + ping.currentPingIP + ")" + " " + dataBytesLength);
            }

            @Override
            public void pingDidTimeout(Ping ping, int sequenceNumber) {
                Log.d("==", ping.currentPingIP + " icmp_seq=" + sequenceNumber + "timeout");
            }

            @Override
            public void pingReceivePacket(Ping ping, int sequenceNumber, int dataBytesLength) {
                Log.d("==", dataBytesLength + " bytes from " + ping.currentPingIP + " icmp_seq=" + sequenceNumber);
            }

            @Override
            public void pingComplete(Ping ping, int sendPacketsCount, int receivedPacketsCount) {
                Log.d("==", sendPacketsCount + " packets transmitted, " + receivedPacketsCount + " received," + " " + (receivedPacketsCount*1.0 / sendPacketsCount) + " packet loss");
            }
        };

        ping.startPing();
    }
}
