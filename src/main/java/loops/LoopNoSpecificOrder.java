package loops;

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
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

// https://mkyong.com/java/jmh-java-forward-loop-vs-reverse-loop/
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class LoopNoSpecificOrder {
	
	@Param({ "1", "10", "100", "10000000" })
	private int N;
	
	private List<String> DATA_FOR_TESTING = createData();
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + LoopNoSpecificOrder.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	@Setup
	public void setup() {
		DATA_FOR_TESTING = createData();
	}
	
	private List<String> createData() {
		final List<String> data = new ArrayList<>();
		for (int i = 0; i < N; ++i) {
			data.add("Number : " + i);
		}
		return data;
	}
	
	@Benchmark
	public void reverseFor(final Blackhole bh) {
		for (int i = DATA_FOR_TESTING.size() - 1; i >= 0; --i) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
		}
	}
	
	@Benchmark
	public void forwardFor(final Blackhole bh) {
		for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
		}
	}
	
	@Benchmark
	public void forwardForAlt(final Blackhole bh) {
		for (int i = 0, len = DATA_FOR_TESTING.size(); i < len; ++i) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
		}
	}
	
	@Benchmark
	public void forwardWhile(final Blackhole bh) {
		int i = 0;
		while (i < DATA_FOR_TESTING.size()) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
			i++;
		}
	}
	
	@Benchmark
	public void forwardWhileAlt(final Blackhole bh) {
		int i = 0;
		final int len = DATA_FOR_TESTING.size();
		while (i < len) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
			++i;
		}
	}
	
	@Benchmark
	public void reverseWhile(final Blackhole bh) {
		int i = DATA_FOR_TESTING.size() - 1;
		while (i >= 0) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
			i--;
		}
	}
	
	@Benchmark
	public void reverseWhileAlt(final Blackhole bh) {
		int i = DATA_FOR_TESTING.size() - 1;
		while (i > -1) {
			final String s = DATA_FOR_TESTING.get(i);
			bh.consume(s);
			--i;
		}
	}
	
	@Benchmark
	public void loopForEach(final Blackhole bh) {
		for (final String s : DATA_FOR_TESTING) {
			bh.consume(s);
		}
	}
	
	@Benchmark
	public void loopIterator(final Blackhole bh) {
		final Iterator<String> iterator = DATA_FOR_TESTING.iterator();
		while (iterator.hasNext()) {
			final String s = iterator.next();
			bh.consume(s);
		}
	}
	
	@Benchmark
	public void collectionForEach(final Blackhole bh) {
		DATA_FOR_TESTING.forEach(s -> bh.consume(bh));
	}
	
	@Benchmark
	public void collectionForEachMethodRef(final Blackhole bh) {
		DATA_FOR_TESTING.forEach(bh::consume);
	}
	
	@Benchmark
	public void collectionStreamForEach(final Blackhole bh) {
		DATA_FOR_TESTING.stream().forEach(s -> bh.consume(s));
	}
	
	@Benchmark
	public void collectionStreamForEachMethodRef(final Blackhole bh) {
		DATA_FOR_TESTING.stream().forEach(bh::consume);
	}
	
	@Benchmark
	public void collectionStreamParallelIndirect(final Blackhole bh) {
		// not in order!
		DATA_FOR_TESTING.stream().parallel().forEach(s -> bh.consume(s));
	}
	
	@Benchmark
	public void collectionStreamParallelIndirectMethodRef(final Blackhole bh) {
		// not in order!
		DATA_FOR_TESTING.stream().parallel().forEach(bh::consume);
	}
	
	@Benchmark
	public void collectionStreamParallelDirect(final Blackhole bh) {
		// not in order!
		DATA_FOR_TESTING.parallelStream().forEach(s -> bh.consume(s));
	}
	
	@Benchmark
	public void collectionStreamParallelDirectMethodRef(final Blackhole bh) {
		// not in order!
		DATA_FOR_TESTING.parallelStream().forEach(bh::consume);
	}
	
}

/*
JDK 16-ea
Benchmark                                                           (N)  Mode  Cnt          Score         Error  Units
LoopNoSpecificOrder.forwardWhileAlt                                   1  avgt    3          4,185 ±       0,082  ns/op
LoopNoSpecificOrder.reverseWhileAlt                                   1  avgt    3          4,406 ±       0,087  ns/op
LoopNoSpecificOrder.reverseFor                                        1  avgt    3          4,412 ±       0,060  ns/op
LoopNoSpecificOrder.reverseWhile                                      1  avgt    3          4,412 ±       0,030  ns/op
LoopNoSpecificOrder.forwardForAlt                                     1  avgt    3          4,535 ±       0,101  ns/op
LoopNoSpecificOrder.forwardWhile                                      1  avgt    3          4,554 ±       0,037  ns/op
LoopNoSpecificOrder.forwardFor                                        1  avgt    3          4,571 ±       0,387  ns/op
LoopNoSpecificOrder.loopForEach                                       1  avgt    3          4,805 ±       0,510  ns/op
LoopNoSpecificOrder.loopIterator                                      1  avgt    3          4,887 ±       0,159  ns/op
LoopNoSpecificOrder.collectionForEach                                 1  avgt    3          5,064 ±       0,294  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                        1  avgt    3          5,567 ±       0,215  ns/op
LoopNoSpecificOrder.collectionStreamForEach                           1  avgt    3         11,940 ±       0,436  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef                  1  avgt    3         12,157 ±       0,147  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect                  1  avgt    3         31,369 ±       0,340  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect                    1  avgt    3         31,410 ±       7,041  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef         1  avgt    3         31,842 ±       0,846  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef           1  avgt    3         31,968 ±       0,273  ns/op
LoopNoSpecificOrder.reverseFor                                       10  avgt    3         35,347 ±       3,480  ns/op
LoopNoSpecificOrder.reverseWhileAlt                                  10  avgt    3         35,444 ±       2,591  ns/op
LoopNoSpecificOrder.reverseWhile                                     10  avgt    3         35,553 ±       1,844  ns/op
LoopNoSpecificOrder.forwardForAlt                                    10  avgt    3         36,370 ±       0,059  ns/op
LoopNoSpecificOrder.forwardWhileAlt                                  10  avgt    3         36,448 ±       0,581  ns/op
LoopNoSpecificOrder.forwardWhile                                     10  avgt    3         37,684 ±       0,579  ns/op
LoopNoSpecificOrder.forwardFor                                       10  avgt    3         37,858 ±       1,318  ns/op
LoopNoSpecificOrder.loopForEach                                      10  avgt    3         38,094 ±       1,483  ns/op
LoopNoSpecificOrder.loopIterator                                     10  avgt    3         38,530 ±       6,117  ns/op
LoopNoSpecificOrder.collectionStreamForEach                          10  avgt    3         38,644 ±       0,604  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef                 10  avgt    3         38,677 ±       0,397  ns/op
LoopNoSpecificOrder.collectionForEach                                10  avgt    3         39,779 ±       0,667  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                       10  avgt    3         40,620 ±       0,598  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect                   10  avgt    3      15977,920 ±    2658,242  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef          10  avgt    3      16280,243 ±     494,502  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect                 10  avgt    3      16464,259 ±    1278,352  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef        10  avgt    3      16485,050 ±     327,396  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef                100  avgt    3        311,575 ±      16,829  ns/op
LoopNoSpecificOrder.collectionStreamForEach                         100  avgt    3        312,161 ±       7,682  ns/op
LoopNoSpecificOrder.reverseFor                                      100  avgt    3        352,007 ±       2,142  ns/op
LoopNoSpecificOrder.reverseWhile                                    100  avgt    3        352,478 ±       6,728  ns/op
LoopNoSpecificOrder.reverseWhileAlt                                 100  avgt    3        356,232 ±     135,238  ns/op
LoopNoSpecificOrder.forwardForAlt                                   100  avgt    3        365,538 ±      36,377  ns/op
LoopNoSpecificOrder.forwardWhileAlt                                 100  avgt    3        366,788 ±      25,378  ns/op
LoopNoSpecificOrder.loopIterator                                    100  avgt    3        377,933 ±      11,732  ns/op
LoopNoSpecificOrder.loopForEach                                     100  avgt    3        378,146 ±       9,825  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                      100  avgt    3        399,564 ±       4,714  ns/op
LoopNoSpecificOrder.collectionForEach                               100  avgt    3        401,223 ±       1,970  ns/op
LoopNoSpecificOrder.forwardFor                                      100  avgt    3        401,979 ±      12,245  ns/op
LoopNoSpecificOrder.forwardWhile                                    100  avgt    3        402,897 ±      23,894  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect                  100  avgt    3      25895,179 ±    2294,616  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef         100  avgt    3      26312,096 ±     682,350  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect                100  avgt    3      26430,941 ±    1637,360  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef       100  avgt    3      26504,734 ±    1344,912  ns/op
LoopNoSpecificOrder.reverseWhileAlt                            10000000  avgt    3   50811785,556 ± 2068188,816  ns/op
LoopNoSpecificOrder.reverseWhile                               10000000  avgt    3   50833722,225 ± 1654666,462  ns/op
LoopNoSpecificOrder.reverseFor                                 10000000  avgt    3   50857318,311 ± 1124578,512  ns/op
LoopNoSpecificOrder.forwardForAlt                              10000000  avgt    3   52236841,493 ± 1657947,922  ns/op
LoopNoSpecificOrder.forwardWhileAlt                            10000000  avgt    3   52464262,486 ± 1542932,652  ns/op
LoopNoSpecificOrder.loopIterator                               10000000  avgt    3   53972854,480 ±  494892,477  ns/op
LoopNoSpecificOrder.loopForEach                                10000000  avgt    3   53996990,860 ± 2063649,080  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef           10000000  avgt    3   54496452,717 ±  688540,991  ns/op
LoopNoSpecificOrder.collectionStreamForEach                    10000000  avgt    3   54551796,739 ±  830282,627  ns/op
LoopNoSpecificOrder.forwardWhile                               10000000  avgt    3   55134116,300 ±  402061,945  ns/op
LoopNoSpecificOrder.forwardFor                                 10000000  avgt    3   55385944,015 ± 1759556,097  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                 10000000  avgt    3   60661561,564 ± 1046867,557  ns/op
LoopNoSpecificOrder.collectionForEach                          10000000  avgt    3   60712849,898 ± 1128186,723  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef  10000000  avgt    3  131716748,246 ± 1905555,241  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef    10000000  avgt    3  138204793,151 ± 1280369,513  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect             10000000  avgt    3  139293820,028 ± 8171583,508  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect           10000000  avgt    3  151948654,545 ± 8486858,414  ns/op
*/
