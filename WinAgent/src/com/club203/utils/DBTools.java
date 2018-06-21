package com.club203.utils;

import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBTools {
	public static SqlSessionFactory sessionFactory;
    static{
        try {
            //使用MyBatis提供的Resources类加载mybatis的配置文件
            Reader reader = Resources.getResourceAsReader("mybatis.cfg.xml");
            //构建sqlSession的工厂
            sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    /**
     * 创建能执行映射文件中sql的sqlSession
     * @return
     */
    public static SqlSession getSession(){
        return sessionFactory.openSession();
    }
    
    /**
     * 关闭SqlSession
     * @param session
     */
    public static void closeSqlSession(SqlSession session){
		if(session != null){
			session.close();
		}
	}
    
    /**
     * 获取当前网络时间
     * @return 自1970年1月1日的毫秒值
     */
    public static long getNetworkTime() {
        try {
            URL url = new URL("http://www.baidu.com");
            URLConnection conn = url.openConnection();
            conn.connect();
            long dateL = conn.getDate();
            return dateL;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
}
