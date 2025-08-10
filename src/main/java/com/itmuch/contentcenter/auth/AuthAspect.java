package com.itmuch.contentcenter.auth;

import com.itmuch.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

@Component
@Aspect
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAspect {

    @Autowired
    private final JwtOperator jwtOperator;

    private void checkToken() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();

        String token = httpServletRequest.getHeader("X-Token");
        if (!jwtOperator.validateToken(token)) {
            throw new SecurityException("Invalid Token!");
        }

        Claims claimsFromToken = jwtOperator.getClaimsFromToken(token);
        httpServletRequest.setAttribute("id", claimsFromToken.get("id"));
        httpServletRequest.setAttribute("wxNickname", claimsFromToken.get("wxNickname"));
        httpServletRequest.setAttribute("role", claimsFromToken.get("roles"));
    }

    private static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest httpServletRequest = attributes.getRequest();
        return httpServletRequest;
    }

    @Around("@annotation(com.itmuch.contentcenter.auth.CheckLogin))")
    public Object checkLogin(ProceedingJoinPoint pjp) throws Throwable {
        try {
            this.checkToken();
        } catch (Throwable e) {
            throw new SecurityException("用户无权访问！");
        }

        return pjp.proceed();
    }

    @Around("@annotation(com.itmuch.contentcenter.auth.CheckAuthorization))")
    public Object checkAuthorization(ProceedingJoinPoint pjp) throws Throwable {
        try {
            // 校验TOKEN是否合法
            this.checkToken();

            // 校验用户的角色是否合法
            HttpServletRequest httpServletRequest = getHttpServletRequest();
            String role = (String)httpServletRequest.getAttribute("role");
            MethodSignature signature = (MethodSignature) pjp.getSignature();
            Method method = signature.getMethod();
            CheckAuthorization checkAuthorization = method.getAnnotation(CheckAuthorization.class);
            String value = checkAuthorization.value();

            if (!Objects.equals(value, role)) {
                throw new SecurityException("用户无权访问！");
            }
        } catch (Throwable e) {
            throw new SecurityException("用户无权访问！", e);
        }

        return pjp.proceed();
    }
    
}
