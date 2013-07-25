package net.airvantage.taukari.dao;

import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;
import static au.com.bytecode.opencsv.CSVWriter.NO_QUOTE_CHARACTER;

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

/**
 * Exposes CSV manipulations methods.
 */
public class CsvManager {

	/**
	 * Opens a {@link SampleIterable} on a file.
	 * 
	 * @param path
	 *            the full path to the file to read.
	 * @return a {@link SampleIterable}.
	 */
	public SampleIterable getSampleIterable(String path) {
		return new SampleIterable(path);
	}

	/**
	 * Opens a {@link SampleWriter} ready to write samples in a file. Will not
	 * write the "name" column.
	 * 
	 * @param headers
	 *            the headers to write in the file
	 * @param path
	 *            the full path of the file to write in
	 * @return a {@link SampleWriter}
	 * @throws IOException
	 *             if the named file exists but is a directory rather than a
	 *             regular file, does not exist but cannot be created, or cannot
	 *             be opened for any other reason
	 */
	public SampleWriter getSampleWriter(String[] headers, String path) throws IOException {
		return new SampleWriter(headers, new CSVWriter(new FileWriter(path), DEFAULT_SEPARATOR, NO_QUOTE_CHARACTER),
				false);
	}

	/**
	 * Opens a {@link SampleWriter} ready to write samples in a file.
	 * 
	 * 
	 * @param headers
	 *            the headers to write in the file
	 * @param path
	 *            the full path of the file to write in
	 * @param writeName
	 *            whether to write the "name" column or not.
	 * @return a {@link SampleWriter}
	 * @throws IOException
	 *             if the named file exists but is a directory rather than a
	 *             regular file, does not exist but cannot be created, or cannot
	 *             be opened for any other reason
	 */
	public SampleWriter getSampleWriter(String[] headers, String path, boolean writeName) throws IOException {
		return new SampleWriter(headers, new CSVWriter(new FileWriter(path), DEFAULT_SEPARATOR, NO_QUOTE_CHARACTER),
				writeName);
	}

	/**
	 * Copies a CSV file. It can filter columns and copy only a subset of the
	 * lines of the input file.
	 * 
	 * @param sourcePath
	 *            the full path of the source file
	 * @param destPath
	 *            the full path of the destination file
	 * @param columns
	 *            the columns to copy. All columns are copied if null.
	 * @param sampling
	 *            a ratio of samples to copy. 0 means no sample, 1 or more or
	 *            null means all samples.
	 */
	public void copyCSV(String sourcePath, String destPath, Set<String> columns, Double sampling) {

		Random rand = new Random();

		CSVReader reader = null;
		CSVWriter writer = null;
		try {
			reader = new CSVReader(new FileReader(sourcePath));
			writer = new CSVWriter(new FileWriter(destPath), DEFAULT_SEPARATOR, NO_QUOTE_CHARACTER);

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

	/**
	 * Opens and inspect a CSV file. Returns a {@link CsvInfo} filled with what
	 * was found in the file.
	 * 
	 * @param path
	 *            the full path of the file.
	 * @param nbSamples
	 *            the number of lines to return on the {@link CsvInfo}.
	 * @return null if the file cannot be found or is not a valid CSV.
	 */
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

	/**
	 * Writes a CSV file filled with random numeric values (double between 0 and
	 * 1).
	 * 
	 * @param path
	 *            the full path of the file
	 * @param nbColumns
	 *            the number of columns to generate
	 * @param nbLines
	 *            the number of lines to generate.
	 */
	public void generateRandomCSV(String path, int nbColumns, int nbLines) {
		Random rand = new Random();
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter(path), DEFAULT_SEPARATOR, NO_QUOTE_CHARACTER);
			String[] line = new String[nbColumns];
			for (int i = 0; i <= nbLines; i++) {
				for (int j = 0; j < nbColumns; j++) {
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
