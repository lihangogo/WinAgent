package com.club203.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class ShowMessageDlg extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel text;
    Toolkit tk = Toolkit.getDefaultToolkit();
    Dimension screensize = tk.getScreenSize();
    int height = screensize.height;
    int width = screensize.width;
    private String str = null;

    /**
     * 显示提示内容
     * @param str 提示信息
     */
    public ShowMessageDlg(String str) {
        this.str = str;
        new Thread(new Runnable() {
            @Override
            public void run() {
                initGUI();
            }
        }).start();
    }

    /**
     * 初始化
     */
    private void initGUI() {
        try {
            setUndecorated(true);
            setLocationRelativeTo(null);
            setVisible(true);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            {
                text = new JLabel("<html>" + str + "</html>", JLabel.CENTER);
                getContentPane().add(text, BorderLayout.CENTER);
                text.setBackground(new java.awt.Color(255, 251, 240));
            }
            pack();
            setBounds(width / 2 - 180, height - 150, 360, 100);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
