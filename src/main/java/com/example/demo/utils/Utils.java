package com.example.demo.utils;

import jakarta.annotation.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.*;

public class Utils {
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static void copyNonEmptyProperties(Object src, Object target) {

        BeanUtils.copyProperties(src, target,getNullPropertyNames(src));
    }
    // then use Spring BeanUtils to copy and ignore null using our function
    public static void copyNonEmptyProperties(Object src, Object target, String[] ignoreList) {
        String[] nulls = getNullPropertyNames(src);
        ArrayList<String> resultList = new ArrayList<>();
        Collections.addAll(resultList, ignoreList);
        Collections.addAll(resultList, nulls);
        BeanUtils.copyProperties(src, target, resultList.toArray(String[]::new));
    }
}
