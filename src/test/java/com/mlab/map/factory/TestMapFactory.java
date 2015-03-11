package com.mlab.map.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.mlab.gpx.api.GpxDocument;
import com.mlab.gpx.api.GpxFactory;
import com.mlab.gpx.impl.Track;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.map.layer.GpxLayer;
import com.mlab.map.layer.ShpLayer;
import com.mlab.map.layer.TifLayer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class TestMapFactory {

	private final Logger LOG = Logger.getLogger(getClass().getName());
	
	@Test
	public void testCommonFactories() {
		LOG.info("Testing GeoToolsFactory.testCommonFactories()");
		assertTrue(StyleFac.getStyleFactory().getClass().isAssignableFrom(org.geotools.styling.StyleFactoryImpl.class));
		assertTrue(GeoToolsFactory.getFilterFactory().getClass().isAssignableFrom(org.geotools.filter.FilterFactoryImpl.class));
		assertTrue(GeoToolsFactory.getGeometryFactory().getClass().isAssignableFrom(com.vividsolutions.jts.geom.GeometryFactory.class));
	}
	@Test
	public void testCreateShpLayer() {
		LOG.info("Testing GeoToolsFactory.testCreateShpLayer()");
		File file = new File("src/test/resources/ptos2.shp");
		assertTrue(file.exists());
		ShpLayer l = GeoToolsFactory.createShpLayer(file);
		assertNotNull(l);
		assertEquals(l.getLayer().getTitle(), "ptos2.shp");
	}
	@Test
	public void testCreateGpxLayer() {
		LOG.info("Testing GeoToolsFactory.testCreateGpxLayer()");
		String filename = "20130318_125729.gpx";
		File file = new File("src/test/resources/"+filename);
		assertTrue(file.exists());
		GpxDocument gpxdoc = GpxFactory.readGpxDocument(file);
		assertNotNull(gpxdoc);
		GpxLayer l = GeoToolsFactory.createGpxLayer(gpxdoc);
		assertNotNull(l);
		assertEquals(l.getName(), filename);
	}
	
	@Test
	public void testCreateShapefileDataStore() {
		LOG.info("Testing GeoToolsFactory.testCreateShapefileDataStore()");
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("LocationTest");
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.add("the_geom",Point.class);
		builder.length(15).add("Name",String.class);
		final SimpleFeatureType LOCATION = builder.buildFeatureType();
		assertNotNull(LOCATION);
		//System.out.println(LOCATION);
		File shp = new File("src/test/resources/test.shp");
		SimpleFeatureStore store = GeoToolsFactory.createShapefileDataStore(shp, LOCATION);
		assertNotNull(store);
		assertTrue(shp.exists());
		//System.out.println(store);
		//SimpleFeatureType SHAPE_TYPE = store.getSchema();
		//System.out.println(SHAPE_TYPE);
		File indexfile = new File("src/test/resources/test.shx");
		assertTrue(indexfile.exists());
		File dbffile = new File("src/test/resources/test.dbf");
		assertTrue(dbffile.exists());
		File projfile = new File("src/test/resources/test.prj");
		assertTrue(projfile.exists());
	}
	@Test
	public void testCreateShapefileDataStore2() {
		LOG.info("Testing GeoToolsFactory.testCreateShapefileDataStore2()");
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("LinestringTest");
		CoordinateReferenceSystem crs=null;
		try {
			crs=CRS.decode("EPSG:4326");
		} catch (Exception e) {
			LOG.info("TestMapFactory.testCreateShapefileDataStore2() ERROR: can't decode EPSG:4326");
			assertTrue(false);
		}
		builder.setCRS(crs);
		builder.add("the_geom",LineString.class);
		builder.length(15).add("Name",String.class);
		final SimpleFeatureType TYPE = builder.buildFeatureType();
		assertNotNull(TYPE);
		File shp = new File("src/test/resources/testline.shp");
		SimpleFeatureStore store = GeoToolsFactory.createShapefileDataStore(shp, TYPE);
		assertNotNull(store);
		assertTrue(shp.exists());
		System.out.println(store);
		SimpleFeatureType SHAPE_TYPE = store.getSchema();
		System.out.println(SHAPE_TYPE);
		File indexfile = new File("src/test/resources/testline.shx");
		assertTrue(indexfile.exists());
		File dbffile = new File("src/test/resources/testline.dbf");
		assertTrue(dbffile.exists());
		File projfile = new File("src/test/resources/testline.prj");
		assertTrue(projfile.exists());
	}

	@Test
	public void testSaveAsShapefile() {
		LOG.info("Testing GeoToolsFactory.testSaveAsShapefile()");
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("LocationTest");
		builder.setCRS(DefaultGeographicCRS.WGS84);
		builder.add("the_geom",Point.class);
		builder.length(15).add("Name",String.class);
		final SimpleFeatureType LOCATION = builder.buildFeatureType();
		assertNotNull(LOCATION);
		
		SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(LOCATION);
		Point p1 = GeoToolsFactory.getGeometryFactory().createPoint(new Coordinate(-3.98, 42.7));
		featBuilder.add(p1);
		featBuilder.add("P1");
		SimpleFeature feat = featBuilder.buildFeature(null);
		
		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		collection.add(feat);
		
		assertTrue(collection.size()==1);
		
		File shp = new File("src/test/resources/test.shp");
		boolean result = GeoToolsFactory.saveAsShapefile(shp, LOCATION, collection);
		assertTrue(result);
		
	}
	@Test
	public void testCreateTifLayer() {
		System.out.print("Testing GeoToolsFactory.testCreateTifLayer()");
		URL url = ClassLoader.getSystemResource("ovr.tif");
		TifLayer l = GeoToolsFactory.createTifLayer(new File(url.getPath()));
		assertNotNull(l);
		assertEquals(l.getLayer().getBounds().getHeight(), 180.0,0.01);

		url = ClassLoader.getSystemResource("world.tif");
		Assert.assertNotNull(url.getPath());
		File file = new File(url.getPath());
		Assert.assertTrue(file.exists());
		TifLayer l2 = GeoToolsFactory.createTifLayer(file);
//		assertNotNull(l2);
//		assertNotNull(l2.getLayer());
//		assertNotNull(l2.getLayer().getBounds());
//		System.out.println(l2.getLayer().getBounds());
//		assertEquals(18835.0, l2.getLayer().getBounds().getHeight(), 0.01);

		
		System.out.println("OK");
	}
	@Test
	public void testGetEPSGCRS() {
		System.out.print("Testing GeoToolsFactory.testGetEPSGCRS()");
		
		// Valid crs
		CoordinateReferenceSystem crs = GeoToolsFactory.getEPSGCRS(25830);
		assertNotNull(crs);
		assertEquals( "EPSG:ETRS89 / UTM zone 30N", crs.getName().toString());
		// Not valid crs
		LOG.info("Next severe is forced by test");
		crs = GeoToolsFactory.getEPSGCRS(30);
		assertNull(crs);

		System.out.println("OK");
		
	}
	@Test
	public void testSaveSegmentAsLineShapefile() {
		System.out.print("Testing GeoToolsFactory.saveSegmentAsLineShapefile()");
		URL url = ClassLoader.getSystemResource("20130318_125729.gpx");
		File file = new File(url.getPath());
		assertTrue(file.exists());
		
		GpxDocument gpxdoc = GpxFactory.readGpxDocument(file);
		assertNotNull(gpxdoc);
		assertTrue(gpxdoc.hasTracks());
		Track track = gpxdoc.getTracks().get(0);
		assertTrue(track.hasSegments());
		TrackSegment segment = (TrackSegment)track.get(0);

		File destfile = null;
		try {
			destfile = File.createTempFile("RPTest", ".shp", new File("tmp"));			
			//destfile.deleteOnExit();
		} catch (IOException e) {
			Assert.fail("Can't create tmp file: "+ e.getMessage());
		}
		boolean result = GeoToolsFactory.saveSegmentAsLineShapefile(destfile, segment);
		assertTrue(result);
		assertTrue(destfile.exists());
		System.out.println(destfile.getAbsolutePath());
		
		System.out.println("OK");
		
	}
	@Test
	public void testSaveSegmentAsPointsShapefile() {
		System.out.print("Testing GeoToolsFactory.saveSegmentAsPointsShapefile()");
		URL url = ClassLoader.getSystemResource("20130318_125729.gpx");
		File file = new File(url.getPath());
		assertTrue(file.exists());
		
		GpxDocument gpxdoc = GpxFactory.readGpxDocument(file);
		assertNotNull(gpxdoc);
		assertTrue(gpxdoc.hasTracks());
		Track track = gpxdoc.getTracks().get(0);
		assertTrue(track.hasSegments());
		TrackSegment segment = (TrackSegment)track.get(0);

		File destfile = new File ("tmp/RPTest.shp");			
		boolean result = GeoToolsFactory.saveSegmentAsPointsShapefile(destfile, segment);
		assertTrue(result);
		assertTrue(destfile.exists());
		System.out.println(destfile.getAbsolutePath());
		System.out.println("OK");
		
	}

	@Test
	public void testLineStringToPointFeatureCollection() {
		System.out.print("Testing GeoToolsFactory.lineStringToPointFeatureCollection()...");
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
		Coordinate[] coords = {new Coordinate(0.0,0.0), new Coordinate(10.0,10.0)};
		LineString ls = factory.createLineString(coords);
		Assert.assertNotNull(ls);
		System.out.println("LineString.getNumPoints():"+ls.getNumPoints());
		SimpleFeatureCollection coll = GeoToolsFactory.lineStringToPointFeatureCollection(ls);
		Assert.assertNotNull(coll);
		Assert.assertEquals(2, coll.size());
		System.out.println("OK");
	}
	@Test
	public void testLineStringToLineFeatureCollection() {
		System.out.print("Testing GeoToolsFactory.lineStringToLineFeatureCollection()...");
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
		Coordinate[] coords = {new Coordinate(0.0,0.0), new Coordinate(10.0,10.0)};
		LineString ls = factory.createLineString(coords);
		Assert.assertNotNull(ls);
		System.out.println("LineString.getNumPoints():"+ls.getNumPoints());
		SimpleFeatureCollection coll = GeoToolsFactory.lineStringToLineFeatureCollection(ls);
		Assert.assertNotNull(coll);
		Assert.assertEquals(1, coll.size());
		System.out.println("OK");
	}
	
	@Test
	public void testSaveAsShapefile_LineString() {
		System.out.print("Testing GeoToolsFactory.saveAsShapefile_LineString()...");
		GeometryFactory factory = JTSFactoryFinder.getGeometryFactory();
		Coordinate[] coords = {new Coordinate(0.0,0.0), new Coordinate(10.0,10.0)};
		LineString ls = factory.createLineString(coords);
		Assert.assertNotNull(ls);
		System.out.println("LineString.getNumPoints():"+ls.getNumPoints());
		SimpleFeatureCollection coll = GeoToolsFactory.lineStringToPointFeatureCollection(ls);
		Assert.assertNotNull(coll);
		File shp = new File("src/test/resources/test2.shp");
		boolean result = GeoToolsFactory.saveAsShapefile(shp, ls, true);
		assertTrue(result);
		
		coll = GeoToolsFactory.lineStringToLineFeatureCollection(ls);
		Assert.assertNotNull(coll);
		shp = new File("src/test/resources/test3.shp");
		result = GeoToolsFactory.saveAsShapefile(shp, ls, false);
		assertTrue(result);
		
		System.out.println("OK");
	}

	@Test
	public void testCreatePointFeatureType() {
		System.out.print("Testing GeoToolsFactory.createPointFeatureType()...");
		String wkt = "LOCAL_CS[\"Generic cartesian 2D\",LOCAL_DATUM[\"Unknow\",0]"+
				",UNIT[\"m\",1.0],AXIS[\"x\",EAST],AXIS[\"y\",NORTH]]";
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(wkt);
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotNull(crs);
		final SimpleFeatureType TYPE = GeoToolsFactory.createPointFeatureType(crs);		
		Assert.assertNotNull(TYPE);
		System.out.println("OK");
	}
}
