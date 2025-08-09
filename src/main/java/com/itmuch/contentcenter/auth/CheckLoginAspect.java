package com.itmuch.contentcenter.auth;

import com.itmuch.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@Aspect
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckLoginAspect {

    @Autowired
    private final JwtOperator jwtOperator;

    @Around("@annotation(com.itmuch.contentcenter.auth.CheckLogin))")
    public Object around(ProceedingJoinPoint pjp) {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest httpServletRequest = attributes.getRequest();

            String token = httpServletRequest.getHeader("X-Token");
            if (!jwtOperator.validateToken(token)) {
                throw new SecurityException("Invalid Token!");
            }

            Claims claimsFromToken = jwtOperator.getClaimsFromToken(token);
            httpServletRequest.setAttribute("id", claimsFromToken.get("id"));
            httpServletRequest.setAttribute("wxNickname", claimsFromToken.get("wxNickname"));
            httpServletRequest.setAttribute("roles", claimsFromToken.get("roles"));

            return pjp.proceed();
        } catch (Throwable e) {
            throw new SecurityException("Invalid Token!");
        }
    }
    
}
