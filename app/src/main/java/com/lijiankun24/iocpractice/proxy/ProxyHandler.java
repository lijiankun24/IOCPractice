package com.lijiankun24.iocpractice.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * ProxyHandler.java
 * <p>
 * Created by lijiankun on 18/3/27.
 */

public class ProxyHandler implements InvocationHandler {

    private Subject mRealObject = null;

    public ProxyHandler(Subject realObject) {
        mRealObject = realObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("ProxyHandler PreProcess");
        method.invoke(mRealObject, args);
        System.out.println("ProxyHandler PostProcess");
        return null;
    }
}
