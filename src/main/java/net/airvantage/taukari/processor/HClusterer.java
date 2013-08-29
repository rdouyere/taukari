package net.airvantage.taukari.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.airvantage.taukari.dao.SampleIterable;
import net.airvantage.taukari.dao.SampleWriter;
import net.airvantage.taukari.model.Sample;
import ch.usi.inf.sape.hac.HierarchicalAgglomerativeClusterer;
import ch.usi.inf.sape.hac.agglomeration.AgglomerationMethod;
import ch.usi.inf.sape.hac.agglomeration.CompleteLinkage;
import ch.usi.inf.sape.hac.dendrogram.Dendrogram;
import ch.usi.inf.sape.hac.dendrogram.DendrogramBuilder;
import ch.usi.inf.sape.hac.dendrogram.DendrogramNode;
import ch.usi.inf.sape.hac.dendrogram.ObservationNode;
import ch.usi.inf.sape.hac.experiment.DissimilarityMeasure;
import ch.usi.inf.sape.hac.experiment.Experiment;

/**
 * Hierarchical clustering.
 * <p>
 * The results of the clustering pass is a binary tree. Leaves are the input
 * data and nodes are clusters.
 * </p>
 * <p>
 * The output contains the full tree (clusters and input data) with additional
 * informations relatives to the clustering: level, number of leaves under the
 * node, radius...).
 * </p>
 * <p>
 * Beware that the computation is in O(n^2) complexity.
 * </p>
 */
public class HClusterer {

	/**
	 * Generates the hierarchical cluster.
	 * 
	 * Warning: even if we give {@link Iterable}, the implementation will load
	 * everything in memory.
	 * 
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
	 */
	public void clusterSamples(String[] variables, String[] variablesToUse, SampleIterable samples, SampleWriter writer) {

		int[] variableIndices = getVariableIndices(variables, variablesToUse);

		SampleExperiment experiment = new SampleExperiment(samples, variableIndices);
		SampleDissimilarityMeasure dissimilarityMeasure = new SampleDissimilarityMeasure();
		AgglomerationMethod agglomerationMethod = new CompleteLinkage();

		// long start = System.currentTimeMillis();

		DendrogramBuilder dendrogramBuilder = new DendrogramBuilder(experiment.getNumberOfObservations());
		HierarchicalAgglomerativeClusterer clusterer = new HierarchicalAgglomerativeClusterer(experiment,
				dissimilarityMeasure, agglomerationMethod);
		clusterer.cluster(dendrogramBuilder);

		Dendrogram dendrogram = dendrogramBuilder.getDendrogram();
		DendrogramNode root = dendrogram.getRoot();

		// System.out.println("took: " + (System.currentTimeMillis() - start) +
		// "ms.");

		walk(root, experiment, writer, 0, "_", new ArrayList<String>());

	}

	/**
	 * Walks the dendogram in a recursive way and writes content to the
	 * {@link SampleWriter}.
	 */
	private WalkRes walk(DendrogramNode current, SampleExperiment exp, SampleWriter writer, int depth, String name,
			List<String> parents) {

		DendrogramNode left = current.getLeft();
		DendrogramNode right = current.getRight();

		boolean isLeaf = false;
		if (left == null && right == null) {
			isLeaf = true;
		}

		WalkRes leftRes = null;
		WalkRes rightRes = null;

		List<String> parentsSub = new ArrayList<String>(parents);
		parentsSub.add(name);
		if (left != null) {
			leftRes = walk(left, exp, writer, depth + 1, name + "l", parentsSub);
		}
		if (right != null) {
			rightRes = walk(right, exp, writer, depth + 1, name + "r", parentsSub);
		}

		int subCounts = 0;
		List<double[]> subData = new ArrayList<double[]>();

		if (isLeaf) {
			int obs = ((ObservationNode) current).getObservation();
			subCounts = 1;
			subData.add(exp.getSample(obs).getContent());
		} else {
			if (leftRes != null) {
				subCounts += leftRes.getNbLeaves();
				subData.addAll(leftRes.getLeavesData());
			}
			if (rightRes != null) {
				subCounts += rightRes.getNbLeaves();
				subData.addAll(rightRes.getLeavesData());
			}
		}

		WalkRes res = new WalkRes(subCounts, subData);

		double[] data = res.getLeavesDataAverages();
		double radius = res.getRadius();
		double[] sampleContent = new double[data.length + 4];
		for (int i = 0; i < data.length; i++) {
			sampleContent[i] = data[i];
		}

		sampleContent[data.length] = depth;
		sampleContent[data.length + 1] = subCounts;
		sampleContent[data.length + 2] = radius;
		sampleContent[data.length + 3] = isLeaf ? 1 : 0;

		writer.writeSample(new Sample(sampleContent, name));

		return res;
	}

	/**
	 * Represents a node of the hierarchical cluster. Contains aggregate
	 * informations about lower levels (such as the number of leaves and the
	 * data associated to those leaves).
	 */
	private class WalkRes {

		private final int nbLeaves;
		private final List<double[]> leavesData;
		private int nbData;

		public WalkRes(int nbLeaves, List<double[]> leavesData) {
			this.nbLeaves = nbLeaves;
			this.leavesData = leavesData;
			for (double[] leafData : leavesData) {
				if (leafData != null) {
					if (nbData <= leafData.length) {
						nbData = leafData.length;
					}
				}
			}
		}

		public int getNbLeaves() {
			return nbLeaves;
		}

		public List<double[]> getLeavesData() {
			return leavesData;
		}

		/**
		 * Computes a nodes aggregated data as the mean of its leaves data.
		 */
		public double[] getLeavesDataAverages() {
			double[] ret = new double[nbData];

			int nbLeaves = 0;
			for (double[] leafData : leavesData) {
				for (int i = 0; i < leafData.length; i++) {
					ret[i] += leafData[i];
				}
				nbLeaves++;
			}

			for (int i = 0; i < nbData; i++) {
				ret[i] = ret[i] / nbLeaves;
			}

			return ret;
		}

		/**
		 * Computes the radius of the node: the maximum distance of a leaf.
		 */
		public double getRadius() {
			double[] avg = getLeavesDataAverages();

			double ret = 0;
			double dist = 0;
			for (double[] leafData : leavesData) {
				for (int i = 0; i < leafData.length; i++) {
					double d = leafData[i] - avg[i];
					dist += d * d;
				}
				dist = Math.sqrt(dist);

				if (dist > ret) {
					ret = dist;
				}
			}

			return ret;
		}
	}

	/**
	 * {@link Experiment} implementation that knows about {@link Sample}s.
	 * <p>
	 * It does load everything in memory. This could be changed to handle large
	 * samples.
	 * </p>
	 */
	private class SampleExperiment implements Experiment {

		private final List<Sample> samples;
		private final int[] indices;

		public SampleExperiment(SampleIterable samplesReader, int[] indices) {
			samples = new ArrayList<Sample>();
			for (Sample s : samplesReader) {
				samples.add(s);
			}
			this.indices = indices;
		}

		/**
		 * A "point" is the sub-vector of a sample containing selected variables
		 * (or all if no selected variables).
		 */
		public double[] getPoint(int obs) {
			if (indices == null) {
				return samples.get(obs).getContent();
			} else {
				double[] arr = new double[indices.length];
				for (int i = 0; i < indices.length; i++) {
					arr[i] = samples.get(obs).getContent()[indices[i]];
				}
				return arr;
			}
		}

		/**
		 * Returns the sample for a given indice.
		 */
		public Sample getSample(int obs) {
			return samples.get(obs);
		}

		@Override
		public int getNumberOfObservations() {
			return samples.size();
		}

	}

	/**
	 * Simple implementation of {@link DissimilarityMeasure} that knows to
	 * extract relevant data from {@link SampleExperiment}.
	 * <p>
	 * Computes a simple Euclidean distance.
	 * </p>
	 */
	private class SampleDissimilarityMeasure implements DissimilarityMeasure {

		@Override
		public double computeDissimilarity(Experiment experiment, int observation1, int observation2) {
			SampleExperiment e = (SampleExperiment) experiment;

			double[] o1 = e.getPoint(observation1);
			double[] o2 = e.getPoint(observation2);

			double v = 0;
			for (int i = 0; i < o1.length; i++) {
				double d = o1[i] - o2[i];
				v += d * d;
			}

			return Math.sqrt(v);
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

}
