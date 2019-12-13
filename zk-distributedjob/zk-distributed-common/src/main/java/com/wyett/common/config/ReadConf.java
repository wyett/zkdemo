package com.wyett.common.config;

import java.lang.annotation.*;

/**
 * @author : wyettLei
 * @date : Created in 2019/9/30 15:55
 * @description: TODO
 * */

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReadConf {
    String value() default "";
}