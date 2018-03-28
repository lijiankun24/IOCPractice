package com.lijiankun24.iocpractice.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Client.java
 * <p>
 * Created by lijiankun on 18/3/27.
 */

public class Client {

    public static void main(String[] args) {
        Subject realObject = new RealObject();
        //    我们要代理哪个真实对象，就将该对象传进去，最后是通过该真实对象来调用其方法的
        InvocationHandler handler = new ProxyHandler(realObject);

        Subject proxyObject = (Subject) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                realObject.getClass().getInterfaces(), handler);
        proxyObject.request();
    }
}
