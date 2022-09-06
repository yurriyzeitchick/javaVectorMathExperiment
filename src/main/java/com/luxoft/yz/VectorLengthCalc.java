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

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class VectorLengthCalc
{
    public static final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_256;

    public static double calculate(VectorLine vl) {
        double length = 0D;

        int upperBound = SPECIES.loopBound(vl.latitudes().length);
        int i = 0;
        for (; i < upperBound; i += SPECIES.length()) {
            var vX1 = DoubleVector.fromArray(SPECIES, vl.latitudes(), i);
            var vX2 = DoubleVector.fromArray(SPECIES, vl.latitudes(), i + 1);
            var vY1 = DoubleVector.fromArray(SPECIES, vl.longitudes(), i);
            var vY2 = DoubleVector.fromArray(SPECIES, vl.longitudes(), i + 1);

            var vdx = vX2.sub(vX1);
            var vdy = vY2.sub(vY1);
            var vHypotenuses = vdx.mul(vdx).add(vdy.mul(vdy)).lanewise(VectorOperators.SQRT);

            length += vHypotenuses.reduceLanes(VectorOperators.ADD);
        }

        for (; i < vl.latitudes().length - 1; i++)
        {
            var dx = vl.latitudes()[i + 1] - vl.latitudes()[i];
            var dy = vl.longitudes()[i + 1] - vl.longitudes()[i];
            length += Math.sqrt(dx * dx + dy * dy);
        }

        return length;
    }
}
