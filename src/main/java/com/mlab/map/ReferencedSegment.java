package com.mlab.map;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mlab.gpx.api.WayPoint;
import com.mlab.gpx.impl.SimpleWayPoint;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.gpx.impl.util.Util;
import com.mlab.map.factory.GeoToolsMapFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.linearref.LengthLocationMap;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

/**
 * Implements linear referencing in TrackSegment using
 * JTS Linear Referencing classes.<br/>
 * It has a TrackSegment, a LineString with utm coordinates for nodes, 
 * a LocationIdexedLine, a LengthIndexedLine and a LengthLocationMap.<br/>
 * It wrappes methods from LocationIndexedLine and LengthLocatonMap.<br/>
 * 
 * @author shiguera
 *
 */
public class ReferencedSegment {
	
	private final Logger LOG = Logger.getLogger(getClass().getName());
	
	TrackSegment segment;
	/**
	 *  LineString con los puntos en coordenadas utm
	 */
	LineString lineString;
	/**
	 * LocationIndexedLine sobre la LineString de ptos utm 
	 */
	LocationIndexedLine locationIndexedLine;
	/**
	 * LengthIndexedLine sobre la LineString de ptos utm
	 */
	LengthIndexedLine lengthIndexedLine;
	/**
	 * LengthLocationMap para pasar de una referenciación lineal a la otra
	 * @param segment
	 */
	LengthLocationMap map;
	
	boolean isValid;
	
	// Constructor
	public ReferencedSegment(TrackSegment segment) {
		this();
		this.segment = segment;
		if(segment == null) {
			LOG.error("ReferencedSegment() ERROR: Segment == null");
			isValid=false;
			return;
		}		
		lineString = GeoToolsMapFactory.segmentToLineStringUtm(segment);
		if(lineString == null) {
			LOG.error("ReferencedSegment() ERROR: Can't create utm lineString");
			isValid=false;
			return;
		}
		this.locationIndexedLine = new LocationIndexedLine(lineString);
		this.lengthIndexedLine = new LengthIndexedLine(lineString);
		this.map = new LengthLocationMap(lineString);
		isValid=true;
	}
	private ReferencedSegment() {
		this.segment = null;
		this.lineString = null;
		this.locationIndexedLine = null;
		this.lengthIndexedLine = null;
		this.map = null;
	}
	
	// Getters
	public TrackSegment getSegment() {
		return segment;
	}
	public LineString getLineString() {
		return lineString;
	}
	
	// Métodos de LocationIndexedLine
	public LinearLocation project(Coordinate pt) {
		return this.locationIndexedLine.project(pt);
	}
	public LinearLocation project(double lon, double lat) {
		double[] xy = Util.proyUtmWGS84(lon, lat);
		return this.locationIndexedLine.project(new Coordinate(xy[0], xy[1]));
	}
	public LinearLocation getStartLocation() {
		return this.locationIndexedLine.getStartIndex();
	}
	public LinearLocation getEndLocation() {
		return this.locationIndexedLine.getEndIndex();
	}
	/**
	 * Devuelve las coordenadas UTM de una location
	 * @param location
	 * @return
	 */
	public Coordinate extractPoint(LinearLocation location) {
		return this.locationIndexedLine.extractPoint(location);
	}
	/**
	 * Devuelve el WayPoint de una location (Coordenadas geográficas)
	 * @param loc
	 * @return
	 */
	public WayPoint extractWayPoint(LinearLocation loc) {
		WayPoint wp = null;
		int segmentIndex = loc.getSegmentIndex();
		if(loc.isVertex()) {
			WayPoint wpp = segment.getWayPoint(segmentIndex);
			wp = new SimpleWayPoint("","",wpp.getTime(),wpp.getLongitude(),wpp.getLatitude(),wpp.getAltitude());
		} else {
			WayPoint wp1 = segment.getWayPoint(segmentIndex); 
			long t1 = wp1.getTime();
			WayPoint wp2 = segment.getWayPoint(segmentIndex+1);
			long t2 = wp2.getTime();
			double fraction = loc.getSegmentFraction();
			long fract = (long)((t2-t1) * fraction);
			long t = t1 + fract;
			double fraclon = (wp2.getLongitude() - wp1.getLongitude())*fraction;
			double longitude = wp1.getLongitude() + fraclon;
			double fraclat = (wp2.getLatitude() - wp1.getLatitude()) * fraction;
			double latitude = wp1.getLatitude() + fraclat;
			double fracaltitude = (wp2.getAltitude()-wp1.getAltitude())*fraction;
			double altitude = wp1.getAltitude() + fracaltitude;
			List<Double> values = new ArrayList<Double>();
			values.add(longitude);
			values.add(latitude);
			values.add(altitude);
			wp = new SimpleWayPoint("", "", t, values);
		}
		return wp;
	}
	public Geometry extractLine(LinearLocation loc1, LinearLocation loc2) {
		return this.locationIndexedLine.extractLine(loc1, loc2);
	}
	/**
	 * Computes the index for a given point on the line.<br/>
	 * The supplied point does not necessarily have to lie precisely 
	 * on the line, but if it is far from the line the accuracy 
	 * and performance of this function is not guaranteed. 
	 * Use project() to compute a guaranteed result for points 
	 * which may be far from the line.
	 * 
	 * @param pt pt - a point on the line
	 * 
	 * @return the index of the point
	 */
	public LinearLocation getLocation(Coordinate pt) {
		return this.locationIndexedLine.indexOf(pt);
	}
	public LinearLocation getLocation(double lon, double lat) {
		double[] xy = Util.proyUtmWGS84(lon, lat);
		return this.locationIndexedLine.indexOf(new Coordinate(xy[0], xy[1]));
	}
	/**
	 * Computes the indices for a subline of the line. (The subline must 
	 * conform to the line; that is, all vertices in the subline 
	 * (except possibly the first and last) must be vertices of the 
	 * line and occcur in the same order).
	 * 
	 * @param subline - a subLine of the line
	 * 
	 * @return a pair of indices for the start and end of the subline.
	 */
	public LinearLocation[] getLocations(Geometry subline) {
		return this.locationIndexedLine.indicesOf(subline);
	}
	public boolean isValidLocation(LinearLocation loc) {
		return this.locationIndexedLine.isValidIndex(loc);
	}

	// Métodos de LengthLocationMap
	public double getLength(LinearLocation loc) {
		return this.map.getLength(loc);
	}
	public LinearLocation getLocation(double length) {
		return this.map.getLocation(length);
	}
	public LocationIndexedLine getLocationIndexedLine() {
		return locationIndexedLine;
	}
	public LengthIndexedLine getLengthIndexedLine() {
		return lengthIndexedLine;
	}
	
	public boolean isValidLength(double length) {
		return lengthIndexedLine.isValidIndex(length);
	}
	public boolean isValid() {
		return isValid;
	}
}
