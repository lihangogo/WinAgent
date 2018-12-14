package com.service;

public interface AdminService {

    /**
     * 根据id号查询管理员是否存在
     * @param id
     * @return
     */
    boolean searchAdmin(int id);
}
