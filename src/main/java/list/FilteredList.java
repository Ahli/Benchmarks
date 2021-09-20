package list;

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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class FilteredList {
	
	@Param({ "1", "10", "100", "10000000" })
	private int N;
	private List<Integer> DATA_FOR_TESTING;
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + FilteredList.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	@Setup
	public void setup() {
		DATA_FOR_TESTING = createData();
	}
	
	private List<Integer> createData() {
		final List<Integer> data = new ArrayList<>(N);
		for (int i = 0; i < N; ++i) {
			data.add(i);
		}
		return data;
	}
	
	@Benchmark
	public void streamCollectorToList(final Blackhole bh) {
		final List<Integer> filteredList =
				DATA_FOR_TESTING.stream().filter(v -> v % 2 == 0).collect(Collectors.toList());
		bh.consume(filteredList);
	}
	
	@Benchmark
	public void streamCollectorToUnmodifiableList(final Blackhole bh) {
		final List<Integer> filteredList =
				DATA_FOR_TESTING.stream().filter(v -> v % 2 == 0).collect(Collectors.toUnmodifiableList());
		bh.consume(filteredList);
	}
	
	@Benchmark
	public void streamToList(final Blackhole bh) {
		final List<Integer> filteredList = DATA_FOR_TESTING.stream().filter(v -> v % 2 == 0).toList();
		bh.consume(filteredList);
	}
	
	@Benchmark
	public void iterator(final Blackhole bh) {
		final List<Integer> filteredList = new ArrayList<>();
		for (final Integer v : DATA_FOR_TESTING) {
			if (v % 2 == 0) {
				filteredList.add(v);
			}
		}
		bh.consume(filteredList);
	}
	
	@Benchmark
	public void index(final Blackhole bh) {
		final List<Integer> filteredList = new ArrayList<>();
		for (int i = 0, len = DATA_FOR_TESTING.size(); i < len; ++i) {
			if (DATA_FOR_TESTING.get(i) % 2 == 0) {
				filteredList.add(DATA_FOR_TESTING.get(i));
			}
		}
		bh.consume(filteredList);
	}
}
/*
JDK 16.0.2

Benchmark                                            (N)   Mode  Cnt         Score          Error  Units
FilteredList.index                                     1  thrpt    3  84972067,138 ±  2390782,689  ops/s
FilteredList.index                                    10  thrpt    3  31792614,589 ±   214648,132  ops/s
FilteredList.index                                   100  thrpt    3   2250821,957 ±   156558,782  ops/s
FilteredList.index                              10000000  thrpt    3        14,247 ±        1,209  ops/s
FilteredList.iterator                                  1  thrpt    3  78532506,120 ± 10412484,490  ops/s
FilteredList.iterator                                 10  thrpt    3  26083306,622 ±   264546,338  ops/s
FilteredList.iterator                                100  thrpt    3   2699798,295 ±   444017,545  ops/s
FilteredList.iterator                           10000000  thrpt    3        14,326 ±        0,397  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3  24478225,365 ±   321620,428  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3  14208232,407 ±  1211935,209  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3   1517891,826 ±    67832,764  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3        14,434 ±        1,497  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3  16350763,865 ± 20200024,822  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3   8526937,973 ±  3982036,385  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3   1370671,104 ±   251893,279  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3        11,825 ±        2,883  ops/s
FilteredList.streamToList                              1  thrpt    3  17577262,066 ±   216806,591  ops/s
FilteredList.streamToList                             10  thrpt    3  11476887,708 ±  1748699,380  ops/s
FilteredList.streamToList                            100  thrpt    3   1782501,089 ±   198397,413  ops/s
FilteredList.streamToList                       10000000  thrpt    3        16,821 ±        2,504  ops/s
*/
