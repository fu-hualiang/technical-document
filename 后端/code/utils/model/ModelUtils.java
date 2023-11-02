package com.example.javawebtest.utils.model;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于实体(Entity)与数据传输对象(DTO)之间进行属性值交换
 */
public class ModelUtils {

    /**
     * 生成并返回一个 targetClass 类型的对象
     * 该对象包含有 source 对象的相同属性名的属性值
     *
     * @param source      属性值来源
     * @param targetClass 属性值传递的目标类型
     * @return targetClass 类型的对象
     */
    public static <S, T> T make(S source, Class<T> targetClass) {
        if (source == null) return null;
        T target = null;
        try {
            target = targetClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (target != null) BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 生成并返回一个 List<targetClass> 对象
     * 该 List 对象中的元素包含有 originList 对象中对应元素的相同属性名的属性值
     *
     * @param sourceList  属性值来源
     * @param targetClass 属性值传递的目标类型
     * @return 以 targetClass 类型的对象为元素的 List
     */
    public static <S, T> List<T> make(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) return new ArrayList<>();
        List<T> targetList = new ArrayList<>();
        for (S source : sourceList) {
            T target = null;
            try {
                target = targetClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (target != null) BeanUtils.copyProperties(source, target);
            targetList.add(target);
        }
        return targetList;
    }

    /**
     * 将 source 的非空属性值复制给 target 的同名同类型属性
     */
    public static <S, T> void copy(S source, T target) {
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Class<?> targetClass = target.getClass();

        for (Field sourceField : sourceFields) {
            sourceField.setAccessible(true);
            try {
                // 跳过空属性值
                if (sourceField.get(source) == null) continue;
                Field targetField = targetClass.getDeclaredField(sourceField.getName());
                targetField.setAccessible(true);
                targetField.set(target, sourceField.get(source));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
