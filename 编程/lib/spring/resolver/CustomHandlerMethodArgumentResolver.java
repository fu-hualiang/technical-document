package com.example.graduation.resolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CustomHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String camelCaseParameterName = parameter.getParameterName();
        // 获取参数类型
        Class<?> parameterType = parameter.getParameterType();
        if (camelCaseParameterName == null) {
            return null;
        }
        // 用户定义的类型
        if (parameterType.getClassLoader()!=null){
            // 创建请求参数对象实例
            Object parameterObject = parameterType.getDeclaredConstructor().newInstance(); // 无参构造器
            // 获取请求参数对象的所有属性
            Field[] fields = parameterType.getDeclaredFields();
            // 循环处理所有属性
            for (Field field : fields) {
                // 获取属性名称，驼峰
                String fieldName = field.getName();
                // 从请求中获取属性值
                String fieldValue = webRequest.getParameter(camelToUnderline(fieldName));
                if (fieldValue != null) {
                    // 设置属性值
                    Method setMethod = parameterType.getMethod("set" + StringUtils.capitalize(fieldName), field.getType());
                    Constructor<?> constructor = field.getType().getDeclaredConstructor(String.class);
                    Object value = constructor.newInstance(fieldValue);
                    setMethod.invoke(parameterObject, value);
                }
            }
            return parameterObject;
        }
        // 取出方法上的 RequestParam 注解
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        // 如果有 RequestParam 注解，优先使用注解中的参数名
        if (requestParam != null && requestParam.value().length() > 0) {
            return webRequest.getParameter(requestParam.value());
        }
        if (String.class.isAssignableFrom(parameterType)) {
            return webRequest.getParameter(camelToUnderline(camelCaseParameterName));
        } else {
            Method valueOfMethod = parameterType.getMethod("valueOf", String.class);
            return valueOfMethod.invoke(parameterType, webRequest.getParameter(camelToUnderline(camelCaseParameterName)));
        }
    }

    private String camelToUnderline(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        String[] words = str.split("");
        List<String> wordList = Arrays.asList(words);
        for (int i = 0, len = wordList.size(); i < len; i++) {
            String word = wordList.get(i);
            if ((word.compareTo("A") >= 0 && word.compareTo("Z") <= 0) ||
                    (word.compareTo("0") >= 0 && word.compareTo("9") <= 0)) {
                sb.append("_").append(word.toLowerCase());
            } else {
                sb.append(word);
            }
        }
        return sb.toString();
    }

}