package com.stu.helloserver.config;

import com.stu.helloserver.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类：注册拦截器并配置拦截/放行规则
 */
@Configuration // 标记为配置类，SpringBoot自动加载
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册权限拦截器
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**") // 拦截所有/api开头的请求
                .excludePathPatterns(       // 放行以下接口（无需鉴权）
                        "/api/users/login",  // 登录接口
                        "/api/users",        // 新增用户接口
                        "/api/users/*"       // 获取单个用户接口
                );
    }
}