package com.mlab.map.swing;

import java.awt.Font;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public abstract class StatusBarLabel extends JLabel {
	
	private final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

	
	public StatusBarLabel(String text, ImageIcon icon) {
		setFont(FONT);
		setIcon(icon);		
		setLayout();
	}
	
	abstract protected void setLayout();
	
	public static ImageIcon createImageIcon(String path, int width, int height) {
		URL imgURL = ClassLoader.getSystemResource("img/"+path);
	    return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
	}




}
