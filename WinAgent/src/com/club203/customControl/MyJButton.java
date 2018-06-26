package com.club203.customControl;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class MyJButton extends JButton{
	
	private BufferedImage image_over; // 鼠标在按钮上的图片  
    private BufferedImage image_off; // 鼠标不在按钮上的图片  
    private BufferedImage image_pressed; // 鼠标按下按钮时的图片  
    private int buttonWidth; // 宽  
    private int buttonHeight; // 高  
    private int[] pixels; // 储存图片数据的数组，用于计算contains  
    private boolean mouseOn;  
    private boolean mousePressed;  
  
    /** 
     * @param img_off 
     *            鼠标不在按钮上的图片 
     * @param img_over 
     *            鼠标在按钮上的图片 
     * @param img_pressed 
     *            鼠标按下按钮时的图片 
     */  
    public MyJButton(String img_off, String img_over, String img_pressed) {  
        mouseOn = false;  
        mousePressed = false;  
        // 加载图片  
        try {  
            image_over = ImageIO.read(new FileInputStream(img_over));  
            image_off = ImageIO.read(new FileInputStream(img_off));  
            image_pressed = ImageIO.read(new FileInputStream(img_pressed));  
        } catch (FileNotFoundException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        } catch (IOException e1) {  
            // TODO Auto-generated catch block  
            e1.printStackTrace();  
        }  
  
        buttonWidth = image_off.getWidth();  
        buttonHeight = image_off.getHeight();  
  
        // 读取图片数据  
        pixels = new int[buttonWidth * buttonHeight];  
        // 抓取像素数据  
        PixelGrabber pg = new PixelGrabber(image_off, 0, 0, buttonWidth,  
                buttonHeight, pixels, 0, buttonWidth);  
        try {  
            pg.grabPixels();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        // 必须设置！否则会有残影！  
        this.setOpaque(false);  
        this.setPreferredSize(new Dimension(buttonWidth, buttonHeight));  
        this.addMouseListener(new MouseHandler());  
    }  
  
    // 读取图片文件  
    public BufferedImage loadImage(String filename) {  
        File file = new File(filename);  
  
        if (!file.exists())  
            return null;  
  
        try {  
            return ImageIO.read(file);  
        } catch (IOException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    // 覆盖此方法绘制自定义的图片  
    @Override  
    public void paintComponent(Graphics g) {  
        g.drawImage(image_off, 0, 0, this);  
        if (mouseOn)  
            g.drawImage(image_over, 0, 0, this);  
        else if (mousePressed)  
            g.drawImage(image_pressed, 0, 0, this);  
    }  
  
    // 覆盖此方法绘制自定义的边框  
    @Override  
    public void paintBorder(Graphics g) {  
        // 不要边框  
    }  
  
    @Override  
    public void setSize(Dimension d) {  
        // TODO Auto-generated method stub  
        super.setSize(d);  
    }  
  
    public boolean contains(int x, int y) {  
        // 不判定的话会越界，在组件之外也会激发这个方法  
        if (!super.contains(x, y))  
            return false;  
  
        int alpha = (pixels[(buttonWidth * y + x)] >> 24) & 0xff;  
  
        repaint();  
        if (alpha == 0) {  
            return false;  
        } else {  
            return true;  
        }  
    }  
  
    // 处理进入、离开图片范围的消息  
    class MouseHandler extends MouseAdapter {  
        public void mouseExited(MouseEvent e) {  
            mouseOn = false;  
            repaint();  
        }  
  
        public void mouseEntered(MouseEvent e) {  
            mouseOn = true;  
            repaint();  
        }  
  
        public void mousePressed(MouseEvent e) {  
            mouseOn = false;  
            mousePressed = true;  
            repaint();  
        }  
  
        public void mouseReleased(MouseEvent e) {  
            // 防止在按钮之外的地方松开鼠标  
            if (contains(e.getX(), e.getY()))  
                mouseOn = true;  
            else  
                mouseOn = false;  
  
            mousePressed = false;  
            repaint();  
        }  
    }   
}
