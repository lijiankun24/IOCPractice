package com.lijiankun24.iocpractice.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Main.java
 * <p>
 * Created by lijiankun on 18/3/27.
 */

public class Main {

    public static void main(String[] args) {
        User user = newInstanceUser();
        invokeMethod(user);
        setField(user);
    }

    /**
     * 通过反射创建一个 User 对象
     *
     * @return 返回该 User 对象
     */
    private static User newInstanceUser() {
        Class<?> clazz = User.class;
        User user = null;
        try {
            Constructor<?>[] constructors = clazz.getConstructors();
            user = (User) constructors[0].newInstance("lijiankun", 20);
            System.out.println("The name is " + user.getName());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 通过反射调用 User 中的方法
     *
     * @param user User 对象
     */
    private static void invokeMethod(User user) {
        Class<?> clazz = user.getClass();
        try {
            Method method = clazz.getDeclaredMethod("setName", String.class);
            method.setAccessible(true);
            method.invoke(user, "24");
            System.out.println("The name is " + user.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射修改 User 对象中的私有属性
     *
     * @param user User 对象
     */
    private static void setField(User user) {
        Class<?> clazz = user.getClass();
        try {
            Field field = clazz.getDeclaredField("name");
            field.setAccessible(true);
            field.set(user, "newName");
            System.out.println("The name is " + user.getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
