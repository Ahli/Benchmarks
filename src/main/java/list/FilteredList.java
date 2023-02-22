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
JDK 17 - laptop 6+6 cores
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

JDK 18-ea+26 - desktop 4+4 cores
Benchmark                                            (N)   Mode  Cnt         Score          Error  Units
FilteredList.index                                     1  thrpt    3  91640238,270 ± 57384033,446  ops/s
FilteredList.index                                    10  thrpt    3  33816426,355 ±  7226062,706  ops/s
FilteredList.index                                   100  thrpt    3   2998374,026 ±   545464,181  ops/s
FilteredList.index                              10000000  thrpt    3        16,698 ±        1,955  ops/s
FilteredList.iterator                                  1  thrpt    3  87837358,469 ± 82884535,670  ops/s
FilteredList.iterator                                 10  thrpt    3  27935108,245 ± 61737185,672  ops/s
FilteredList.iterator                                100  thrpt    3   2937155,924 ±    63249,738  ops/s
FilteredList.iterator                           10000000  thrpt    3        16,867 ±        3,131  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3  28994500,245 ± 33927733,118  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3  14900972,960 ±  9945051,531  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3   2088215,783 ±    67385,443  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3        17,526 ±        1,253  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3  20128155,977 ± 10520006,413  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3  12172559,117 ±  6598067,872  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3   1841028,922 ±    42903,441  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3        15,625 ±        0,790  ops/s
FilteredList.streamToList                              1  thrpt    3  18764041,319 ± 11920137,225  ops/s
FilteredList.streamToList                             10  thrpt    3  13061681,080 ±   855521,872  ops/s
FilteredList.streamToList                            100  thrpt    3   1973051,781 ±   632589,957  ops/s
FilteredList.streamToList                       10000000  thrpt    3        16,449 ±        8,938  ops/s

JDK 19.0.2 - desktop 4+4 cores - balanced cpu
Benchmark                                            (N)   Mode  Cnt          Score          Error  Units
FilteredList.index                                     1  thrpt    3   87122074,856 ± 74005948,930  ops/s
FilteredList.index                                    10  thrpt    3   26938544,008 ±  2528900,145  ops/s
FilteredList.index                                   100  thrpt    3    2457668,495 ±   143885,465  ops/s
FilteredList.index                              10000000  thrpt    3         15,866 ±        2,742  ops/s
FilteredList.iterator                                  1  thrpt    3  100577259,174 ±  2467381,355  ops/s
FilteredList.iterator                                 10  thrpt    3   28611599,549 ±   153430,268  ops/s
FilteredList.iterator                                100  thrpt    3    2627545,027 ±   175754,713  ops/s
FilteredList.iterator                           10000000  thrpt    3         15,704 ±        1,549  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3   28588763,629 ±  1046750,848  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3   16337562,218 ±   184250,511  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3    1681277,844 ±   106860,103  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3         14,869 ±        0,897  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3   18919025,430 ±  1282101,557  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3   11029412,038 ±   379997,224  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3    1546224,311 ±    24143,591  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3         13,381 ±        2,197  ops/s
FilteredList.streamToList                              1  thrpt    3   20422821,728 ±   846936,585  ops/s
FilteredList.streamToList                             10  thrpt    3   13279744,675 ±   598410,635  ops/s
FilteredList.streamToList                            100  thrpt    3    1909367,810 ±   128577,685  ops/s
FilteredList.streamToList                       10000000  thrpt    3         17,876 ±        3,939  ops/s

Process finished with exit code 0

*/
