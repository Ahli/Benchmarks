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

JDK-24 - Desktop 9800X3D highPerformance
Benchmark                                                           (N)   Mode  Cnt           Score           Error  Units
LoopNoSpecificOrder.collectionForEach                                 1  thrpt    3   666485929,190 ±  23309965,495  ops/s
LoopNoSpecificOrder.collectionForEach                                10  thrpt    3   284469154,299 ±  12786032,042  ops/s
LoopNoSpecificOrder.collectionForEach                               100  thrpt    3    64831752,136 ±   1573896,256  ops/s
LoopNoSpecificOrder.collectionForEach                          10000000  thrpt    3         111,080 ±        16,481  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                        1  thrpt    3   865998582,583 ±  18019341,220  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                       10  thrpt    3   284915739,722 ±  15609911,545  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                      100  thrpt    3    64245239,428 ±   1241696,175  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                 10000000  thrpt    3         111,130 ±        22,933  ops/s
LoopNoSpecificOrder.collectionStreamForEach                           1  thrpt    3   312678967,045 ±   2535115,551  ops/s
LoopNoSpecificOrder.collectionStreamForEach                          10  thrpt    3   208052033,351 ±   6235694,031  ops/s
LoopNoSpecificOrder.collectionStreamForEach                         100  thrpt    3    59705104,011 ±    282919,334  ops/s
LoopNoSpecificOrder.collectionStreamForEach                    10000000  thrpt    3         110,748 ±        13,943  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                  1  thrpt    3   304085365,307 ±   7163346,639  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                 10  thrpt    3   209649036,281 ±   1719203,601  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                100  thrpt    3    59266072,821 ±   1098966,517  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef           10000000  thrpt    3         110,854 ±        14,557  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                    1  thrpt    3    97987398,281 ±   2650819,492  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                   10  thrpt    3      156267,068 ±     44692,286  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                  100  thrpt    3      214778,723 ±     44498,345  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect             10000000  thrpt    3         116,843 ±         8,407  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef           1  thrpt    3    99195089,907 ±   3244714,119  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef          10  thrpt    3      153213,046 ±     31586,400  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef         100  thrpt    3      227712,192 ±     15695,968  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef    10000000  thrpt    3         117,092 ±         5,575  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                  1  thrpt    3    98067827,977 ±   4199749,272  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                 10  thrpt    3      156135,792 ±     33875,883  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                100  thrpt    3      225084,990 ±     32314,121  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect           10000000  thrpt    3         115,946 ±         2,337  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef         1  thrpt    3   102524195,171 ±   3615772,368  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef        10  thrpt    3      150136,360 ±     32827,163  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef       100  thrpt    3      260787,629 ±     14378,122  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef  10000000  thrpt    3         116,333 ±        30,124  ops/s
LoopNoSpecificOrder.forwardFor                                        1  thrpt    3   878275049,470 ±  61483841,478  ops/s
LoopNoSpecificOrder.forwardFor                                       10  thrpt    3   378823691,547 ±  21521903,563  ops/s
LoopNoSpecificOrder.forwardFor                                      100  thrpt    3    67187529,235 ±   1054308,472  ops/s
LoopNoSpecificOrder.forwardFor                                 10000000  thrpt    3         116,381 ±         5,855  ops/s
LoopNoSpecificOrder.forwardForAlt                                     1  thrpt    3   893586030,922 ±  14173704,837  ops/s
LoopNoSpecificOrder.forwardForAlt                                    10  thrpt    3   374984132,647 ±   1870693,365  ops/s
LoopNoSpecificOrder.forwardForAlt                                   100  thrpt    3    65274028,584 ±  32743150,338  ops/s
LoopNoSpecificOrder.forwardForAlt                              10000000  thrpt    3         115,909 ±         4,836  ops/s
LoopNoSpecificOrder.forwardWhile                                      1  thrpt    3   882341236,324 ±  22228041,730  ops/s
LoopNoSpecificOrder.forwardWhile                                     10  thrpt    3   380348670,821 ±   3624630,756  ops/s
LoopNoSpecificOrder.forwardWhile                                    100  thrpt    3    67697704,838 ±   1316653,315  ops/s
LoopNoSpecificOrder.forwardWhile                               10000000  thrpt    3         116,505 ±         0,194  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                   1  thrpt    3   890244148,961 ±  10021881,985  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                  10  thrpt    3   372833700,468 ±  59834092,077  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                 100  thrpt    3    67424945,238 ±   1764419,538  ops/s
LoopNoSpecificOrder.forwardWhileAlt                            10000000  thrpt    3         116,830 ±         3,990  ops/s
LoopNoSpecificOrder.loopForEach                                       1  thrpt    3   634956858,592 ±  22080970,900  ops/s
LoopNoSpecificOrder.loopForEach                                      10  thrpt    3   270232852,594 ±   4786268,086  ops/s
LoopNoSpecificOrder.loopForEach                                     100  thrpt    3    60635713,809 ±  11078812,126  ops/s
LoopNoSpecificOrder.loopForEach                                10000000  thrpt    3         115,603 ±         1,265  ops/s
LoopNoSpecificOrder.loopIterator                                      1  thrpt    3   634822739,378 ±  11708231,628  ops/s
LoopNoSpecificOrder.loopIterator                                     10  thrpt    3   269907145,536 ±   2856528,528  ops/s
LoopNoSpecificOrder.loopIterator                                    100  thrpt    3    59286994,206 ±   1472981,412  ops/s
LoopNoSpecificOrder.loopIterator                               10000000  thrpt    3         115,162 ±        11,587  ops/s
LoopNoSpecificOrder.reverseFor                                        1  thrpt    3  1440648098,963 ± 204167258,988  ops/s
LoopNoSpecificOrder.reverseFor                                       10  thrpt    3   367166838,237 ±   6300807,418  ops/s
LoopNoSpecificOrder.reverseFor                                      100  thrpt    3    64025740,665 ±    987324,961  ops/s
LoopNoSpecificOrder.reverseFor                                 10000000  thrpt    3         116,458 ±         2,519  ops/s
LoopNoSpecificOrder.reverseWhile                                      1  thrpt    3  1465670389,117 ±  23021744,527  ops/s
LoopNoSpecificOrder.reverseWhile                                     10  thrpt    3   370770566,682 ±   4449118,256  ops/s
LoopNoSpecificOrder.reverseWhile                                    100  thrpt    3    64834687,448 ±     22896,474  ops/s
LoopNoSpecificOrder.reverseWhile                               10000000  thrpt    3         116,387 ±         1,705  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                   1  thrpt    3   874106389,301 ±   8304468,228  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                  10  thrpt    3   386284300,257 ±   1027837,545  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                 100  thrpt    3    62601811,855 ±    202440,288  ops/s
LoopNoSpecificOrder.reverseWhileAlt                            10000000  thrpt    3         116,483 ±         2,382  ops/s

JDK-25 - Desktop 9800X3D balanced
Benchmark                                                           (N)   Mode  Cnt           Score          Error  Units
LoopNoSpecificOrder.collectionForEach                                 1  thrpt    3   891717382,684 ±  5919954,518  ops/s
LoopNoSpecificOrder.collectionForEach                                10  thrpt    3   293369908,827 ±  3588182,942  ops/s
LoopNoSpecificOrder.collectionForEach                               100  thrpt    3    65627660,684 ±  4124318,771  ops/s
LoopNoSpecificOrder.collectionForEach                          10000000  thrpt    3         112,491 ±        1,417  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                        1  thrpt    3   881776087,096 ± 11403883,784  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                       10  thrpt    3   295002628,110 ± 11441420,162  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                      100  thrpt    3    67828778,808 ±   788739,250  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                 10000000  thrpt    3         112,504 ±        3,428  ops/s
LoopNoSpecificOrder.collectionStreamForEach                           1  thrpt    3   335185932,748 ± 13190370,855  ops/s
LoopNoSpecificOrder.collectionStreamForEach                          10  thrpt    3   220998477,649 ±  2761611,561  ops/s
LoopNoSpecificOrder.collectionStreamForEach                         100  thrpt    3    62293696,573 ±  1390881,571  ops/s
LoopNoSpecificOrder.collectionStreamForEach                    10000000  thrpt    3         110,766 ±        2,965  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                  1  thrpt    3   335547529,571 ±  1248318,927  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                 10  thrpt    3   217396712,046 ±  2430501,734  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                100  thrpt    3    62127186,046 ±  1048123,962  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef           10000000  thrpt    3         110,827 ±        3,468  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                    1  thrpt    3    96931042,369 ±  1467347,518  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                   10  thrpt    3      181029,196 ±    22594,099  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                  100  thrpt    3      242755,884 ±    23751,760  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect             10000000  thrpt    3         114,763 ±        3,999  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef           1  thrpt    3    99765158,132 ±  1544698,681  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef          10  thrpt    3      180398,690 ±     7605,632  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef         100  thrpt    3      236019,738 ±    41953,828  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef    10000000  thrpt    3         116,055 ±        2,401  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                  1  thrpt    3    99410128,895 ±  5989841,821  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                 10  thrpt    3      182368,928 ±     6472,457  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                100  thrpt    3      237271,804 ±    10653,832  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect           10000000  thrpt    3         116,109 ±        1,195  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef         1  thrpt    3    99334141,856 ±   331165,543  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef        10  thrpt    3      157309,816 ±     4561,194  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef       100  thrpt    3      230975,470 ±    29501,823  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef  10000000  thrpt    3         115,520 ±        0,651  ops/s
LoopNoSpecificOrder.forwardFor                                        1  thrpt    3   905440440,771 ± 16743753,309  ops/s
LoopNoSpecificOrder.forwardFor                                       10  thrpt    3   369388546,475 ±  1817892,205  ops/s
LoopNoSpecificOrder.forwardFor                                      100  thrpt    3    69134788,722 ±  3005085,792  ops/s
LoopNoSpecificOrder.forwardFor                                 10000000  thrpt    3         113,254 ±        4,761  ops/s
LoopNoSpecificOrder.forwardForAlt                                     1  thrpt    3   905161504,376 ± 31168357,677  ops/s
LoopNoSpecificOrder.forwardForAlt                                    10  thrpt    3   376965490,942 ±  4565059,302  ops/s
LoopNoSpecificOrder.forwardForAlt                                   100  thrpt    3    69176175,854 ±   459389,317  ops/s
LoopNoSpecificOrder.forwardForAlt                              10000000  thrpt    3         113,072 ±        1,068  ops/s
LoopNoSpecificOrder.forwardWhile                                      1  thrpt    3   904712032,683 ± 37648274,795  ops/s
LoopNoSpecificOrder.forwardWhile                                     10  thrpt    3   368884508,442 ±  8763199,953  ops/s
LoopNoSpecificOrder.forwardWhile                                    100  thrpt    3    69027392,736 ±  2192192,069  ops/s
LoopNoSpecificOrder.forwardWhile                               10000000  thrpt    3         113,208 ±        2,512  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                   1  thrpt    3   907750252,261 ± 37511258,584  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                  10  thrpt    3   377500719,181 ±  7226632,414  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                 100  thrpt    3    69141355,776 ±  4003341,981  ops/s
LoopNoSpecificOrder.forwardWhileAlt                            10000000  thrpt    3         113,016 ±        1,135  ops/s
LoopNoSpecificOrder.loopForEach                                       1  thrpt    3   630071164,875 ± 17160321,500  ops/s
LoopNoSpecificOrder.loopForEach                                      10  thrpt    3   277718880,381 ±  3727586,173  ops/s
LoopNoSpecificOrder.loopForEach                                     100  thrpt    3    61446207,889 ±  5037011,838  ops/s
LoopNoSpecificOrder.loopForEach                                10000000  thrpt    3         112,716 ±        1,077  ops/s
LoopNoSpecificOrder.loopIterator                                      1  thrpt    3   630068167,554 ± 20500434,730  ops/s
LoopNoSpecificOrder.loopIterator                                     10  thrpt    3   277618642,071 ±  8203713,467  ops/s
LoopNoSpecificOrder.loopIterator                                    100  thrpt    3    60883802,735 ±   926221,365  ops/s
LoopNoSpecificOrder.loopIterator                               10000000  thrpt    3         112,674 ±        1,609  ops/s
LoopNoSpecificOrder.reverseFor                                        1  thrpt    3  1488923560,376 ± 72023413,654  ops/s
LoopNoSpecificOrder.reverseFor                                       10  thrpt    3   376331422,813 ±  2907854,639  ops/s
LoopNoSpecificOrder.reverseFor                                      100  thrpt    3    64961215,664 ±  2979316,039  ops/s
LoopNoSpecificOrder.reverseFor                                 10000000  thrpt    3         111,888 ±        3,762  ops/s
LoopNoSpecificOrder.reverseWhile                                      1  thrpt    3  1484588609,774 ± 73852595,074  ops/s
LoopNoSpecificOrder.reverseWhile                                     10  thrpt    3   375832138,867 ±  8134545,338  ops/s
LoopNoSpecificOrder.reverseWhile                                    100  thrpt    3    65029518,684 ±  1987546,198  ops/s
LoopNoSpecificOrder.reverseWhile                               10000000  thrpt    3         112,115 ±        2,749  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                   1  thrpt    3   878096282,177 ± 14763967,339  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                  10  thrpt    3   387790975,367 ± 19547684,100  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                 100  thrpt    3    64125948,646 ±  1080119,513  ops/s
LoopNoSpecificOrder.reverseWhileAlt                            10000000  thrpt    3         112,093 ±        1,289  ops/s

JDK-21.0.8 - Desktop 9800X3D balanced
Benchmark                                                           (N)   Mode  Cnt           Score           Error  Units
LoopNoSpecificOrder.collectionForEach                                 1  thrpt    3   769117627,927 ±   4020504,388  ops/s
LoopNoSpecificOrder.collectionForEach                                10  thrpt    3   279728854,158 ±    258780,697  ops/s
LoopNoSpecificOrder.collectionForEach                               100  thrpt    3    67776287,141 ±    710266,264  ops/s
LoopNoSpecificOrder.collectionForEach                          10000000  thrpt    3         110,507 ±         1,297  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                        1  thrpt    3   740067616,796 ±  17070279,847  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                       10  thrpt    3   283057163,320 ±  61139917,509  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                      100  thrpt    3    66875132,109 ±    104086,921  ops/s
LoopNoSpecificOrder.collectionForEachMethodRef                 10000000  thrpt    3         109,181 ±         0,655  ops/s
LoopNoSpecificOrder.collectionStreamForEach                           1  thrpt    3   340263740,315 ±   2990747,104  ops/s
LoopNoSpecificOrder.collectionStreamForEach                          10  thrpt    3   218101021,089 ± 110494452,558  ops/s
LoopNoSpecificOrder.collectionStreamForEach                         100  thrpt    3    62420019,311 ±   2612835,896  ops/s
LoopNoSpecificOrder.collectionStreamForEach                    10000000  thrpt    3         103,458 ±        55,357  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                  1  thrpt    3   329077368,988 ±  76111132,157  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                 10  thrpt    3   214709247,133 ±  77071394,426  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef                100  thrpt    3    59788110,798 ±   6574821,415  ops/s
LoopNoSpecificOrder.collectionStreamForEachMethodRef           10000000  thrpt    3         104,159 ±         2,672  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                    1  thrpt    3    83626127,242 ±  12943999,846  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                   10  thrpt    3      154724,257 ±      2470,907  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect                  100  thrpt    3      117534,685 ±     16386,602  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirect             10000000  thrpt    3         113,833 ±         1,037  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef           1  thrpt    3    81223806,556 ±   5133488,346  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef          10  thrpt    3      153303,349 ±     90844,020  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef         100  thrpt    3      124233,405 ±     13067,855  ops/s
LoopNoSpecificOrder.collectionStreamParallelDirectMethodRef    10000000  thrpt    3         115,552 ±         8,858  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                  1  thrpt    3    82106726,802 ±   5491962,572  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                 10  thrpt    3      156208,547 ±      3897,096  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect                100  thrpt    3      123727,265 ±     18645,254  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirect           10000000  thrpt    3         116,371 ±         2,837  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef         1  thrpt    3    82154137,312 ±   2432192,715  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef        10  thrpt    3      187338,160 ±      9901,496  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef       100  thrpt    3      148751,630 ±      3147,438  ops/s
LoopNoSpecificOrder.collectionStreamParallelIndirectMethodRef  10000000  thrpt    3         116,385 ±         0,899  ops/s
LoopNoSpecificOrder.forwardFor                                        1  thrpt    3   911033666,744 ±   9189671,364  ops/s
LoopNoSpecificOrder.forwardFor                                       10  thrpt    3   369337500,577 ±  13609291,800  ops/s
LoopNoSpecificOrder.forwardFor                                      100  thrpt    3    68984083,545 ±    608692,288  ops/s
LoopNoSpecificOrder.forwardFor                                 10000000  thrpt    3         110,810 ±         5,421  ops/s
LoopNoSpecificOrder.forwardForAlt                                     1  thrpt    3   910688193,167 ±  12721980,295  ops/s
LoopNoSpecificOrder.forwardForAlt                                    10  thrpt    3   383078175,357 ±   1753300,761  ops/s
LoopNoSpecificOrder.forwardForAlt                                   100  thrpt    3    69229511,819 ±   2181809,962  ops/s
LoopNoSpecificOrder.forwardForAlt                              10000000  thrpt    3         110,588 ±         1,405  ops/s
LoopNoSpecificOrder.forwardWhile                                      1  thrpt    3   903455879,187 ±  20760744,379  ops/s
LoopNoSpecificOrder.forwardWhile                                     10  thrpt    3   371256408,869 ±   7534866,400  ops/s
LoopNoSpecificOrder.forwardWhile                                    100  thrpt    3    68700387,710 ±   3495632,602  ops/s
LoopNoSpecificOrder.forwardWhile                               10000000  thrpt    3         111,100 ±         4,625  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                   1  thrpt    3   910236560,709 ±  14343629,158  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                  10  thrpt    3   382169024,022 ±  18472660,589  ops/s
LoopNoSpecificOrder.forwardWhileAlt                                 100  thrpt    3    69158497,737 ±   2343392,468  ops/s
LoopNoSpecificOrder.forwardWhileAlt                            10000000  thrpt    3         110,780 ±         6,552  ops/s
LoopNoSpecificOrder.loopForEach                                       1  thrpt    3   545729949,447 ±   4413938,913  ops/s
LoopNoSpecificOrder.loopForEach                                      10  thrpt    3   254276539,639 ±   2137958,607  ops/s
LoopNoSpecificOrder.loopForEach                                     100  thrpt    3    60481196,153 ±   2289926,608  ops/s
LoopNoSpecificOrder.loopForEach                                10000000  thrpt    3         110,817 ±         1,230  ops/s
LoopNoSpecificOrder.loopIterator                                      1  thrpt    3   545772644,630 ±  12463276,615  ops/s
LoopNoSpecificOrder.loopIterator                                     10  thrpt    3   253991790,560 ±   8199737,324  ops/s
LoopNoSpecificOrder.loopIterator                                    100  thrpt    3    60569289,957 ±   1949623,715  ops/s
LoopNoSpecificOrder.loopIterator                               10000000  thrpt    3         110,722 ±         3,969  ops/s
LoopNoSpecificOrder.reverseFor                                        1  thrpt    3  1491836598,960 ±  29014138,529  ops/s
LoopNoSpecificOrder.reverseFor                                       10  thrpt    3   370045220,050 ±  60639577,779  ops/s
LoopNoSpecificOrder.reverseFor                                      100  thrpt    3    64506697,158 ±   2635753,748  ops/s
LoopNoSpecificOrder.reverseFor                                 10000000  thrpt    3         108,845 ±         6,197  ops/s
LoopNoSpecificOrder.reverseWhile                                      1  thrpt    3  1485274827,588 ±  13585461,413  ops/s
LoopNoSpecificOrder.reverseWhile                                     10  thrpt    3   373038606,347 ±  15302758,345  ops/s
LoopNoSpecificOrder.reverseWhile                                    100  thrpt    3    63135195,159 ±   4209643,753  ops/s
LoopNoSpecificOrder.reverseWhile                               10000000  thrpt    3         109,024 ±         1,944  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                   1  thrpt    3   901650639,328 ±  56022785,791  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                  10  thrpt    3   391706341,518 ±   6064261,627  ops/s
LoopNoSpecificOrder.reverseWhileAlt                                 100  thrpt    3    64161163,939 ±    576506,266  ops/s
LoopNoSpecificOrder.reverseWhileAlt                            10000000  thrpt    3         109,108 ±         2,163  ops/s
*/
