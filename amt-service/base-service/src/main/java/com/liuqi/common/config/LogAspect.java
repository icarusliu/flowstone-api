package com.liuqi.common.config;

import com.alibaba.fastjson.JSON;
import com.liuqi.common.annotations.Log;
import com.liuqi.common.bean.UserContext;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.utils.WebUtils;
import com.liuqi.sys.bean.dto.UserLogDTO;
import com.liuqi.sys.service.UserLogEntityService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面 
 *
 * @author  LiuQi 2025/6/5-12:07
 * @version V1.0
 **/
@Configuration
@Aspect
@Slf4j
public class LogAspect {
    @Autowired
    private UserLogEntityService logService;

    @Autowired
    private HttpServletRequest request;

    @Pointcut("@annotation(com.liuqi.common.annotations.Log)")
    public void pointCut(){}

    @Around("pointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Throwable e = null;
        Object result = null;
        try {
           result = joinPoint.proceed();
        } catch (Throwable ex) {
            e = ex;
        }

        // 无Log注解时不处理
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (null == logAnnotation) {
            if (null != e) {
                throw e;
            }
            return result;
        }

        // 未登录用户不处理
        UserLogDTO userLogDTO = new UserLogDTO();
        UserContext userContext = UserContextHolder.get()
                .orElse(null);
        if (null == userContext) {
            if (null != e) {
                throw e;
            }
            return result;
        }

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();

        // 构建参数Map
        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            paramMap.put(parameterNames[i], args[i]);
        }

        userLogDTO.setCreateTime(LocalDateTime.now());
        userLogDTO.setUserId(userContext.getUserId());
        userLogDTO.setUsername(userContext.getUsername());
        userLogDTO.setIp(WebUtils.getRequestHost(request));
        userLogDTO.setModule(logAnnotation.value());
        userLogDTO.setType(logAnnotation.op().getCode());
        userLogDTO.setSpentTime((int) (System.currentTimeMillis() - startTime));
        userLogDTO.setParams(JSON.toJSONString(paramMap));
        String resultStr = "";
        if (null == result) {
            if (null != e) {
                resultStr = e.getMessage();
            }
        } else {
            resultStr = JSON.toJSONString(result);
        }
        userLogDTO.setResult(resultStr);
        userLogDTO.setSuccess(null == e);
        userLogDTO.setPath(request.getRequestURI());
        userLogDTO.setMethod(request.getMethod());
        try {
            logService.insertAsync(userLogDTO);
        } catch (Exception ex) {
            log.error("添加日志失败", ex);
        }
        return result;
    }
}
