package com.club203.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 这里鉴权对话框，作为所有鉴权对话框的基类，仅包含界面
 * @author hehaoxing
 */
public abstract class AuthenDialog extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//相对于1920 x 1080显示器的分辨率
	private final int WIDTH = 270;
	private final int HEIGHT = 150;
	//当前分辨率
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//用户名密码框
	protected final JTextField usernameField;
	protected final JPasswordField passwordField;
	//确认按钮
	protected final JButton submitButton;
	protected final JButton resetButton;
	
	private final static Logger logger = LoggerFactory.getLogger(AuthenDialog.class);

	public AuthenDialog() {
		setTitle("接入鉴权");
		logger.info("Starting initializing authentication dialog");
		changeSwingSkin();
		Container container = getContentPane();
		container.setLayout(null);
		
		JLabel userLabel = new JLabel("用户名：");
		userLabel.setBounds(widthZoom(15), heightZoom(15), widthZoom(200), heightZoom(25));
		usernameField = new JTextField();
		usernameField.setBounds(widthZoom(85), heightZoom(15), widthZoom(160), heightZoom(25));
		
		JLabel passLabel = new JLabel("密码：");
		passLabel.setBounds(widthZoom(15), heightZoom(50), widthZoom(200), heightZoom(25));
		passwordField = new JPasswordField();
		passwordField.setBounds(widthZoom(85), heightZoom(50), widthZoom(160), heightZoom(25));

		submitButton = new JButton("确定");
		submitButton.setBounds(widthZoom(60), heightZoom(85), widthZoom(60), heightZoom(25));
		resetButton = new JButton("重置");
		resetButton.setBounds(widthZoom(150), heightZoom(85), widthZoom(60), heightZoom(25));

		container.add(userLabel);
		container.add(passLabel);
		container.add(usernameField);
		container.add(passwordField);
		container.add(submitButton);	
		container.add(resetButton);
		
		setResizable(false);
		setSize(widthZoom(WIDTH), heightZoom(HEIGHT));
		setLocationRelativeTo(null);
			
		//设置模态对话框，用于阻塞建连操作
		//即输入完成后才能退出
		setModal(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public int widthZoom(int width) {
		double widthZ = 1.0 * width / 1920;
		return (int)(widthZ * screenSize.getWidth());
	}
	
	public int heightZoom(int height) {
		double heightZ = 1.0 * height / 1080;
		return (int)(heightZ * screenSize.getHeight());
	}
	
	public void changeSwingSkin() {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "主界面皮肤加载失败");
			logger.info("Using the default look and feel.");
		}
	}
}
