package com.example.lighthouse.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 转换实体(Entity)与数据传输对象(DTO)
 */
public class MyConverter {
    /**
     * 创建并返回一个 targetClass 的对象,该对象包含有传入的 originObject 的相同属性名的属性值
     * originObject 可以是普通对象或者List对象
     * @param originObject
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> Object beanConverter(Object originObject, Class<T> targetClass) {
        if (originObject == null) return null;
        if (originObject instanceof List){
            //originObject是List类型
            if (((List<?>) originObject).size() == 0)
                return new ArrayList<>();
            List<T> targetList = new ArrayList<>();
            for (Object origin : ((List<?>) originObject)) {
                T target = null;
                try {
                    target = targetClass.getConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BeanUtils.copyProperties(origin, target);
                targetList.add(target);
            }
            return targetList;
        }else{
            //originObject不是List类型
            T target = null;
            try {
                target = targetClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(originObject, target);
            return target;
        }
    }

    public static <T> T singleBeanConverter(Object origin, Class<T> targetClass) {
        if (origin == null)
            return null;
        T target = null;
        try {
            target = targetClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(origin, target);
        return target;
    }

    public static <T> List<T> listBeanConverter(List originList, Class<T> targetClass) {
        if (originList == null || originList.size() == 0)
            return new ArrayList<>();
        List<T> targetList = new ArrayList<>();
        for (Object origin : originList) {
            T target = null;
            try {
                target = targetClass.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(origin, target);
            targetList.add(target);
        }
        return targetList;
    }
}
