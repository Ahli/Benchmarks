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
JDK 16
Benchmark                                                           (N)  Mode  Cnt         Score          Error  Units
LoopNoSpecificOrder.forwardWhileAlt                                   1  avgt    3         3,701 ±        0,328  ns/op
LoopNoSpecificOrder.forwardForAlt                                     1  avgt    3         3,771 ±        1,727  ns/op
LoopNoSpecificOrder.reverseFor                                        1  avgt    3         3,907 ±        0,413  ns/op
LoopNoSpecificOrder.reverseWhileAlt                                   1  avgt    3         3,910 ±        0,140  ns/op
LoopNoSpecificOrder.reverseWhile                                      1  avgt    3         3,905 ±        0,445  ns/op
LoopNoSpecificOrder.forwardFor                                        1  avgt    3         4,001 ±        0,289  ns/op
LoopNoSpecificOrder.forwardWhile                                      1  avgt    3         4,015 ±        0,268  ns/op
LoopNoSpecificOrder.loopForEach                                       1  avgt    3         4,228 ±        0,259  ns/op
LoopNoSpecificOrder.loopIterator                                      1  avgt    3         4,236 ±        0,293  ns/op
LoopNoSpecificOrder.collectionForEach                                 1  avgt    3         4,434 ±        0,144  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                        1  avgt    3         4,485 ±        0,109  ns/op
LoopNoSpecificOrder.collectionStreamForEach                           1  avgt    3        10,551 ±        0,802  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef                  1  avgt    3        10,685 ±        0,278  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect                    1  avgt    3        27,483 ±        0,569  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect                  1  avgt    3        28,596 ±        1,059  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef         1  avgt    3        28,650 ±        0,561  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef           1  avgt    3        28,855 ±        1,750  ns/op
LoopNoSpecificOrder.reverseFor                                       10  avgt    3        31,162 ±        2,195  ns/op
LoopNoSpecificOrder.reverseWhileAlt                                  10  avgt    3        31,235 ±        1,603  ns/op
LoopNoSpecificOrder.reverseWhile                                     10  avgt    3        31,453 ±        5,122  ns/op
LoopNoSpecificOrder.forwardForAlt                                    10  avgt    3        32,300 ±        3,317  ns/op
LoopNoSpecificOrder.loopIterator                                     10  avgt    3        33,702 ±        2,312  ns/op
LoopNoSpecificOrder.forwardWhileAlt                                  10  avgt    3        32,207 ±        1,805  ns/op
LoopNoSpecificOrder.loopForEach                                      10  avgt    3        33,673 ±        1,848  ns/op
LoopNoSpecificOrder.forwardWhile                                     10  avgt    3        33,422 ±        2,301  ns/op
LoopNoSpecificOrder.collectionStreamForEach                          10  avgt    3        34,103 ±        0,471  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef                 10  avgt    3        34,063 ±        1,256  ns/op
LoopNoSpecificOrder.collectionForEach                                10  avgt    3        35,036 ±        2,168  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                       10  avgt    3        35,213 ±        0,250  ns/op
LoopNoSpecificOrder.forwardFor                                       10  avgt    3        35,214 ±        0,264  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef        10  avgt    3     20899,968 ±     4074,523  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect                   10  avgt    3     21084,779 ±     1133,502  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect                 10  avgt    3     21156,221 ±     1466,906  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef          10  avgt    3     21281,275 ±     2337,908  ns/op
LoopNoSpecificOrder.collectionStreamForEach                         100  avgt    3       274,867 ±       20,915  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef                100  avgt    3       274,596 ±       30,947  ns/op
LoopNoSpecificOrder.reverseFor                                      100  avgt    3       311,075 ±       25,825  ns/op
LoopNoSpecificOrder.reverseWhileAlt                                 100  avgt    3       311,752 ±       25,432  ns/op
LoopNoSpecificOrder.reverseWhile                                    100  avgt    3       312,770 ±       22,892  ns/op
LoopNoSpecificOrder.forwardForAlt                                   100  avgt    3       323,966 ±       59,941  ns/op
LoopNoSpecificOrder.forwardWhileAlt                                 100  avgt    3       324,424 ±       15,421  ns/op
LoopNoSpecificOrder.forwardWhile                                    100  avgt    3       333,064 ±       27,574  ns/op
LoopNoSpecificOrder.loopIterator                                    100  avgt    3       334,011 ±       24,334  ns/op
LoopNoSpecificOrder.loopForEach                                     100  avgt    3       335,130 ±       29,153  ns/op
LoopNoSpecificOrder.forwardFor                                      100  avgt    3       351,686 ±        6,389  ns/op
LoopNoSpecificOrder.collectionForEach                               100  avgt    3       353,271 ±       11,328  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                      100  avgt    3       355,887 ±        6,671  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef         100  avgt    3     24117,114 ±     1342,333  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect                100  avgt    3     24208,972 ±      754,521  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef       100  avgt    3     24258,947 ±      588,675  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect                  100  avgt    3     24472,473 ±      707,904  ns/op
LoopNoSpecificOrder.reverseFor                                 10000000  avgt    3  44593080,633 ±  2873888,165  ns/op
LoopNoSpecificOrder.reverseWhileAlt                            10000000  avgt    3  44777890,974 ±  4850944,670  ns/op
LoopNoSpecificOrder.forwardWhileAlt                            10000000  avgt    3  45118940,502 ±  2819394,545  ns/op
LoopNoSpecificOrder.forwardForAlt                              10000000  avgt    3  45925768,546 ±  3125943,461  ns/op
LoopNoSpecificOrder.reverseWhile                               10000000  avgt    3  45962776,540 ± 28758656,077  ns/op
LoopNoSpecificOrder.collectionStreamForEachMethodRef           10000000  avgt    3  46051046,636 ±   570112,775  ns/op
LoopNoSpecificOrder.collectionStreamForEach                    10000000  avgt    3  46090787,433 ±   970096,351  ns/op
LoopNoSpecificOrder.forwardWhile                               10000000  avgt    3  47520161,527 ±  2703467,405  ns/op
LoopNoSpecificOrder.loopIterator                               10000000  avgt    3  48659008,814 ±  3789991,955  ns/op
LoopNoSpecificOrder.forwardFor                                 10000000  avgt    3  48959984,662 ± 26902186,178  ns/op
LoopNoSpecificOrder.loopForEach                                10000000  avgt    3  48976752,310 ±  3775488,257  ns/op
LoopNoSpecificOrder.collectionForEach                          10000000  avgt    3  51273060,265 ±   644429,336  ns/op
LoopNoSpecificOrder.collectionForEachMethodRef                 10000000  avgt    3  51478125,812 ±   192823,784  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect           10000000  avgt    3  80711467,596 ±  7751314,760  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect             10000000  avgt    3  84539719,328 ±  1021443,748  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef    10000000  avgt    3  85609337,643 ±  5309282,531  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef  10000000  avgt    3  86791702,586 ±  1428162,832  ns/op
*/
