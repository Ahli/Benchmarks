package random;

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

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class RandomInt {
	private static final int BOUND = 100;
	private static final int BOUND_MINUS_ONE = BOUND - 1;
	private final Random random = new Random();
	private final SecureRandom secureRandom = new SecureRandom();
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + RandomInt.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	@Benchmark
	public void randomObj(final Blackhole bh) {
		final int i = random.nextInt(BOUND);
		bh.consume(i);
	}
	
	@Benchmark
	public void secureRandomObj(final Blackhole bh) {
		final int i = secureRandom.nextInt(BOUND);
		bh.consume(i);
	}
	
	@Benchmark
	public void threadLocal(final Blackhole bh) {
		final int i = ThreadLocalRandom.current().nextInt(BOUND);
		bh.consume(i);
	}
	
	@Benchmark
	public void mathRandom(final Blackhole bh) {
		final int i = (int) (Math.random() * BOUND_MINUS_ONE);
		bh.consume(i);
	}
}

/*
JDK 16.0.2 - laptop 6+6 cores
Benchmark                   Mode  Cnt          Score           Error  Units
RandomInt.mathRandom       thrpt    3   59798407,359 ±   1525333,217  ops/s
RandomInt.randomObj        thrpt    3  120296216,908 ±   1507199,492  ops/s
RandomInt.secureRandomObj  thrpt    3    1258376,314 ±     29481,553  ops/s
RandomInt.threadLocal      thrpt    3  229589836,545 ± 140921692,025  ops/s

JDK 17 - desktop 4+4 cores
Benchmark                   Mode  Cnt          Score           Error  Units
RandomInt.mathRandom       thrpt    3   66340968,925 ±   2301364,616  ops/s
RandomInt.randomObj        thrpt    3  130940510,779 ±  77147573,380  ops/s
RandomInt.secureRandomObj  thrpt    3    1360249,247 ±     90740,665  ops/s
RandomInt.threadLocal      thrpt    3  252509940,437 ± 167041711,256  ops/s

JDK 18-ea+26 - desktop 4+4 cores
Benchmark                   Mode  Cnt          Score           Error  Units
RandomInt.mathRandom       thrpt    3   66437283,645 ±   2419059,806  ops/s
RandomInt.randomObj        thrpt    3  128654811,271 ±  75993215,491  ops/s
RandomInt.secureRandomObj  thrpt    3    1395814,309 ±     56318,045  ops/s
RandomInt.threadLocal      thrpt    3  258730797,850 ± 103217057,845  ops/s
*/
