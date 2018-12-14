package com.service;

import com.entity.User;

public interface UserService {

    /**
     * 注册用户
     * @param user
     * @return
     */
    boolean addUser(User user);

    /**
     *  获取用户
     * @param userName
     * @param password
     * @return
     */
    User getUser(String userName,String password);

    /**
     *  更新用户信息
     * @param user
     * @return
     */
    boolean updateUser(User user);

    /**
     *  找回密码
     * @param username
     * @return
     */
    boolean retrievePassword(String username);
}
