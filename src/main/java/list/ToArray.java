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

JDK-24 - Desktop 9800X3D highPerformance
Benchmark                (N)   Mode  Cnt          Score         Error  Units
ToArray.emptyArray         1  thrpt   10   77710591,618 ±  315706,407  ops/s
ToArray.emptyArray        10  thrpt   10   68713639,221 ±  316266,715  ops/s
ToArray.emptyArray       100  thrpt   10   23192731,344 ±   91321,891  ops/s
ToArray.emptyArray  10000000  thrpt   10        102,634 ±      14,950  ops/s
ToArray.index              1  thrpt   10  343040315,318 ± 2721007,401  ops/s
ToArray.index             10  thrpt   10  119064443,165 ±  357559,019  ops/s
ToArray.index            100  thrpt   10   12675345,517 ±   27766,855  ops/s
ToArray.index       10000000  thrpt   10         23,681 ±       2,307  ops/s
ToArray.methodRef          1  thrpt   10   78447984,947 ±  169963,418  ops/s
ToArray.methodRef         10  thrpt   10   69736021,695 ±  239855,132  ops/s
ToArray.methodRef        100  thrpt   10   23143470,787 ±  522460,683  ops/s
ToArray.methodRef   10000000  thrpt   10        106,194 ±      15,383  ops/s
ToArray.sizedArray         1  thrpt   10   81741923,644 ±  592714,034  ops/s
ToArray.sizedArray        10  thrpt   10   70101953,638 ±  801901,269  ops/s
ToArray.sizedArray       100  thrpt   10   21921989,142 ±  157823,635  ops/s
ToArray.sizedArray  10000000  thrpt   10        103,728 ±      14,287  ops/s

JDK-25 - Desktop 9800X3D balanced
Benchmark                (N)   Mode  Cnt          Score         Error  Units
ToArray.emptyArray         1  thrpt   10   80812224,500 ±  245381,199  ops/s
ToArray.emptyArray        10  thrpt   10   71381700,030 ±  252666,802  ops/s
ToArray.emptyArray       100  thrpt   10   23816031,484 ±  120748,854  ops/s
ToArray.emptyArray  10000000  thrpt   10        112,157 ±      13,149  ops/s
ToArray.index              1  thrpt   10  411576049,146 ± 1825943,772  ops/s
ToArray.index             10  thrpt   10  121557928,765 ±  818622,785  ops/s
ToArray.index            100  thrpt   10   15831432,164 ±   24332,477  ops/s
ToArray.index       10000000  thrpt   10         24,238 ±       3,000  ops/s
ToArray.methodRef          1  thrpt   10   80390123,687 ±  196477,601  ops/s
ToArray.methodRef         10  thrpt   10   71161463,206 ±  195179,481  ops/s
ToArray.methodRef        100  thrpt   10   23698495,639 ±  129111,890  ops/s
ToArray.methodRef   10000000  thrpt   10        105,357 ±      13,016  ops/s
ToArray.sizedArray         1  thrpt   10   83302020,225 ± 1485330,137  ops/s
ToArray.sizedArray        10  thrpt   10   72294430,185 ±  292959,003  ops/s
ToArray.sizedArray       100  thrpt   10   24020920,264 ±   97230,926  ops/s
ToArray.sizedArray  10000000  thrpt   10        110,593 ±      15,202  ops/s

JDK-21.0.8 - Desktop 9800X3D balanced
Benchmark                (N)   Mode  Cnt          Score         Error  Units
ToArray.emptyArray         1  thrpt   10   70458758,310 ±  866770,872  ops/s
ToArray.emptyArray        10  thrpt   10   58750978,674 ±  145179,049  ops/s
ToArray.emptyArray       100  thrpt   10   22681412,040 ±   84202,631  ops/s
ToArray.emptyArray  10000000  thrpt   10        100,354 ±      15,321  ops/s
ToArray.index              1  thrpt   10  377277332,554 ± 2193906,003  ops/s
ToArray.index             10  thrpt   10   89426265,335 ±  339462,342  ops/s
ToArray.index            100  thrpt   10   10433823,778 ±   32380,546  ops/s
ToArray.index       10000000  thrpt   10         23,353 ±       3,400  ops/s
ToArray.methodRef          1  thrpt   10   69330243,718 ±  417653,691  ops/s
ToArray.methodRef         10  thrpt   10   63571092,237 ±  665753,442  ops/s
ToArray.methodRef        100  thrpt   10   22672771,355 ±   80856,463  ops/s
ToArray.methodRef   10000000  thrpt   10         98,836 ±      15,922  ops/s
ToArray.sizedArray         1  thrpt   10   73070625,728 ±  136176,668  ops/s
ToArray.sizedArray        10  thrpt   10   63697282,465 ±  262918,012  ops/s
ToArray.sizedArray       100  thrpt   10   22419400,059 ±  129967,035  ops/s
ToArray.sizedArray  10000000  thrpt   10        101,057 ±      14,584  ops/s

https://shipilev.net/blog/2016/arrays-wisdom-ancients/
=> use zero array instead of sized array
but methodRef seems great as well
*/
