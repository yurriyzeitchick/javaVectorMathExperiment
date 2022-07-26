package com.luxoft.yz;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class GpgpuLengthCalculationMethodsTest
{
    @Test
    public void test_With17PointsWktGeom_ShallReturnEqualLengthValuesForScalarAndCudaAproaches()
    {
        var geom = LengthExperiment.getGeom(WktGeometries.WKT_LS_17_POINTS);
        var threshold = 2.220446049250313E-16;
        var tolerance = GpuCudaLengthCalc.calculate(VectorLine.of((LineString)geom)) - geom.getLength();
        Assertions.assertTrue(Math.abs(tolerance) <= threshold,
                "Scalar and CUDA methods shall be equal within precision: " + threshold);
    }


    @Test
    public void test_With17PointsWktGeom_ShallReturnEqualLengthValuesForScalarAndOpenCLApproaches()
    {
        var geom = LengthExperiment.getGeom(WktGeometries.WKT_LS_17_POINTS);
        var threshold = 2.220446049250313E-16;
        var tolerance = GpuOpenclLengthCalc.calculate(VectorLine.of((LineString)geom)) - geom.getLength();
        Assertions.assertTrue(Math.abs(tolerance) <= threshold,
                "Scalar and OpenCL methods shall be equal within precision: " + threshold);
    }
}
