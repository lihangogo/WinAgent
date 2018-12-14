package com.serviceImpl;

import com.daoImpl.AdminDaoImpl;
import com.service.AdminService;

public class AdminServiceImpl implements AdminService {

    @Override
    public boolean searchAdmin(int id) {

        return new AdminDaoImpl().findAdmin(id);
    }
}
