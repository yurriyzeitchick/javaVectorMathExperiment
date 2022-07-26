package com.luxoft.yz;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class LengthExperiment {

    private static Properties config;

    public static void main(String[] args) {
        readConfig();
        /*runFor(WktGeometries.WKT_LS_17_POINTS);
        runFor(WktGeometries.WKT_LS_19_POINTS);
        runFor(WktGeometries.WKT_LS_250_POINTS);*/
        cudaLengthExperiment();
        //runAparapiGPU();


    }

    public static void readConfig() {
        config = new Properties();
        try {
            config.load(LengthExperiment.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void runFor(String wkt) {
        var geom = getGeom(wkt);
        System.out.printf("============= Length calculation experiment for  %s with %d points =============\n", geom.getGeometryType(), geom.getNumPoints());
        System.out.printf("Scalar length calculation: %f \n", geom.getLength());
        System.out.printf("Vector length calculation: %f \n\n", VectorLengthCalc.calculate(VectorLine.of((LineString) geom)));

    }

    public static void runJcudaGPU() {
        var geom = getGeom(WktGeometries.WKT_LS_17_POINTS);
        GpuCudaLengthCalc.init();
        var length = GpuCudaLengthCalc.calculate(VectorLine.of((LineString) geom));
        System.out.println(length);

    }

    public static void cudaLengthExperiment() {
        GpuCudaLengthCalc.init();
        var geometriesIterator = getGeometriesFromShapeFile();

        var startTime = System.currentTimeMillis();
        while (geometriesIterator.hasNext()) {
            Geometry geom = (Geometry)geometriesIterator.next().getDefaultGeometry();
            if (geom.getGeometryType().equalsIgnoreCase(MultiLineString.TYPENAME_MULTILINESTRING)) {
                MultiLineString multiLineString = (MultiLineString) geom;
                for (int i = 0; i < multiLineString.getNumGeometries(); i++)
                    GpuCudaLengthCalc.calculate(VectorLine.of((LineString) multiLineString.getGeometryN(i)));
            }
            else if (geom.getGeometryType().equalsIgnoreCase(LineString.TYPENAME_LINESTRING)) {
                GpuCudaLengthCalc.calculate(VectorLine.of((LineString) geom));
            }
        }


        var endTime = System.currentTimeMillis();
        System.out.println("CUDA length experiment took:" + LocalTime.ofInstant(Instant.ofEpochMilli(endTime - startTime), TimeZone.getDefault().toZoneId()).format(
                DateTimeFormatter.ofPattern("mm:ss:nnn")));
    }

    public static SimpleFeatureIterator getGeometriesFromShapeFile() {
        DataStore dataStore = null;
        try {
            dataStore = FileDataStoreFinder.getDataStore(Path.of(config.getProperty("test.shapefile.path")).toFile());
            var featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            return featureSource.getFeatures().features();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (dataStore != null)
                dataStore.dispose();
        }
    }

    public static void runAparapiGPU() {
        var geom = getGeom(WktGeometries.WKT_LS_17_POINTS);
        var length = GpuOpclLengthCalc.calculate(VectorLine.of((LineString) geom));
        System.out.println(length);
    }

    public static double calcLengthScalar(LineString lineString)
    {
        return lineString.getLength();
    }

    public static Geometry getGeom(String wkt) {
        var wktReader = new WKTReader();
        try {
            return wktReader.read(wkt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
