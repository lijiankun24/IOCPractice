package com.lijiankun24.iocpractice.ioc;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OnClick.java
 * <p>
 * Created by lijiankun on 18/3/21.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType = View.OnClickListener.class, listenerSetter = "setOnClickListener",
        methodName = "onClick")
public @interface OnClick {
    int[] value();
}
