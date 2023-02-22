package string;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class IndexOfChar {
	
	private static final String STR = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + IndexOfChar.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	@Benchmark
	public void stringIndexOfChar(final Blackhole bh) {
		final int i = STR.indexOf('H');
		bh.consume(i);
	}
	
	@Benchmark
	public void stringIndexOfString(final Blackhole bh) {
		final int i = STR.indexOf("H");
		bh.consume(i);
	}
	
	@Benchmark
	public void stringContains(final Blackhole bh) {
		final boolean found = STR.contains("H");
		bh.consume(found);
	}
	
}
/*
JDK 17.0.1 - desktop 4+4 cores
Benchmark                         Mode  Cnt          Score          Error  Units
IndexOfChar.stringContains       thrpt    3  231105030,988 ±  3028295,129  ops/s
IndexOfChar.stringIndexOfChar    thrpt    3  671552177,044 ± 25471593,411  ops/s
IndexOfChar.stringIndexOfString  thrpt    3  238023856,555 ±  8498432,357  ops/s

JDK 19.0.2 - desktop 4+4 cores - balanced cpu
Benchmark                         Mode  Cnt          Score          Error  Units
IndexOfChar.stringContains       thrpt    3  258370134,668 ±  2530471,236  ops/s
IndexOfChar.stringIndexOfChar    thrpt    3  671175777,586 ± 47950436,449  ops/s
IndexOfChar.stringIndexOfString  thrpt    3  238790873,426 ± 28001554,627  ops/s

 */
