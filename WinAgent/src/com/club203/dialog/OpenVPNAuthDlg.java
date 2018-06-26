package com.club203.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.AccountBean;
import com.club203.beans.UserBean;
import com.club203.proxy.openvpn.Openvpn;
import com.club203.service.dbService.AccountService;
import com.club203.service.dbService.UserService;
import com.club203.utils.EncryptUtils;

/**
 * 与OpenVPN的建连逻辑相关联，鉴权对话框
 * @author hehaoxing
 */
public class OpenVPNAuthDlg extends AuthenDialog {	
	private String username;
	private String passwd;
	private UserBean user;
	private AccountBean account;
	
	private boolean correctInput = false;
	private final static Logger logger = LoggerFactory.getLogger(OpenVPNAuthDlg.class);
	
	public OpenVPNAuthDlg() {
		super();
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
	
	class Submit implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(new String(usernameField.getText()).trim().length() == 0 || new String(passwordField.getPassword()).trim().length() == 0) {
				JOptionPane.showMessageDialog(OpenVPNAuthDlg.this, "用户名密码不允许为空", "警告", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			//记录用户名与密码
			OpenVPNAuthDlg.this.username = new String(usernameField.getText()).trim();
			OpenVPNAuthDlg.this.passwd = new String(passwordField.getPassword()).trim();
			
			//验证用户名与密码
			UserService userService=new UserService();
			user=userService.selectUserByIdent(OpenVPNAuthDlg.this.username, OpenVPNAuthDlg.this.passwd);
			if(null!=user)	{
				account=new AccountService().selectAccountByUID(user.getUid());
				if(account.getBalance()>0) {	//账户余额足够
					correctInput = true;
					logger.info("User: "+user.getUserName()+" login successful");
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
				}else {   //账户余额不足
					correctInput = false;
					logger.info("User: "+user.getUserName()+" account balance is insufficient，Please recharge in time!");
				}
			}
			else {
				correctInput = false;
				logger.info("User: "+user.getUserName()+" login failed");
			}	
		}
	}
	
	class Reset implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			usernameField.setText("");
			passwordField.setText("");
			logger.info("Clearing username and password succeddful");
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
