package com.utils;


import java.util.Date;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 利用javamail技术发送邮件，实现找回密码的功能
 * @author lihan
 */
public class JMail {
    /**
     * 禁用无参构造方法
     */
    private JMail(){
    }
    /**
     * 发送邮件
     * @param to 收件人邮箱地址
     * @param mess1   邮件内容
     * @return 是否发送成功
     */
    public static boolean sendMail(String to,String mess1){
        //发件人的邮箱地址和密码
        String from="18653059888@163.com";
        String password="Lh991978";
        //发件人邮箱的SMTP服务器地址
        String mailSMTPHost="smtp.163.com";

        //创建参数配置，用于连接邮件服务器的参数配置
        Properties pro=new Properties();
        pro.setProperty("mail.transport.protocol", "smtp");
        pro.setProperty("mail.host", mailSMTPHost);
        pro.setProperty("mail.smtp.auth", "true");

        //根据配置创建会话对象，用于和邮件服务器交互
        Session session =Session.getDefaultInstance(pro);
        session.setDebug(true);
        try{
            //创建一封邮件
            MimeMessage message=createMimeMessage(session, from, to,mess1);
            //根据session获取邮件传输对象
            Transport transport =session.getTransport();
            //使用邮箱账号和密码连接邮件服务器
            transport.connect(from, password);
            //发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            //发送邮件
            transport.close();
            //邮件发送成功
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 创建一封只包含文本的简单邮件
     * @param session  会话对象
     * @param sendMail	发件人邮箱地址
     * @param receiveMail 收件人邮箱地址
     * @param mess   邮件内容
     * @return	邮件对象
     * @throws Exception
     */
    private static MimeMessage createMimeMessage(Session session,
                                                 String sendMail,String receiveMail,String mess)
                                                    throws Exception{
        //创建一封空邮件
        MimeMessage message=new MimeMessage(session);
        //设置发送方属性
        message.setFrom(new InternetAddress(sendMail, "加速器", "UTF-8"));
        //设置接收方属性
        message.setRecipient(MimeMessage.RecipientType.TO,
                new InternetAddress(receiveMail,"注册用户","UTF-8"));
        //设置邮件主题及编码方式
        message.setSubject("找回密码", "UTF-8");
        //设置邮件正文，使用了HTML标签
        message.setContent(mess,"text/html;charset=UTF-8");
        //设置发送时间
        message.setSentDate(new Date());
        //保存设置
        message.saveChanges();
        return message;
    }
}
