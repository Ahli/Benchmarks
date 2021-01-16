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
	public void collectionStream(final Blackhole bh) {
		DATA_FOR_TESTING.stream().forEach(s -> bh.consume(s));
	}
	
	@Benchmark
	public void collectionStreamParallelIndirect(final Blackhole bh) {
		// not in order!
		DATA_FOR_TESTING.stream().parallel().forEach(s -> bh.consume(s));
	}
	
	@Benchmark
	public void collectionStreamParallelDirect(final Blackhole bh) {
		// not in order!
		DATA_FOR_TESTING.parallelStream().forEach(s -> bh.consume(s));
	}
	
}

/*
JDK 16-ea
Benchmark                                                  (N)  Mode  Cnt          Score         Error  Units
LoopNoSpecificOrder.forwardWhileAlt                          1  avgt    3          4,129 ±       0,032  ns/op
LoopNoSpecificOrder.forwardForAlt                            1  avgt    3          4,142 ±       0,235  ns/op
LoopNoSpecificOrder.reverseFor                               1  avgt    3          4,341 ±       0,149  ns/op
LoopNoSpecificOrder.reverseWhile                             1  avgt    3          4,349 ±       0,171  ns/op
LoopNoSpecificOrder.reverseWhileAlt                          1  avgt    3          4,437 ±       2,522  ns/op
LoopNoSpecificOrder.forwardWhile                             1  avgt    3          4,602 ±       3,463  ns/op
LoopNoSpecificOrder.forwardFor                               1  avgt    3          4,604 ±       1,070  ns/op
LoopNoSpecificOrder.loopIterator                             1  avgt    3          4,697 ±       0,496  ns/op
LoopNoSpecificOrder.loopForEach                              1  avgt    3          5,199 ±       1,661  ns/op
LoopNoSpecificOrder.collectionStream                         1  avgt    3         13,524 ±       0,610  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect           1  avgt    3         31,820 ±       0,924  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect         1  avgt    3         32,568 ±       2,810  ns/op
LoopNoSpecificOrder.reverseWhileAlt                         10  avgt    3         34,733 ±       0,372  ns/op
LoopNoSpecificOrder.reverseWhile                            10  avgt    3         34,805 ±       1,134  ns/op
LoopNoSpecificOrder.reverseFor                              10  avgt    3         34,825 ±       0,931  ns/op
LoopNoSpecificOrder.forwardWhileAlt                         10  avgt    3         36,051 ±       2,260  ns/op
LoopNoSpecificOrder.forwardForAlt                           10  avgt    3         36,411 ±       0,990  ns/op
LoopNoSpecificOrder.forwardWhile                            10  avgt    3         37,475 ±       1,744  ns/op
LoopNoSpecificOrder.loopIterator                            10  avgt    3         37,543 ±       0,463  ns/op
LoopNoSpecificOrder.loopForEach                             10  avgt    3         37,835 ±       1,939  ns/op
LoopNoSpecificOrder.forwardFor                              10  avgt    3         37,900 ±       3,445  ns/op
LoopNoSpecificOrder.collectionStream                        10  avgt    3         38,290 ±       0,195  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect          10  avgt    3      16053,994 ±    2625,870  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect        10  avgt    3      16555,019 ±     768,306  ns/op
LoopNoSpecificOrder.collectionStream                       100  avgt    3        309,164 ±      18,400  ns/op
LoopNoSpecificOrder.reverseWhile                           100  avgt    3        346,929 ±       9,719  ns/op
LoopNoSpecificOrder.reverseWhileAlt                        100  avgt    3        347,170 ±       7,475  ns/op
LoopNoSpecificOrder.reverseFor                             100  avgt    3        347,236 ±       7,451  ns/op
LoopNoSpecificOrder.forwardForAlt                          100  avgt    3        359,814 ±       8,723  ns/op
LoopNoSpecificOrder.loopForEach                            100  avgt    3        372,037 ±       6,450  ns/op
LoopNoSpecificOrder.loopIterator                           100  avgt    3        373,660 ±       2,532  ns/op
LoopNoSpecificOrder.forwardWhile                           100  avgt    3        395,812 ±      11,594  ns/op
LoopNoSpecificOrder.forwardFor                             100  avgt    3        396,860 ±       1,819  ns/op
LoopNoSpecificOrder.forwardWhileAlt                        100  avgt    3        414,047 ±      22,294  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect         100  avgt    3      26350,370 ±    2479,448  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect       100  avgt    3      26592,532 ±     736,482  ns/op
LoopNoSpecificOrder.reverseWhile                      10000000  avgt    3   49982747,589 ± 2257380,576  ns/op
LoopNoSpecificOrder.reverseWhileAlt                   10000000  avgt    3   50157309,500 ± 1131304,606  ns/op
LoopNoSpecificOrder.reverseFor                        10000000  avgt    3   50328926,631 ±  286532,052  ns/op
LoopNoSpecificOrder.forwardForAlt                     10000000  avgt    3   51846443,018 ± 4405068,263  ns/op
LoopNoSpecificOrder.forwardWhileAlt                   10000000  avgt    3   51872612,504 ± 1730461,053  ns/op
LoopNoSpecificOrder.loopForEach                       10000000  avgt    3   53540329,347 ± 2152381,249  ns/op
LoopNoSpecificOrder.collectionStream                  10000000  avgt    3   53666342,903 ± 3165920,476  ns/op
LoopNoSpecificOrder.forwardFor                        10000000  avgt    3   54289124,766 ± 2035623,515  ns/op
LoopNoSpecificOrder.forwardWhile                      10000000  avgt    3   54785233,384 ± 4169770,078  ns/op
LoopNoSpecificOrder.loopIterator                      10000000  avgt    3   56913099,728 ± 6345699,526  ns/op
LoopNoSpecificOrder.collectionStreamParallelDirect    10000000  avgt    3  131993394,116 ± 8213786,959  ns/op
LoopNoSpecificOrder.collectionStreamParallelIndirect  10000000  avgt    3  140527012,963 ± 2586567,141  ns/op
*/
