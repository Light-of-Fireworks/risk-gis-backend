package com.riskgis.util;

import cn.hutool.core.util.StrUtil;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GISUtil {

    private static final GeometryFactory GEOMETRY_FACTORY = JTSFactoryFinder.getGeometryFactory();

    public static String wktPoint(double lng, double lat) {
        return StrUtil.format("POINT({} {})", lng, lat);
    }

    public static String wktPolygon(double[][] coordinates) {
        StringBuilder sb = new StringBuilder("POLYGON((");
        for (int i = 0; i < coordinates.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(coordinates[i][0]).append(" ").append(coordinates[i][1]);
        }
        sb.append("))");
        return sb.toString();
    }

    public static String wktLineString(double[][] coordinates) {
        StringBuilder sb = new StringBuilder("LINESTRING(");
        for (int i = 0; i < coordinates.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(coordinates[i][0]).append(" ").append(coordinates[i][1]);
        }
        sb.append(")");
        return sb.toString();
    }

    public static double[] parsePoint(String wkt) {
        String coords = wkt.replace("POINT(", "").replace(")", "").trim();
        String[] parts = coords.split("\\s+");
        return new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
    }

    public static Geometry wktToGeometry(String wkt) throws ParseException {
        WKTReader reader = new WKTReader(GEOMETRY_FACTORY);
        return reader.read(wkt);
    }

    public static String geometryToWkt(Geometry geometry) {
        return geometry.toText();
    }

    public static Point createPoint(double lng, double lat) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat));
    }

    public static Polygon createPolygon(double[][] coordinates) {
        Coordinate[] coords = new Coordinate[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            coords[i] = new Coordinate(coordinates[i][0], coordinates[i][1]);
        }
        return GEOMETRY_FACTORY.createPolygon(coords);
    }

    public static double calculateDistance(Geometry geom1, Geometry geom2) {
        return geom1.distance(geom2);
    }

    public static Geometry createBuffer(Geometry geometry, double distance) {
        return geometry.buffer(distance);
    }

    public static boolean intersects(Geometry geom1, Geometry geom2) {
        return geom1.intersects(geom2);
    }

    public static double calculateArea(Geometry geometry) {
        return geometry.getArea();
    }
}
