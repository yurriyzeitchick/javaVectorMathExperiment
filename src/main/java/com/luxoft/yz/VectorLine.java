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

import org.locationtech.jts.geom.LineString;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public record VectorLine(double[] latitudes, double[] longitudes) {

    public static VectorLine of(LineString geom)
    {
        int dim = geom.getNumPoints();
        double[] xCoords = new double[dim];
        double[] yCoords = new double[dim];

        var coords = geom.getCoordinateSequence();
        for (int i = 0; i < dim; i++)
        {
            var coordinate = coords.getCoordinate(i);
            xCoords[i] = coordinate.x;
            yCoords[i] = coordinate.y;
        }
        return new VectorLine(xCoords, yCoords);
    }
}
