package com.lijiankun24.iocpractice.ioc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BindView.java
 * <p>
 * Created by lijiankun on 18/3/19.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int value();
}
