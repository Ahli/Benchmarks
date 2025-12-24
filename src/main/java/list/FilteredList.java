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

JDK-24 - Desktop 9800X3D highPerformance
Benchmark                                            (N)   Mode  Cnt          Score         Error  Units
FilteredList.index                                     1  thrpt    3  229747938,472 ± 8883092,486  ops/s
FilteredList.index                                    10  thrpt    3   73910678,784 ± 2933252,812  ops/s
FilteredList.index                                   100  thrpt    3    6658519,801 ±  367847,245  ops/s
FilteredList.index                              10000000  thrpt    3         31,651 ±       8,065  ops/s
FilteredList.iterator                                  1  thrpt    3  190737242,979 ± 6355297,423  ops/s
FilteredList.iterator                                 10  thrpt    3   57278213,622 ± 4020009,507  ops/s
FilteredList.iterator                                100  thrpt    3    6789313,588 ±   35789,492  ops/s
FilteredList.iterator                           10000000  thrpt    3         30,338 ±       2,094  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3   70872554,811 ± 1161881,210  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3   34873042,911 ± 7896343,233  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3    4400403,993 ±  242099,709  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3         31,547 ±      17,942  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3   43965881,638 ± 1988624,028  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3   32034182,737 ± 4488363,153  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3    4186433,435 ±   99729,718  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3         28,790 ±       1,249  ops/s
FilteredList.streamToList                              1  thrpt    3   39446643,748 ± 2280388,734  ops/s
FilteredList.streamToList                             10  thrpt    3   34416972,082 ±  188797,407  ops/s
FilteredList.streamToList                            100  thrpt    3    4956716,635 ± 1045895,473  ops/s
FilteredList.streamToList                       10000000  thrpt    3         35,798 ±       4,763  ops/s

JDK-25 - Desktop 9800X3D balanced
Benchmark                                            (N)   Mode  Cnt          Score         Error  Units
FilteredList.index                                     1  thrpt    3  238189970,724 ± 4937768,911  ops/s
FilteredList.index                                    10  thrpt    3   77650386,073 ± 2483900,302  ops/s
FilteredList.index                                   100  thrpt    3    7219804,435 ±   96586,408  ops/s
FilteredList.index                              10000000  thrpt    3         34,854 ±       1,491  ops/s
FilteredList.iterator                                  1  thrpt    3  226902368,717 ± 2124451,653  ops/s
FilteredList.iterator                                 10  thrpt    3   60740127,105 ± 1765368,109  ops/s
FilteredList.iterator                                100  thrpt    3    7148187,435 ±   78598,160  ops/s
FilteredList.iterator                           10000000  thrpt    3         33,356 ±       3,221  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3   75484275,623 ±  699180,943  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3   49032753,423 ± 1297570,166  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3    4621379,302 ±   61454,896  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3         34,437 ±       0,373  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3   51650793,822 ± 1198135,470  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3   33251055,440 ±  846711,151  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3    4328616,123 ±   44547,972  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3         29,325 ±       2,963  ops/s
FilteredList.streamToList                              1  thrpt    3   41698923,255 ± 1898104,830  ops/s
FilteredList.streamToList                             10  thrpt    3   36253405,998 ± 1849098,272  ops/s
FilteredList.streamToList                            100  thrpt    3    4976244,021 ±  201634,684  ops/s
FilteredList.streamToList                       10000000  thrpt    3         37,917 ±       3,877  ops/s

JDK-21.0.8 - Desktop 9800X3D balanced
Benchmark                                            (N)   Mode  Cnt          Score           Error  Units
FilteredList.index                                     1  thrpt    3  243803174,452 ±   7039588,628  ops/s
FilteredList.index                                    10  thrpt    3   68505319,251 ± 282531513,992  ops/s
FilteredList.index                                   100  thrpt    3    6787769,086 ±     74065,722  ops/s
FilteredList.index                              10000000  thrpt    3         32,269 ±        17,425  ops/s
FilteredList.iterator                                  1  thrpt    3  231644866,345 ±  14365443,708  ops/s
FilteredList.iterator                                 10  thrpt    3   76618273,454 ±   3904413,943  ops/s
FilteredList.iterator                                100  thrpt    3    6741877,076 ±    209205,692  ops/s
FilteredList.iterator                           10000000  thrpt    3         34,635 ±         2,157  ops/s
FilteredList.streamCollectorToList                     1  thrpt    3   64691957,889 ±   1908228,544  ops/s
FilteredList.streamCollectorToList                    10  thrpt    3   43813705,186 ±   1171672,809  ops/s
FilteredList.streamCollectorToList                   100  thrpt    3    4274552,795 ±     67159,695  ops/s
FilteredList.streamCollectorToList              10000000  thrpt    3         30,130 ±         3,772  ops/s
FilteredList.streamCollectorToUnmodifiableList         1  thrpt    3   43044504,291 ±   7027564,605  ops/s
FilteredList.streamCollectorToUnmodifiableList        10  thrpt    3   32973340,733 ±    644492,760  ops/s
FilteredList.streamCollectorToUnmodifiableList       100  thrpt    3    3383693,722 ±     17821,909  ops/s
FilteredList.streamCollectorToUnmodifiableList  10000000  thrpt    3         31,178 ±         5,934  ops/s
FilteredList.streamToList                              1  thrpt    3   37459081,609 ±    110622,694  ops/s
FilteredList.streamToList                             10  thrpt    3   30782810,841 ±    383341,420  ops/s
FilteredList.streamToList                            100  thrpt    3    4031407,451 ±     72408,812  ops/s
FilteredList.streamToList                       10000000  thrpt    3         35,304 ±         0,729  ops/s
*/
