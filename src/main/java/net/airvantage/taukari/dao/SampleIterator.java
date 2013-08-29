package net.airvantage.taukari.dao;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import net.airvantage.taukari.model.Sample;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Stream-style sample reader backed by a CSV file.
 */
public class SampleIterator implements Iterator<Sample>, Closeable {

	private final CSVReader reader;
	private Sample next = null;

	public SampleIterator(CSVReader reader) {
		this.reader = reader;
		try {
			reader.readNext();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readNext();
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public Sample next() {
		Sample ret = next;
		readNext();
		return ret;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	private void readNext() {
		try {
			String[] readNext = reader.readNext();
			if (readNext != null && readNext.length > 0) {
				double[] doubles = new double[readNext.length];
				for (int i = 0; i < readNext.length; i++) {
					try {
						doubles[i] = Double.parseDouble(readNext[i]);
					} catch (NumberFormatException e) {

					}
				}
				next = new Sample(doubles);
			} else {
				next = null;
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
