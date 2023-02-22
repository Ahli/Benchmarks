package verify;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class UUIDCheck {
	
	private static final Pattern singleRegexPattern = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");
	@Param({ "1000" })
	private int N;
	private List<String> DATA_FOR_TESTING = createData();
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + UUIDCheck.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	@Setup
	public void setup() {
		DATA_FOR_TESTING = createData();
	}
	
	private List<String> createData() {
		final List<String> data = new ArrayList<>(N);
		for (int i = 0; i < N; ++i) {
			data.add(UUID.randomUUID().toString());
		}
		return data;
	}
	
	@Benchmark
	public void parse(final Blackhole bh) {
		for (int i = DATA_FOR_TESTING.size() - 1; i >= 0; --i) {
			final String s = DATA_FOR_TESTING.get(i);
			boolean result;
			try {
				UUID.fromString(s);
				result = true;
			} catch (final IllegalArgumentException ignored) {
				result = false;
			}
			bh.consume(s);
			bh.consume(result);
		}
	}
	
	@Benchmark
	public void singleRegex(final Blackhole bh) {
		for (int i = DATA_FOR_TESTING.size() - 1; i >= 0; --i) {
			final String s = DATA_FOR_TESTING.get(i);
			final boolean result = singleRegexPattern.matcher(s).matches();
			
			bh.consume(s);
			bh.consume(result);
		}
	}
	
}
/*
JDK-17 - laptop 6+6 cores
Benchmark               (N)   Mode  Cnt      Score      Error  Units
UUIDCheck.parse        1000  thrpt    3  21386,249 ± 1029,487  ops/s
UUIDCheck.singleRegex  1000  thrpt    3   2477,466 ±   91,031  ops/s

JDK-17 - desktop 4+4 cores
Benchmark               (N)   Mode  Cnt      Score     Error  Units
UUIDCheck.parse        1000  thrpt    3  28152,459 ± 841,852  ops/s
UUIDCheck.singleRegex  1000  thrpt    3   2725,334 ±  13,362  ops/s

JDK 18-ea+26 - desktop 4+4 cores
Benchmark               (N)   Mode  Cnt      Score      Error  Units
UUIDCheck.parse        1000  thrpt    3  31115,779 ± 1285,629  ops/s
UUIDCheck.singleRegex  1000  thrpt    3   2913,940 ± 3501,117  ops/s

JDK 19.0.2 - desktop 4+4 cores - balanced cpu
Benchmark               (N)   Mode  Cnt      Score      Error  Units
UUIDCheck.parse        1000  thrpt    3  33804,665 ±  656,818  ops/s
UUIDCheck.singleRegex  1000  thrpt    3   2925,839 ± 3127,967  ops/s

*/
