package com.example.javawebtest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CustomeWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    public HandlerMethodArgumentResolver customHandlerMethodArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customHandlerMethodArgumentResolver);
        System.out.println(customHandlerMethodArgumentResolver);
    }
}
