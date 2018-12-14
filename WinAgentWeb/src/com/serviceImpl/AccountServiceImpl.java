package com.serviceImpl;

import com.dao.AccountDao;
import com.dao.PayRecordDao;
import com.daoImpl.AccountDaoImpl;
import com.daoImpl.PayRecordDaoImpl;
import com.entity.Account;
import com.service.AccountService;

public class AccountServiceImpl implements AccountService{

    @Override
    public boolean pay(Account account,int addMoney) {
        AccountDao accountDao=new AccountDaoImpl();
        PayRecordDao payRecordDao=new PayRecordDaoImpl();
        if(accountDao.updateBalanceByUID(account.getUserID(),account.getBalance(),addMoney)){
            payRecordDao.addRecord(account.getUserID(), account.getBalance());
            return true;
        }
        return false;
    }

    @Override
    public Account getAccount(int uid) {
        AccountDao accountDao=new AccountDaoImpl();
        Account account=accountDao.getAccountByUID(uid);
        if(account!=null)
            return account;
        return null;
    }
}
