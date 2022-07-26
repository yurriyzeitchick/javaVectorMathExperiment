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
