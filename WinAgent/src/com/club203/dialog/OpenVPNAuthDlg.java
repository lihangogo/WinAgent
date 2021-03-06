package com.club203.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.AccountBean;
import com.club203.beans.UserBean;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.dbService.AccountService;
import com.club203.service.dbService.OnlineService;
import com.club203.service.dbService.UserService;
import com.club203.utils.EncryptUtils;

/**
 * 与OpenVPN的建连逻辑相关联，鉴权对话框
 * @author LiHan
 */
public class OpenVPNAuthDlg extends AuthenDialog {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String passwd;
	private UserBean user;
	private AccountBean account;
	
	private boolean correctInput = false;
	private final static Logger logger = LoggerFactory.getLogger(OpenVPNAuthDlg.class);
	private static String rememberFilePath = "conf/remember.txt";
	
	public OpenVPNAuthDlg() {
		super();
		init();
		submitButton.addActionListener(new Submit());
		submitButton.registerKeyboardAction(new Submit(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		resetButton.addActionListener(new Reset());
		//删除OpenVPN的历史鉴权文件
		new File(Openvpn.getAuthenFilepath()).delete();
		logger.info("Initializing authentication dialog successful");
		//若之前调用了setModel方法，构造方法在窗口关闭前不会返回
		setVisible(true);
	}
	
	/**
	 * 初始化用户名密码控件
	 */
	private void init() {
		File file=null;
		BufferedReader br=null;
		try {
			file=new File(rememberFilePath);
			if(!file.exists()) {
				usernameField.setText("");
				passwordField.setText("");
			}else {
				br=new BufferedReader(new FileReader(file));
				String str=br.readLine().trim();
				if(str.length()==0) {
					usernameField.setText("");
					passwordField.setText("");
				}else {
					String[] strs=str.split(" ");
					usernameField.setText(strs[0]);
					passwordField.setText(strs[1]);
					logger.info("Read remembered information successful");
				}	
				br.close();
			}	
		}catch(Exception e) {
			if(br!=null) {
				try {
					br.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}	
		}
	}
	
	/**
	 * 提交按钮的消息响应
	 * @author club203LH
	 *
	 */
	class Submit implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(new String(usernameField.getText()).trim().length() == 0 || new String(passwordField.getPassword()).trim().length() == 0) {
				JOptionPane.showMessageDialog(OpenVPNAuthDlg.this, "用户名密码不允许为空", "警告", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			//记录用户名与密码，并加密密码
			OpenVPNAuthDlg.this.username = new String(usernameField.getText().trim());
			OpenVPNAuthDlg.this.passwd = new String(
					EncryptUtils.encrypt1(
							new String(
									passwordField.getPassword()).trim()));
			
			//验证用户名与密码
			UserService userService=new UserService();
			user=userService.selectUserByIdent(OpenVPNAuthDlg.this.username, OpenVPNAuthDlg.this.passwd);
			if(null!=user)	{
				
				//验证当前账号是否已登录
				OnlineService onlineService=new OnlineService();
				if(onlineService.isOnline(user.getUid())) {
					JOptionPane.showMessageDialog(OpenVPNAuthDlg.this, "该账号已登录，请勿重复登录...", 
							"警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				//验证当前在线用户数是否达到100人上限
				if(!onlineService.checkOnlineNumber()) {					
					JOptionPane.showMessageDialog(OpenVPNAuthDlg.this, "当前在线用户人数已达上限，请稍后再试...", 
							"警告", JOptionPane.WARNING_MESSAGE);
					return;
				}
				account=new AccountService().selectAccountByUID(user.getUid());
				if(account.getBalance()>0) {	//账户余额足够
					correctInput = true;
					logger.info("User: "+user.getUserName()+" login successful");
					
					//判断是否需要保存用户名和密码
					rememberPassword();
					
					try(FileWriter filewriter = new FileWriter(Openvpn.getAuthenFilepath())){
						filewriter.write(new String(usernameField.getText()).trim());
						filewriter.write(" ");
						filewriter.write(new String(passwordField.getPassword()).trim());
						filewriter.write(" ");
						filewriter.write(new String(""+user.getUid()));
						filewriter.write(" ");
						filewriter.write(new String(""+account.getBalance()));
						filewriter.close();
					}catch(IOException E) {
						correctInput = false;
					}finally {
						EncryptUtils.hideFile(Openvpn.getAuthenFilepath());
					}
					logger.info("Inputing username and password successful");
					OpenVPNAuthDlg.this.dispose();
				}else {   
					//账户余额不足
					correctInput = false;					
					JOptionPane.showMessageDialog(OpenVPNAuthDlg.this, "账户余额不足，请及时充值！", 
							"警告", JOptionPane.WARNING_MESSAGE);
					logger.info("User: "+user.getUserName()+" account balance is insufficient，Please recharge in time!");
				}
			}
			else {
				correctInput = false;
				logger.info("User: "+ OpenVPNAuthDlg.this.username +" login failed");
			}	
		}
	}
	
	/**
	 * 重置按钮的消息响应
	 * @author club203LH
	 *
	 */
	class Reset implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			usernameField.setText("");
			passwordField.setText("");
			username="";
			passwd="";
			logger.info("Clearing username and password succeddful");
		}
	}
	
	/**
	 * 记住用户名和密码选择控件的处理
	 */
	private void rememberPassword() {
		boolean bool=rememberCheckBox.isSelected();
		if(!bool)
			return;
		String userName="";
		String password="";
		userName = new String(usernameField.getText().trim());
		password = new String(passwordField.getPassword()).trim();
		File file=null;
		try {
			file=new File(rememberFilePath);		
			if(!file.exists())
				file.createNewFile();
			FileWriter fileWriter=new FileWriter(file,false);
			fileWriter.write(userName+" "+password);
			fileWriter.flush();
			fileWriter.close();	
			logger.info("Remember username and password succeddful");
		}catch(Exception exception) {
			logger.info("Remember username and password failed");
			JOptionPane.showMessageDialog(OpenVPNAuthDlg.this, "记住密码失败", "警告", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public boolean isCurrentInput() {
		return correctInput;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public UserBean getUser() {
		return user;
	}
	
}
