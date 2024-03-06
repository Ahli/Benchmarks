package hashmap;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class VersusArrayListForStringKey {
	private static final int MAX = 1024;
	private final List<String> listKey = new ArrayList<>(MAX);
	private final List<String> listValue = new ArrayList<>(MAX);
	private final Map<String, String> map = HashMap.newHashMap(MAX);
	@Param({"1", "2", "3", "4", "5", "10"})
	private int N;
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + VersusArrayListForStringKey.class.getSimpleName() + ".*")
						.forks(1)
						.build();
		new Runner(opt).run();
	}
	
	@Setup
	public void setup() {
		for (int i = 0; i < MAX; ++i) {
			String key = Integer.toString(i);
			String val = UUID.randomUUID().toString();
			listKey.add(key);
			listValue.add(val);
			map.put(key, val);
		}
	}
	
	@Benchmark
	public void arrayListSearch(final Blackhole bh) {
		for (int i = 0; i < N; ++i) {
			String key = Integer.toString(i);
			bh.consume(listSearch(key));
			bh.consume(i);
		}
	}
	
	private String listSearch(String key) {
		for (int i = 0, n = listKey.size(); i < n; ++i) {
			if (key.equals(listKey.get(i))) {
				return listValue.get(i);
			}
		}
		return null;
	}
	
	@Benchmark
	public void mapAccess(final Blackhole bh) {
		for (int i = 0; i < N; ++i) {
			String key = Integer.toString(i);
			bh.consume(map.get(key));
			bh.consume(i);
		}
	}
	
}
/*
JDK 21.0.2 - desktop 4+4 cores - balanced cpu
Benchmark                                    (N)   Mode  Cnt          Score          Error  Units
VersusArrayListForStringKey.arrayListSearch    1  thrpt    3  124319301,966 ± 20728062,194  ops/s
VersusArrayListForStringKey.arrayListSearch    2  thrpt    3   50836959,014 ±  1908534,427  ops/s
VersusArrayListForStringKey.arrayListSearch    3  thrpt    3   30467349,644 ± 19462181,370  ops/s
VersusArrayListForStringKey.arrayListSearch    4  thrpt    3   21374990,198 ±  4290953,802  ops/s
VersusArrayListForStringKey.arrayListSearch   10  thrpt    3    4932567,654 ±    24634,520  ops/s
VersusArrayListForStringKey.mapAccess          1  thrpt    3   90021118,110 ± 12214685,386  ops/s
VersusArrayListForStringKey.mapAccess          2  thrpt    3   46552723,181 ±  1475399,371  ops/s
VersusArrayListForStringKey.mapAccess          3  thrpt    3   30228430,246 ± 32259640,094  ops/s
VersusArrayListForStringKey.mapAccess          4  thrpt    3   23568836,969 ±  1525839,833  ops/s => map better for 3+ items
VersusArrayListForStringKey.mapAccess         10  thrpt    3    9413785,232 ±   968732,476  ops/s

*/
