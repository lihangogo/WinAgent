package com.club203.dialog;

import java.awt.Dimension;  
import java.awt.Image;  
import java.awt.TrayIcon;  
import java.awt.event.MouseAdapter;  
import java.awt.event.MouseEvent;  
  
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;  
import javax.swing.event.PopupMenuListener;  

/**
 * @author hehaoxing
 * 系统托盘
 */
public class AgentTrayIcon extends TrayIcon {
    //选择菜单载体
	private JDialog dialog;
	/** 
	* 构造方法，创建带指定图像、工具提示和弹出菜单的 MyTrayIcon 
	* @param frame 弹窗对应的主窗口
	* @param image 显示在系统托盘的图标 
	* @param ps    鼠标移动到系统托盘图标上的提示信息 
	* @param Jmenu 弹出菜单 
	*/  
	public AgentTrayIcon(JFrame frame, Image image, String ps, JPopupMenu Jmenu) {  
		super(image, ps);  
		this.dialog = new JDialog();  
		this.dialog.setUndecorated(true);//取消窗体装饰  
		this.dialog.setAlwaysOnTop(true);//设置窗体始终位于上方     
		//设置系统图标大小为自动调整  
		this.setImageAutoSize(true);  
		//为TrayIcon设置鼠标监听器 
		this.addMouseListener(new MouseAdapter() {  
			@Override 
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()== 2) { //鼠标双击图标  
					frame.setExtendedState(JFrame.NORMAL);//设置状态为正常  
					frame.setVisible(true);//显示主窗体  
				}  
			}
			@Override 
			public void mouseEntered(MouseEvent e) { }
			@Override 
			public void mouseExited(MouseEvent e) {
				dialog.setVisible(false);
			}
			@Override 
			public void mousePressed(MouseEvent e) { }
			@Override  
			public void mouseReleased(MouseEvent e) {        
				//鼠标右键在组件上释放时调用，显示弹出菜单  
				if (e.getButton() == MouseEvent.BUTTON3 && Jmenu != null) {  
					//设置dialog的显示位置  
					Dimension size = Jmenu.getPreferredSize();  
					dialog.setLocation(e.getX() - size.width - 10, e.getY() - size.height - 10);  
					dialog.setVisible(true);
					//显示弹出菜单Jmenu
					Jmenu.show(dialog.getContentPane(), 0, 0);
				}  
			}  
		});  
		//为弹出菜单添加监听器  
		Jmenu.addPopupMenuListener(new PopupMenuListener() {  
			@Override  
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) { }  
			@Override  
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {  
				dialog.setVisible(false);  
			}  
			@Override  
			public void popupMenuCanceled(PopupMenuEvent e) {  
				dialog.setVisible(false);  
		}});
	}  

	/**
	 * 关闭托盘，这里指关闭载体使其不再显示
	 */
	public void close() {
		dialog.dispose();
	}
}