package net.airvantage.taukari.model;

import java.util.Arrays;

/**
 * Represents a sample as a set of variable values.
 * <p>
 * When manipulating lists of samples, the expected convention is that, for each
 * sample, the variables are in the same order. This allows to process variable
 * vectors.
 * </p>
 */
public class Sample {

	private final double[] content;

	public Sample(double[] content) {
		this.content = content;
	}

	public double[] getContent() {
		return content;
	}

	@Override
	public String toString() {
		return Arrays.toString(content);
	}

}
