package com.stu.helloserver.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限校验拦截器：校验请求头中的Authorization令牌
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取令牌
        String token = request.getHeader("Authorization");

        // 2. 令牌为空则拦截，返回401未授权
        if (token == null || token.trim().isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            // 封装错误响应并返回
            Result<Object> errorResult = Result.error(ResultCode.TOKEN_INVALID);
            new ObjectMapper().writeValue(response.getWriter(), errorResult);
            return false; // 拦截请求，不继续执行
        }

        // 3. 令牌存在则放行
        return true;
    }
}