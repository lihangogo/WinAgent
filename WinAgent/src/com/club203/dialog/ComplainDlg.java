package com.club203.dialog;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.service.openvpn.ReconnectOpenvpnProxy;

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
	
	/**
	 * 初始化对话框
	 */
	private void init() {
		try {
			Image image = ImageIO.read(this.getClass().getResource("/img/complain.png"));
			image.getWidth(this);
			image.getHeight(this);
		}catch(IOException e) {
			logger.info("Failed to load image : complain.png");
		}
		
		
	}
}
