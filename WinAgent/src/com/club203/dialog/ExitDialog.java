package com.club203.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;

/**
 * @author hehaoxing
 * 退出窗口，防止误操作
 */
public class ExitDialog extends JDialog {
	//相对于1920 x 1080显示器的分辨率
	private final int WIDTH = 270;
	private final int HEIGHT = 150;
	//当前分辨率
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//选择条目
	private JRadioButton minimize = new JRadioButton("最小化到系统托盘", true); 
	private JRadioButton terminator = new JRadioButton("退出客户端");
	//确认
	private JButton confirmButton = new JButton("确定");
	//选择条目组
	private ButtonGroup group = new ButtonGroup();
	//对应的操作：0代表取消(直接关闭)、1代表最小化、2代表退出
	private int operator = 0;
	
	public ExitDialog() {
		super();
		setTitle("关闭提示");
		setAlwaysOnTop(true);
		Container container = getContentPane();
		container.setLayout(null);
		minimize.setBounds(widthZoom(30), heightZoom(25), widthZoom(200), heightZoom(20));
		terminator.setBounds(widthZoom(30), heightZoom(50), widthZoom(200), heightZoom(20));
		group.add(minimize);
		group.add(terminator);
		container.add(minimize);
		container.add(terminator);
		
		confirmButton.setBounds(widthZoom(155), heightZoom(75), widthZoom(75), heightZoom(25));
		confirmButton.addActionListener(new ConfirmListener());
		container.add(confirmButton);
		
		this.addWindowListener(new TerminatorListener());
		setResizable(false);		
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setSize(widthZoom(WIDTH), heightZoom(HEIGHT));
		setModal(true);
		setVisible(true);
	}
	
	/**
	 * 调整参数来调整缩放倍数
	 */
	private int widthZoom(int width) {
		double widthZ = 1.0 * width / 1920;
		return (int)(widthZ * screenSize.getWidth());
	}
	
	private int heightZoom(int height) {
		double heightZ = 1.0 * height / 1080;
		return (int)(heightZ * screenSize.getHeight());
	}
	
	public int getOperater() {
		return operator;
	}
	
	class TerminatorListener extends WindowAdapter{
		@Override
		public void windowClosing(WindowEvent e) {
            ExitDialog.this.dispose();
            operator = 0;
		}
	}
	
	class ConfirmListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(minimize.isSelected()) {
				operator = 1;
			} else if(terminator.isSelected()) {
				operator = 2;
			} else {
				operator = 0;
			}
			ExitDialog.this.dispose();
		}
	}

}
