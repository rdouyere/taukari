package net.airvantage.taukari.dao;

import java.util.List;

/**
 * General informations about a csv file.
 */
public class CsvInfo {

	private final String[] columns;
	private final int nbLines;
	private final List<String[]> samples;

	public CsvInfo(String[] columns, int nbLines, List<String[]> samples) {
		super();
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

}