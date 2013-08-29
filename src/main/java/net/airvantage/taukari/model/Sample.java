package net.airvantage.taukari.model;

import java.util.Arrays;

/**
 * Represents a sample as a set of variable values.
 * <p>
 * When manipulating lists of samples, the expected convention is that, for each
 * sample, the variables are in the same order. This allows to process variable
 * vectors.
 * </p>
 * <p>
 * Current definition of sample is very restrictive as only doubles are allowed
 * with a name. We should be more open. Maybe stay on doubles for variables but
 * with as many side content as we want...
 * </p>
 */
public class Sample {

	private final double[] content;
	private final String name;

	public Sample(double[] content, String name) {
		this.content = content;
		this.name = name;
	}

	public Sample(double[] content) {
		this(content, null);
	}

	public double[] getContent() {
		return content;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (name != null) {
			sb.append(name);
		}
		if (content != null) {
			sb.append(Arrays.toString(content));
		}
		return sb.toString();
	}

}
