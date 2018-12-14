package com.dao;

import com.entity.Admin;

/**
 *  管理员接口
 */
public interface AdminDao {

    /**
     * 添加管理员
     * @param admin
     * @return
     */
    boolean addAdmin(Admin admin);

    /**
     * 删除管理员
     * @param admin
     * @return
     */
    boolean delAdmin(Admin admin);

    /**
     * 根据管理员ID删除管理员信息
     * @param aid
     * @return
     */
    boolean delAdminByAID(int aid);

    /**
     * 更新管理员信息
     * @param admin
     * @return
     */
    boolean updateAdmin(Admin admin);

    /**
     * 根据用户名和密码判断Admin是否存在
     * @param userName
     * @param password
     * @return
     */
    boolean findAdmin(String userName,String password);

    /**
     * 根据用户名和密码获取Admin
     * @param userName
     * @param password
     * @return
     */
    Admin getAdmin(String userName,String password);

    /**
     * 根据id号查询admin
     * @param id
     * @return
     */
    boolean findAdmin(int id);
}
