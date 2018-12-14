package com.dao;

import com.entity.User;

import java.util.ArrayList;

/**
 *  用户接口
 */
public interface UserDao {

    /**
     *  添加用户
     * @param user
     * @return
     */
    boolean addUser(User user);

    /**
     *  批量添加用户
     * @param users
     * @return
     */
    boolean addUsers(ArrayList<User> users);

    /**
     *  删除用户
     * @param user
     * @return
     */
    boolean delUser(User user);

    /**
     *  批量删除用户
     * @param users
     * @return
     */
    boolean delUsers(ArrayList<User> users);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    boolean updateUser(User user);

    /**
     * 获取用户的ID
     * @param userName
     * @param password
     * @return
     */
    int getUID(String userName,String password);

    /**
     *  根据用户名和密码获取User实体
     * @param userName 用户名
     * @param password 密码
     * @return User实体
     */
    User findUserByUserNameAndPass(String userName, String password);

    /**
     *  根据用户名获取User实体
     * @param userName
     * @return
     */
    User findUserByUserName(String userName);
    /**
     *  根据用户名获取该用户的注册邮箱
     * @param userName
     * @return
     */
    String getEmailByUserName(String userName);

    /**
     *  根据用户名获取用户ID
     * @param userName
     * @return
     */
    int getUIDByUserName(String userName);

    /**
     * 根据用户的ID获取用户的注册邮箱地址
     * @param uid
     * @return
     */
    String getEmailByUID(int uid);

    /**
     *  判断用户名是否已经存在
     * @param userName
     * @return
     */
    boolean isExisted(String userName);

    /**
     *  判断用户是否存在
     * @param userName
     * @param password
     * @return
     */
    boolean isExisted(String userName,String password);
}
