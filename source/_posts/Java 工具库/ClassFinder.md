category: Java工具
date: 2016-06-14
title: ClassFinder
---
```java
import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;

public class ClassFinder {
    private final static Logger logger = Logger.getLogger(ClassFinder.class);


    public static List<Class> findClasses(String packageName, String suffix) {
        Collection<String> packageSet = new HashSet<>();
        packageSet.add(packageName);

        Collection<String> suffixSet = new HashSet<>();
        suffixSet.add(suffix);

        return findClasses(packageSet, suffixSet);
    }

    public static List<Class> findClasses(Collection<String> packageNameColl, Collection<String> suffixColl) {
        logger.trace("find classes from:" + packageNameColl + " suffixes:" + suffixColl);
        List<String> classNameList = getAllClasseNames();
        classNameList = filter(classNameList, packageNameColl, suffixColl);

        return loadClasses(classNameList);
    }

    private static List<Class> loadClasses(List<String> classNames) {
        List<Class> retList = new ArrayList<>();
        for (String className : classNames) {
            try {
                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                retList.add(clazz);
            } catch (ClassNotFoundException e) {
                logger.error("[can't load class:" + className + "]", e);
            }
        }

        return retList;
    }

    private static List<String> filter(List<String> classNameList, Collection<String> packageNameColl, Collection<String> suffixColl) {
        List<String> retList = new ArrayList<>();
        for (String className : classNameList) {
            if (isFit(className, packageNameColl, suffixColl))
                retList.add(className);
        }

        return retList;
    }

    private static Collection<URL> getClassPathUrls() {
        String pathSeparator = System.getProperty("path.separator", ":");
        String classpath = System.getProperty("java.class.path", ".");
        String[] pathArray = classpath.split(pathSeparator);
        Collection<URL> urls = new HashSet<>();
        for (String path : pathArray) {
            if (path.endsWith(".jar")) {
                try {
                    URL jarUrl = new URL("jar:file:" + path + "!/");
                    urls.add(jarUrl);
                } catch (MalformedURLException e) {
                    logger.error("[jarPath:" + path + "]", e);
                }
            } else {
                try {
                    URL fileUrl = new URL("file://" + path);
                    urls.add(fileUrl);
                } catch (MalformedURLException e) {
                    logger.error("[path:" + path + "]", e);
                }
            }
        }

        return urls;
    }

    public static List<String> getAllClasseNames() {
        Collection<URL> urls = getClassPathUrls();
        List<String> classNames = new ArrayList<>();
        for (URL location : urls) {
            logger.trace("find classes from:" + location);
            try {
                if ("jar".equals(location.getProtocol())) {
                    classNames.addAll(jar(location));
                } else if ("file".equals(location.getProtocol())) {
                    classNames.addAll(file(location));
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }

        return classNames;
    }

    private static boolean isFit(String className, Collection<String> packageNameColl, Collection<String> suffixColl) {
        boolean packageFlag = false;
        for (String packageName : packageNameColl) {
            if (className.startsWith(packageName)) {
                packageFlag = true;
                break;
            }
        }

        if (!packageFlag)
            return false;

        boolean suffixFlag = false;
        for (String suffix : suffixColl) {
            if (className.endsWith(suffix)) {
                suffixFlag = true;
                break;
            }
        }

        return suffixFlag;
    }

    private static List<String> jar(URL location) throws IOException {
        List<String> classNames = new ArrayList<>();
        JarURLConnection conn = (JarURLConnection) location.openConnection();
        JarFile jarfile = conn.getJarFile();

        Enumeration<JarEntry> jarEntrys = jarfile.entries();
        while (jarEntrys.hasMoreElements()) {
            JarEntry jarEntry = jarEntrys.nextElement();

            if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                continue;
            }

            String className = jarEntry.getName();
            className = className.replaceFirst(".class$", "");
            className = className.replace('/', '.');
            classNames.add(className);
        }

        return classNames;
    }

    private static List<String> file(URL location) {
        List<String> classNames = new ArrayList<>();
        File dir = new File(URLDecoder.decode(location.getPath()));
        if ("META-INF".equals(dir.getName())) {
            dir = dir.getParentFile(); // Scrape "META-INF" off
        }
        if (dir.isDirectory()) {
            scanDir(dir, classNames, "");
        }
        return classNames;
    }

    private static void scanDir(File dir, List<String> classNames, String packageName) {
        logger.trace("scan dir: " + dir);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDir(file, classNames, packageName + file.getName() + ".");
            } else if (file.getName().endsWith(".class")) {
                String name = file.getName();
                name = name.replaceFirst(".class$", "");
                classNames.add(packageName + name);
            }
        }
    }
}

```