package net.airvantage.taukari.dao;

import java.util.Arrays;
import java.util.List;

/**
 * Data structure for general informations about a csv file.
 */
public class CsvInfo {

	/** The number of columns (found in the header) */
	private final String[] columns;
	/** The number of line (header excluded) */
	private final int nbLines;
	/** Some of the lines, randomly sampled */
	private final List<String[]> samples;

	public CsvInfo(String[] columns, int nbLines, List<String[]> samples) {
		this.columns = columns;
		this.nbLines = nbLines;
		this.samples = samples;
	}

	public String[] getColumns() {
		return columns;
	}

	public int getNbLines() {
		return nbLines;
	}

	public List<String[]> getSamples() {
		return samples;
	}

	@Override
	public String toString() {
		return "CsvInfo [columns=" + Arrays.toString(columns) + ", nbLines=" + nbLines + ", samples=" + samples + "]";
	}

}