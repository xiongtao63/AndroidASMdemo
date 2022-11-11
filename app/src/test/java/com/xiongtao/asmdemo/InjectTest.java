package com.xiongtao.asmdemo;


public class InjectTest {
    public void testApp(){
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("execute:"+(end-start)+" ms.");
    }
}
