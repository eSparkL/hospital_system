package com.shanzhu.hospital.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Component
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                        new JwtInterceptor())
                .addPathPatterns("/**")
                // 公开访问的接口 - 按类别分组

                // 1. 新闻资讯相关（新增）
                .excludePathPatterns("/news/**")

                // 2. 文件上传接口（新增）
                .excludePathPatterns("/upload/**")

                // 3. 文件导出
                .excludePathPatterns("/patient/pdf")

                // 4. 登录接口（保持原样）
                .excludePathPatterns("/**/login")

                // 5. 病患注册（保持原样）
                .excludePathPatterns("/**/addPatient")

                // 6. 可选的：Swagger文档接口（如果有）
                .excludePathPatterns("/swagger-ui/**")
                .excludePathPatterns("/swagger-resources/**")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/webjars/**")

                // 7. 静态资源
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/assets/**")
                .excludePathPatterns("/css/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/uploads/**")  // 新增：上传的图片访问路径

                // 8. 错误页面
                .excludePathPatterns("/error");
    }

}