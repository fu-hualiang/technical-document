package org.example;

import org.example.bean.ApplicationContext;
import org.example.bean.ApplicationContextImpl;
import org.example.service.UserService;

public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ApplicationContextImpl("org.example");
        UserService userService = (UserService) context.getBean(UserService.class);
        userService.run();
    }
}