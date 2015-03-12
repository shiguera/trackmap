package com.mlab.map.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;

public class MapToolBarImpl extends JToolBar implements MapToolBar {
	private static final long serialVersionUID = 1L;

	protected MapView mapView;
	
	List<JButton> btns;

	public MapToolBarImpl() {
		super();
		setFloatable(false);
		setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		btns = new ArrayList<JButton>();
	}

	private JButton createZoomInButton(JMapPane mappane) {
		JButton btn = new JButton(new ZoomInAction(mappane));
		return btn;	
	}
	private JButton createZoomOutButton(JMapPane mappane) {
		JButton btn = new JButton(new ZoomOutAction(mappane));
		return btn;	
	}
	private JButton createPanButton(JMapPane mappane) {
		JButton btn = new JButton(new PanAction(mappane));
		return btn;	
	}
	
	@Override
	public void addButton(JButton btn) {
		btns.add(btn);
		add(btn);
	}

	@Override
	public void setDefaultButtons(JMapPane mappane) {
		addButton(createZoomInButton(mappane));
		addButton(createZoomOutButton(mappane));
		addButton(createPanButton(mappane));		
	}

	@Override
	public int getButtonsCount() {
		return btns.size();
	}

	@Override
	public JButton getButton(int index) {
		return btns.get(index);
	}

	@Override
	public JToolBar getComponent() {
		return this;
	}

}
