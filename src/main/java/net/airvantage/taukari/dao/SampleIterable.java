package net.airvantage.taukari.dao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import net.airvantage.taukari.model.Sample;
import au.com.bytecode.opencsv.CSVReader;

/**
 * {@link Iterable} of sample backed by a CSV file.
 */
public class SampleIterable implements Iterable<Sample> {

	private final String path;

	public SampleIterable(String path) {
		this.path = path;
	}

	@Override
	public Iterator<Sample> iterator() {
		try {
			return new SampleIterator(new CSVReader(new FileReader(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
