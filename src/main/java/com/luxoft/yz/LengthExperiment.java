/* This file is a part of javaVectorMathExperiment.
 * javaVectorMathExperiment is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.luxoft.yz;

import org.geotools.data.DataStore;
import org.geotools.data.FileDataStoreFinder;
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
import java.util.*;
import java.util.function.Consumer;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class LengthExperiment {

    private static Properties config;

    public static void main(String[] args) {
        readConfig();
        compareGPGPU();
    }

    public static void readConfig() {
        config = new Properties();
        try {
            config.load(LengthExperiment.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void compareGPGPU() {
        var geometries = getGeometriesFromShapeFile();
        var vectors = convert(geometries);

        System.out.println("Measuring CUDA");
        measure("CUDA", vectors.size(), vectors, LengthExperiment::cudaLengthExperiment);
        measure("CUDA", vectors.size(), vectors, LengthExperiment::cudaLengthExperiment);
        measure("CUDA", vectors.size(), vectors, LengthExperiment::cudaLengthExperiment);

        System.out.println("\n\nMeasuring OpenCL");
        measure("OpenCL", vectors.size(), vectors, LengthExperiment::openclLengthExperiment);
        measure("OpenCL", vectors.size(), vectors, LengthExperiment::openclLengthExperiment);
        measure("OpenCL", vectors.size(), vectors, LengthExperiment::openclLengthExperiment);

        System.out.println("\n\nMeasuring SCALAR");
        measure("SCALAR", geometries.size(), geometries, LengthExperiment::scalarLengthExperiment);
        measure("SCALAR", geometries.size(), geometries, LengthExperiment::scalarLengthExperiment);
        measure("SCALAR", geometries.size(), geometries, LengthExperiment::scalarLengthExperiment);
    }

    public static void runFor(String wkt) {
        var geom = getGeom(wkt);
        System.out.printf("============= Length calculation experiment for  %s with %d points =============\n", geom.getGeometryType(), geom.getNumPoints());
        System.out.printf("Scalar length calculation: %f \n", geom.getLength());
        System.out.printf("Vector length calculation: %f \n\n", VectorLengthCalc.calculate(VectorLine.of((LineString) geom)));

    }

    public static Collection<VectorLine> convert(Collection<Geometry> geometries) {
        var vectors = new ArrayList<VectorLine>(geometries.size());
        for (var geom : geometries) {
            if (geom.getGeometryType().equalsIgnoreCase(MultiLineString.TYPENAME_MULTILINESTRING)) {
                for (int i = 0; i < geom.getNumGeometries(); i++)
                    vectors.add(VectorLine.of((LineString) geom.getGeometryN(i)));
            }
            else if (geom.getGeometryType().equalsIgnoreCase(LineString.TYPENAME_LINESTRING)) {
                vectors.add(VectorLine.of((LineString) geom));
            }
        }
        return vectors;
    }

    public static <L> void measure(String methodName, int geometriesCount, Collection<L> items, Consumer<Collection<L>> calculator) {
        var startTime = System.currentTimeMillis();
        calculator.accept(items);
        var endTime = System.currentTimeMillis();
        System.out.println(methodName + " length experiment took:" + LocalTime.ofInstant(Instant.ofEpochMilli(endTime - startTime),
                TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("mm:ss:nnn")));
    }

    public static void scalarLengthExperiment(Collection<Geometry> geometries) {
        for (var geom : geometries)
            geom.getLength();
    }

    public static void cudaLengthExperiment(Collection<VectorLine> vectors) {
        GpuCudaLengthCalc.init();
        for (var vector : vectors)
            GpuCudaLengthCalc.calculate(vector);
    }

    public static void openclLengthExperiment(Collection<VectorLine> vectors) {
       for (var vector : vectors)
           GpuOpenclLengthCalc.calculate(vector);
    }

    public static List<Geometry> getGeometriesFromShapeFile() {
        DataStore dataStore = null;
        try {
            dataStore = FileDataStoreFinder.getDataStore(Path.of(config.getProperty("test.shapefile.path")).toFile());
            var featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            var iterator = featureSource.getFeatures().features();
            var geometries = new ArrayList<Geometry>();

            while (iterator.hasNext())
                geometries.add((Geometry) iterator.next().getDefaultGeometry());

            iterator.close();
            return geometries;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (dataStore != null)
                dataStore.dispose();
        }
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
