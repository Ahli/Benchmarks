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
	
	@Benchmark
	public void measureRandom() {
		random.nextDouble();
	}
	
	@Benchmark
	public void measureSecureRandom() {
		secureRandom.nextDouble();
	}
	
	@Benchmark
	public void measureThreadLocal() {
		ThreadLocalRandom.current().nextDouble();
	}
}

/*
JDK 16-ea
Benchmark                         Mode  Cnt     Score   Error  Units
RandomDouble.measureRandom        avgt    6    16,959 ± 0,236  ns/op
RandomDouble.measureSecureRandom  avgt    6  1607,737 ± 6,814  ns/op
RandomDouble.measureThreadLocal   avgt    6     1,205 ± 0,012  ns/op <<<
*/
