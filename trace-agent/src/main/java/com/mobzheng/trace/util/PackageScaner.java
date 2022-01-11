package com.mobzheng.trace.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PackageScaner {
    public PackageScaner(Consumer<Class<?>> accept) {
        this.accept = accept;
    }

    //抽象方法，等待以后使用工具的人编写
    public void dealClass(Class<?> klass) {
    }

    ;


    public final Consumer<Class<?>> accept;

    public static void scanPackage(String packageName, Consumer<Class<?>> accept) {
        PackageScaner packageScaner = new PackageScaner(accept);
        packageScaner.scanPackage(packageName);
    }

    //扫描包
    public void scanPackage(String packageName) {
        //获得当前线程的上下文加载器
        ClassLoader classLoader = this.getClass().getClassLoader();
        //将包名转化URL的文件路径的格式
        String path = packageName.replace(".", "/");

        try {
            //用类加载器获得指定路径下的资源的URL对象的枚举
            Enumeration<URL> urls = classLoader.getResources(path);
            //遍历枚举
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                //getProtocol方法是用来获得指定URL的协议名称
                //判断是否是Jar包
                if (url.getProtocol().equals("jar")) {
                    dealJar(url);
                } else {
                    try {
                        //url转化为等价的uri，用以生成一个文件类型对象
                        File curFile = new File(url.toURI());
                        //处理指定包里的文件
                        dealPackage(curFile, packageName);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void dealJar(URL url) {
        try {
            //通过URL对象获得一个Jar包的URL连接
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            //通过上述的连接获得jar文件
            JarFile jarFile = connection.getJarFile();
            //获得zip文件项的枚举
            Enumeration<JarEntry> entryList = jarFile.entries();
            //遍历枚举
            while (entryList.hasMoreElements()) {
                JarEntry jarEntry = entryList.nextElement();
                //如果每一个元素不是目录，且不是class文件会跳入下一次循环
                //确保找到每一个.class文件
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                //通过class文件名得到文件名称以及类名称
                String className = jarEntry.getName();
                className = className.replace(".class", "");
                className = className.replace("/", ".");

                try {
                    //获得类对象，并且用抽象方法处理
                    Class<?> klass = this.getClass().getClassLoader().loadClass(className);
//                    dealClass(klass);
                    accept.accept(klass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void dealPackage(File curFile, String packageName) {
        //获得文件的集合
        File[] files = curFile.listFiles();
        //遍历每个文件
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (!fileName.endsWith(".class")) {
                    continue;
                }
                String replace = file.getPath().replace("\\", ".");
                String className = replace.substring(replace.indexOf(packageName)).replace(".class", "");

                try {
                    //通过类名生成指定类对象，用抽象方法处理这个类
                    Class<?> klass = Class.forName(className);
//                    dealClass(klass);
                    accept.accept(klass);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                //递归调用该方法，知道没有文件为止，即遍历该目录下的所有文件
                dealPackage(file, packageName);
            }
        }
    }
}
