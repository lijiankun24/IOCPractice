# IOCPractice

控制反转 IoC(Inversion of Control) 意思是把创建对象的权利交给框架，是框架的重要特性，并非面向对象编程的专业术语。----[百度百科](https://baike.baidu.com/item/%E6%8E%A7%E5%88%B6%E5%8F%8D%E8%BD%AC/1158025?fr=aladdin&fromid=4853&fromtitle=ioc)

在 Java 开发中最著名的 IoC 框架莫过于 Spring，在 Android 开发中，IoC 框架也有很多，比如：ButterKnife、EventBus、Dagger、Dagger2 等框架都运用了 IoC 的思想。本篇文章即介绍 IoC 在 Android 中的使用，通过开发一个简单的 IoC 框架帮助你更加深刻的理解 IoC 的思想及其相关知识。本篇中涉及到的代码均在 Github 上面[IOCPractice](https://github.com/lijiankun24/IOCPractice)。

<div align=center>
      <img src="/screenshot/catalog.png" width="616" height="335"/>
</div>

## 1. IoC 简介
IoC 控制反转不是一种技术，而是一种编程思想，体现着“不要找我们，我们找你”的思想。

在传统的编程写法中，大多都是在类内部通过关键字 `new` 主动创建类的实例对象并赋给引用，但是这样写会造成类和类之间耦合度过高的后果，难于测试。有了 IoC 容器之后，创建对象和查找依赖的工作就交给框架完成，并由容器注入对象，这样通过 IoC 容器实现了类和类之间的解耦。

IoC对编程带来的最大改变不是从代码上，而是从思想上，发生了“主从换位”的变化。应用程序原本是老大，要获取什么资源都是主动出击，但是在IoC/DI思想中，应用程序就变成被动的了，被动的等待IoC容器来创建并注入它所需要的资源。 ---- [谈谈对Spring IOC的理解](https://blog.csdn.net/qq_22654611/article/details/52606960)

提到 IoC 就不得不提到另一个概念：依赖注入（Dependence Injection，简称DI），DI 的概念是指组件之间的依赖关系由容器在运行期间决定。看完对 DI 的定义也许觉得很模糊，不清楚到底讲了什么意思。其实 IoC 和 DI 表达的是同一个意思，只不过是从不同的角度去描述。

### 1.1 IoC
在传统程序的编写过程中，在一个类中如果需要另外一个对象，就需要通过关键字 `new` 出另一个类的对象，然后才可以开始使用这个对象，使用完成之后，再将这个对象销毁，在这个过程中对象和对象之间耦合度很高。

有了 IoC 容器之后，所有的类都会在 IoC 容器中注册，并告诉 IoC 容器我是什么类，我需要什么对象，并且在运行到适当的时刻，将需要的对象创建并赋给我的引用，在适当的时刻再将该对象进行销毁。同时也会将创建我的对象，并赋给需要我的对象的类中。

在使用 IoC 容器框架编程时，对象的创建、销毁都是由 IoC 容器控制，而不是由引用它的对象控制。对于某个对象来说，以前是由它控制某个对象，现在是所有的对象都由 IoC 容器控制，所以叫控制反转。

### 1.2 DI
IoC 是指在程序运行期间，动态地向某个对象提供它所需要的对象，而 DI 就是实现这个思想的方法。比如，在 User 对象中需要使用到 Address 对象，我只需要告诉 IoC 容器，我需要一个 Address 对象，然后 IoC 容器会创建一个 Address 对象，然后像打针一样注入到 User 对象中，依赖注入就是这样来的。

在 Java 1.3 之后，可以通过反射在运行期间动态的创建对象、调用对象的方法、改变对象的属性，DI 就是通过反射实现注入的。

## 2. IoC 相关知识
在学习 IoC 之前，需要具备几点基础的 Java 知识，分别是注解、反射和动态代理，下面分别介绍。

### 2.1 注解
在平时写代码的时候，多多少少都会接触到注解，比如：`@Override`、`@Deprecated` 等等，还有一些第三方库中也会有，比如 ButterKnife、EventBus、Dagger2 等。

用完之后只有一个感受：真是太方便了，其实注解并没有多么复杂，和写普通的类（Class）、接口（Interface）差不了多少。

首先，看一下我们平时使用注解的时候的代码，比如使用 ButterKnife 绑定一个控件或为一个控件添加点击事件的代码如下所示：
``` Java
class ExampleActivity extends Activity {
  @BindView(R.id.user) EditText username;
  @BindView(R.id.pass) EditText password;

  @BindString(R.string.login_error) String loginErrorMessage;

  @OnClick(R.id.submit) void submit() {
    // TODO call server...
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    ButterKnife.bind(this);
    // TODO Use fields...
  }
}
```
那如果我们自己想写一个这样的注解，该怎么写呢？代码如下：
``` Java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int value();
}
```
如果对注解不熟悉，看见上面的代码可能会感觉到懵逼，没关系，我们一一介绍其中的含义：
* `@Target(ElementType.FIELD)`：表示该注解的作用域，其取值有以下几个：
  ``` Java
  public enum ElementType {
    TYPE,               // 类、接口（包括注解类型）、枚举类，
    FIELD,              // 属性
    METHOD,             // 方法
    PARAMETER,          // 参数
    CONSTRUCTOR,        // 构造方法
    LOCAL_VARIABLE,     // 局部变量
    ANNOTATION_TYPE,    // 注解
    PACKAGE,            // 包
  }
  ```
* `@Retention(RetentionPolicy.RUNTIME)`：表示在什么级别保留此信息：
  ``` Java
  public enum RetentionPolicy {
      SOURCE,          // 源码注解，注解仅存在源码级别，在编译的时候丢弃该注解

      CLASS,           // 编译时注解，注解会在 class 文件中存在，但是在运行时会被丢弃

      RUNTIME          // 运行时注解，注解会被记录在 class 文件中，在 VM 运行时也会保留该注解，所以可以通过反射获得该注解
  }
  ```

* `@interface`：表示此文件是一个注解，和 `Class`、`Interface` 一个级别

* `int value();`：表示此注解的一个方法，可以通过注解的这个方法得到一个 `int` 注解值

### 2.2 反射
通俗的讲，反射就是把一个类、类的属性和类的方法当做对象一样来操作，在运行态的时候可以动态地创建对象、调用对象的方法、修改对象的属性。

比如现在有一个类，如下所示：
``` Java
package com.lijiankun24.iocpractice;

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

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
```

通过普通的 `new` 的方式创建一个 User 的对象，并使用它，这种方式应该都非常熟悉了，但是用反射的方式该怎么实现呢？可以通过如下代码实现：
``` Java
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
        try {S
            Field field = clazz.getDeclaredField("name");
            field.setAccessible(true);  // 如果方法或者属性是私有的，则需要设置它的访问性为 true
            field.set(user, "newName");
            System.out.println("The name is " + user.getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
```

上述代码简单讲了下通过反射怎么动态地创建对象、调用对象的方法、修改对象的属性，至于更详细的反射则需要看书去学习。

### 2.3 动态代理
代理模式的概念就是：为原对象提供一个代理对象，原对象和代理对象对外提供相同的 API 方法，客户通过代理对象间接地操作原对象，但是客户并不知道它操作的是代理对象。
<div align=center>
      <img src="/screenshot/dynamic_proxy.png" width="474" height="372"/>
</div>

如上图所示，简单示意了代理模式的概念，其中
* Subject：表示一个接口类，有一个公共的方法 `request()`
* RealObject：实现了 `Subject` 接口，向外提供 `request()` 方法，是原对象（我们把它称之为委托对象）
* ProxyObject：也实现了 `Subject` 接口，向外提供 `request()` 方法，是代理对象
* Client：表示一个客户对象，想要调用 `RealObject` 的 `request()` 方法，但是并不是直接调用 `RealObject` 的 `request()` 方法，而是通过 `ProxyObject` 的 `request()` 方法间接地调用 `RealObject` 的 `request()` 方法

代理的概念如上所述，但是有不同的实现方式，分为静态代理和动态代理：
* 静态代理：代理类在编译后就已经实现好了，也就是在编译之后，代理类就是一个 class 文件
* 动态代理：代理类是在运行期间生成的。也就是说，在编译之后，并没有代理类对应的 class 文件，而是在运行期间动态的生成字节码文件，并加载到虚拟机中的。

#### 动态代理
在 IoC 中会用到 Java 的动态代理，所以在这里也是重点讲一个 Java 动态代理。

在 Java 的动态代理中一定会涉及到一个接口和一个类，分别是：`InvocationHandler(Interface)`和 `Proxy(Class)`。下面我们通过一个例子，更加形象地学习 Java 的动态代理。

首先定义一个接口如下所示：
``` Java
public interface Subject{
    void request();

    int add(int a, int b);
}
```

接着定义一个类实现这个接口，这个类就是我们的委托对象，代码如下：
``` Java
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
```

然后有一个调用处理器的类，这个调用处理器的类需要实现 `InvocationHandler` 接口，代码如下所示：
``` Java
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
```

最后就是一个 Client 类
``` Java
public class Client {

    public static void main(String[] args) {
        Subject realObject = new RealObject();
        InvocationHandler handler = new ProxyHandler(realObject);

        Subject proxyObject = (Subject) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                realObject.getClass().getInterfaces(), handler);
        proxyObject.request();
    }
}
```

通过上面的例子，我想大家对 Java 中的动态代理心里大概已经有概念，其实也不难，可以分为以下几个步骤：
* 首先需要有一个接口，其中定义了委托类和代理类都会实现的方法
* 定义委托类，实现该接口
* 定义调用处理器的类，实现 `InvocationHandler` 接口，`InvocationHandler` 的源码如下所示，其中只有一个方法，这个方法非常重要：
``` Java
public interface InvocationHandler {

    /**
     *
     * @param   proxy  指委托对象
     * @param   method 指调用的委托对象的方法的对象
     * @param   args   指调用的委托对象的方法的参数
     */
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable;
}
```
* 通过 `Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)` 方法创建一个代理对象，就可以调用代理对象的方法了，如下所示：
``` java
Subject proxyObject = (Subject) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                realObject.getClass().getInterfaces(), handler);
proxyObject.request();
```

好了，以上就介绍了 IoC 在 Android 中应用所需要用到的 Java 知识，下面就通过实现一个简单的 IoC 框架，更加深入地理解 IoC 在 Android 中的应用吧。

## 3. IoC 在 Android 中的应用
如果大家接触过 ButterKnife 框架的话，对下面这样的代码一定不陌生，
``` java
class ExampleActivity extends Activity {
  @BindView(R.id.user) EditText username;
  @BindView(R.id.pass) EditText password;

  @BindString(R.string.login_error) String loginErrorMessage;

  @OnClick(R.id.submit) void submit() {
    // TODO call server...
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.simple_activity);
    ButterKnife.bind(this);
    // TODO Use fields...
  }
}
```
在传统的写法中，我们要向获得在 xml 文件中声明的控件实例，需要通过 `findViewById` 来实现，但是 ButterKnife 只是用 `@BindView(R.id.xxx)` 就实现了同样的效果。同样的，ButterKnife 也用 `OnClick(R.id.xxx)` 为某个控件设置了监听回调事件，用 `BindString(R.string.xxx)` 实现了为某个 String 对象赋值，那我们也尝试着用 IoC 实现这样的一个框架。

### 3.1 注入布局文件
首先编写一个注解文件
```Java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentView {
    int value();
}
```
然后编写 ButterKnife 管理文件，如下所示：
``` java
public class ButterKnife {

    private static final String METHOD_SET_CONTENTVIEW = "setContentView";

    public static void inject(Activity activity) {
        injectContentView(activity);
    }

    private static void injectContentView(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        // 在 Activity 类上查找 ContentView.class 的注解
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            // 得到 布局文件的 Id 值
            int layoutId = contentView.value();
            if (layoutId != -1) {
                try {
                    // 通过反射调用 Activity 的 setContentView(int layoutId) 方法
                    Method method = clazz.getMethod(METHOD_SET_CONTENTVIEW, int.class);
                    method.setAccessible(true);
                    method.invoke(activity, layoutId);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }  
}
```

在 `Activity` 中就可以使用它注入布局文件了
``` Java
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
    }
}
```

### 3.2 注入 Views
首先编写注入 Views 的注解文件
``` Java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int value();
}
```
在 `ButterKnife` 中同样的去实现这个注解
``` Java
public class ButterKnife {

    private static final String METHOD_SET_CONTENTVIEW = "setContentView";

    private static final String METHOD_FIND_VIEW_BY_ID = "findViewById";

    public static void inject(Activity activity) {
        injectContentView(activity);
        injectView(activity);
    }

    ......

    private static void injectView(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        // 首先获取到 Activity 中所有的属性
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 遍历查找每个属性上是否存在 BindView.class 注解
            BindView viewInject = field.getAnnotation(BindView.class);
            if (viewInject != null) {
                // 得到控件的 id 值
                int viewId = viewInject.value();
                if (viewId != -1) {
                    try {
                        // 通过反射调用 Activity 的 findViewById(int id) 方法
                        Method method = clazz.getMethod(METHOD_FIND_VIEW_BY_ID, int.class);
                        Object resView = method.invoke(activity, viewId);
                        // 并将得到的控件对象赋予 Activity 中的该属性
                        field.setAccessible(true);
                        field.set(activity, resView);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
```

然后在 `Activity` 中也可以使用这个注解
``` Java
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    private TextView TV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        TV1.setText("Change the text");
    }
}
```

### 3.3 注入 Views 的事件监听
和上面有点不同的是，为了扩展的方便，需要为监听注解编写一个注解，如下所示
``` java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventType {
    Class<?> listenerType();  // 监听接口的类型

    String listenerSetter();  // 设置监听接口的 set 方法

    String methodName();      // 监听接口中的回调方法名称
}
```

然后就是编写事件监听注解
``` java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventType(listenerType = View.OnClickListener.class, listenerSetter = "setOnClickListener",
        methodName = "onClick")
public @interface OnClick {
    int[] value();
}
```

编写在 ButterKnife 中的使用
``` java
public class ButterKnife {

    private static final String METHOD_SET_CONTENTVIEW = "setContentView";

    private static final String METHOD_FIND_VIEW_BY_ID = "findViewById";

    public static void inject(Activity activity) {
        injectContentView(activity);
        injectView(activity);
        injectEvent(activity);
    }

    ......

    private static void injectEvent(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        // 得到 Activity 中所有的方法
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            // 遍历所有的方法，并得到方法上所有的注解对象
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                // 遍历每一个方法的所有注解
                Class<? extends Annotation> annotationType = annotation.annotationType();
                EventType eventBase = annotationType.getAnnotation(EventType.class);
                // 查找是否存在 EventType.class 注解
                if (eventBase != null) {
                    String listenerSetter = eventBase.listenerSetter();
                    String methodName = eventBase.methodName();
                    Class listenerType = eventBase.listenerType();
                    try {
                        // 通过反射调用注解的 value() 方法得到 viewIds 值
                        Method method1 = annotationType.getDeclaredMethod("value");
                        int[] viewIds = (int[]) method1.invoke(annotation, null);
                        // 通过 Java 动态代理的方式为每个 View 设置监听器
                        DynamicHandler handler = new DynamicHandler(activity);
                        handler.addMethod(methodName, method);
                        Object listener = Proxy.newProxyInstance(listenerType.getClassLoader()
                                , new Class[]{listenerType}, handler);
                        for (int viewId : viewIds) {
                            View view = activity.findViewById(viewId);
                            Method method2 = view.getClass().getMethod(listenerSetter, listenerType);
                            method2.invoke(view, listener);
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
```

在上述注入事件监听的过程中，用到了 Java 的动态代理，所以需要额外的编写一个 `InvocationHandler` 调用处理器类，如下所示：
``` java

class ButterKnifeDynamicHandler implements InvocationHandler {

    private WeakReference<Object> mObjectWR = null;

    private final HashMap<String, Method> mMethodHashMap = new HashMap<>();

    ButterKnifeDynamicHandler(Object object) {
        mObjectWR = new WeakReference<>(object);
    }

    void addMethod(String methodName, Method method) {
        mMethodHashMap.put(methodName, method);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object handler = mObjectWR.get();
        String name = method.getName();
        method = mMethodHashMap.get(name);
        if (handler != null && method != null) {
            return method.invoke(mObjectWR.get(), args);
        }
        return null;
    }
}
```

最后在 `Activity` 中使用它即可：
```java
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv1)
    private TextView TV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        TV1.setText("Change the text");
    }

    @OnClick({R.id.tv1, R.id.tv2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                Toast.makeText(MainActivity.this, "IOC For Test tv1", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv2:
                Toast.makeText(MainActivity.this, "IOC For Test tv2", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
```

这样，一个使用 IoC 思想实现的仿 ButterKnife 框架就实现了，上面涉及到的代码都在 Github 上面：[IoCPractice](https://github.com/lijiankun24/IOCPractice).

------------
**参考资料：**

[谈谈对Spring IOC的理解](https://blog.csdn.net/qq_22654611/article/details/52606960)  -- [Acrash](https://blog.csdn.net/qq_22654611)

[Android 进阶 教你打造 Android 中的 IOC 框架 【ViewInject】 （上）](https://blog.csdn.net/lmj623565791/article/details/39269193) -- [Hongyang](https://blog.csdn.net/lmj623565791)

[Android 进阶 教你打造 Android 中的 IOC 框架 【ViewInject】 （下）](https://blog.csdn.net/lmj623565791/article/details/39269193) -- [Hongyang](https://blog.csdn.net/lmj623565791)

[如何实现自定义Java运行时注解功能](https://www.jianshu.com/p/44ea03482da9) -- [r17171709](https://www.jianshu.com/u/470afcb80dc2?order_by=commented_at)

[代理模式及Java实现动态代理](https://www.jianshu.com/p/6f6bb2f0ece9) -- [xiazdong](https://www.jianshu.com/u/71dc075b6521)

[java的动态代理机制详解](http://www.cnblogs.com/xiaoluo501395377/p/3383130.html) -- [xiaoluo501395377](http://www.cnblogs.com/xiaoluo501395377/)
