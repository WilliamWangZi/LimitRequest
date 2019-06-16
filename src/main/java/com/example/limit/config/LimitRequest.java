package com.example.limit.config;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class LimitRequest {

    private static ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
    @Pointcut("@annotation(com.example.limit.annotation.Limit)")
    public void limit(){}

    @Before("limit()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        //获取方法名
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        //获取登录用户session
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String user = (String)request.getSession().getAttribute("user");
        //利用putIfAbsent的特性，如果key存在，返回对应val，如果key不存在，返回null
        String val = concurrentHashMap.putIfAbsent(method.getName() + user ,user);
        if(val != null){
            HttpServletResponse response = attributes.getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write("请求过于频繁".getBytes());
            throw new Exception("请求过于频繁");
        }

    }
    @After("limit()")
    public void after(JoinPoint joinPoint){
        //获取方法名
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        //获取登录用户session
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String user = (String)request.getSession().getAttribute("user");
        concurrentHashMap.remove(method.getName() + user);
    }
}
