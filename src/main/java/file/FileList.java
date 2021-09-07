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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Warmup(iterations = 2, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgs = { "-Xms2G", "-Xmx2G" })
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class FileList {
	
	private final Path path = Path.of("C:\\projects\\GalaxyObsUi\\dev\\heroes\\AhliObs.StormInterface");
	private final FileList.FileListingVisitor reusableFileVisitor = new FileList.FileListingVisitor();
	
	public static void main(final String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder().include(".*" + FileList.class.getSimpleName() + ".*").forks(1).build();
		new Runner(opt).run();
	}
	
	private static List<File> searchInListFiles(final File directory) {
		final List<File> result = new ArrayList<>();
		final File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0, len = files.length; i < len; ++i) {
				if (files[i].isDirectory()) {
					result.addAll(searchInListFiles(files[i]));
				} else {
					result.add(files[i]);
				}
			}
		}
		return result;
	}
	
	@Benchmark
	public void walkParallelToFile(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final List<File> files = walk.parallel().map(Path::toFile).filter(File::isFile).toList();
			bh.consume(checkResultFile(files));
		}
	}
	
	private static List<File> checkResultFile(final List<File> files) {
		if (files.size() != 106) {
			throw new IllegalArgumentException("Wrong result! Received " + files.size());
		}
		return files;
	}
	
	@Benchmark
	public void walkParallelToFile2(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final List<Path> paths = walk.parallel().filter(p -> p.toFile().isFile()).toList();
			bh.consume(checkResultPath(paths));
		}
	}
	
	private static List<Path> checkResultPath(final List<Path> files) {
		if (files.size() != 106) {
			throw new IllegalArgumentException("Wrong result! Received " + files.size());
		}
		return files;
	}
	
	@Benchmark
	public void walkParallelNioFiles(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final List<Path> paths = walk.parallel().filter(Files::isRegularFile).toList();
			bh.consume(checkResultPath(paths));
		}
	}
	
	@Benchmark
	public void walkToFile(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final List<File> files = walk.map(Path::toFile).filter(File::isFile).toList();
			bh.consume(checkResultFile(files));
		}
	}
	
	@Benchmark
	public void walkToFile2(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final List<Path> paths = walk.filter(p -> p.toFile().isFile()).toList();
			bh.consume(checkResultPath(paths));
		}
	}
	
	@Benchmark
	public void nioFiles(final Blackhole bh) throws IOException {
		try (final Stream<Path> walk = Files.walk(path)) {
			final List<Path> paths = walk.filter(Files::isRegularFile).toList();
			bh.consume(checkResultPath(paths));
		}
	}
	
	@Benchmark
	public void commonsList(final Blackhole bh) {
		final List<File> files =
				(List<File>) FileUtils.listFilesAndDirs(path.toFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE);
		for (int i = files.size() - 1; i >= 0; --i) {
			if (files.get(i).isDirectory()) {
				files.remove(i);
			}
		}
		bh.consume(checkResultFile(files));
	}
	
	@Benchmark
	public void commonsListStream(final Blackhole bh) {
		final List<File> files = FileUtils.listFilesAndDirs(path.toFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
				.stream()
				.filter(File::isFile)
				.toList();
		bh.consume(checkResultFile(files));
	}
	
	@Benchmark
	public void commonsListWithoutFilter(final Blackhole bh) {
		final List<File> files = (List<File>) FileUtils.listFiles(path.toFile(), null, true);
		bh.consume(checkResultFile(files));
	}
	
	@Benchmark
	public void fileListFiles(final Blackhole bh) {
		final List<File> files = searchInListFiles(path.toFile());
		bh.consume(checkResultFile(files));
	}
	
	@Benchmark
	public void fileVisitorNew(final Blackhole bh) throws IOException {
		final FileList.FileListingVisitor fileVisitor = new FileList.FileListingVisitor();
		Files.walkFileTree(path, fileVisitor);
		final List<Path> paths = fileVisitor.getFilePaths();
		bh.consume(checkResultPath(paths));
	}
	
	@Benchmark
	public void fileVisitorReuse(final Blackhole bh) throws IOException {
		reusableFileVisitor.resetFiles();
		Files.walkFileTree(path, reusableFileVisitor);
		final List<Path> paths = reusableFileVisitor.getFilePaths();
		bh.consume(checkResultPath(paths));
	}
	
	private static class FileListingVisitor extends SimpleFileVisitor<Path> {
		private List<Path> paths = new ArrayList<>();
		
		@Override
		public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
			paths.add(file);
			return FileVisitResult.CONTINUE;
		}
		
		public List<Path> getFilePaths() {
			return paths;
		}
		
		public void resetFiles() {
			paths = new ArrayList<>();
		}
	}
}
/*
JDK-17
Benchmark                           Mode  Cnt    Score    Error  Units
FileList.commonsList               thrpt    3  145,295 ± 41,899  ops/s
FileList.commonsListStream         thrpt    3  146,067 ± 40,383  ops/s
FileList.commonsListWithoutFilter  thrpt    3  146,216 ± 18,926  ops/s
FileList.fileListFiles             thrpt    3  206,113 ±  7,691  ops/s
FileList.fileVisitorNew            thrpt    3  708,607 ± 56,160  ops/s
FileList.fileVisitorReuse          thrpt    3  693,937 ± 69,826  ops/s
FileList.nioFiles                  thrpt    3  224,719 ± 14,395  ops/s
FileList.walkParallelNioFiles      thrpt    3  239,753 ± 47,667  ops/s
FileList.walkParallelToFile        thrpt    3  224,530 ± 30,553  ops/s
FileList.walkParallelToFile2       thrpt    3  226,469 ± 72,374  ops/s
FileList.walkToFile                thrpt    3  216,895 ± 13,613  ops/s
FileList.walkToFile2               thrpt    3  219,540 ± 13,080  ops/s
*/
