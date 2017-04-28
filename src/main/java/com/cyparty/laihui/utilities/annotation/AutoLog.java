package com.cyparty.laihui.utilities.annotation;

import java.lang.annotation.*;

/**
 * Created by lh2 on 2017/4/27.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {
    /**
     * 要执行的操作类型比如：add操作
     **/
    String operationType() default "";

    /**
     * 要执行的具体操作比如：添加用户
     **/
    String operationName() default "";
}
