package file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class FileCount {
	
	private final Path path = Path.of("D:\\projects\\Galaxy-Observer-UI\\dev\\heroes\\AhliObs.StormInterface");
//	private final Path path = Path.of("D:\\GalaxyObsUi\\dev\\heroes\\AhliObs.StormInterface");
	private final FileCountingVisitor reusableFileVisitor = new FileCountingVisitor();
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt =
				new OptionsBuilder().include(".*" + FileCount.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	private static int searchInListFiles(final File directory) {
		int count = 0;
		final File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0, len = files.length; i < len; ++i) {
				count += files[i].isDirectory() ? searchInListFiles(files[i]) : 1;
			}
		}
		return count;
	}
	
	@Benchmark
	public void walkParallelToFile(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final long count = walk.parallel().map(Path::toFile).filter(File::isFile).count();
			bh.consume(checkResult(count));
		}
	}
	
	private static long checkResult(final long val) {
		if (val != 107) {
			throw new IllegalArgumentException("Wrong result! Received " + val);
		}
		return val;
	}
	
	@Benchmark
	public void walkParallelToFile2(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final long count = walk.parallel().filter(p -> p.toFile().isFile()).count();
			bh.consume(checkResult(count));
		}
	}
	
	@Benchmark
	public void walkParallelNioFiles(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final long count = walk.parallel().filter(Files::isRegularFile).count();
			bh.consume(checkResult(count));
		}
	}
	
	@Benchmark
	public void walkToFile(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final long count = walk.map(Path::toFile).filter(File::isFile).count();
			bh.consume(checkResult(count));
		}
	}
	
	@Benchmark
	public void walkToFile2(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final long count = walk.filter(p -> p.toFile().isFile()).count();
			bh.consume(checkResult(count));
		}
	}
	
	@Benchmark
	public void nioFiles(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final long count = walk.filter(Files::isRegularFile).count();
			bh.consume(checkResult(count));
		}
	}
	
	@Benchmark
	public void commonsList(final Blackhole bh) {
		final Collection<File> files =
				FileUtils.listFilesAndDirs(path.toFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE);
		int count = files.size();
		for (final File file : files) {
			if (file.isDirectory()) {
				--count;
			}
		}
		bh.consume(checkResult(count));
	}
	
	private static int checkResult(final int val) {
		if (val != 107) {
			throw new IllegalArgumentException("Wrong result! Received " + val);
		}
		return val;
	}
	
	@Benchmark
	public void commonsListStream(final Blackhole bh) {
		final long count = FileUtils.listFilesAndDirs(path.toFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
				.stream()
				.filter(File::isFile)
				.count();
		bh.consume(checkResult(count));
	}
	
	@Benchmark
	public void commonsListWithoutFilter(final Blackhole bh) {
		final int count = FileUtils.listFiles(path.toFile(), null, true).size();
		bh.consume(checkResult(count));
	}
	
	@Benchmark
	public void fileListFiles(final Blackhole bh) {
		final int count = searchInListFiles(path.toFile());
		bh.consume(checkResult(count));
	}
	
	@Benchmark
	public void fileVisitorNew(final Blackhole bh) throws IOException {
		final FileCountingVisitor fileVisitor = new FileCountingVisitor();
		Files.walkFileTree(path, fileVisitor);
		final int count = fileVisitor.getCount();
		bh.consume(checkResult(count));
	}
	
	@Benchmark
	public void fileVisitorReuse(final Blackhole bh) throws IOException {
		reusableFileVisitor.resetCount();
		Files.walkFileTree(path, reusableFileVisitor);
		final int count = reusableFileVisitor.getCount();
		bh.consume(checkResult(count));
	}
	
	private static class FileCountingVisitor extends SimpleFileVisitor<Path> {
		private int count = 0;
		
		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
			++count;
			return FileVisitResult.CONTINUE;
		}
		
		public int getCount() {
			return count;
		}
		
		public void resetCount() {
			count = 0;
		}
	}
}

/*
JDK-17 - laptop 6+6 cores
Benchmark                            Mode  Cnt    Score     Error  Units
FileCount.commonsList               thrpt    3  141,008 ±  67,194  ops/s
FileCount.commonsListStream         thrpt    3  144,502 ±  27,829  ops/s
FileCount.commonsListWithoutFilter  thrpt    3  146,173 ±  25,957  ops/s
FileCount.fileListFiles             thrpt    3  204,373 ±   9,286  ops/s
FileCount.fileVisitorNew            thrpt    3  700,763 ±  68,691  ops/s
FileCount.fileVisitorReuse          thrpt    3  696,485 ± 108,512  ops/s
FileCount.nioFiles                  thrpt    3  223,732 ±  13,447  ops/s
FileCount.walkParallelNioFiles      thrpt    3  239,868 ±  35,452  ops/s
FileCount.walkParallelToFile        thrpt    3  229,768 ±   5,082  ops/s
FileCount.walkParallelToFile2       thrpt    3  232,495 ±  11,099  ops/s
FileCount.walkToFile                thrpt    3  218,120 ±  17,642  ops/s
FileCount.walkToFile2               thrpt    3  217,200 ±  16,594  ops/s

JDK-24 - Desktop 9800X3D highPerformance
Benchmark                            Mode  Cnt     Score     Error  Units
FileCount.commonsList               thrpt    3   233,558 ±  16,220  ops/s
FileCount.commonsListStream         thrpt    3   232,402 ±  15,965  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   293,479 ±  21,683  ops/s
FileCount.fileListFiles             thrpt    3   436,419 ±   9,180  ops/s
FileCount.fileVisitorNew            thrpt    3  1729,877 ± 131,918  ops/s
FileCount.fileVisitorReuse          thrpt    3  1708,454 ±  26,471  ops/s
FileCount.nioFiles                  thrpt    3   493,156 ±  22,014  ops/s
FileCount.walkParallelNioFiles      thrpt    3  1096,457 ±  19,988  ops/s
FileCount.walkParallelToFile        thrpt    3  1089,594 ±  55,237  ops/s
FileCount.walkParallelToFile2       thrpt    3  1073,747 ± 517,849  ops/s
FileCount.walkToFile                thrpt    3   475,423 ± 178,759  ops/s
FileCount.walkToFile2               thrpt    3   479,015 ±  25,938  ops/s

JDK-25 - Desktop 9800X3D balanced
Benchmark                            Mode  Cnt     Score     Error  Units
FileCount.commonsList               thrpt    3   239,354 ±  10,282  ops/s
FileCount.commonsListStream         thrpt    3   240,636 ±   6,299  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   306,875 ±   8,231  ops/s
FileCount.fileListFiles             thrpt    3   453,164 ±  11,944  ops/s
FileCount.fileVisitorNew            thrpt    3  1783,586 ± 194,349  ops/s
FileCount.fileVisitorReuse          thrpt    3  1819,221 ± 270,672  ops/s
FileCount.nioFiles                  thrpt    3   518,457 ±  11,674  ops/s
FileCount.walkParallelNioFiles      thrpt    3  1329,485 ±  44,171  ops/s
FileCount.walkParallelToFile        thrpt    3  1348,739 ± 153,641  ops/s
FileCount.walkParallelToFile2       thrpt    3  1345,313 ±  21,330  ops/s
FileCount.walkToFile                thrpt    3   513,282 ±   9,873  ops/s
FileCount.walkToFile2               thrpt    3   515,998 ±  23,690  ops/s

JDK-21.0.8 - Desktop 9800X3D balanced
Benchmark                            Mode  Cnt     Score     Error  Units
FileCount.commonsList               thrpt    3   237,881 ±   8,390  ops/s
FileCount.commonsListStream         thrpt    3   237,787 ±  10,807  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   303,777 ±  12,198  ops/s
FileCount.fileListFiles             thrpt    3   449,208 ±   3,166  ops/s
FileCount.fileVisitorNew            thrpt    3  1774,183 ±  14,019  ops/s
FileCount.fileVisitorReuse          thrpt    3  1745,650 ± 183,838  ops/s
FileCount.nioFiles                  thrpt    3   511,299 ±  49,355  ops/s
FileCount.walkParallelNioFiles      thrpt    3  1333,990 ±  11,411  ops/s
FileCount.walkParallelToFile        thrpt    3  1327,384 ±  55,287  ops/s
FileCount.walkParallelToFile2       thrpt    3  1328,213 ±  34,307  ops/s
FileCount.walkToFile                thrpt    3   506,710 ±   8,780  ops/s
FileCount.walkToFile2               thrpt    3   510,161 ±  23,975  ops/s
*/
