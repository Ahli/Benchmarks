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
JDK 17 - laptop 12 cores
Benchmark                                            (N)   Mode  Cnt         Score          Error  Units
FilteredList.index                                     1  thrpt    3  80553379,694 ± 15341868,672  ops/s
FilteredList.index                                    10  thrpt    3  29105678,303 ± 13632789,764  ops/s
FilteredList.index                                   100  thrpt    3   2645946,599 ±   346825,113  ops/s
FilteredList.index                              10000000  thrpt    3        14,069 ±        1,110  ops/s
FilteredList.iterator                                  1  thrpt    3  84373621,042 ±   648472,772  ops/s
FilteredList.iterator                                 10  thrpt    3  28233176,916 ±  1341368,615  ops/s
FilteredList.iterator                                100  thrpt    3   2778516,076 ±   233962,824  ops/s
FilteredList.iterator                           10000000  thrpt    3        14,435 ±        0,971  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3  24011730,659 ±  1610835,650  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3  16276396,945 ±  2883297,273  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3   1976124,363 ±   174458,448  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3        14,364 ±        2,110  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3  16564206,697 ±  3528700,870  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3  10264099,472 ±   408373,552  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3   2404510,546 ±   135183,374  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3        12,856 ±        0,661  ops/s
FilteredList.streamToList                              1  thrpt    3  16816965,581 ±  1894188,415  ops/s
FilteredList.streamToList                             10  thrpt    3  12418396,969 ±   938229,134  ops/s
FilteredList.streamToList                            100  thrpt    3   2081558,570 ±   269245,968  ops/s
FilteredList.streamToList                       10000000  thrpt    3        17,054 ±        1,261  ops/s
*/
