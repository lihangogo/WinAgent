package com.service;

import com.entity.Account;

public interface AccountService {

    /**
     * 支付操作
     * @param account
     * @return
     */
    boolean pay(Account account,int addMoney);

    /**
     *  获取账户信息
     * @param uid
     * @return
     */
    Account getAccount(int uid);
}
