package net.airvantage.taukari.dao;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import net.airvantage.taukari.model.Sample;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Stream-style writer for samples.
 */
public class SampleWriter implements Closeable {

	private final CSVWriter writer;
	private final boolean writeName;

	public SampleWriter(String[] headers, CSVWriter writer, boolean writeName) {
		this.writer = writer;
		this.writeName = writeName;
		if (writeName) {
			String[] headersWithName = Arrays.copyOf(headers, headers.length + 1);
			headersWithName[headers.length] = "name";
			writer.writeNext(headersWithName);
		} else {
			writer.writeNext(headers);
		}
	}

	public void writeSample(Sample s) {
		String[] output = null;
		if (writeName) {
			output = new String[s.getContent().length + 1];
			for (int i = 0; i < s.getContent().length; i++) {
				output[i] = Double.toString(s.getContent()[i]);
			}
			output[s.getContent().length] = s.getName();
		} else {
			output = new String[s.getContent().length];
			for (int i = 0; i < s.getContent().length; i++) {
				output[i] = Double.toString(s.getContent()[i]);
			}
		}

		writer.writeNext(output);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
