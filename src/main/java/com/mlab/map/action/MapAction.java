package com.mlab.map.action;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

public class MapAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	public final static int LARGE_ICON_WIDTH = 22;
	public final static int LARGE_ICON_HEIGHT = 22;
	public final static int SMALL_ICON_WIDTH = 18;
	public final static int SMALL_ICON_HEIGHT = 18;
	
	protected ActionListener actionListener;
	
	public MapAction(ActionListener actionListener, String text, String actionCommand, 
			String iconName, String desc) {
		super(text);
		putValue(LARGE_ICON_KEY,  createImageIcon(iconName, LARGE_ICON_WIDTH, LARGE_ICON_HEIGHT));
		putValue(SMALL_ICON,  createImageIcon(iconName, SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT));		
		putValue(SHORT_DESCRIPTION, desc);
		putValue(ACTION_COMMAND_KEY, actionCommand);
		this.actionListener = actionListener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		actionListener.actionPerformed(e);
	}	

	private ImageIcon createImageIcon(String path, int width, int height) {
		URL imgURL = ClassLoader.getSystemResource("img/"+path);
	    return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
	}
}
