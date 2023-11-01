package org.example.dao.impl;

import org.example.annotation.MyComponent;
import org.example.dao.UserDao;

@MyComponent
public class UserDaoImpl implements UserDao {
    @Override
    public void run() {
        System.out.println("----------------run---------------");
    }
}
