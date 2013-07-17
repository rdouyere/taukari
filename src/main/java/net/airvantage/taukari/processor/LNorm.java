package net.airvantage.taukari.processor;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

public class LNorm {

	private final StandardDeviation stdev_proc = new StandardDeviation();
	private final Mean mean_proc = new Mean();
	private double stdev;
	private double mean;

	public void increment(double d) {
		stdev_proc.increment(d);
		mean_proc.increment(d);
	}

	public void terminate() {
		stdev = stdev_proc.getResult();
		mean = mean_proc.getResult();
	}

	public double getStdev() {
		return stdev;
	}

	public void setStdev(double stdev) {
		this.stdev = stdev;
	}

	public double getMean() {
		return mean;
	}

	public void setMean(double mean) {
		this.mean = mean;
	}
}