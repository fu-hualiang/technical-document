package com.example.lighthouse.utils;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Character.toUpperCase;

/**
 * 用于实体(Entity)与数据传输对象(DTO)之间进行属性值交换
 */
public class MyBeanUtils {

    /**
     * 生成并返回一个 targetClass 类型的对象
     * 该对象包含有 originObject 对象的相同属性名的属性值
     *
     * @param origin 原对象，属性值来源
     * @param targetClass 目标类，属性值传递的目标
     * @return 根据目标类生成对象
     */
    public static <T> T BeanBuilder(Object origin, Class<T> targetClass) {
        if (origin == null) return null;
        T target = null;
        try {
            target = targetClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (target != null) BeanUtils.copyProperties(origin, target);
        return target;
    }

    /**
     * 生成并返回一个 List<targetClass> 类型的对象
     * 该 List 对象中的元素包含有 originList 对象中对应元素的相同属性名的属性值
     *
     * @param originList 原对象，属性值来源
     * @param targetClass 目标类，属性值传递的目标
     * @return 以目标类生成的对象为元素的List
     */
    public static <T> List<T> BeanBuilder(List<Object> originList, Class<T> targetClass) {
        if (originList == null || originList.size() == 0) return new ArrayList<>();
        List<T> targetList = new ArrayList<>();
        for (Object origin : originList) {
            T target = null;
            try {
                target = targetClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (target != null) BeanUtils.copyProperties(origin, target);
            targetList.add(target);
        }
        return targetList;
    }

    /**
     * 将 origin 中属性的值传递给 target 中具有相同属性名的属性，并返回 target
     *
     * @param origin 原对象，属性值来源
     * @param target 目标对象，属性值传递的目标
     * @return 属性值修改后的目标对象
     */
    public static <T> T BeanReplicator(Object origin, T target) {
        if (origin == null) return null;
        BeanUtils.copyProperties(origin, target);
        return target;
    }

    /**
     * 依据 map 中的属性名映射关系，将 origin 中属性的值传递给 target 中的属性，并返回 target
     *
     * @param origin 原对象，属性值来源
     * @param target 目标对象，属性值传递的目标
     * @param map 属性名映射关系
     * @return 属性值修改后的目标对象
     */
    public static <T> T BeanReplicator(Object origin, T target, Map<String, String> map) {
        if (origin == null) return null;
        for (String key : map.keySet()) {
            try {
                Method getMethod = origin.getClass().getMethod("get" + upperCase(key));
                Type returnType = getMethod.getGenericReturnType();
                Method setMethod = target.getClass().getMethod("set" + upperCase(map.get(key)), (Class<?>) returnType);
                setMethod.invoke(target, getMethod.invoke(origin));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return target;
    }
    /**
     * 首字母大写(进行字母的ascii编码前移，效率是最高的)
     *
     * @param str 需要转化的字符串
     */
    private static String upperCase(String str){
        char[] chars = str.toCharArray();
        chars[0] = toUpperCase(chars[0]);
        return String.valueOf(chars);
    }

}
