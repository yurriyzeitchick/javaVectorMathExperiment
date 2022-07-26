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
