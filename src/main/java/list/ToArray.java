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
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
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
JDK 17 - laptop 12 cores
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

https://shipilev.net/blog/2016/arrays-wisdom-ancients/
=> use zero array instead of sized array
*/
