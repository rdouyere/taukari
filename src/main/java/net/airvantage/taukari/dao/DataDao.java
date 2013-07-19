package net.airvantage.taukari.dao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class DataDao {

	public SampleIterable getSampleIterable(String path) throws FileNotFoundException {
		return new SampleIterable(path);
	}

	public SampleWriter getSampleWriter(String[] headers, String path, boolean writeName) throws IOException {
		return new SampleWriter(headers, new CSVWriter(new FileWriter(path), ',', CSVWriter.NO_QUOTE_CHARACTER),
				writeName);
	}

	public SampleWriter getSampleWriter(String[] headers, String path) throws IOException {
		return new SampleWriter(headers, new CSVWriter(new FileWriter(path), ',', CSVWriter.NO_QUOTE_CHARACTER), false);
	}

	/**
	 * Creates a copy of a source CSV. This is done with streams.
	 * <p>
	 * It can filter content on a list of columns and/or a sampling of lines.
	 * </p>
	 * 
	 * @param sourcePath
	 * @param destPath
	 * @param columns
	 * @param sampling
	 */
	public void copyCSV(String sourcePath, String destPath, Set<String> columns, Double sampling) {

		Random rand = new Random();

		CSVReader reader = null;
		CSVWriter writer = null;
		try {
			reader = new CSVReader(new FileReader(sourcePath));
			writer = new CSVWriter(new FileWriter(destPath), ',', CSVWriter.NO_QUOTE_CHARACTER);

			// Handling header
			List<Integer> selectedColumns = new ArrayList<Integer>();
			String[] inputLine = reader.readNext();
			for (int i = 0; i < inputLine.length; i++) {
				if (columns == null || columns.contains(inputLine[i])) {
					selectedColumns.add(i);
				}
			}

			// Write
			boolean isHeaderLine = true;
			if (selectedColumns.size() > 0) {
				do {
					if (isHeaderLine || sampling == null || rand.nextDouble() < sampling) {
						String[] outputLine = new String[selectedColumns.size()];
						boolean okLine = true;
						for (int i = 0; i < selectedColumns.size(); i++) {
							if (inputLine.length > selectedColumns.get(i)) {
								outputLine[i] = inputLine[selectedColumns.get(i)];
							} else {
								okLine = false;
								break;
							}
						}
						if (okLine) {
							writer.writeNext(outputLine);
						}
					}
					isHeaderLine = false;
				} while ((inputLine = reader.readNext()) != null);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
		}

	}

	public CsvInfo inspectCSV(String path, int nbSamples) {
		CsvInfo ret = null;

		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(path));
			String[] columns = reader.readNext();
			List<String[]> samples = new ArrayList<String[]>();
			int nbLines = 0;
			int nb = 0;
			String[] nextLine = null;
			while ((nextLine = reader.readNext()) != null) {
				if (nb < nbSamples) {
					samples.add(nextLine);
					nb++;
				}
				nbLines++;
			}
			ret = new CsvInfo(columns, nbLines, samples);
		} catch (IOException e) {
		} finally {
			IOUtils.closeQuietly(reader);
		}

		return ret;
	}

	public void generateRandomCSV(String path, int nbVariables, int nbSamples) {
		Random rand = new Random();
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(path), ',', CSVWriter.NO_QUOTE_CHARACTER);
			String[] line = new String[nbVariables];
			for (int i = 0; i <= nbSamples; i++) {
				for (int j = 0; j < nbVariables; j++) {
					if (i == 0) {
						// Header line
						line[j] = "var" + j;
					} else {
						// Content line
						line[j] = Double.toString(rand.nextDouble());
					}
				}
				writer.writeNext(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
