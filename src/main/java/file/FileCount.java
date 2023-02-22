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
	
	//private final Path path = Path.of("C:\\projects\\GalaxyObsUi\\dev\\heroes\\AhliObs.StormInterface");
	private final Path path = Path.of("D:\\GalaxyObsUi\\dev\\heroes\\AhliObs.StormInterface");
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
		if (val != 106) {
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
		if (val != 106) {
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

JDK-17 - desktop 4+4 cores
Benchmark                            Mode  Cnt     Score     Error  Units
FileCount.commonsList               thrpt    3   202,416 ±  55,389  ops/s
FileCount.commonsListStream         thrpt    3   198,928 ±  45,240  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   212,674 ±  20,996  ops/s
FileCount.fileListFiles             thrpt    3   306,973 ±  23,200  ops/s
FileCount.fileVisitorNew            thrpt    3  1018,713 ±  26,616  ops/s
FileCount.fileVisitorReuse          thrpt    3  1002,946 ± 137,545  ops/s
FileCount.nioFiles                  thrpt    3   336,743 ±  47,296  ops/s
FileCount.walkParallelNioFiles      thrpt    3   362,599 ±  42,196  ops/s
FileCount.walkParallelToFile        thrpt    3   342,325 ±  30,531  ops/s
FileCount.walkParallelToFile2       thrpt    3   345,580 ±  37,512  ops/s
FileCount.walkToFile                thrpt    3   316,545 ±  19,916  ops/s
FileCount.walkToFile2               thrpt    3   322,524 ±  15,077  ops/s

JDK 18-ea+26 - desktop 4+4 cores
Benchmark                            Mode  Cnt     Score     Error  Units
FileCount.commonsList               thrpt    3   185,779 ± 104,183  ops/s
FileCount.commonsListStream         thrpt    3   197,667 ±  42,522  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   211,115 ±  13,087  ops/s
FileCount.fileListFiles             thrpt    3   300,536 ±   9,023  ops/s
FileCount.fileVisitorNew            thrpt    3  1001,923 ± 123,345  ops/s
FileCount.fileVisitorReuse          thrpt    3  1009,001 ±  43,050  ops/s
FileCount.nioFiles                  thrpt    3   335,463 ±  60,877  ops/s
FileCount.walkParallelNioFiles      thrpt    3   360,747 ±  78,603  ops/s
FileCount.walkParallelToFile        thrpt    3   341,309 ±  36,202  ops/s
FileCount.walkParallelToFile2       thrpt    3   345,039 ±  40,918  ops/s
FileCount.walkToFile                thrpt    3   317,564 ±   7,350  ops/s
FileCount.walkToFile2               thrpt    3   319,553 ±   5,329  ops/s

JDK 18.0.2.1 - desktop 4+4 cores - balanced cpu
Benchmark                            Mode  Cnt     Score     Error  Units
FileCount.commonsList               thrpt    3   182,386 ±  81,562  ops/s
FileCount.commonsListStream         thrpt    3   184,167 ±  49,093  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   203,298 ±  17,290  ops/s
FileCount.fileListFiles             thrpt    3   292,470 ±   6,293  ops/s
FileCount.fileVisitorNew            thrpt    3   989,957 ±  36,133  ops/s
FileCount.fileVisitorReuse          thrpt    3  1009,643 ± 145,371  ops/s
FileCount.nioFiles                  thrpt    3   313,640 ±  73,673  ops/s
FileCount.walkParallelNioFiles      thrpt    3   341,984 ±  67,899  ops/s
FileCount.walkParallelToFile        thrpt    3   323,575 ±  59,053  ops/s
FileCount.walkParallelToFile2       thrpt    3   322,039 ±  35,663  ops/s
FileCount.walkToFile                thrpt    3   303,592 ±   7,760  ops/s
FileCount.walkToFile2               thrpt    3   302,763 ±   9,321  ops/s

JDK 19.0.2 - desktop 4+4 cores - balanced cpu
Benchmark                            Mode  Cnt     Score    Error  Units
FileCount.commonsList               thrpt    3    63,371 ± 33,371  ops/s
FileCount.commonsListStream         thrpt    3    65,016 ±  3,412  ops/s
FileCount.commonsListWithoutFilter  thrpt    3   102,598 ±  8,126  ops/s
FileCount.fileListFiles             thrpt    3   121,203 ± 48,474  ops/s
FileCount.fileVisitorNew            thrpt    3  1013,030 ± 29,474  ops/s
FileCount.fileVisitorReuse          thrpt    3  1039,878 ± 13,825  ops/s
FileCount.nioFiles                  thrpt    3   125,676 ±  2,749  ops/s
FileCount.walkParallelNioFiles      thrpt    3   359,246 ± 38,702  ops/s
FileCount.walkParallelToFile        thrpt    3   353,187 ± 45,692  ops/s
FileCount.walkParallelToFile2       thrpt    3   353,752 ± 44,418  ops/s
FileCount.walkToFile                thrpt    3   123,300 ±  9,472  ops/s
FileCount.walkToFile2               thrpt    3   122,743 ±  5,391  ops/s
*/
