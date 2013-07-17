package net.airvantage.taukari.processor;

import net.airvantage.taukari.dao.SampleIterable;
import net.airvantage.taukari.dao.SampleWriter;
import net.airvantage.taukari.model.Sample;

import org.apache.commons.io.IOUtils;

/**
 * Stream based vector normalization.
 */
public class Normalizer {

	public void normalizeSamples(int nbVariables, SampleIterable samples, SampleWriter writer) {

		try {
			LNorm[] norms = new LNorm[nbVariables];
			for (int i = 0; i < nbVariables; i++) {
				norms[i] = new LNorm();
			}

			// First loop
			for (Sample s : samples) {
				System.out.println(s);
				// For each variable
				for (int i = 0; i < nbVariables; i++) {
					norms[i].increment(s.getContent()[i]);
				}
			}

			for (int i = 0; i < nbVariables; i++) {
				norms[i].terminate();
			}

			for (Sample s : samples) {
				double[] out = new double[s.getContent().length];
				for (int i = 0; i < nbVariables; i++) {
					if (norms[i].getStdev() == 0) {
						out[i] = s.getContent()[i] - norms[i].getMean();
					} else {
						out[i] = (s.getContent()[i] - norms[i].getMean()) / norms[i].getStdev();
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
