package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.system.Clock;
import org.briarproject.bramble.system.SystemClock;
import org.briarproject.bramble.test.TestDatabaseConfig;
import org.briarproject.bramble.test.UTest;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.briarproject.bramble.test.TestUtils.deleteTestDirectory;
import static org.briarproject.bramble.test.TestUtils.getMean;
import static org.briarproject.bramble.test.TestUtils.getMedian;
import static org.briarproject.bramble.test.TestUtils.getStandardDeviation;
import static org.briarproject.bramble.test.UTest.Z_CRITICAL_0_01;

public abstract class DatabaseOpenCloseComparisonTest
		extends DatabasePerformanceTest {

	private static final int RUNS = 100;

	abstract Database<Connection> createDatabase(boolean conditionA,
			DatabaseConfig databaseConfig, Clock clock);

	@Override
	protected void benchmark(String name,
			BenchmarkTask<Database<Connection>> task) throws Exception {
		List<BenchmarkResult> aResults, bResults;
		boolean aFirst = random.nextBoolean();
		if (aFirst) {
			aResults = benchmark(true, name, task);
			bResults = benchmark(false, name, task);
		} else {
			bResults = benchmark(false, name, task);
			aResults = benchmark(true, name, task);
		}
		writeResult(name, "open", getOpenDurations(aResults),
				getOpenDurations(bResults));
		writeResult(name, "run", getRunDurations(aResults),
				getRunDurations(bResults));
		writeResult(name, "close", getCloseDurations(aResults),
				getCloseDurations(bResults));
		writeResult(name, "size", getSizes(aResults), getSizes(bResults));
	}

	private List<BenchmarkResult> benchmark(boolean conditionA, String name,
			BenchmarkTask<Database<Connection>> task) throws Exception {
		createDatabase(conditionA);
		List<BenchmarkResult> results = new ArrayList<>(RUNS);
		for (int i = 0; i < RUNS; i++) {
			BenchmarkResult result = benchmark(conditionA, task);
			System.out.println(String.format("%s\t%s\t%,d\t%,d\t%,d\t%,d",
					name, conditionA, result.open, result.run, result.close,
					result.size));
			results.add(result);
		}
		return results;
	}

	private void createDatabase(boolean conditionA) throws Exception {
		deleteTestDirectory(getTestDir(conditionA));
		Database<Connection> db = openDatabase(conditionA);
		populateDatabase(db);
		db.close();
	}

	private BenchmarkResult benchmark(boolean conditionA,
			BenchmarkTask<Database<Connection>> task) throws Exception {
		// Open the DB
		long start = System.nanoTime();
		Database<Connection> db = openDatabase(conditionA);
		long open = System.nanoTime() - start;
		// Run the task
		start = System.nanoTime();
		task.run(db);
		long run = System.nanoTime() - start;
		// Replace a randomly chosen contact group
		replaceContactGroup(db);
		// Close the DB
		start = System.nanoTime();
		db.close();
		long close = System.nanoTime() - start;
		// Measure disk space
		long size = measureDiskSpace(getTestDir(conditionA));
		return new BenchmarkResult(open, run, close, size);
	}

	private File getTestDir(boolean conditionA) {
		if (conditionA) return new File(testDir, "a");
		else return new File(testDir, "b");
	}

	private Database<Connection> openDatabase(boolean conditionA)
			throws DbException {
		Database<Connection> db = createDatabase(conditionA,
				new TestDatabaseConfig(getTestDir(conditionA), MAX_SIZE),
				new SystemClock());
		db.open();
		return db;
	}

	private long measureDiskSpace(File f) throws IOException {
		if (f.isFile()) return f.length();
		if (f.isDirectory()) {
			File[] children = f.listFiles();
			if (children != null) {
				long total = 0;
				for (File child : children) total += measureDiskSpace(child);
				return total;
			}
		}
		return 0;
	}

	private List<Double> getOpenDurations(List<BenchmarkResult> results) {
		List<Double> durations = new ArrayList<>(results.size());
		for (BenchmarkResult r : results) durations.add((double) r.open);
		return durations;
	}

	private List<Double> getRunDurations(List<BenchmarkResult> results) {
		List<Double> durations = new ArrayList<>(results.size());
		for (BenchmarkResult r : results) durations.add((double) r.run);
		return durations;
	}

	private List<Double> getCloseDurations(List<BenchmarkResult> results) {
		List<Double> durations = new ArrayList<>(results.size());
		for (BenchmarkResult r : results) durations.add((double) r.close);
		return durations;
	}

	private List<Double> getSizes(List<BenchmarkResult> results) {
		List<Double> durations = new ArrayList<>(results.size());
		for (BenchmarkResult r : results) durations.add((double) r.size);
		return durations;
	}

	private void writeResult(String name, String stage, List<Double> aSamples,
			List<Double> bSamples) throws IOException {
		UTest.Result comparison =
				UTest.test(aSamples, bSamples, Z_CRITICAL_0_01);
		String result = String.format(
				"%s\t%s\t%,d\t%,d\t%,d\t%,d\t%,d\t%,d\t%s", name, stage,
				(long) getMean(aSamples), (long) getMedian(aSamples),
				(long) getStandardDeviation(aSamples),
				(long) getMean(bSamples), (long) getMedian(bSamples),
				(long) getStandardDeviation(bSamples), comparison);
		writeResult(result);
	}

	private static class BenchmarkResult {

		private final long open, run, close, size;

		private BenchmarkResult(long open, long run, long close, long size) {
			this.open = open;
			this.run = run;
			this.close = close;
			this.size = size;
		}
	}
}
