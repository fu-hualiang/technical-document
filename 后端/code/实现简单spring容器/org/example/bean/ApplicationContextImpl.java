package org.example.bean;

import org.example.annotation.MyComponent;
import org.example.annotation.MyResource;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApplicationContextImpl implements ApplicationContext {

    private Map<Class<?>, Object> beanContainer = new HashMap<>();

    private String rootPath;

    public ApplicationContextImpl(String basePackage) throws Exception {
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource(basePackage.replaceAll("\\.", "\\\\"));
        if (url == null) return;
        String path = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
        // /D:/code/java-test/ioc/target/classes/
        rootPath = path.substring(0, path.length() - basePackage.length());

        loadBean(new File(path));

        injectBean();
    }

    private void loadBean(File file) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) return;
            for (File f : files) {
                loadBean(f);
            }
        } else {
            String path = file.getAbsolutePath().substring(rootPath.length() - 1);
            if (path.endsWith(".class")) {
                String className = path.replaceAll("\\\\", ".").substring(0, path.length() - 6);
                Class<?> clazz = Class.forName(className);
                if (clazz.getAnnotation(MyComponent.class) != null) {
                    Object o = clazz.getConstructor().newInstance();
                    if (clazz.getInterfaces().length > 0) {
                        beanContainer.put(clazz.getInterfaces()[0], o);
                    } else {
                        beanContainer.put(clazz, o);
                    }
                }
            }
        }
    }

    private void injectBean() throws IllegalAccessException {
        Set<Map.Entry<Class<?>, Object>> entries = beanContainer.entrySet();
        for (Map.Entry<Class<?>, Object> entry : entries) {
            Object object = entry.getValue();
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                MyResource myResource = field.getAnnotation(MyResource.class);
                if (myResource != null) {
                    field.setAccessible(true);
                    field.set(object, beanContainer.get(field.getType()));
                }
            }
        }
    }

    @Override
    public Object getBean(Class<?> clazz) {
        return beanContainer.get(clazz);
    }
}
