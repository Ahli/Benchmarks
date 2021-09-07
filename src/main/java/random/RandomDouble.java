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
public class RandomDouble {
	private final Random random = new Random();
	private final SecureRandom secureRandom = new SecureRandom();
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + RandomDouble.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	@Benchmark
	public void randomObj(final Blackhole bh) {
		final double i = random.nextDouble();
		bh.consume(i);
	}
	
	@Benchmark
	public void secureRandomObj(final Blackhole bh) {
		final double i = secureRandom.nextDouble();
		bh.consume(i);
	}
	
	@Benchmark
	public void threadLocal(final Blackhole bh) {
		final double i = ThreadLocalRandom.current().nextDouble();
		bh.consume(i);
	}
	
	@Benchmark
	public void mathRandom(final Blackhole bh) {
		final double i = Math.random();
		bh.consume(i);
	}
}

/*
JDK 17
Benchmark                      Mode  Cnt          Score         Error  Units
RandomDouble.mathRandom       thrpt    3   60104513,508 ±  385168,918  ops/s
RandomDouble.randomObj        thrpt    3   60254504,016 ± 1499933,840  ops/s
RandomDouble.secureRandomObj  thrpt    3     640035,457 ±   59971,996  ops/s
RandomDouble.threadLocal      thrpt    3  182857277,962 ± 8434544,009  ops/s
*/
