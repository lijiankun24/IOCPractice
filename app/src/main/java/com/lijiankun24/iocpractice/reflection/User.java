package com.lijiankun24.iocpractice.reflection;

/**
 * User.java
 * <p>
 * Created by lijiankun on 18/3/27.
 */

public class User {

    private String name;

    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
