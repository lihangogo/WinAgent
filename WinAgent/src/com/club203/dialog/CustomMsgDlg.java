package com.club203.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

/**
 * @author hehaoxing
 * 自定义弹出对话框
 * Swing自带MessageDialog存在一些不合理的特性
 */
public class CustomMsgDlg extends JDialog {
	//相对于1920 x 1080显示器的分辨率
	private final int WIDTH = 270;
	private final int HEIGHT = 150;
	//当前分辨率
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private CustomMsgDlg(String message){
		super();
		setTitle("提示");		
		setAlwaysOnTop(true);
		Container container = getContentPane();
		container.setLayout(null);	
		JLabel infoLabel = new JLabel();
		infoLabel.setFont(new Font(null, 0, 12));
		infoLabel.setBounds(widthZoom(75), heightZoom(22), widthZoom(180), heightZoom(40));
		//加载图标
		JLabel iconLabel = null;
		try {
			Image image = ImageIO.read(this.getClass().getResource("/img/info.png"));
			ImageIcon icon = new ImageIcon(image);
			iconLabel = new JLabel(icon);
			iconLabel.setBounds(10, 20, 60, 60);
		} catch (IOException e1) {		}
		try {
			JlabelSetText(infoLabel, message);
		} catch (InterruptedException e) { }

		JButton submitButton = new JButton("确定");
		submitButton.setBounds(widthZoom(195), heightZoom(75), widthZoom(55), heightZoom(28));		
		container.add(infoLabel);
		container.add(submitButton);
		container.add(iconLabel);
		//敲回车关窗口
		submitButton.addActionListener((e) -> CustomMsgDlg.this.dispose());
		submitButton.registerKeyboardAction((e) -> CustomMsgDlg.this.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		setResizable(false);		
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(widthZoom(WIDTH), heightZoom(HEIGHT));
		setDialogTimer(60);
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
	
	/**
	 * 设置对话框定时器
	 */
	private void setDialogTimer(int seconds) {
		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor(); 
		timer.schedule(() -> this.dispose(), seconds, TimeUnit.SECONDS);
	}
	
	private void JlabelSetText(JLabel jLabel, String longString) 
            throws InterruptedException {
        StringBuilder builder = new StringBuilder("<html>");
        char[] chars = longString.toCharArray();
        FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
        int start = 0;
        int len = 0;
        while (start + len < longString.length()) {
            while (true) {
                len++;
                if (start + len > longString.length())break;
                if (fontMetrics.charsWidth(chars, start, len) 
                        > jLabel.getWidth()) {
                    break;
                }
            }
            builder.append(chars, start, len-1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, longString.length()-start);
        builder.append("</html>");
        jLabel.setText(builder.toString());
    }
	
	public static void customMessageShow(String message) {
		//添加到事件派发线程
		EventQueue.invokeLater(new Runnable() {		
			@Override
			public void run() {
				new Thread(() -> {
					//重要，否则多个弹窗会出现只显示按钮的情况
					JDialog jDialog = new CustomMsgDlg(message);
					jDialog.setVisible(false);
					jDialog.repaint();
					jDialog.setVisible(true);
				}).start();  //设置不可见，重新绘制并可见，相当于一次刷新，解决现实白屏问题(Swing Bug真多)
			}
		});
	}
}

