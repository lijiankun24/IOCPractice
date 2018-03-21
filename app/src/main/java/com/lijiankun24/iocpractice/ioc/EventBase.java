package com.lijiankun24.iocpractice.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EventBase.java
 * <p>
 * Created by lijiankun on 18/3/21.
 */

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {
    Class<?> listenerType();

    String listenerSetter();

    String methodName();
}
