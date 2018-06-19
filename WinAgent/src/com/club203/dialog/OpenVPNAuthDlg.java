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

import com.club203.proxy.openvpn.Openvpn;
import com.club203.utils.EncryptUtils;

/**
 * 与OpenVPN的建连逻辑相关联，鉴权对话框
 * @author hehaoxing
 */
public class OpenVPNAuthDlg extends AuthenDialog {	
	private String username;
	private String passwd;
	
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
		logger.info("Initializing authentication dialog sucessful");
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
			try(FileWriter filewriter = new FileWriter(Openvpn.getAuthenFilepath())) {	
				filewriter.write(new String(usernameField.getText()).trim());
				filewriter.write("\n");
				filewriter.write(new String(passwordField.getPassword()).trim());
				//记录用户名与密码
				OpenVPNAuthDlg.this.username = new String(usernameField.getText()).trim();
				OpenVPNAuthDlg.this.passwd = new String(passwordField.getPassword()).trim();
				correctInput = true;
			} catch (IOException ex) { 
				correctInput = false;
			} finally {
				EncryptUtils.hideFile(Openvpn.getAuthenFilepath());
			}
			logger.info("Inputing username and password successful");
			OpenVPNAuthDlg.this.dispose();
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
}