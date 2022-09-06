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

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.JCudaDriver;
import jcuda.vec.VecDouble;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import static jcuda.driver.JCudaDriver.*;

/**
 * @author YZaychyk
 * @since 1.0
 **/
public class GpuCudaLengthCalc
{
    public static GpuCudaLengthCalc getInstance() {
        return new GpuCudaLengthCalc();
    }

    public static void main(String[] args)
    {
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        // Initialize the driver and create a context for the first device.
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        CUcontext context = new CUcontext();
        cuCtxCreate(context, 0, device);

        // Afterwards, initialize the vector library, which will
        // attach to the current context
        VecDouble.init();

        // Allocate and fill the host input data
        int n = 50000;
        double hostX[] = new double[n];
        double hostY[] = new double[n];
        for(int i = 0; i < n; i++)
        {
            hostX[i] = (double)i;
            hostY[i] = (double)i;
        }

        // Allocate the device pointers, and copy the
        // host input data to the device
        CUdeviceptr deviceX = new CUdeviceptr();
        cuMemAlloc(deviceX, n * Sizeof.DOUBLE);
        cuMemcpyHtoD(deviceX, Pointer.to(hostX), n * Sizeof.DOUBLE);

        CUdeviceptr deviceY = new CUdeviceptr();
        cuMemAlloc(deviceY, n * Sizeof.DOUBLE);
        cuMemcpyHtoD(deviceY, Pointer.to(hostY), n * Sizeof.DOUBLE);

        CUdeviceptr deviceResult = new CUdeviceptr();
        cuMemAlloc(deviceResult, n * Sizeof.DOUBLE);

        // Perform the vector operations
        VecDouble.cos(n, deviceX, deviceX);               // x = cos(x)
        VecDouble.mul(n, deviceX, deviceX, deviceX);      // x = x*x
        VecDouble.sin(n, deviceY, deviceY);               // y = sin(y)
        VecDouble.mul(n, deviceY, deviceY, deviceY);      // y = y*y
        VecDouble.add(n, deviceResult, deviceX, deviceY); // result = x+y

        // Allocate host output memory and copy the device output
        // to the host.
        double hostResult[] = new double[n];
        cuMemcpyDtoH(Pointer.to(hostResult), deviceResult, n * Sizeof.DOUBLE);

        // Verify the result
        boolean passed = true;
        for(int i = 0; i < n; i++)
        {
            double expected =
                    Math.cos(hostX[i])*Math.cos(hostX[i])+
                            Math.sin(hostY[i])*Math.sin(hostY[i]);
            if (Math.abs(hostResult[i] - expected) > 1e-14)
            {
                System.out.println(
                        "At index "+i+ " found "+hostResult[i]+
                                " but expected "+expected);
                passed = false;
                break;
            }
        }
        System.out.println("Test "+(passed?"PASSED":"FAILED"));

        // Clean up.
        cuMemFree(deviceX);
        cuMemFree(deviceY);
        cuMemFree(deviceResult);
        VecDouble.shutdown();
    }

    public static void init() {
        JCudaDriver.setExceptionsEnabled(true);

        cuInit(0);
        var dev = new CUdevice();
        cuDeviceGet(dev, 0);
        var ctx = new CUcontext();
        cuCtxCreate(ctx, 0, dev);
    }

    public static double calculate(VectorLine line) {
        // Enable exceptions and omit all subsequent error checks
        JCudaDriver.setExceptionsEnabled(true);

        var x1 = line.latitudes();
        var x2 = Arrays.copyOfRange(x1, 1, x1.length);
        var y1 = line.longitudes();
        var y2 = Arrays.copyOfRange(y1, 1, y1.length);

        var vecLength = x2.length;

        VecDouble.init();

        var devPtrX1 = createDoubleVectorPointer(x1, x2.length);
        var devPtrX2 = createDoubleVectorPointer(x2, x2.length);
        var devPtrY1 = createDoubleVectorPointer(y1, y2.length);
        var devPtrY2 = createDoubleVectorPointer(y2, y2.length);

        var devPtrDX = createDoubleVectorPointer(null, vecLength, false);
        var devPtrDY = createDoubleVectorPointer(null, vecLength, false);

        double[] hypotenuses = new double[x2.length];
        var devResultPtr = createDoubleVectorPointer(hypotenuses, hypotenuses.length, false);

        VecDouble.sub(vecLength, devPtrDX, devPtrX2, devPtrX1);
        VecDouble.sub(vecLength, devPtrDY, devPtrY2, devPtrY1);
        VecDouble.mul(vecLength, devPtrDX, devPtrDX, devPtrDX);
        VecDouble.mul(vecLength, devPtrDY, devPtrDY, devPtrDY);
        VecDouble.add(vecLength, devResultPtr, devPtrDX, devPtrDY);
        VecDouble.sqrt(vecLength, devResultPtr, devResultPtr);

        cuMemcpyDtoH(Pointer.to(hypotenuses), devResultPtr, vecLength * Sizeof.DOUBLE);

        cuMemFree(devResultPtr);
        cuMemFree(devPtrDX);
        cuMemFree(devPtrDY);
        cuMemFree(devPtrX1);
        cuMemFree(devPtrX2);
        cuMemFree(devPtrY1);
        cuMemFree(devPtrY2);

        VecDouble.shutdown();

        return DoubleStream.of(hypotenuses).sum();
    }

    private static CUdeviceptr createDoubleVectorPointer(double[] array, int length, boolean copyToDevice) {
        var devPtr = new CUdeviceptr();
        var size = length * Sizeof.DOUBLE;
        cuMemAlloc(devPtr, size);

        if (copyToDevice)
            cuMemcpyHtoD(devPtr, Pointer.to(array), size);

        return devPtr;
    }

    private static CUdeviceptr createDoubleVectorPointer(double[] array, int length) {
        return createDoubleVectorPointer(array, length, true);
    }
}
