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
