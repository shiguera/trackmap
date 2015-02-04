package com.mlab.map.factory;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.IllegalFilterException;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.style.ContrastMethod;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class StyleFac {

	private final static Logger LOG = Logger.getLogger(StyleFac.class);
	
	static final org.geotools.styling.StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	
	
	// Style factory
	public static StyleFactory getStyleFactory() {
		return styleFactory;
	}


	/**
	 * Create a Style to display the specified band of the GeoTIFF image
	 * as a greyscale layer.
	 * <p>
	 * This method is a helper for createGreyScale() and is also called directly
	 * by the displayLayers() method when the application first starts.
	 *
	 * @param band the image band to use for the greyscale display
	 *
	 * @return a new Style instance to render the image in greyscale
	 */
	public static Style createGreyscaleStyle(int band) {
	    ContrastEnhancement ce = styleFactory.contrastEnhancement(GeoToolsMapFactory.filterFactory.literal(1.0), ContrastMethod.NORMALIZE);
	    SelectedChannelType sct = styleFactory.createSelectedChannelType(String.valueOf(band), ce);
	    RasterSymbolizer sym = styleFactory.getDefaultRasterSymbolizer();
	    ChannelSelection sel = styleFactory.channelSelection(sct);
	    sym.setChannelSelection(sel);
	    return SLD.wrapSymbolizers(sym);
	}


	/**
	 * This method examines the names of the sample dimensions in the provided coverage looking for
	 * "red...", "green..." and "blue..." (case insensitive match). If these names are not found
	 * it uses bands 1, 2, and 3 for the red, green and blue channels. It then sets up a raster
	 * symbolizer and returns this wrapped in a Style.
	 *
	 * @return a new Style object containing a raster symbolizer set up for RGB image
	 */
	public static Style createRGBStyle(AbstractGridCoverage2DReader reader) {
	    GridCoverage2D cov = null;
	    try {
	        cov = reader.read(null);
	    } catch (IOException giveUp) {
	        throw new RuntimeException(giveUp);
	    }
	    // We need at least three bands to create an RGB style
	    int numBands = cov.getNumSampleDimensions();
	    if (numBands < 3) {
	        return null;
	    }
	    // Get the names of the bands
	    String[] sampleDimensionNames = new String[numBands];
	    for (int i = 0; i < numBands; i++) {
	        GridSampleDimension dim = cov.getSampleDimension(i);
	        sampleDimensionNames[i] = dim.getDescription().toString();
	    }
	    final int RED = 0, GREEN = 1, BLUE = 2;
	    int[] channelNum = { -1, -1, -1 };
	    // We examine the band names looking for "red...", "green...", "blue...".
	    // Note that the channel numbers we record are indexed from 1, not 0.
	    for (int i = 0; i < numBands; i++) {
	        String name = sampleDimensionNames[i].toLowerCase();
	        if (name != null) {
	            if (name.matches("red.*")) {
	                channelNum[RED] = i + 1;
	            } else if (name.matches("green.*")) {
	                channelNum[GREEN] = i + 1;
	            } else if (name.matches("blue.*")) {
	                channelNum[BLUE] = i + 1;
	            }
	        }
	    }
	    // If we didn't find named bands "red...", "green...", "blue..."
	    // we fall back to using the first three bands in order
	    if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
	        channelNum[RED] = 1;
	        channelNum[GREEN] = 2;
	        channelNum[BLUE] = 3;
	    }
	    // Now we create a RasterSymbolizer using the selected channels
	    SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
	    ContrastEnhancement ce = styleFactory.contrastEnhancement(GeoToolsMapFactory.filterFactory.literal(1.0), ContrastMethod.NORMALIZE);
	    for (int i = 0; i < 3; i++) {
	        sct[i] = styleFactory.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
	    }
	    RasterSymbolizer sym = styleFactory.getDefaultRasterSymbolizer();
	    ChannelSelection sel = styleFactory.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
	    sym.setChannelSelection(sel);
	
	    return SLD.wrapSymbolizers(sym);
	}


	/**
	 * Here is a programmatic alternative to using JSimpleStyleDialog to
	 * get a Style. This methods works out what sort of feature geometry
	 * we have in the shapefile and then delegates to an appropriate style
	 * creating method.
	 */
	public static Style createStyle(FeatureSource<?, ?> featureSource, Color color, int width) {
	    //LOG.debug("featureSource=null:"+String.format("%b", featureSource==null));
		SimpleFeatureType schema = (SimpleFeatureType)featureSource.getSchema();
	    Class<?> geomType = schema.getGeometryDescriptor().getType().getBinding();
	
	    if (Polygon.class.isAssignableFrom(geomType)
	            || MultiPolygon.class.isAssignableFrom(geomType)) {
	        return StyleFac.createPolygonStyle(color, width);
	
	    } else if (LineString.class.isAssignableFrom(geomType)
	            || MultiLineString.class.isAssignableFrom(geomType)) {
	        return StyleFac.createLineStyle(color, width);
	
	    } else {
	        return StyleFac.createPointStyle(color, width);
	    }
	}


	/**
	 * Create a Style to draw polygon features with a thin blue outline and
	 * a cyan fill
	 */
	public static Style createPolygonStyle(Color color, int width) {
	
	    // create a partially opaque outline stroke
	    Stroke stroke = styleFactory.createStroke(
	            GeoToolsMapFactory.filterFactory.literal(Color.BLACK),
	            GeoToolsMapFactory.filterFactory.literal(width),
	            GeoToolsMapFactory.filterFactory.literal(0.5));
	
	    // create a partial opaque fill
	    Fill fill = styleFactory.createFill(
	            GeoToolsMapFactory.filterFactory.literal(color),
	            GeoToolsMapFactory.filterFactory.literal(0.0));
	
	    /*
	     * Setting the geometryPropertyName arg to null signals that we want to
	     * draw the default geomettry of features
	     */
	    PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);
	
	    Rule rule = styleFactory.createRule();
	    rule.symbolizers().add(sym);
	    FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
	    Style style = styleFactory.createStyle();
	    style.featureTypeStyles().add(fts);
	
	    return style;
	}


	/**
	 * Create a Style to draw line features as thin blue lines
	 */
	public static Style createLineStyle(Color color, int width) {
	    Stroke stroke = styleFactory.createStroke(
	            GeoToolsMapFactory.filterFactory.literal(color),
	            GeoToolsMapFactory.filterFactory.literal(width));
	    /*
	     * Setting the geometryPropertyName arg to null signals that we want to
	     * draw the default geomettry of features
	     */
	    LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);
	
	    Rule rule = styleFactory.createRule();
	    rule.symbolizers().add(sym);
	    FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(new Rule[]{rule});
	    Style style = styleFactory.createStyle();
	    style.featureTypeStyles().add(fts);
	
	    return style;
	}


	public static Style createPointStyle(Color color, int width) {
		Graphic gr = styleFactory.createDefaultGraphic();
	
		Mark mark = styleFactory.getCircleMark();
	
		mark.setStroke(styleFactory.createStroke(
				GeoToolsMapFactory.filterFactory.literal(Color.BLACK), GeoToolsMapFactory.filterFactory.literal(1)));
	
		mark.setFill(styleFactory.createFill(GeoToolsMapFactory.filterFactory.literal(color)));
	
		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(mark);
		gr.setSize(GeoToolsMapFactory.filterFactory.literal(width));
	
		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);
	
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);
	
		return style;
	}
	public static Style createPointStyle(Color lineColor, int lineWidth, Color fillColor, int size) {
		Graphic gr = styleFactory.createDefaultGraphic();
	
		Mark mark = styleFactory.getCircleMark();
	
		mark.setStroke(styleFactory.createStroke(
				GeoToolsMapFactory.filterFactory.literal(lineColor), GeoToolsMapFactory.filterFactory.literal(lineWidth)));
	
		mark.setFill(styleFactory.createFill(GeoToolsMapFactory.filterFactory.literal(fillColor)));
	
		gr.graphicalSymbols().clear();
		gr.graphicalSymbols().add(mark);
		gr.setSize(GeoToolsMapFactory.filterFactory.literal(size));
	
		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);
	
		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);
	
		return style;
	}


	public static Style createCircleLabeledStyle(Color color, int size, String columnName) {
	    StyleBuilder sb = new StyleBuilder();
	    Mark circle = sb.createMark(StyleBuilder.MARK_CIRCLE, color, Color.BLACK, 0);
	    Graphic gr = sb.createGraphic(null ,circle, null, 0.7, size, 0);
	    PointSymbolizer ps = sb.createPointSymbolizer(gr);
	    
	    Rule r;
	    TextSymbolizer ts = null;
	    try {
	        ts = sb.createTextSymbolizer(Color.BLACK, sb.createFont(new Font("Arial", Font.PLAIN, 12)), columnName);
	    } catch(IllegalFilterException ife) {
	    	System.out.println("GeoToolsMapFactory.createStarLabeledStyle() Exception: "+ife.getMessage());
	    }
	    
	    if(ts != null) {
	        ts.setHalo(sb.createHalo(Color.WHITE, 1, 1));
	        ts.setLabelPlacement(sb.createPointPlacement(0, 0, 5, 0, 0));
	        r = sb.createRule(new Symbolizer[] {ps, ts});
	    }
	    else r = sb.createRule(new Symbolizer[] {ps});
	    
	    Style newStyle = sb.createStyle();
	    newStyle.featureTypeStyles().add(sb.createFeatureTypeStyle(null, r));
	    return newStyle;
	}

}
