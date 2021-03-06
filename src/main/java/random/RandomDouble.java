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

@Warmup(iterations = 3, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2, jvmArgs = { "-Xms2G", "-Xmx2G" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
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
JDK 16-ea
RandomDouble.threadLocal      avgt    6     4,195 ±  0,093  ns/op
RandomDouble.randomObj        avgt    6    16,987 ±  0,386  ns/op
RandomDouble.mathRandom       avgt    6    17,117 ±  0,112  ns/op
RandomDouble.secureRandomObj  avgt    6  1624,399 ± 47,418  ns/op
*/
