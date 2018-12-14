package com.serviceImpl;

import com.dao.AccountDao;
import com.dao.UserDao;
import com.daoImpl.AccountDaoImpl;
import com.daoImpl.UserDaoImpl;
import com.entity.User;
import com.service.UserService;
import com.utils.EncryptUtils;
import com.utils.JMail;

public class UserServiceImpl implements UserService {

    /**
     * 用户注册，添加其用户信息和账户信息
     * @param user
     * @return
     */
    @Override
    public boolean addUser(User user) {
        String userName=user.getUserName();
        user.setPassword(EncryptUtils.encrypt1(user.getPassword()));  //加密密码
        UserDao userDao=new UserDaoImpl();
        AccountDao accountDao=new AccountDaoImpl();
        if(userDao.isExisted(userName))
            return false;
        else{
            if(userDao.addUser(user)){
                int uid=userDao.getUID(user.getUserName(),user.getPassword());
                if(accountDao.addAccount(uid)){
                    return true;
                }else{
                    userDao.delUser(user);
                    return false;
                }
            }
            else
                return false;
        }
    }

    /**
     * 获取用户对象
     * @param userName
     * @param password
     * @return
     */
    @Override
    public User getUser(String userName, String password) {
        password=EncryptUtils.encrypt1(password);
        UserDao userDao=new UserDaoImpl();
        User user=null;
        if(userDao.isExisted(userName,password))
            user=userDao.findUserByUserNameAndPass(userName,password);
        return user;
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public boolean updateUser(User user) {
        UserDao userDao=new UserDaoImpl();
        if(userDao.updateUser(user))
            return true;
        return false;
    }

    /**
     * 获取密码
     * @param username
     * @return
     */
    @Override
    public boolean retrievePassword(String username) {
        UserDao userDao=new UserDaoImpl();
        if(!userDao.isExisted(username))
            return false;
        User user=userDao.findUserByUserName(username);
        String to=user.getEmail();
        String message="用户"+user.getUserName()+"    您的密码是 "+user.getPassword()+"   ,请尽量记住。。。";
        if(JMail.sendMail(to,message))
            return true;
        return false;
    }

}
