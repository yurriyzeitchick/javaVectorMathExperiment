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
