package com.club203.core;

import java.awt.AWTException;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.club203.beans.Proxy;
import com.club203.config.ConfReader;
import com.club203.config.ProxyReader;
import com.club203.dialog.AgentTrayIcon;
import com.club203.dialog.ComplainDlg;
import com.club203.dialog.ExitDialog;
import com.club203.dialog.MessageDialog;

/**
 * 负责客户端界面的展示
 * 仅通过与AgentPresenter类通信改变状态
 */

public class AgentView extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	
	//相对于1920 x 1080显示器的分辨率，长度可伸缩
	private final int WIDTH = 340;
	private final int HEIGHT = 80 + serviceTypeCount * 100;
	//当前分辨率
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	//展示客户端目前状态
	private static JLabel proxyStatus = new JLabel("");
	//业务种类数量
	private static int serviceTypeCount = ConfReader.getConfig().getServiceType().length;
	
	private List<JLabel> proxyLabel = new ArrayList<>(serviceTypeCount);
	private List<JButton> proxyOpen = new ArrayList<>(serviceTypeCount);
	private List<JButton> proxyClose = new ArrayList<>(serviceTypeCount);
	private List<JComboBox<String>> proxyComboBox = new ArrayList<>(serviceTypeCount);
	//系统托盘类
	private AgentTrayIcon trayIcon;
	//维护一个业务逻辑类
	private AgentPresenter agent = null;
	
	private final static Logger logger = LoggerFactory.getLogger(AgentView.class);
	
	public AgentView(String title){
		super(title);
		changeSwingSkin();
		setType(Window.Type.NORMAL);
		logger.info("Starting initializing winagent gui");
		Container container = getContentPane();
		setLayout(null);
		
		String[] serviceTypeList = ConfReader.getConfig().getServiceType();
		
		for(int i = 0; i < serviceTypeList.length; i++){
			JLabel label = new JLabel(serviceTypeList[i]);
			label.setText(serviceTypeList[i]);
			label.setBounds(widthZoom(25), heightZoom(25 + i * 100), widthZoom(300), heightZoom(25));
			proxyLabel.add(label);
			container.add(proxyLabel.get(i));
			
			JButton buttonOpen = new JButton("打开");
			buttonOpen.setText("打开");
			if(serviceTypeList[i].trim().equals("校外线路")) {
				buttonOpen.setEnabled(false);
			}else {
				buttonOpen.setEnabled(true);
			}	
			buttonOpen.setBounds(widthZoom(40), heightZoom(75 + i * 100), widthZoom(100), heightZoom(25));
			proxyOpen.add(buttonOpen);
			container.add(proxyOpen.get(i));
			
			JButton buttonClose = new JButton("关闭");
			buttonClose.setText("关闭");
			buttonClose.setEnabled(false);
			buttonClose.setBounds(widthZoom(190), heightZoom(75 + i * 100), widthZoom(100), heightZoom(25));
			proxyClose.add(buttonClose);
			container.add(proxyClose.get(i));
			
			JComboBox<String> comboBox = new JComboBox<>();
			comboBox.setBounds(widthZoom(150), heightZoom(25 + i * 100), widthZoom(150), heightZoom(25));
			proxyComboBox.add(comboBox);
			container.add(proxyComboBox.get(i));
		}
		//状态栏
		proxyStatus.setBounds(widthZoom(25), heightZoom(15 + serviceTypeList.length * 100), widthZoom(210), heightZoom(25));
		setGuiText("没有正在使用的代理");
		container.add(proxyStatus);	
		addComboboxFromConf(ProxyReader.getProxy());
		
		//投诉按钮
		JButton buttonComplain = new JButton("投诉");
		buttonComplain.setText("投诉");
		buttonComplain.setEnabled(true);
		buttonComplain.setBounds(widthZoom(230), heightZoom(15 + serviceTypeList.length * 100), widthZoom(60), heightZoom(25));
		buttonComplain.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				ComplainDlg.complainDlgShow("...");
			}
		});
		container.add(buttonComplain);
		
		agent = AgentPresenter.getAgentPresenter();
		//服务类型从1开始，而数据索引一般从0开始
		for(int i = 0; i < serviceTypeList.length; i++) {
			proxyOpen.get(i).addActionListener(new proxyOpenAction(i + 1));
			proxyClose.get(i).addActionListener(new proxyCloseAction(i + 1));
		}
		WindowListener listener = new Terminator();
		this.addWindowListener(listener);
		//设置程序图标
		try {
			Image image=Toolkit.getDefaultToolkit().createImage("img/logo.png");
			this.setIconImage(image);
		} catch (Exception e) { }
		//增加系统托盘图标
		this.trayIcon = addSystemTray();
		
		setVisible(true);
		setResizable(false);
		setSize(widthZoom(WIDTH), heightZoom(HEIGHT));
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		
		logger.info("Initializing winagent gui sucessful");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public int widthZoom(int width) {
		double widthZ = 1.3 * width / 1920;
		return (int)(widthZ * screenSize.getWidth());
	}
	
	public int heightZoom(int height) {
		double heightZ = 1.3 * height / 1080;
		return (int)(heightZ * screenSize.getHeight());
	}
	
	public void setProxyStatus(String str) {
		proxyStatus.setText(str);
	}
	
	/**
	 * 设置状态栏变量
	 */
	public void setGuiText(String text) {
		proxyStatus.setText(text);
	}
	
	public static void changeSwingSkin() {
		try{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			logger.info("Using nimbus look and feel");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "主界面皮肤加载失败");
			logger.info("Using default look and feel");
		}
	}
	
	public void addComboboxFromConf(Map<String, Proxy> conf){
		Iterator<Map.Entry<String, Proxy>> confIter = conf.entrySet().iterator();
		while(confIter.hasNext()) {  			  
			Map.Entry<String, Proxy> entry = confIter.next();  		  
			String ServiceType = entry.getValue().getServiceType();
		    String ProxyType = entry.getValue().getProxyType();
		    if(ServiceType == null || ProxyType == null){
		    	new MessageDialog("错误的服务类型或代理类型,请检查配置文件.").show();
				continue;
			} 
		    String[] serviceTypeList = ConfReader.getConfig().getServiceType();
		    for(int i = 0; i < serviceTypeList.length; i++) {
		    	if(ServiceType.equals(serviceTypeList[i])){
		    		proxyComboBox.get(i).addItem(entry.getKey());
		    		break;
		    	}
		    }
		}  
	}
	
	class proxyOpenAction implements ActionListener {
		private int serviceType;
		//服务种类从1开始，而索引从0开始
		public proxyOpenAction(int serviceType) {
			this.serviceType = serviceType;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String ProxyName = (String)proxyComboBox.get(serviceType - 1).getSelectedItem();
			agent.openClicked(serviceType, ProxyName);
			
		}
	}
	
	class proxyCloseAction implements ActionListener {
		private int serviceType;
		//服务种类从1开始，而索引从0开始
		public proxyCloseAction(int serviceType) {
			this.serviceType = serviceType;
		}
		public void actionPerformed(ActionEvent arg0) {			
			agent.closeClicked(serviceType);
		}
	}
	
	class Terminator extends WindowAdapter{
		@Override
		public void windowClosing(WindowEvent e) {
            //调用方法关闭客户端
			int operator = new ExitDialog().getOperater();
			switch (operator) {
				case 1:
					//最小化
					if(SystemTray.isSupported()) {
						AgentView.this.setVisible(false);
					}
					break;
				case 2:
					//退出
					agent.shutdown();
					AgentView.this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					AgentView.this.dispose();
					break;
				default:
					break;
			}
		}
	}
	
	/**
	 * 将程序添加到系统托盘中
	 */
	private AgentTrayIcon addSystemTray() {
		//判断系统是否支持托盘功能
		if(SystemTray.isSupported()) {
			Image image = null;
			try {
				image=Toolkit.getDefaultToolkit().createImage("img/logo.png");
			} catch (Exception e) {
				return null;
			}
			JPopupMenu popupMenu = new JPopupMenu();
			JMenuItem itemExit = new JMenuItem("退出");
			JMenuItem itemShow = new JMenuItem("显示");
			itemExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AgentView.this.agent.shutdown();
					AgentView.this.removeSystemTray();
					AgentView.this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					AgentView.this.dispose();
				}
			});
			itemShow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AgentView.this.setVisible(true);
				}
			});
			popupMenu.add(itemExit);
			popupMenu.add(itemShow);
			AgentTrayIcon trayIcon = new AgentTrayIcon(AgentView.this, image, "WinAgent", popupMenu);
			SystemTray sysTray = SystemTray.getSystemTray();
			try {
				sysTray.add(trayIcon);
			} catch (AWTException e) { }
			return trayIcon;
		}
		return null;
	}
	
	/**
	 * 关闭系统托盘
	 */
	public void removeSystemTray() {
		SystemTray sysTray = SystemTray.getSystemTray();
		sysTray.remove(trayIcon);
		if(trayIcon != null) {
			trayIcon.close();
		}
	}
	
	public void setOpenButton(int i, boolean status) {
		proxyOpen.get(i).setEnabled(status);
	}
	
	public void setCloseButton(int i, boolean status) {
		proxyClose.get(i).setEnabled(status);
	}
	
}
