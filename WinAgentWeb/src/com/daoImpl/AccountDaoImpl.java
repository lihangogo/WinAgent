package com.daoImpl;

import com.dao.AccountDao;
import com.entity.Account;
import com.utils.C3P0Util;
import com.utils.RandomIDUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDaoImpl implements AccountDao {
    @Override
    public boolean addAccount(int uid) {
        try{
            String accid= RandomIDUtils.getRandomID();
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="insert into account(accid,uid,balance,latestpay,latestpaynum)" +
                    " values(?,?,?, NOW() ,? )";
            int update=qr.update(sql,accid,uid,10000,10000 );
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getBalanceByUID(int uid) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from account where uid='"+uid+"'";
            Object object=qr.query(sql, resultSet -> {
                Integer balance=null;
                if(resultSet.next()){
                    balance=new Integer(resultSet.getInt(3));
                }
                else
                    balance=null;
                return balance;
            });
            return (Integer) object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delAccount(Account account) {
        return false;
    }

    @Override
    public boolean delAccountByAccid(String accid) {
        return false;
    }

    @Override
    public boolean delAccountByUID(int uid) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from account where uid=?";
            int update=qr.update(sql,uid);
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateBalanceByUID(int uid, int money,int addMoney) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="update account set balance=?,latestpay=NOW(),latestpaynum=? " +
                    "where uid=?";
            int update=qr.update(sql,money,
                    addMoney, uid);
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account getAccountByUID(int uid) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from account where uid='"+uid+"'";
            Object object=qr.query(sql, rs -> {
                Account account=new Account();
                if(rs.next()){
                    account.setAccountID(rs.getString(1));
                    account.setUserID(rs.getInt(2));
                    account.setBalance(rs.getInt(3));
                    account.setLatestPayTime(rs.getTimestamp(4).toLocaleString());
                    account.setLatestPayNum(rs.getInt(5));
                }
                else
                    account=null;
                return account;
            });
            return (Account) object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
