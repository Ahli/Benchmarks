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
import org.openjdk.jmh.annotations.Threads;
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
@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class LoopNoSpecificOrder {
	
	@Param({ "1", "10", "100", "10000000" })
	private int N;
	
	private List<String> DATA_FOR_TESTING;
	
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
		final List<String> data = new ArrayList<>(N);
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
JDK 16.0.2 - laptop 6+6 cores
Benchmark                                                           (N)   Mode  Cnt          Score          Error  Units
LoopNoSpecificOrder.collectionForEach                                 1  thrpt    3  203402703,396 ±  9909232,654  ops/s
LoopNoSpecificOrder.collectionForEach                                10  thrpt    3   25723081,359 ±   744434,520  ops/s
LoopNoSpecificOrder.collectionForEach                               100  thrpt    3    2554291,494 ±   146758,248  ops/s
LoopNoSpecificOrder.collectionForEach                          10000000  thrpt    3         17,352 ±        0,426  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                        1  thrpt    3  190053915,930 ± 46526195,228  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                       10  thrpt    3   25489685,923 ±  1766287,644  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                      100  thrpt    3    2552080,536 ±   335655,541  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                 10000000  thrpt    3         15,956 ±       20,324  ops/s
LoopNoSpecificOrder.collectionStreamForEach                           1  thrpt    3   84752590,216 ±  4387031,317  ops/s
LoopNoSpecificOrder.collectionStreamForEach                          10  thrpt    3   26400702,439 ±  3180923,524  ops/s
LoopNoSpecificOrder.collectionStreamForEach                         100  thrpt    3    3276792,284 ±   575710,383  ops/s
LoopNoSpecificOrder.collectionStreamForEach                    10000000  thrpt    3         19,471 ±        0,232  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                  1  thrpt    3   83842789,171 ±  4038393,188  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                 10  thrpt    3   26567609,914 ±  1862940,474  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                100  thrpt    3    3278061,189 ±   356023,611  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef           10000000  thrpt    3         19,181 ±        1,038  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                    1  thrpt    3   33315837,458 ±   720660,295  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                   10  thrpt    3      57889,352 ±     4009,257  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                  100  thrpt    3      35289,433 ±     2258,130  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect             10000000  thrpt    3          7,066 ±        0,118  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef           1  thrpt    3   33260056,529 ±  1097171,137  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef          10  thrpt    3      56871,358 ±     7100,153  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef         100  thrpt    3      35006,697 ±     2737,105  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef    10000000  thrpt    3          7,456 ±        1,047  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                  1  thrpt    3   33042671,616 ±   843801,213  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                 10  thrpt    3      56722,383 ±     3202,331  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                100  thrpt    3      34843,113 ±     2014,715  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect           10000000  thrpt    3          6,916 ±        0,107  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef         1  thrpt    3   32392728,518 ±  3614364,594  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef        10  thrpt    3      56337,359 ±    10522,818  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef       100  thrpt    3      34522,600 ±     1029,638  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef  10000000  thrpt    3          7,146 ±        2,533  ops/s
LoopNoSpecificOrder.forwardFor                                        1  thrpt    3  224378422,506 ± 33793676,067  ops/s
LoopNoSpecificOrder.forwardFor                                       10  thrpt    3   26909314,250 ±  4286145,448  ops/s
LoopNoSpecificOrder.forwardFor                                      100  thrpt    3    2536323,660 ±   832873,758  ops/s
LoopNoSpecificOrder.forwardFor                                 10000000  thrpt    3         19,132 ±        1,612  ops/s
LoopNoSpecificOrder.forwardForAlt                                     1  thrpt    3  244842142,991 ±  7734599,508  ops/s
LoopNoSpecificOrder.forwardForAlt                                    10  thrpt    3   28223622,753 ±  1019229,417  ops/s
LoopNoSpecificOrder.forwardForAlt                                   100  thrpt    3    2803369,047 ±   505545,027  ops/s
LoopNoSpecificOrder.forwardForAlt                              10000000  thrpt    3         20,301 ±        0,492  ops/s
LoopNoSpecificOrder.forwardWhile                                      1  thrpt    3  226302454,438 ± 11046847,476  ops/s
LoopNoSpecificOrder.forwardWhile                                     10  thrpt    3   27170866,969 ±  1600471,188  ops/s
LoopNoSpecificOrder.forwardWhile                                    100  thrpt    3    2558347,492 ±   436124,574  ops/s
LoopNoSpecificOrder.forwardWhile                               10000000  thrpt    3         19,283 ±        0,411  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                   1  thrpt    3  243201668,108 ± 38639796,889  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                  10  thrpt    3   28307441,519 ±  1495438,653  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                 100  thrpt    3    2821663,073 ±   283910,781  ops/s
LoopNoSpecificOrder.forwardWhileAlt                            10000000  thrpt    3         20,257 ±        0,802  ops/s
LoopNoSpecificOrder.loopForEach                                       1  thrpt    3  214782815,014 ± 29348018,381  ops/s
LoopNoSpecificOrder.loopForEach                                      10  thrpt    3   27002159,773 ±  1926914,758  ops/s
LoopNoSpecificOrder.loopForEach                                     100  thrpt    3    2722797,556 ±   114778,762  ops/s
LoopNoSpecificOrder.loopForEach                                10000000  thrpt    3         18,586 ±        0,823  ops/s
LoopNoSpecificOrder.loopIterator                                      1  thrpt    3  207182424,119 ± 29241804,026  ops/s
LoopNoSpecificOrder.loopIterator                                     10  thrpt    3   26950490,141 ±  3062478,473  ops/s
LoopNoSpecificOrder.loopIterator                                    100  thrpt    3    2715504,177 ±   147847,455  ops/s
LoopNoSpecificOrder.loopIterator                               10000000  thrpt    3         19,715 ±        0,969  ops/s
LoopNoSpecificOrder.reverseFor                                        1  thrpt    3  232406384,072 ± 23590705,654  ops/s
LoopNoSpecificOrder.reverseFor                                       10  thrpt    3   28996705,820 ±  3768915,709  ops/s
LoopNoSpecificOrder.reverseFor                                      100  thrpt    3    2927181,220 ±   110595,311  ops/s
LoopNoSpecificOrder.reverseFor                                 10000000  thrpt    3         21,811 ±        0,170  ops/s
LoopNoSpecificOrder.reverseWhile                                      1  thrpt    3  233903697,153 ±  6792926,912  ops/s
LoopNoSpecificOrder.reverseWhile                                     10  thrpt    3   28673450,689 ±  5545629,484  ops/s
LoopNoSpecificOrder.reverseWhile                                    100  thrpt    3    2905707,774 ±    27178,547  ops/s
LoopNoSpecificOrder.reverseWhile                               10000000  thrpt    3         22,005 ±        0,349  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                   1  thrpt    3  233162335,252 ± 22020716,965  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                  10  thrpt    3   28925720,517 ±   522364,080  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                 100  thrpt    3    2919611,383 ±   157490,986  ops/s
LoopNoSpecificOrder.reverseWhileAlt                            10000000  thrpt    3         22,045 ±        0,366  ops/s

*/
