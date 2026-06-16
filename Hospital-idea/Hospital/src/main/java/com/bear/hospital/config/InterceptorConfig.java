package com.bear.hospital.config;

import com.bear.hospital.interceptors.JwtInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtInterceptor())
                // 拦截所有API请求
                .addPathPatterns("/**")
                // 放行登录接口
                .excludePathPatterns("/**/login")
                // 放行患者PDF导出（不需要登录即可下载）
                .excludePathPatterns("/patient/pdf");
    }
}
