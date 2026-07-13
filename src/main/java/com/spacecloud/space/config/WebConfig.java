package com.spacecloud.space.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 🚨 이 어노테이션이 누락되면 스프링이 설정을 그냥 무시합니다!
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 브라우저에서 /images/** 주소로 들어오면 C:/upload/ 폴더로 연결해주는 핵심 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/app/upload");
    }
}