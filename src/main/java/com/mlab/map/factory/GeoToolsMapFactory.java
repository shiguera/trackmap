package com.mlab.map.factory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.coverageio.gdal.ecw.ECWFormat;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.styling.Style;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.mlab.gpx.api.GpxDocument;
import com.mlab.gpx.api.GpxFactory;
import com.mlab.gpx.api.GpxFactory.Type;
import com.mlab.gpx.api.WayPoint;
import com.mlab.gpx.impl.TrackSegment;
import com.mlab.gpx.impl.util.Util;
import com.mlab.map.layer.GpxLayer;
import com.mlab.map.layer.LineStringLayer;
import com.mlab.map.layer.MobileLayer;
import com.mlab.map.layer.ShpLayer;
import com.mlab.map.layer.TifLayer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

public class GeoToolsMapFactory {
	static final Logger LOG = Logger.getLogger(GeoToolsMapFactory.class);

	private final static String GENERIC_CARTESIAN_2D = "LOCAL_CS[\"Generic cartesian 2D\",LOCAL_DATUM[\"Unknow\",0]"+
			",UNIT[\"m\",1.0],AXIS[\"x\",EAST],AXIS[\"y\",NORTH]]";
	private final static int PNOA_CRS_CODE = 25830;
	private static final double[] PNOA_ENVELOPE_EPSG_25830 = new double[] {
			-50000.0, 1172000.0, 3950000.0, 4873000.0 };
	private static final double[] PERU_ENVELOPE_EPSG_3857 = new double[] {
			-9053692.109860493, -7642428.133379688, -2078767.5046918981,
			-4174.481202784049 };
	private static final double[] WORLD_ENVELOPE_EPSG_3857 = new double[] {
			-17079392.0, 17079392.0, -20037508.0, 20037508.0 };
	private static final double[] SPAIN_ENVELOPE_EPSG_4326 = new double[] {
			-10.0, 5.0, 37.0, 43.0 };

	static final FilterFactory2 filterFactory = CommonFactoryFinder
			.getFilterFactory2();
	private static final GeometryFactory geometryFactory = JTSFactoryFinder
			.getGeometryFactory();

	private static CRSAuthorityFactory crsFactory;
	private static CoordinateReferenceSystem WGS84_CRS;

	public static FilterFactory2 getFilterFactory() {
		return filterFactory;
	}

	public static GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}

	// Creación de capas
	public static TifLayer createTifLayer(File mapFile) {
		return new TifLayer(mapFile);
	}

	public static TifLayer createTifLayer(File mapFile, boolean colorbands) {
		return new TifLayer(mapFile, colorbands);
	}

	public static Layer readRasterLayer(File rasterMapFile) {
		Layer rasterLayer = null;
		AbstractGridFormat format = GridFormatFinder.findFormat(rasterMapFile);
		if (format != null) {
			AbstractGridCoverage2DReader reader = format
					.getReader(rasterMapFile);
			if (reader != null) {
				Style style = StyleFac.createGreyscaleStyle(1);
				rasterLayer = new GridReaderLayer(reader, style);
			} else {
				LOG.warn("GeoToolsMapFactory.readRasterLayer() ERROR: can't create reader");
			}
		} else {
			LOG.warn("GeoToolsMapFactory.readRasterLayer() ERROR: can't create AbstractGridFormat");
		}
		return rasterLayer;
	}

	public static Layer readECWLayer(File rasterMapFile) {
		Layer rasterLayer = null;
		ECWFormat format = new ECWFormat();
		// AbstractGridFormat format =
		// GridFormatFinder.findFormat(rasterMapFile);
		if (format != null) {
			AbstractGridCoverage2DReader reader = format
					.getReader(rasterMapFile);
			if (reader != null) {
				Style style = StyleFac.createGreyscaleStyle(1);
				rasterLayer = new GridReaderLayer(reader, style);
			} else {
				LOG.warn("ERROR: can't create reader");
			}
		}
		return rasterLayer;
	}

	public static Layer readRasterColorLayer(File mapFile) {
		Layer rasterLayer = null;
		AbstractGridFormat format = GridFormatFinder.findFormat(mapFile);
		if (format != null) {
			AbstractGridCoverage2DReader reader = format.getReader(mapFile);
			if (reader != null) {
				Style style = StyleFac.createRGBStyle(reader);
				rasterLayer = new GridReaderLayer(reader, style);
			} else {
				LOG.warn("GeoToolsMapFactory.readRasterColorLayer() ERROR: can't create reader");
			}
		} else {
			LOG.warn("GeoToolsMapFactory.readRasterColorLayer() ERROR: can't create AbstractGridFormat");
		}
		return rasterLayer;
	}

	public static ShpLayer createShpLayer(File file) {
		if (!file.exists()) {
			LOG.warn("GeoToolsMapFactory.createShpLayer() ERROR: File doesn't exist "
					+ file.getPath());
			return null;
		}
		ShpLayer layer = new ShpLayer();
		layer.setLayer(file);
		return layer;
	}

	public static ShpLayer createShpLayer(File file, Style style) {
		if (!file.exists()) {
			LOG.error("GeoToolsMapFactory.createShpLayer2() Error: File doesn't exist "
				+ file.getPath());
			return null;
		}
		ShpLayer layer = new ShpLayer();
		layer.setLayer(file, style);
		return layer;
	}

	public static LineStringLayer createPointLineStringLayer(LineString ls) {
		return new LineStringLayer(ls, true);
	}
	public static LineStringLayer createLineLineStringLayer(LineString ls) {
		return new LineStringLayer(ls, false);
	}
	
	public static MobileLayer createOnePointLayer(String name, double x,
			double y) {
		MobileLayer layer = new MobileLayer(name, x, y);
		return layer;

	}
	
	public static GpxLayer createGpxLayer(File gpxfile) {
		if(gpxfile == null || !gpxfile.exists()) {
			return null;
		}
		GpxFactory factory = GpxFactory.getFactory(Type.SimpleGpxFactory);
		GpxDocument document = factory.readGpxDocument(gpxfile);
		if (document == null) {
			return null;
		}
		return createGpxLayer(document);
		
	}
	public static GpxLayer createGpxLayer(GpxDocument gpxdoc) {
		GpxLayer gpxlayer = new GpxLayer(gpxdoc);
		return gpxlayer;
	}

	// CoordinateReferenceSystem
	public static CoordinateReferenceSystem getWGS84CRS() {
		if (WGS84_CRS == null) {
			try {
				crsFactory = CRS.getAuthorityFactory(true);
				WGS84_CRS = crsFactory
						.createCoordinateReferenceSystem("EPSG:4326");
			} catch (Exception e) {
				LOG.error("getWGS84CRS() ERROR: can't create crs");
				System.exit(-1);
			}
		}
		return WGS84_CRS;
	}
	public static CoordinateReferenceSystem getEPSGCRS(int crscode) {
		String code = String.format("EPSG:%d", crscode);
		// System.out.println("GeoToolsMapFactory.getEPSGCrs() : "+code);
		CoordinateReferenceSystem crs = null;
		try {
			crsFactory = CRS.getAuthorityFactory(true);
			crs = crsFactory.createCoordinateReferenceSystem(code);
		} catch (Exception e) {
			LOG.error("GeoToolsMapFactory.getEPSGCRS() ERROR: can't create crs");
			return null;
		}
		return crs;
	}

	// CoordinateReferenceSystem: Transformations
	public static MathTransform mathTransform(CoordinateReferenceSystem source,
			CoordinateReferenceSystem target) {
		MathTransform tr = null;
		try {
			tr = CRS.findMathTransform(source, target);
		} catch (Exception e) {
			LOG.warn("GeoToolsMapFactory.mathTransform() ERROR: tr=null");
		}
		return tr;
	}
	public static MathTransform generateTransformFromWGS84(
			CoordinateReferenceSystem crs) {
		if(crs==null) {
			return null;
		}
		CoordinateReferenceSystem origin = null;
		try {
			origin = CRS.decode("EPSG:4326");
		} catch (Exception e) {
			LOG.error("GeoToolsMapFactory.generateTransformFromWGS84() ERROR: can't create EPSG:4326");
			return null;
		}
		CoordinateReferenceSystem dest = crs;
		boolean lenient = true;
		MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(origin, dest, lenient);
		} catch (FactoryException e) {
			LOG.error("GeoToolsMapFactory.generateTransformFromWGS84() ERROR: can't create transform");
		}
		return transform;
	}
	public static MathTransform generateTransformToWGS84(
			CoordinateReferenceSystem crs) {
		if(crs==null) {
			LOG.error("GeoToolsMapFactory.generateTransformToWGS84() ERROR: crs==null");
			return null;
		}
		CoordinateReferenceSystem origin = null;
		try {
			origin = CRS.decode("EPSG:4326");
		} catch (Exception e) {
			LOG.error("GeoToolsMapFactory.generateTransformToWGS84() ERROR: can't create EPSG:4326");
			return null;
		}
		CoordinateReferenceSystem dest = crs;
		boolean lenient = true;
		MathTransform transform = null;
		try {
			transform = CRS.findMathTransform(origin, dest, lenient);
		} catch (FactoryException e) {
			LOG.error("GeoToolsMapFactory.generateTransformToWGS84() ERROR: can't create transform");
		}
		return transform;
	}

	// CoordinateReferenceSystem Bounds
	public static ReferencedEnvelope getEPSG23030Bounds() {
		Envelope rect = new Envelope(229395.8528, 770604.1472, 3982627.8377,
				7095075.2268);
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.decode("EPSG:23030");
		} catch (Exception e) {
			return null;
		}
		ReferencedEnvelope bounds = new ReferencedEnvelope(rect, crs);
		return bounds;
	}
	public static ReferencedEnvelope getPNOABounds() {
		CoordinateReferenceSystem crs = GeoToolsMapFactory.getEPSGCRS(PNOA_CRS_CODE);
		ReferencedEnvelope env = new ReferencedEnvelope(
				PNOA_ENVELOPE_EPSG_25830[0], PNOA_ENVELOPE_EPSG_25830[1],
				PNOA_ENVELOPE_EPSG_25830[2], PNOA_ENVELOPE_EPSG_25830[3], crs);
		return env;
	}
	public static ReferencedEnvelope getWorldEpsg3857Bounds() {
		CoordinateReferenceSystem crs = GeoToolsMapFactory.getEPSGCRS(3857);
		ReferencedEnvelope env = new ReferencedEnvelope(
				WORLD_ENVELOPE_EPSG_3857[0], WORLD_ENVELOPE_EPSG_3857[1],
				WORLD_ENVELOPE_EPSG_3857[2], WORLD_ENVELOPE_EPSG_3857[3], crs);
		return env;
	}
	public static ReferencedEnvelope getPeruEpsg3857Bounds() {
		CoordinateReferenceSystem crs = GeoToolsMapFactory.getEPSGCRS(3857);
		ReferencedEnvelope env = new ReferencedEnvelope(
				PERU_ENVELOPE_EPSG_3857[0], PERU_ENVELOPE_EPSG_3857[1],
				PERU_ENVELOPE_EPSG_3857[2], PERU_ENVELOPE_EPSG_3857[3], crs);
		return env;
	}
	public static ReferencedEnvelope getSpainEpsg4326Bounds() {
		CoordinateReferenceSystem crs = GeoToolsMapFactory.getWGS84CRS();
		ReferencedEnvelope env = new ReferencedEnvelope(
				SPAIN_ENVELOPE_EPSG_4326[0], SPAIN_ENVELOPE_EPSG_4326[1],
				SPAIN_ENVELOPE_EPSG_4326[2], SPAIN_ENVELOPE_EPSG_4326[3], crs);
		return env;
	}

	// Utility methods
	public static LineString segmentToLinestringLonLat(TrackSegment segment) {
		List<Coordinate> lposnes = new ArrayList<Coordinate>();
		for (int i = 0; i < segment.size(); i++) {
			WayPoint wpt = segment.getWayPoint(i);
			double lon = wpt.getLongitude();
			double lat = wpt.getLatitude();
			Coordinate pos = new Coordinate(lon, lat);
			lposnes.add(pos);
		}
		Coordinate[] coords = new Coordinate[lposnes.size()];
		lposnes.toArray(coords);
		if(coords.length>1) {
			return geometryFactory.createLineString(coords);
		} else {
			return null;
		}

	}
	public static LineString segmentToLinestringLatLon(TrackSegment segment) {
		List<Coordinate> lposnes = new ArrayList<Coordinate>();
		for (int i = 0; i < segment.size(); i++) {
			WayPoint wpt = segment.getWayPoint(i);
			double lon = wpt.getLongitude();
			double lat = wpt.getLatitude();
			Coordinate pos = new Coordinate(lat, lon);
			lposnes.add(pos);
		}
		Coordinate[] coords = new Coordinate[lposnes.size()];
		lposnes.toArray(coords);

		return geometryFactory.createLineString(coords);

	}
	public static LineString segmentToLineStringUtm(TrackSegment segment) {
		List<Coordinate> lposnes = new ArrayList<Coordinate>();
		for (int i = 0; i < segment.size(); i++) {
			WayPoint wpt = segment.getWayPoint(i);
			double lon = wpt.getLongitude();
			double lat = wpt.getLatitude();
			double[] xy = Util.proyUtmWGS84(lon, lat);
			Coordinate pos = new Coordinate(xy[0], xy[1]);
			lposnes.add(pos);
		}
		Coordinate[] coords = new Coordinate[lposnes.size()];
		lposnes.toArray(coords);
		if(coords.length>1) {
			return geometryFactory.createLineString(coords);					
		} else {
			return null;
		}
	}
	public static SimpleFeatureCollection segmentToFeatureCollection(
			TrackSegment segment) {
		DefaultFeatureCollection coll = new DefaultFeatureCollection();

		SimpleFeatureType TYPE = null;
		try {
			TYPE = DataUtilities
					.createType("Location", "the_geom:Point:srid=4326," + // <-
																			// the
																			// geometry
																			// attribute:
																			// Point
																			// type
							"name:String,longitude:Double,latitude:Double,altitude:Double");
		} catch (Exception e) {
			LOG.error("Can't create FeatureType");
			return null;
		}

		SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(TYPE);
		for (int i = 0; i < segment.size(); i++) {
			WayPoint wp = segment.getWayPoint(i);
			Point p = geometryFactory.createPoint(new Coordinate(wp
					.getLongitude(), wp.getLatitude()));
			featBuilder.add(p);
			featBuilder.add(wp.getName());
			featBuilder.add(wp.getLongitude());
			featBuilder.add(wp.getLatitude());
			featBuilder.add(wp.getAltitude());
			SimpleFeature feature = featBuilder.buildFeature(null);
			coll.add(feature);
			// System.out.println(feature.getDefaultGeometry().toString());
		}
		return coll;
	}
	/**
	 * Crea una FeatureCollection de puntos en base a los puntos de una LineString.<br/>
	 * Utiliza un CRS Cartesian2D .<br/>
	 * El FeatureType lo crea con GeoToolsMapFactory.createPointFeatureType()
	 * 
	 * @param ls LineString en coordenadas cartesianas
	 * 
	 * @return SimpleFeatureCollection
	 */
	public static SimpleFeatureCollection lineStringToPointFeatureCollection(LineString ls) {
		DefaultFeatureCollection coll = new DefaultFeatureCollection();

		String wkt = "LOCAL_CS[\"Generic cartesian 2D\",LOCAL_DATUM[\"Unknow\",0]"+
				",UNIT[\"m\",1.0],AXIS[\"x\",EAST],AXIS[\"y\",NORTH]]";
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(wkt);
		} catch (FactoryException e) {
			LOG.error("Error parsing CRS "+e.getMessage());
		}
		
		final SimpleFeatureType TYPE = GeoToolsMapFactory.createPointFeatureType(crs);

		SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(TYPE);
		for (int i = 0; i < ls.getNumPoints(); i++) {
			Point p = geometryFactory.createPoint(ls.getCoordinateN(i));
			featBuilder.add(p);
			featBuilder.add(String.format("%d", i));
			SimpleFeature feature = featBuilder.buildFeature(null);
			coll.add(feature);
			// System.out.println(feature.getDefaultGeometry().toString());
		}
		return coll;
	}
	/**
	 * Crea una FeatureCollection de puntos en base a los puntos de una LineString.<br/>
	 * Utiliza un CRS Cartesian2D .<br/>
	 * El FeatureType lo crea con GeoToolsMapFactory.createLineFeatureType()
	 * 
	 * @param ls LineString en coordenadas cartesianas
	 * 
	 * @return SimpleFeatureCollection
	 */
	public static SimpleFeatureCollection lineStringToLineFeatureCollection(LineString ls) {
		DefaultFeatureCollection coll = new DefaultFeatureCollection();

		String wkt = "LOCAL_CS[\"Generic cartesian 2D\",LOCAL_DATUM[\"Unknow\",0]"+
				",UNIT[\"m\",1.0],AXIS[\"x\",EAST],AXIS[\"y\",NORTH]]";
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(wkt);
		} catch (FactoryException e) {
			LOG.error("lineStringToLineFeatureCollection(): Error parsing CRS "+e.getMessage());
		}
		
		final SimpleFeatureType TYPE = GeoToolsMapFactory.createLineFeatureType(crs);

		SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(TYPE);
		featBuilder.add(ls);
		featBuilder.add(String.format("1"));
		SimpleFeature feature = featBuilder.buildFeature(null);
		coll.add(feature);
		return coll;
	}
	
	public static SpatialIndex createSpatialIndex(SimpleFeatureCollection features) {
		final SpatialIndex index = new STRtree();
		try {
			features.accepts(new FeatureVisitor() {
				@Override
				public void visit(Feature feature) {
					SimpleFeature simpleFeature = (SimpleFeature) feature;
					Geometry geom = (MultiLineString) simpleFeature
							.getDefaultGeometry();
					// Just in case: check for null or empty geometry
					if (geom != null) {
						Envelope env = geom.getEnvelopeInternal();
						if (!env.isNull()) {
							index.insert(env, new LocationIndexedLine(geom));
						}
					}
				}
			}, new NullProgressListener());
		} catch (IOException e) {
			LOG.error("createSpatialIndex(): Can't create spatial index");
			return null;
		}
		return index;
	}

	/**
	 * Graba una colección de features en un shapefile. El FeatureType que se
	 * pasa debe de coincidir con el de la FeatureCollection. El shpfile no
	 * tiene por qué existir de antemano.
	 * 
	 * @param shpfile
	 * @param TYPE
	 * @param collection
	 * @return
	 */
	public static boolean saveAsShapefile(File shpfile,
			final SimpleFeatureType TYPE, SimpleFeatureCollection collection) {
		boolean result = false;
		SimpleFeatureStore featureStore = createShapefileDataStore(shpfile,
				TYPE);
		if (featureStore == null) {
			LOG.warn("saveAsShapefile(): ERROR: can't create datastore");
			return false;
		}
		Transaction transaction = new DefaultTransaction("create");
		featureStore.setTransaction(transaction);
		try {
			featureStore.addFeatures(collection);
			transaction.commit();
			result = true;
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (IOException e1) {
				LOG.warn("saveAsShapefile(): ERROR: can't rollback transaction\n"
						+ e.getMessage());
				result = false;
			}
		} finally {
			try {
				transaction.close();
			} catch (IOException e) {
				LOG.warn("GeoToolsMapFactory.saveAsShapefile() ERROR: can't close transaction\n"
						+ e.getMessage());
				result = false;
			}
		}
		return result;
	}

	/**
	 * Graba un linestring en un shapefile. Utiliza un CRS Cartesian2D
	 * 
	 * @param shpfile
	 * @param ls
	 * @return
	 */
	public static boolean saveAsShapefile(File shpfile, LineString ls, boolean pointgeometry) {
		CoordinateReferenceSystem crs = null;
		try {
			crs = CRS.parseWKT(GENERIC_CARTESIAN_2D);
		} catch (FactoryException e) {
			LOG.error("saveAsShapefile() Error parsing CRS "+e.getMessage());
		}
		final SimpleFeatureType TYPE;
		SimpleFeatureCollection coll = null;
		if(pointgeometry) {
			TYPE = GeoToolsMapFactory.createPointFeatureType(crs);	
			coll = GeoToolsMapFactory.lineStringToPointFeatureCollection(ls);
		} else {
			TYPE = GeoToolsMapFactory.createLineFeatureType(crs);
			coll = GeoToolsMapFactory.lineStringToLineFeatureCollection(ls);
		}
		if(coll != null) {
			return GeoToolsMapFactory.saveAsShapefile(shpfile, TYPE, coll);	
		} else {
			LOG.warn("saveAsShapefile() Error, FeatureCollection null"); 
			return false;
		}
		
	}
	/**
	 * Crea un fichero shapefile en disco
	 * 
	 * @param shpfile
	 * @param TYPE
	 * @return
	 */
	public static SimpleFeatureStore createShapefileDataStore(File shpfile,
			final SimpleFeatureType TYPE) {
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		ShapefileDataStore dataStore = null;
		String typeName = "";
		SimpleFeatureSource featureSource = null;

		try {
			params.put("url", shpfile.toURI().toURL());
			params.put("create spatial index", Boolean.TRUE);
			dataStore = (ShapefileDataStore) dataStoreFactory
					.createNewDataStore(params);
			dataStore.createSchema(TYPE);
			typeName = dataStore.getTypeNames()[0];
			featureSource = dataStore.getFeatureSource(typeName);
			if (featureSource instanceof SimpleFeatureStore) { // Comprobamos
																// read/write
																// access
				SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
				return featureStore;
			} else {
				LOG.error("createShapefileDataStore(): ERROR: can't grant write acccess");
				return null;
			}
		} catch (Exception e) {
			LOG.error("createShapefileDataStore(): ERROR: can't create shapefile datastore\n"
					+ e.getMessage());
			return null;
		}

	}

	public static boolean saveSegmentAsLineShapefile(File file,
			TrackSegment segment) {
		LineString ls = GeoToolsMapFactory.segmentToLinestringLonLat(segment);
		if (ls == null) {
			LOG.warn("saveSegmentAsLineShapefile(): TrackSegment null, can't save file " + file.getName());
			return false;
		}
		//System.out.println(ls.getLength());
		SimpleFeatureType TYPE = null;
		try {
			TYPE = DataUtilities.createType("Location",
					"the_geom:LineString:srid=4326,name:String");

		} catch (Exception e) {
			LOG.error("saveSegmentAsLineShapefile(): Can't create FeatureType");
			return false;
		}
		// SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		// builder.setName("Segment");
		// builder.setCRS(getWGS84CRS());
		// builder.add("Line", LineString.class);
		// builder.length(15).add("name", String.class);
		// TYPE = builder.buildFeatureType();
		//
		SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(TYPE);
		featBuilder.add(ls);
		featBuilder.add(Util.fileNameWithoutExtension(file));
		SimpleFeature feature = featBuilder.buildFeature(null);

		DefaultFeatureCollection coll = new DefaultFeatureCollection();
		coll.add(feature);

		boolean result = GeoToolsMapFactory.saveAsShapefile(file, TYPE, coll);
		if (!result) {
			LOG.warn("saveSegmentAsLineShapefile(): Can't save file " + file.getName());
		}
		return result;
	}

	public static boolean saveSegmentAsPointsShapefile(File file,
			TrackSegment segment) {

		DefaultFeatureCollection coll = new DefaultFeatureCollection();

		SimpleFeatureType TYPE = null;
		try {
			TYPE = DataUtilities
					.createType("Location", "the_geom:Point:srid=4326," + // <-
																			// the
																			// geometry
																			// attribute:
																			// Point
																			// type
							"name:String,longitude:Double,latitude:Double,altitude:Double");

		} catch (Exception e) {
			LOG.error("saveSegmentAsPointsShapefile(): Can't create FeatureType");
			return false;
		}

		SimpleFeatureBuilder featBuilder = new SimpleFeatureBuilder(TYPE);

		for (int i = 0; i < segment.size(); i++) {
			WayPoint wp = segment.getWayPoint(i);
			Point p = geometryFactory.createPoint(new Coordinate(wp
					.getLongitude(), wp.getLatitude()));
			featBuilder.add(p);
			featBuilder.add(wp.getName());
			featBuilder.add(wp.getLongitude());
			featBuilder.add(wp.getLatitude());
			featBuilder.add(wp.getAltitude());
			SimpleFeature feature = featBuilder.buildFeature(null);
			coll.add(feature);
			// System.out.println(feature.getDefaultGeometry().toString());
		}

		boolean result = GeoToolsMapFactory.saveAsShapefile(file, TYPE, coll);
		if (!result) {
			LOG.warn("saveSegmentAsPointsShapefile(): Can't save file " + file.getName());
		}
		return result;
	}

	public static final SimpleFeatureType createWGS84PointFeatureType() {

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Location");
		builder.setCRS(getWGS84CRS());
		// builder.setSRS( "EPSG:4326" );
		// builder.setCRS(GeoToolsMapFactory.getWGS84CRS()); // <- Coordinate reference
		// system

		// add attributes in order
		builder.add("Location", Point.class);
		builder.length(15).add("gid", String.class); // <- 15 chars width for
														// name field

		// build the type
		SimpleFeatureType TYPE = builder.buildFeatureType();

		// SimpleFeatureType TYPE = null;
		// try {
		// TYPE = DataUtilities.createType("Location",
		// "the_geom:Point:srid=4326," + // <- the geometry attribute: Point
		// type
		// "name:String");
		//
		// } catch (Exception e) {
		// LOG.error("Can't create FeatureType");
		// }

		return TYPE;
	}

	/**
	 * 
	 * @param crs
	 * @return
	 */
	public static final SimpleFeatureType createPointFeatureType(
			CoordinateReferenceSystem crs) {

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

		builder.setName("Points");
		builder.setCRS(crs);
		builder.add("the_geom", Point.class);
		builder.add("gid", String.class); 					

		//builder.setDefaultGeometry("location");

		// build the type
		final SimpleFeatureType LOCATION = builder.buildFeatureType();

		return LOCATION;
	}
	/**
	 * 
	 * @param crs
	 * @return
	 */
	public static final SimpleFeatureType createLineFeatureType(
			CoordinateReferenceSystem crs) {

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

		builder.setName("Points");
		builder.setCRS(crs);
		builder.add("the_geom", LineString.class);
		builder.add("gid", String.class); 					

		//builder.setDefaultGeometry("location");

		// build the type
		final SimpleFeatureType LINETYPE = builder.buildFeatureType();

		return LINETYPE;
	}

}
