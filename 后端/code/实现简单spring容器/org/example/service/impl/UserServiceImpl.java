package org.example.service.impl;

import org.example.annotation.MyComponent;
import org.example.annotation.MyResource;
import org.example.dao.UserDao;
import org.example.service.UserService;

@MyComponent
public class UserServiceImpl implements UserService {

    @MyResource
    UserDao userDao;

    @Override
    public void run() {
        userDao.run();
    }
}
