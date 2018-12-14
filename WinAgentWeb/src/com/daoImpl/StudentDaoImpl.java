package com.daoImpl;

import com.utils.C3P0Util;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;

public class StudentDaoImpl {

    /**
     * 根据学号获取姓名
     * @param id
     * @param name
     * @return
     */
    public boolean getStudentByName(String id,String name){
        try {
            QueryRunner qr = new QueryRunner(C3P0Util.getDataSource());
            String sql = "select * from 2018student where id='"+id+"'";
            Object object=qr.query(sql, resultSet -> {
                String nameTag=null;
                if(resultSet.next())
                    nameTag=new String(resultSet.getString(2));
                return nameTag;
            });
            if(object==null)
                return false;
            else if(object.toString().equals(name))
                return true;
            else
                return false;
        } catch (SQLException e) {
            return false;
        }
    }

}
