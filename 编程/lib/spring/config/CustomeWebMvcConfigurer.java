package com.example.graduation.config;

import com.example.graduation.resolver.CustomHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CustomeWebMvcConfigurer implements WebMvcConfigurer {

    @Autowired
    public CustomHandlerMethodArgumentResolver customHandlerMethodArgumentResolver;

    public CustomeWebMvcConfigurer(List<HandlerMethodArgumentResolver> argumentResolvers) {
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customHandlerMethodArgumentResolver);
    }
}
