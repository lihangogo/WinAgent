package com.club203.dialog;

public class MessageDialog{

	private String Message;
	
	public MessageDialog(String Message){
		this.Message = Message;
	}

	public void show(){
		/**
		 * JOptionPane.showMessageDialog弹窗存在的问题：
		 * 	1. 对话框阻塞问题
		 *  2. 指定父窗口，若窗口最小化，弹出窗口会出现在左上角
		 *  3. 不指定父窗口，则弹出窗口被其他窗口覆盖，不能正常显示
		 */
		CustomMsgDlg.customMessageShow(Message);
	}

}
