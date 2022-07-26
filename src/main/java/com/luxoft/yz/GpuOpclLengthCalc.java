package com.luxoft.yz;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.internal.kernel.KernelManager;

import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class GpuOpclLengthCalc
{
    public static void main(String[] args) {
        KernelManager.instance().getDefaultPreferences().getPreferredDevices(null).forEach(System.out::println);
    }

    public static double calculate(VectorLine line) {
        var x1 = line.latitudes();
        var x2 = Arrays.copyOfRange(x1, 1, x1.length);
        var y1 = line.longitudes();
        var y2 = Arrays.copyOfRange(y1, 1, y1.length);

        double[] hypotenouses = new double[x2.length];

        var kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                double dx = x2[gid] - x1[gid];
                double dy = y2[gid] - y1[gid];

                hypotenouses[gid] = dx * dx + dy * dy;
            }
        };

        kernel.execute(Range.create(x2.length));
        var sb = new StringBuilder();
        KernelManager.instance().reportDeviceUsage(sb, true);
        System.out.println(sb);
        kernel.dispose();
        return DoubleStream.of(hypotenouses).map(Math::sqrt).sum();
    }
}
