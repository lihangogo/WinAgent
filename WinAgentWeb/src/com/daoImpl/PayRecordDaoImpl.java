package com.daoImpl;

import com.dao.PayRecordDao;
import com.entity.PayRecord;
import com.utils.C3P0Util;
import com.utils.RandomIDUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PayRecordDaoImpl implements PayRecordDao{
    @Override
    public boolean addRecord(int uid, int money) {
        try{
            String pid= RandomIDUtils.getRandomID();
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="insert into payrecord(pid,uid,paytime,paynum)" +
                    " values(?,?,NOW() ,? )";
            int update=qr.update(sql,pid,uid,money);
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delRecordByPID(String pid) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from payrecord where pid=?";
            int update=qr.update(sql,pid);
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delRecord(PayRecord payRecord) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from payrecord where pid=?";
            int update=qr.update(sql,payRecord.getPayRecordID());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delRecordsByUID(int uid) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="delete from payrecord where uid=?";
            int update=qr.update(sql,uid);
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<PayRecord> getRecords(int uid) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from payrecord where uid='"+uid+"'";
            Object object=qr.query(sql, new ResultSetHandler() {
                @Override
                public Object handle(ResultSet rs) throws SQLException {
                    ArrayList<PayRecord> payRecords=new ArrayList<>();
                    while(rs.next()){
                        PayRecord payRecord=new PayRecord();
                        payRecord.setPayRecordID(rs.getString(1));
                        payRecord.setUserID(rs.getInt(2));
                        payRecord.setPayTime(rs.getTimestamp(3).toLocaleString());
                        payRecord.setPayNum(rs.getInt(4));
                        payRecords.add(payRecord);
                    }
                    return payRecords;
                }
            });
            return (ArrayList<PayRecord>) object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PayRecord getRecordByPID(String pid) {
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from payrecord where pid='"+pid+"'";
            Object object=qr.query(sql, new ResultSetHandler() {
                @Override
                public Object handle(ResultSet rs) throws SQLException {
                    PayRecord payRecord=new PayRecord();
                    if(rs.next()){
                        payRecord.setPayRecordID(rs.getString(1));
                        payRecord.setUserID(rs.getInt(2));
                        payRecord.setPayTime(rs.getTimestamp(3).toLocaleString());
                        payRecord.setPayNum(rs.getInt(4));
                    }
                    else
                        payRecord=null;
                    return payRecord;
                }
            });
            return (PayRecord) object;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
