package com.cyparty.laihui.utilities.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;

/**
 * Created by lh2 on 2017/4/27.
 */
@Aspect
@Component
public class SystemLogAspect {

    //Controller层切点
    @Pointcut("execution (* com.cyparty.laihui.controller..*.*(..))")
    public void controllerAspect() {
    }

    /**
     * 后置通知 用于拦截Controller层记录用户的操作
     */
    @After("controllerAspect()")
    public void after(JoinPoint joinPoint) {
        System.out.println("测试插入日志");
    }
}
