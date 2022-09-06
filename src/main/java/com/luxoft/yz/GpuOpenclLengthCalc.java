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

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.internal.kernel.KernelManager;

import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class GpuOpenclLengthCalc
{
    public static void main(String[] args) {
        KernelManager.instance().getDefaultPreferences().getPreferredDevices(null).forEach(System.out::println);
    }

    public static double calculate(VectorLine line, boolean addLogging) {
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

        kernel.dispose();
        return DoubleStream.of(hypotenouses).map(Math::sqrt).sum();
    }

    public static double calculate(VectorLine line) {
        return calculate(line, false);
    }
}
