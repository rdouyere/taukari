package net.airvantage.taukari.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.airvantage.taukari.dao.SampleIterable;
import net.airvantage.taukari.dao.SampleWriter;
import net.airvantage.taukari.model.Sample;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

/**
 * Processes a set of samples and generates clusters on some of the variables.
 */
public class Clusterer {

	/**
	 * Generates the clusters on the data using simple K-means++ algorithm.
	 * 
	 * Warning: even if we give {@link Iterable}, the implementation will load
	 * everything in memory.
	 * 
	 * @param nbClusters
	 *            the number of clusters to generate
	 * @param variables
	 *            all the variables of the samples (ordered as in the CSV)
	 * @param variablesToUse
	 *            the variables to use in the clustering
	 * @param samples
	 *            the {@link Iterable} on samples
	 * @param writer
	 *            the writer for samples with the clusters added. Can be null,
	 *            samples with cluster names will not be written to disk in the
	 *            case.
	 * @param centroidWriter
	 *            the writer for the centroids. Can be null, centroids will not
	 *            be written to disk in the case.
	 */
	public void clusterSamples(int nbClusters, String[] variables, String[] variablesToUse, SampleIterable samples,
			SampleWriter writer, SampleWriter centroidWriter) {

		int[] variableIndices = getVariableIndices(variables, variablesToUse);

		List<Point> inputList = new ArrayList<Point>();
		for (Sample s : samples) {
			inputList.add(new Point(s, variableIndices));
		}

		KMeansPlusPlusClusterer<Point> c = new KMeansPlusPlusClusterer<Point>(nbClusters, 10);
		List<CentroidCluster<Point>> cluster = c.cluster(inputList);

		try {
			int i = 0;
			for (CentroidCluster<Point> cl : cluster) {
				String clusterName = "cluster" + i;
				if (writer != null) {
					for (Point point : cl.getPoints()) {
						writer.writeSample(new Sample(point.s.getContent(), clusterName));
					}
				}
				double[] centroidData = cl.getCenter().getPoint();
				double[] copyOf = Arrays.copyOf(centroidData, centroidData.length + 1);
				copyOf[centroidData.length] = cl.getPoints().size();
				if (centroidWriter != null) {
					centroidWriter.writeSample(new Sample(copyOf, clusterName));
				}
				i++;
			}
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(centroidWriter);
		}
	}

	private int[] getVariableIndices(String[] variables, String[] variablesToUse) {
		int[] ret = null;

		if (variablesToUse != null && variablesToUse.length > 0) {
			Set<Integer> set = new HashSet<Integer>();
			for (String element : variablesToUse) {
				boolean found = false;
				int idx = 0;
				for (String var : variables) {
					if (var.equals(element)) {
						found = true;
						break;
					}
					idx++;
				}
				if (found) {
					set.add(idx);
				}
			}

			if (set.size() > 0) {
				ret = new int[set.size()];
				int i = 0;
				for (Integer idx : set) {
					ret[i] = idx;
					i++;
				}
			}

		}

		return ret;
	}

	private class Point implements Clusterable {

		private final Sample s;
		private final int[] indices;

		public Point(Sample s, int[] indices) {
			this.s = s;
			this.indices = indices;
		}

		@Override
		public double[] getPoint() {
			if (indices == null) {
				return s.getContent();
			} else {
				double[] arr = new double[indices.length];
				for (int i = 0; i < indices.length; i++) {
					arr[i] = s.getContent()[indices[i]];
				}
				return arr;
			}
		}
	}
}
