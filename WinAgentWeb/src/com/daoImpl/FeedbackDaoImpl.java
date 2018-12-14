package com.daoImpl;

import com.dao.FeedbackDao;
import com.entity.Feedback;
import com.utils.C3P0Util;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDaoImpl implements FeedbackDao {

    @Override
    public boolean addFeedback(Feedback feedback) {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="insert into feedback(qid,uid,time,category,content)" +
                    " values(?,?,NOW(),?,?)";
            int update=qr.update(sql,feedback.getId(),feedback.getUid(),
                    feedback.getCategory(),feedback.getContent());
            if(update>0)
                return true;
            return false;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Feedback> searchFeedback() {
        try{
            QueryRunner qr=new QueryRunner(C3P0Util.getDataSource());
            String sql="select * from feedback";
            List<Feedback> list=qr.query(sql,new BeanListHandler<>(Feedback.class));
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
