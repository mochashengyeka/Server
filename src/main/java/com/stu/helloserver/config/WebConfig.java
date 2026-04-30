package com.stu.helloserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // 旧拦截器 AuthInterceptor 已移除，由 Spring Security 接管接口鉴权。
    // 如果将来需要添加 CORS、静态资源等 MVC 配置，可在此处覆写对应方法。
}