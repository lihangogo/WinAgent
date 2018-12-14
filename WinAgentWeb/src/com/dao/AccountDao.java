package com.dao;

import com.entity.Account;

/**
 *  账单接口
 */
public interface AccountDao {

    /**
     * 为新注册的用户开通账户
     * @param uid
     * @return
     */
    boolean addAccount(int uid);

    /**
     *  根据用户ID查询账户余额
     * @param uid
     * @return
     */
    int getBalanceByUID(int uid);

    /**
     *  删除账户
     * @param account
     * @return
     */
    boolean delAccount(Account account);

    /**
     * 根据账户ID删除该条记录
     * @param accid
     * @return
     */
    boolean delAccountByAccid(String accid);

    /**
     * 根据用户ID删除该账户
     * @param uid
     * @return
     */
    boolean delAccountByUID(int uid);

    /**
     * 根据用户ID更新余额
     * @param uid
     * @param money
     * @return
     */
    boolean updateBalanceByUID(int uid,int money,int addMoney);

    /**
     *  根据用户ID查找账户
     * @param uid
     * @return
     */
    Account getAccountByUID(int uid);
}
