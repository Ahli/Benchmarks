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
JDK 17 - laptop 6+6 cores
Benchmark                      Mode  Cnt          Score         Error  Units
RandomDouble.mathRandom       thrpt    3   60104513,508 ±  385168,918  ops/s
RandomDouble.randomObj        thrpt    3   60254504,016 ± 1499933,840  ops/s
RandomDouble.secureRandomObj  thrpt    3     640035,457 ±   59971,996  ops/s
RandomDouble.threadLocal      thrpt    3  182857277,962 ± 8434544,009  ops/s

JDK 17 - desktop 4+4 cores
Benchmark                      Mode  Cnt          Score         Error  Units
RandomDouble.mathRandom       thrpt    3   66751109,009 ±  188318,277  ops/s
RandomDouble.randomObj        thrpt    3   66712586,978 ± 1047898,826  ops/s
RandomDouble.secureRandomObj  thrpt    3     691958,587 ±   23782,281  ops/s
RandomDouble.threadLocal      thrpt    3  202124811,564 ± 9384074,291  ops/s

JDK 18-ea+26 - desktop 4+4 cores
Benchmark                      Mode  Cnt          Score           Error  Units
RandomDouble.mathRandom       thrpt    3   66626779,637 ±    855351,948  ops/s
RandomDouble.randomObj        thrpt    3   66095950,988 ±  20344036,953  ops/s
RandomDouble.secureRandomObj  thrpt    3     703243,657 ±     25417,227  ops/s
RandomDouble.threadLocal      thrpt    3  188972688,905 ± 386342506,494  ops/s

JDK 19.0.2 - desktop 4+4 cores - balanced cpu
Benchmark                      Mode  Cnt          Score         Error  Units
RandomDouble.mathRandom       thrpt    3   57228990,898 ± 1023769,680  ops/s
RandomDouble.randomObj        thrpt    3   56922994,303 ± 1099680,491  ops/s
RandomDouble.secureRandomObj  thrpt    3     705011,843 ±   25599,071  ops/s
RandomDouble.threadLocal      thrpt    3  341162412,634 ± 3102598,699  ops/s
*/
