package com.club203.dialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 显示投诉群的图片
 * @author 李瀚
 *
 */
public class ComplainDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(ComplainDlg.class);
	//当前分辨率
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private final int WIDTH = 430;
	private final int HEIGHT = 585;
	
	/**
	 * 投诉二维码框
	 */
	private ComplainDlg() {
		init();
		setResizable(false);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	/**
	 * 初始化对话框
	 */
	private void init() {
		setTitle("反馈");
		try {
			ImageIcon pic=new ImageIcon("img/complain.png");
			pic.setImage(pic.getImage().getScaledInstance(400,500,Image.SCALE_DEFAULT));
			JLabel jLabel=new JLabel(pic);
			getContentPane().add(jLabel);
			jLabel.setHorizontalAlignment(SwingConstants.CENTER);
			
			JButton buttonComplain = new JButton("完成");		
			buttonComplain.setText("完成");
			buttonComplain.setEnabled(true);
			buttonComplain.setBounds(100, 580, 100, 200);
			buttonComplain.addActionListener(new ActionListener() {		
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			getContentPane().add(buttonComplain);
			setLayout(new FlowLayout());
			setAlwaysOnTop(true);
			validate();
		}catch(Exception e) {
			logger.info("Failed to load image : complain.png");
		}
	}
	
	/**
	 * 接口
	 * @param message
	 */
	public static void complainDlgShow(String message) {
		//添加到事件派发线程
		EventQueue.invokeLater(new Runnable() {		
			@Override
			public void run() {
				new Thread(() -> {
					//重要，否则多个弹窗会出现只显示按钮的情况
					JDialog jDialog = new ComplainDlg();
					jDialog.setVisible(false);
					jDialog.repaint();
					jDialog.setVisible(true);
				}).start();  //设置不可见，重新绘制并可见，相当于一次刷新，解决现实白屏问题(Swing Bug真多)
			}
		});
	}
	
	public int widthZoom(int width) {
		double widthZ = 1.0 * width / 1920;
		return (int)(widthZ * screenSize.getWidth());
	}
	
	public int heightZoom(int height) {
		double heightZ = 1.0 * height / 1080;
		return (int)(heightZ * screenSize.getHeight());
	}
}
