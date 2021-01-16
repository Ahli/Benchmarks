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
public class RandomInt {
	private final Random random = new Random();
	private final SecureRandom secureRandom = new SecureRandom();
	
	@Benchmark
	public void measureRandom() {
		random.nextInt();
	}
	
	@Benchmark
	public void measureSecureRandom() {
		secureRandom.nextInt();
	}
	
	@Benchmark
	public void measureThreadLocal() {
		ThreadLocalRandom.current().nextInt();
	}
}

/*
JDK 16-ea
Benchmark                      Mode  Cnt    Score    Error  Units
RandomInt.measureRandom        avgt    6    8,449 ±  0,039  ns/op
RandomInt.measureSecureRandom  avgt    6  798,021 ± 12,846  ns/op
RandomInt.measureThreadLocal   avgt    6    1,204 ±  0,019  ns/op <<<
*/
