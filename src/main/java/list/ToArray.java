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

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class ToArray {
	
	@Param({ "1", "10", "100", "10000000" })
	private int N;
	private List<Integer> DATA_FOR_TESTING;
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder().include(".*" + ToArray.class.getSimpleName() + ".*").forks(1).build();
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
	public void emptyArray(final Blackhole bh) {
		final Integer[] array = DATA_FOR_TESTING.toArray(new Integer[0]);
		bh.consume(array);
	}
	
	@Benchmark
	public void sizedArray(final Blackhole bh) {
		final Integer[] array = DATA_FOR_TESTING.toArray(new Integer[DATA_FOR_TESTING.size()]);
		bh.consume(array);
	}
	
	@Benchmark
	public void methodRef(final Blackhole bh) {
		final Integer[] array = DATA_FOR_TESTING.toArray(Integer[]::new);
		bh.consume(array);
	}
	
	@Benchmark
	public void index(final Blackhole bh) {
		final int len = DATA_FOR_TESTING.size();
		final Integer[] array = new Integer[len];
		for (int i = 0; i < len; ++i) {
			array[i] = DATA_FOR_TESTING.get(i);
		}
		bh.consume(array);
	}
}
/*
JDK 17 - laptop 6+6 cores
Benchmark                (N)   Mode  Cnt          Score          Error  Units
ToArray.emptyArray         1  thrpt    3   45171359,704 ±  3136610,804  ops/s
ToArray.emptyArray        10  thrpt    3   36219163,913 ±  2094975,023  ops/s
ToArray.emptyArray       100  thrpt    3   12832873,797 ±   620097,793  ops/s
ToArray.emptyArray  10000000  thrpt    3         40,550 ±        5,673  ops/s
ToArray.index              1  thrpt    3  115917232,831 ± 13391307,188  ops/s
ToArray.index             10  thrpt    3   34110063,625 ±  1701185,573  ops/s
ToArray.index            100  thrpt    3    3904734,260 ±   188596,897  ops/s
ToArray.index       10000000  thrpt    3         13,252 ±        2,709  ops/s
ToArray.sizedArray         1  thrpt    3   47141587,950 ±  2785563,991  ops/s
ToArray.sizedArray        10  thrpt    3   36519212,175 ±  4637641,192  ops/s
ToArray.sizedArray       100  thrpt    3   12585969,675 ±   546365,287  ops/s
ToArray.sizedArray  10000000  thrpt    3         40,702 ±        7,521  ops/s

JDK 17.0.1 - desktop 4+4 cores
Benchmark                (N)   Mode  Cnt          Score        Error  Units
ToArray.emptyArray         1  thrpt   10   49795303,644 ± 429157,156  ops/s
ToArray.emptyArray        10  thrpt   10   40409156,884 ± 196476,378  ops/s
ToArray.emptyArray       100  thrpt   10   14194002,960 ±  69904,385  ops/s
ToArray.emptyArray  10000000  thrpt   10         53,597 ±      1,612  ops/s
ToArray.index              1  thrpt   10  131221810,439 ± 652872,122  ops/s
ToArray.index             10  thrpt   10   37369697,750 ± 262529,236  ops/s
ToArray.index            100  thrpt   10    4274385,774 ±  14648,788  ops/s
ToArray.index       10000000  thrpt   10         14,981 ±      0,310  ops/s
ToArray.methodRef          1  thrpt   10   49842526,161 ± 342413,535  ops/s
ToArray.methodRef         10  thrpt   10   40495974,410 ± 250730,768  ops/s
ToArray.methodRef        100  thrpt   10   14159500,854 ±  93439,917  ops/s
ToArray.methodRef   10000000  thrpt   10         53,426 ±      2,404  ops/s
ToArray.sizedArray         1  thrpt   10   51828857,433 ± 453681,215  ops/s
ToArray.sizedArray        10  thrpt   10   40727396,095 ± 224975,697  ops/s
ToArray.sizedArray       100  thrpt   10   13593907,058 ± 107273,725  ops/s
ToArray.sizedArray  10000000  thrpt   10         51,937 ±      3,987  ops/s

JDK 18-ea+26 - desktop 4+4 cores
Benchmark                (N)   Mode  Cnt          Score         Error  Units
ToArray.emptyArray         1  thrpt   10   50459842,678 ± 1328546,707  ops/s
ToArray.emptyArray        10  thrpt   10   40493820,667 ±  821448,010  ops/s
ToArray.emptyArray       100  thrpt   10   13952005,259 ±  108035,933  ops/s
ToArray.emptyArray  10000000  thrpt   10         51,378 ±       1,202  ops/s
ToArray.index              1  thrpt   10  126149846,699 ± 9077548,589  ops/s
ToArray.index             10  thrpt   10   36225321,351 ± 3564847,201  ops/s
ToArray.index            100  thrpt   10    4304031,161 ±  254725,994  ops/s
ToArray.index       10000000  thrpt   10         15,385 ±       0,262  ops/s
ToArray.methodRef          1  thrpt   10   50836204,036 ± 1346486,053  ops/s
ToArray.methodRef         10  thrpt   10   40932973,395 ±  627791,881  ops/s
ToArray.methodRef        100  thrpt   10   14053344,868 ±  159942,889  ops/s
ToArray.methodRef   10000000  thrpt   10         51,143 ±       2,351  ops/s
ToArray.sizedArray         1  thrpt   10   50558747,023 ± 1347605,524  ops/s
ToArray.sizedArray        10  thrpt   10   39243103,991 ± 1063376,700  ops/s
ToArray.sizedArray       100  thrpt   10   13578336,688 ±   49571,271  ops/s
ToArray.sizedArray  10000000  thrpt   10         50,971 ±       2,989  ops/s

JDK 19.0.2 - desktop 4+4 cores - balanced cpu
Benchmark                (N)   Mode  Cnt          Score         Error  Units
ToArray.emptyArray         1  thrpt   10   56368834,253 ± 1184817,341  ops/s
ToArray.emptyArray        10  thrpt   10   44722178,342 ±  404111,940  ops/s
ToArray.emptyArray       100  thrpt   10   14559105,918 ±  176461,125  ops/s
ToArray.emptyArray  10000000  thrpt   10         57,384 ±       4,279  ops/s
ToArray.index              1  thrpt   10  178206033,304 ± 2302538,064  ops/s
ToArray.index             10  thrpt   10   37393705,063 ±  401423,378  ops/s
ToArray.index            100  thrpt   10    4749315,685 ±   75663,181  ops/s
ToArray.index       10000000  thrpt   10         15,430 ±       0,715  ops/s
ToArray.methodRef          1  thrpt   10   57541533,183 ±  577948,748  ops/s
ToArray.methodRef         10  thrpt   10   45134819,638 ±  572269,036  ops/s
ToArray.methodRef        100  thrpt   10   14342750,781 ±  368019,038  ops/s
ToArray.methodRef   10000000  thrpt   10         56,005 ±       4,453  ops/s
ToArray.sizedArray         1  thrpt   10   61368263,603 ±  822795,257  ops/s
ToArray.sizedArray        10  thrpt   10   45590801,750 ±  567997,061  ops/s
ToArray.sizedArray       100  thrpt   10   14328995,126 ±  118001,158  ops/s
ToArray.sizedArray  10000000  thrpt   10         58,646 ±       3,486  ops/s


https://shipilev.net/blog/2016/arrays-wisdom-ancients/
=> use zero array instead of sized array
*/
