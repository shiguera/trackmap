package com.mlab.map.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Arrays;

import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.swing.JMapPane;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.control.StatusBarItem;
import org.geotools.swing.event.MapMouseAdapter;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.geotools.swing.locale.LocaleUtils;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.mlab.map.TrackMapModel;
import com.mlab.map.factory.GeoToolsFactory;

/**
 * A status bar item that displays the world coordinates of the mouse cursor
 * position.
 *
 * @see JMapStatusBar
 *
 * @author Michael Bedward
 * @since 8.0
 *
 * @source $URL$
 * @version $Id$
 */
public class JGeoCoordsStatusBarItem extends StatusBarItem {
	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(getClass().getName());
    private static final String COMPONENT_NAME =
            LocaleUtils.getValue("StatusBar", "CoordsItemName");
    
    private static final String TOOL_TIP = LocaleUtils.getValue("StatusBar", "CoordsTooltip");
    private static final int DEFAULT_NUM_INTEGER_DIGITS = 3;
    private static final Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	
    //static final Font DEFAULT_FONT = new Font("Courier", Font.PLAIN, 12);
    static final int DEFAULT_NUM_DECIMAL_DIGITS = 6;

    private static final String NO_COORDS = LocaleUtils.getValue("StatusBar", "CoordsNone");

    
    private final JLabel label;

    private int intLen;
    private int decLen;
    private String numFormat;

    private TrackMapModel model;
    
    /**
     * Creates a new item to display cursor position for the given trackMap pane.
     *
     * @param mapPane the trackMap pane
     */
    public JGeoCoordsStatusBarItem(TrackMapModel model, JMapPane mapPane) {
    	super(COMPONENT_NAME);
        this.model = model;
        if (mapPane == null) {
            throw new IllegalArgumentException("JGeoCoordsStatusBarItem(): mapPane must not be null");
        }

        label = new JLabel();
        label.setFont(DEFAULT_FONT);
        add(label);
        
        setToolTipText(TOOL_TIP);

        decLen = DEFAULT_NUM_DECIMAL_DIGITS;

        setFormat(mapPane.getDisplayArea());

        this.setBorder(null);
        mapPane.addMouseListener(new MapMouseAdapter() {
            @Override
            public void onMouseEntered(MapMouseEvent ev) {
                displayCoords(ev.getWorldPos());
            }

            @Override
            public void onMouseExited(MapMouseEvent ev) {
                displayNoCursor();
            }

            @Override
            public void onMouseMoved(MapMouseEvent ev) {
                displayCoords(ev.getWorldPos());
            }
        });

        mapPane.addMapPaneListener(new MapPaneAdapter() {
            @Override
            public void onDisplayAreaChanged(MapPaneEvent ev) {
                setFormat(((ReferencedEnvelope) ev.getData()));
            }
        });

        displayNoCursor();
    }

    /**
     * Sets te number of digits to display after the decimal place.
     *
     * @param numDecimals number of digits after decimal place
     */
    @Override
    public void setNumDecimals(int numDecimals) {
        decLen = numDecimals;
        setLabelSizeAndFormat();
    }

    /**
     * Displays coordinates of the given position.
     *
     * @param p world position
     */
    private void displayCoords(DirectPosition2D p) {
    	DirectPosition2D pp = new DirectPosition2D();
    	try {
    		MathTransform tr = GeoToolsFactory.generateTransformFromWGS84(model.getCoordinateReferenceSystem());
			if (tr == null) {
				LOG.error("displayCoords(): can't generate transform");
				return;
			}
			pp = (DirectPosition2D) tr.transform(p, pp);
		} catch (Exception e) {
			LOG.warn("displayCoords(): Can't reproject "+p.toString());
		} 
        label.setText(String.format(numFormat, pp.getX(), pp.getY()));
        ensureMinLabelWidth();
    }

    /**
     * Sets a message to indicate that the cursor is out of the trackMap pane.
     */
    private void displayNoCursor() {
        label.setText(NO_COORDS);
    }

    /**
     * Sets the minimum width of the coordinate display label and the
     * format string used to print values. The trackMap extent is used to
     * estimate the number of digits required.
     *
     * @param env trackMap extent
     */
    private void setFormat(ReferencedEnvelope env) {
        if (env == null || env.isEmpty()) {
            intLen = DEFAULT_NUM_INTEGER_DIGITS;
        } else {
            setIntegerLen(env);
        }
        setLabelSizeAndFormat();
    }
    
    /**
     * Sets the minimum width of the coordinate display label and the
     * format string used to print values.
     */
    private void setLabelSizeAndFormat() {
        int minLabelWidth = getStringWidth();
        Dimension labelSize = label.getSize();
        if (labelSize.width < minLabelWidth) {
            label.setMinimumSize(new Dimension(minLabelWidth, labelSize.height+10));
            revalidate();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("%").append(intLen).append(".").append(decLen).append("f, ");
        sb.append("%").append(intLen).append(".").append(decLen).append("f");
        numFormat = sb.toString();
    }

    /**
     * Estimates the maximum display width of the coordinate string based
     * on current integer and fractional part lengths.
     *
     * @return maximum display width
     */
    private int getStringWidth() {
        FontMetrics fm = label.getFontMetrics(label.getFont());
        char[] c = new char[intLen + decLen + 1];
        Arrays.fill(c, '0');
        String s = String.valueOf(c);
        s = s + ", " + s;
        return fm.stringWidth(s);
    }

    /**
     * Examines the trackMap extent and tries to determine the number of digits
     * that will be needed in the display. If a coordinate reference system
     * with valid extent defined, it is used to determine coordinate limits;
     * otherwise the extent of the envelope is used directly. If all else
     * fails, a default number of digits is set.
     *
     * @param env the trackMap extent (may be {@code null})
     */
    private void setIntegerLen(Envelope env) {
        int len = -1;
        if (env != null) {
            // Try to get a valid extent for the CRS and use this to
            // determine num coordinate digits
            CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
            if (crs != null) {
                Envelope validExtent = CRS.getEnvelope(crs);
                if (validExtent != null) {
                    len = getMaxIntegerLen(validExtent);
                }
            }

            if (len < 0) {
                // Use trackMap extent directly
                len = getMaxIntegerLen(env);
            }

        } else {
            // Nothing to go on: use an arbitrary canonicLength
            len = DEFAULT_NUM_INTEGER_DIGITS;
        }

        intLen = len;
    }

    /**
     * Gets the maximum number of digits in the integer part of
     * envelope corner coordinates.
     *
     * @param env the envelope
     * @return maximum number of digits
     */
    private int getMaxIntegerLen(Envelope env) {
        int len = integerPartLen(env.getMinimum(0));
        len = Math.max(len, integerPartLen(env.getMinimum(1)));
        len = Math.max(len, integerPartLen(env.getMaximum(0)));
        len = Math.max(len, integerPartLen(env.getMaximum(1)));

        // Add 1 to allow for negative sign
        return len + 1;
    }

    /**
     * Gets the canonicLength of the integer part of a double value.
     *
     * @param x the value
     *
     * @return number of digits in the integer part
     */
    private int integerPartLen(double x) {
        return 1 + (int) Math.log10( Math.abs(x) );
    }

    /**
     * Checks the current label width against its minimum width and,
     * if the current width is larger, adjusts the minimum to prevent
     * the label growing and shrinking as the cursor is moved.
     */
    private void ensureMinLabelWidth() {
        Dimension minDim = label.getMinimumSize();
        Dimension curDim = label.getSize();

        if (curDim.width > minDim.width) {
            label.setMinimumSize(new Dimension(curDim.width, minDim.height));
        }
    }
}
