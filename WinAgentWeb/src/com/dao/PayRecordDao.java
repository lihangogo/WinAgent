package com.dao;

import com.entity.PayRecord;

import java.util.ArrayList;

/**
 *  支付记录接口
 */
public interface PayRecordDao {

    /**
     * 添加用户充值记录
     * @param uid
     * @param money
     * @return
     */
    boolean addRecord(int uid,int money);

    /**
     *  根据该条记录的ID删除
     * @param pid
     * @return
     */
    boolean delRecordByPID(String pid);

    /**
     * 删除支付记录
     * @param payRecord
     * @return
     */
    boolean delRecord(PayRecord payRecord);

    /**
     * 根据用户ID删除所有所属记录
     * @param uid
     * @return
     */
    boolean delRecordsByUID(int uid);

    /**
     * 根据用户ID获取该用户的所有支付记录
     * @param uid
     * @return
     */
    ArrayList<PayRecord> getRecords(int uid);

    /**
     * 根据记录的ID获取记录信息
     * @param pid
     * @return
     */
    PayRecord getRecordByPID(String pid);

    //无修改
}
