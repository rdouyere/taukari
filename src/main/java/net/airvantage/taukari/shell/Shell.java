package net.airvantage.taukari.shell;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import net.airvantage.taukari.dao.CsvInfo;
import net.airvantage.taukari.dao.DataDao;
import net.airvantage.taukari.dao.RunDao;
import net.airvantage.taukari.processor.Normalizer;
import net.airvantage.taukari.shell.ShellView.ReturnViewConvertor;
import asg.cliche.Command;
import asg.cliche.OutputConverter;
import asg.cliche.Param;
import asg.cliche.ShellFactory;

import com.google.common.collect.Sets;

/**
 * Interactive shell to load, process and in a limited fashion display data.
 */
public class Shell {

	public static final OutputConverter[] CLI_OUTPUT_CONVERTERS = { new ReturnViewConvertor() };

	private final String rootDirectory;

	private final RunDao runDao;

	private final DataDao dataDao;

	private final Normalizer normalizer;

	private String run;

	public Shell(String rootDirectory, RunDao runDao, DataDao dataDao, Normalizer normalizer) {
		this.rootDirectory = rootDirectory;
		this.runDao = runDao;
		this.dataDao = dataDao;
		this.normalizer = normalizer;
	}

	public static void main(String[] args) throws IOException {
		String rootDirectory = "/tmp/mining"; // TODO
		Shell cli = new Shell(rootDirectory, new RunDao(rootDirectory), new DataDao(), new Normalizer());
		ShellFactory.createConsoleShell("", null, cli).commandLoop();
	}

	@Command(abbrev = "ls", description = "lists existing runs")
	public ShellView list() {
		return new ShellView(runDao.list(rootDirectory));
	}

	@Command(abbrev = "lr", description = "list content of a run")
	public ShellView listRun() {
		ShellView rv = new ShellView();
		listRun(null, rv);
		return rv;
	}

	@Command(abbrev = "lr", description = "list content of a run")
	public ShellView listRun(@Param(name = "runName", description = "the run name") String runName) {
		ShellView rv = new ShellView();
		listRun(runName, rv);
		return rv;
	}

	private void listRun(String runName, ShellView rv) {
		if (runName == null || "@".equals(runName)) {
			if (run == null) {
				rv.addErr("no opened run, could not use relative notation");
			} else {
				rv.addMsg(runDao.list(rootDirectory + "/" + run));
			}
		} else {
			rv.addMsg(runDao.list(rootDirectory + "/" + runName));
		}
	}

	@Command(abbrev = "o", description = "opens a existing run")
	public ShellView open(@Param(name = "name", description = "the run name") String name) {
		ShellView rv = new ShellView();

		unload(rv);

		if (runDao.exists(name)) {
			rv.addMsg("opened: " + name);
			run = name;
		} else {
			rv.addErr("failed to open run");
		}

		return rv;
	}

	@Command
	public ShellView close() {
		ShellView rv = new ShellView();
		unload(rv);
		return rv;
	}

	private void unload(ShellView rv) {
		if (run != null) {
			rv.addMsg("unloaded: " + run);
			run = null;
		}
	}

	@Command
	public ShellView show() {
		ShellView rv = new ShellView();

		if (run == null) {
			rv.addErr("no run to show!");
		} else {
			rv.addMsg(run);
		}

		return rv;
	}

	@Command
	public ShellView create(String name) {
		ShellView rv = new ShellView();

		unload(rv);

		// check
		if (runDao.exists(name)) {
			rv.addErr("already existing run");
		} else {
			run = name;
			runDao.create(name);
			rv.addMsg("created: " + run);
			rv.addMsg("opened: " + run);
		}

		return rv;
	}

	@Command
	public ShellView normalized() {
		ShellView rv = new ShellView();

		String input = rootDirectory + "/" + run + "/input.csv";
		String norm = rootDirectory + "/" + run + "/norm.csv";

		CsvInfo inputInfos = dataDao.inspectCSV(input, 0);

		try {
			normalizer.normalizeSamples(inputInfos.getColumns().length, //
					dataDao.getSampleIterable(input),//
					dataDao.getSampleWriter(inputInfos.getColumns(), norm));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rv.addErr(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			rv.addErr(e.getMessage());
		}

		return rv;
	}

	// ----------------- inspect-csv ---------------

	@Command(abbrev = "inspect", description = "inspects a CSV file, giving some general informations")
	public ShellView inspectCsv(//
			@Param(name = "path", description = "path to the CSV file to inspect") String path) {
		ShellView rv = new ShellView();
		inspectCsv(path, 0, rv);
		return rv;
	}

	@Command(abbrev = "inspect", description = "inspects a CSV file, giving some general informations")
	public ShellView inspectCsv(//
			@Param(name = "path", description = "path to the CSV file to inspect") String path,//
			@Param(name = "naSamples", description = "number of samples to display") int nbSamples) {
		ShellView rv = new ShellView();
		inspectCsv(path, nbSamples, rv);
		return rv;
	}

	private void inspectCsv(String path, int nbSamples, ShellView rv) {
		String path_ = path(path, rv);
		if (path_ != null) {
			CsvInfo csvInfos = dataDao.inspectCSV(path_, nbSamples);
			if (csvInfos == null) {
				rv.addErr("could not inspect csv file");
			} else {
				rv.addMsg("Number of lines: " + csvInfos.getNbLines());
				rv.addMsg("Columns:  " + Arrays.toString(csvInfos.getColumns()));
				if (csvInfos.getSamples() != null) {
					for (String[] sample : csvInfos.getSamples()) {
						rv.addMsg("Sample:  " + Arrays.toString(sample));

					}
				}
			}
		}
	}

	// ----------------- load-csv ---------------

	@Command(abbrev = "load")
	public ShellView loadCsv(String name) {
		ShellView rv = new ShellView();
		loadCsv(name, null, null, rv);
		return rv;
	}

	@Command(abbrev = "load")
	public ShellView loadCsv(String name, double sampleRate) {
		ShellView rv = new ShellView();
		loadCsv(name, null, sampleRate, rv);
		return rv;
	}

	@Command(abbrev = "load")
	public ShellView loadCsv(String name, String variables, double sampleRate) {
		ShellView rv = new ShellView();

		Set<String> columns = null;
		if (variables != null) {
			String[] split = variables.split(",");
			if (split != null && split.length > 0) {
				columns = Sets.newHashSet(split);
			}
		}

		loadCsv(name, columns, sampleRate, rv);
		return rv;
	}

	private void loadCsv(String name, Set<String> columns, Double sampleRate, ShellView rv) {
		String destPath = rootDirectory + "/" + run + "/input.csv";

		if (run == null) {
			rv.addErr("cannot load if no current run. Create or open one");
		} else {
			dataDao.copyCSV(name, destPath, columns, sampleRate);
			inspectCsv(destPath, 0, rv);
		}

	}

	// ----------------- load-random ---------------

	@Command(abbrev = "loadr")
	public ShellView loadRandom(int nbVariables, int nbSamples) {
		ShellView rv = new ShellView();

		String destPath = rootDirectory + "/" + run + "/input.csv";

		if (run == null) {
			rv.addErr("cannot load if no current run. Create or open one");
		} else {
			dataDao.generateRandomCSV(destPath, nbVariables, nbSamples);
			inspectCsv(destPath, 0, rv);
		}

		return rv;
	}

	private String path(String path, ShellView rv) {
		String ret = path;

		if (path.startsWith("@")) {
			if (run == null) {
				ret = null;
				rv.addErr("relative path requires an open run");
			} else {
				ret = rootDirectory + "/" + run + "/" + path.substring(1, path.length());
			}
		}

		return ret;
	}
}
