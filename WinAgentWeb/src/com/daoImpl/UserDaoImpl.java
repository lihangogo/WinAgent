package com.daoImpl;

import com.dao.UserDao;
import com.entity.User;
import com.utils.C3P0Util;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDaoImpl implements UserDao{
    @Override
    public boolean addUser(User user){
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="insert into user(username,nickname,password,phone,email,regtime,updatetime,real_name,student_id,ident)" +
                    " values(?,?,?,?,?, NOW() ,NOW(),?,?,? )";
            int update=qr.update(sql,user.getUserName(),user.getNickName(),user.getPassword(),
                    user.getPhone(),user.getEmail(),user.getRealName(),user.getStudentID(),user.getIdent());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addUsers(ArrayList<User> users) {
        return false;
    }

    @Override
    public boolean delUser(User user) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from user where username=?";
            int update=qr.update(sql,user.getUserName());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delUsers(ArrayList<User> users) {
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="update user set nickname=?,password=?," +
                    "updatetime=NOW(),phone=?,email=? where uid=?";
            int update=qr.update(sql,user.getNickName(), user.getPassword(),
                    user.getPhone(),user.getEmail(),user.getUserID());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getUID(String userName, String password) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where username='"+userName+"' and password='"+password+"'";
            Object object=qr.query(sql, resultSet -> {
                Integer uid=null;
                while(resultSet.next()){
                    uid=new Integer(resultSet.getInt(1));
                }
                return uid;
            });
            return (Integer)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findUserByUserNameAndPass(String userName, String password) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where username='"+userName+"' and password='"+password+"'";
            Object object=qr.query(sql, new ResultSetHandler() {
                @Override
                public Object handle(ResultSet rs) throws SQLException {
                    User user=getTheUser(rs);
                    return  user;
                }
            });
            return (User)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findUserByUserName(String userName) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where username='"+userName+"'";
            Object object=qr.query(sql, resultSet -> {
                User user=getTheUser(resultSet);
                return  user;
            });
            return (User)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User getTheUser(ResultSet rs) throws SQLException{
        User user=new User();
        if(rs.next()){
            user.setUserID(rs.getInt(1));
            user.setUserName(rs.getString(2));
            user.setNickName(rs.getString(3));
            user.setPassword(rs.getString(4));
            user.setPhone(rs.getString(5));
            user.setEmail(rs.getString(6));
            user.setRegisterTime(rs.getTimestamp(7).toLocaleString());
            user.setUpdateTime(rs.getTimestamp(8).toLocaleString());
        }else
            user=null;
        return user;
    }

    @Override
    public String getEmailByUserName(String userName) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where username='"+userName+"'";
            Object object=qr.query(sql, resultSet -> {
                StringBuffer email=new StringBuffer();
                if(resultSet.next())
                    email.append(resultSet.getString(6));
                return email;
            });
            return  object.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getUIDByUserName(String userName) {
        return 0;
    }

    @Override
    public String getEmailByUID(int uid) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where uid='"+uid+"'";
            Object object=qr.query(sql, rs->{
                        StringBuffer email=new StringBuffer();
                        if(rs.next())
                            email.append(rs.getString(6));
                        else
                            email.append(" ");
                        return email;
                    });
            return  object.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExisted(String userName) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where username='"+userName+"'";
            Object object=qr.query(sql, rs->{
                if(rs.next())
                    return true;
                else
                    return false;
            });
            return (Boolean)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isExisted(String userName, String password) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from user where username='"+userName+"' and password='"+password+"'";
            Object object=qr.query(sql, rs->{
                if(rs.next())
                    return true;
                else
                    return false;
            });
            return (Boolean)object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
