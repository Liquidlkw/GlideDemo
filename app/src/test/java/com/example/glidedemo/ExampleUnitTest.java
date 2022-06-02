package com.example.glidedemo;

import org.junit.Test;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String a = new String("1");
        ReferenceQueue<String> referenceQueue = new ReferenceQueue<>();
        WeakReference<String> weakReference = new WeakReference<>(a, referenceQueue);
        a=null;

        Thread cleanThread =new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                        System.out.println( "recycle start==========");
                        System.out.println( "recycle "+referenceQueue.remove());
                        System.out.println( "recycle end==========");


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        cleanThread.start();

        try {

            Thread.sleep(1000);
            cleanThread.interrupt();
            Thread.sleep(1000);
            if (cleanThread.isAlive()){
                System.out.println("still alive");
                cleanThread.join(5000);
                System.out.println("------join--------");
            }



        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}