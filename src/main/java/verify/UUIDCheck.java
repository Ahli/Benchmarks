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

@Warmup(iterations = 3)
@Measurement(iterations = 3)
@Fork(value = 2, jvmArgs = { "-Xms2G", "-Xmx2G" })
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Threads(1)
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
		final List<String> data = new ArrayList<>();
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
JDK-16.0.1
Benchmark               (N)  Mode  Cnt       Score       Error  Units
UUIDCheck.parse        1000  avgt    3   46312,854 ± 10897,757  ns/op
UUIDCheck.singleRegex  1000  avgt    3  434284,029 ± 50138,973  ns/op
*/