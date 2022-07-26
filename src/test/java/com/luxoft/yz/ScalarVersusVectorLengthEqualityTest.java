package com.luxoft.yz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class ScalarVersusVectorLengthEqualityTest
{
    @Test
    public void test_With17PointsWktGeom_ShallReturnEqualLengthValuesForScalarAndVectorAlgorithms()
    {
        var geom = LengthExperiment.getGeom(WktGeometries.WKT_LS_17_POINTS);
        Assertions.assertEquals(geom.getLength(), VectorLengthCalc.calculate(VectorLine.of((LineString)geom)),
                "Scalar and vector methods shall return equal length values");
    }

    @Test
    public void test_With19PointsWktGeom_ShallReturnEqualLengthValuesForScalarAndVectorAlgorithms()
    {
        var geom = LengthExperiment.getGeom(WktGeometries.WKT_LS_19_POINTS);
        Assertions.assertEquals(geom.getLength(), VectorLengthCalc.calculate(VectorLine.of((LineString)geom)),
                "Scalar and vector methods shall return equal length values");
    }

    @Test
    public void test_With250PointsWktGeom_ShallReturnEqualLengthValuesForScalarAndVectorAlgorithms()
    {
        var geom = LengthExperiment.getGeom(WktGeometries.WKT_LS_250_POINTS);
        Assertions.assertEquals(geom.getLength(), VectorLengthCalc.calculate(VectorLine.of((LineString)geom)),
                "Scalar and vector methods shall return equal length values");
    }
}
