package com.lijiankun24.iocpractice.proxy;

/**
 * RealObject.java
 * <p>
 * Created by lijiankun on 18/3/27.
 */

public class RealObject implements Subject {

    @Override
    public void request() {
        System.out.println("RealObject request invoked");
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
