package com.wyett.common.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author : wyettLei
 * @date : Created in 2019/9/30 17:10
 * @description: TODO
 */

public class PropertyInvocationHandler implements InvocationHandler {
    private Properties properties;
    public PropertyInvocationHandler(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] obj) {
        ReadConf readConf = method.getAnnotation(ReadConf.class);
        if(readConf == null) {
            return null;
        }
        String value = readConf.value();
        String property = properties.getProperty(value);
//        if(StringUtils.isEmpty(property)) {
//            return null;
//        }

        Class<?> returnClass = method.getReturnType();
        if(returnClass.isPrimitive()) {
            if (returnClass.equals(int.class)) { return Integer.valueOf(property); }
            if (returnClass.equals(long.class)) { return Long.valueOf(property); }
            if (returnClass.equals(float.class)) { return Float.valueOf(property); }
            if (returnClass.equals(double.class)) { return Double.valueOf(property); }
            if (returnClass.equals(boolean.class)) { return Boolean.valueOf(property); }
            if (returnClass.equals(short.class)) { return Short.valueOf(property); }
//            if (returnClass.equals(char.class)) { return Character.valueOf(property); }
            if (returnClass.equals(byte.class)) { return Byte.valueOf(property); }
        } else {
            if (returnClass.equals(String.class)) { return String.valueOf(property); }
            if (returnClass.equals(String[].class)) { return getStringArray(property); }
        }
        return property;
    }

    /**
     * split String by ","
     * @param sv
     * @return
     */
    private String[] getStringArray(String sv) {
        return getStringArray(sv, ",");
    }

    /**
     * split String with splitStr
     * @param sv
     * @param splitStr
     * @return
     */
    private String[] getStringArray(String sv, String splitStr) {
        String[] value = null;
        if (sv == null) {
            value = null;
        } else if(sv.contains(splitStr)) {
            value = sv.split(splitStr);
        } else {
            value[0] = sv;
        }
        return value;
    }
}
