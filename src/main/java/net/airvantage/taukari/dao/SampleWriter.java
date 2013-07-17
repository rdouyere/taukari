package net.airvantage.taukari.dao;

import java.io.Closeable;
import java.io.IOException;

import net.airvantage.taukari.model.Sample;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Stream-style writer for samples.
 */
public class SampleWriter implements Closeable {

	private final CSVWriter writer;

	public SampleWriter(String[] headers, CSVWriter writer) {
		this.writer = writer;
		writer.writeNext(headers);
	}

	public void writeSample(Sample s) {
		String[] output = new String[s.getContent().length];
		for (int i = 0; i < s.getContent().length; i++) {
			output[i] = Double.toString(s.getContent()[i]);
		}
		writer.writeNext(output);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
