package net.airvantage.taukari.processor;

import net.airvantage.taukari.dao.SampleIterable;
import net.airvantage.taukari.dao.SampleWriter;
import net.airvantage.taukari.model.Sample;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

/**
 * Stream based vector normalization.
 */
public class Normalizer {

	/**
	 * Normalizes a stream of samples.
	 * <p>
	 * It requires to scan the the sample stream twice. It is expected that the
	 * stream content does not change in between!
	 * </p>
	 * 
	 * @param nbVariables
	 *            the number of variables of the samples
	 * @param samples
	 *            the input sample stream (which will be traveled twice)
	 * @param writer
	 *            the output, normalize samples.
	 */
	public void normalizeSamples(int nbVariables, SampleIterable samples, SampleWriter writer) {

		try {
			// First loop: compute standard deviation & mean
			MultivariateSummaryStatistics mss = new MultivariateSummaryStatistics(nbVariables, true);
			for (Sample s : samples) {
				// For each variable
				mss.addValue(s.getContent());
			}

			// Second loop compute normalized values
			for (Sample s : samples) {
				double[] out = new double[s.getContent().length];
				for (int i = 0; i < nbVariables; i++) {
					if (mss.getStandardDeviation()[i] == 0) {
						out[i] = s.getContent()[i] - mss.getMean()[i];
					} else {
						out[i] = (s.getContent()[i] - mss.getMean()[i]) / mss.getStandardDeviation()[i];
					}
				}
				Sample sout = new Sample(out);
				writer.writeSample(sout);
			}
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
