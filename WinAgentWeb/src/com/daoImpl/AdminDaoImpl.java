package com.daoImpl;

import com.dao.AdminDao;
import com.entity.Admin;
import com.utils.C3P0Util;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDaoImpl implements AdminDao{

    @Override
    public boolean addAdmin(Admin admin) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="insert into admin(username,password,authority)" +
                    " values(?,?,?)";
            int update=qr.update(sql,admin.getUserName(),admin.getPassword(),
                    admin.getAuthority());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delAdmin(Admin admin) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from admin where admin_id=?";
            int update=qr.update(sql,admin.getAdminID());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delAdminByAID(int aid) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from admin where admin_id=?";
            int update=qr.update(sql,aid);
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateAdmin(Admin admin) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="update admin set username=?,password=?,authority=?," +
                    "where admin_id=?";
            int update=qr.update(sql,admin.getUserName(),
                    admin.getPassword(), admin.getAuthority());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean findAdmin(String userName, String password) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from admin where username='"+userName+"' and password='"+password+"'";
            Object object=qr.query(sql, rs->{
                if(rs.next())
                    return new Integer(1);
                return null;
            });
            if(object!=null)
                return true;
            else
                return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Admin getAdmin(String userName, String password) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from admin where username='"+userName+"' and password='"+password+"'";
            Object object=qr.query(sql,rs -> {
                Integer adminID=null;
                Admin admin=new Admin();
                if(rs.next()){
                    adminID=new Integer(rs.getInt(1));
                    admin.setAdminID(adminID);
                    admin.setUserName(rs.getString(2));
                    admin.setAuthority(rs.getString(3));
                    admin.setPassword(rs.getString(4));
                }
                else
                    admin=null;
                return admin;
            });
            return (Admin)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean findAdmin(int id) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from admin where admin_id='"+id+"'";
            Object object=qr.query(sql, rs->{
                if(rs.next())
                    return true;
                return false;
            });
            return (boolean)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
