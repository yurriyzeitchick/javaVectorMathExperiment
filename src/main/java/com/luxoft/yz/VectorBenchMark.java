package com.luxoft.yz;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.WinPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author YZaychyk
 * @since 1.0
 **/
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 1, jvmArgsPrepend = "--add-modules=jdk.incubator.vector")
public class VectorBenchMark
{
    private Geometry geom17Points;
    private Geometry geom19Points;
    private Geometry geom250Points;

    private VectorLine vectorLine17Points;
    private VectorLine vectorLine19Points;
    private VectorLine vectorLine250Points;

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(VectorBenchMark.class.getSimpleName())
                .addProfiler(WinPerfAsmProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        geom17Points = LengthExperiment.getGeom(WktGeometries.WKT_LS_17_POINTS);
        geom19Points = LengthExperiment.getGeom(WktGeometries.WKT_LS_19_POINTS);
        geom250Points = LengthExperiment.getGeom(WktGeometries.WKT_LS_250_POINTS);

        vectorLine17Points = VectorLine.of((LineString) geom17Points);
        vectorLine19Points = VectorLine.of((LineString) geom19Points);
        vectorLine250Points = VectorLine.of((LineString) geom250Points);
    }

    @Benchmark
    public double lengthVector17Ponts() {
        return VectorLengthCalc.calculate(vectorLine17Points);
    }

    @Benchmark
    public double lengthVector19Ponts() {
        return VectorLengthCalc.calculate(vectorLine19Points);
    }

    @Benchmark
    public double lengthVector250Ponts() {
        return VectorLengthCalc.calculate(vectorLine250Points);
    }

    @Benchmark
    public double lengthScalar17Points() {
        return LengthExperiment.calcLengthScalar((LineString)geom17Points);
    }

    @Benchmark
    public double lengthScalar19Points() {
        return LengthExperiment.calcLengthScalar((LineString)geom19Points);
    }

    @Benchmark
    public double lengthScalar250Points() {
        return LengthExperiment.calcLengthScalar((LineString)geom250Points);
    }
}
