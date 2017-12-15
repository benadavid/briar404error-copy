package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.system.Clock;
import org.briarproject.bramble.system.SystemClock;
import org.briarproject.bramble.test.TestDatabaseConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;

import static org.briarproject.bramble.test.TestUtils.deleteTestDirectory;

public abstract class DatabaseOpenCloseComparisonTest
		extends DatabasePerformanceTest {

	private static final int RUNS = 10;

	abstract Database<Connection> createDatabase(boolean conditionA,
			DatabaseConfig databaseConfig, Clock clock);

	@Override
	protected void benchmark(String name,
			BenchmarkTask<Database<Connection>> task) throws Exception {
		benchmark(true, name, task);
		benchmark(false, name, task);
	}

	private void benchmark(boolean conditionA, String name,
			BenchmarkTask<Database<Connection>> task) throws Exception {
		createDatabase(conditionA);
		for (int i = 0; i < RUNS; i++)
			writeResult(name, conditionA, benchmark(conditionA, task));
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

	private void writeResult(String name, boolean conditionA,
			BenchmarkResult res) throws IOException {
		String result = String.format("%s\t%s\t%,d\t%,d\t%,d\t%,d", name,
				conditionA, res.open, res.run, res.close, res.size);
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
