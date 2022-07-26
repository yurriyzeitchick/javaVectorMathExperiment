package com.luxoft.yz;

import org.locationtech.jts.geom.LineString;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class CalcLengthGt
{
    public static void main(String[] args) {

    }

    public static double length(LineString line)
    {
        return line.getLength();
    }
}
