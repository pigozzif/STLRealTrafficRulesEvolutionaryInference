package localSearch.gaussianProcesses.regression;

import localSearch.gaussianProcesses.GpDataset;
import localSearch.gaussianProcesses.GpPosterior;

public class RegressionPosterior extends GpPosterior {

	public RegressionPosterior(GpDataset inputData, double[] mean,
                               double[] var) {
		super(inputData, mean, var);
	}

	final public double[] getLowerBound(final double beta) {
		final double std[] = getStandardDeviation();
		final double[] bound = new double[std.length];
		for (int i = 0; i < std.length; i++)
			bound[i] = getMean()[i] - beta * std[i];
		return bound;
	}

	final public double[] getUpperBound(final double beta) {
		final double std[] = getStandardDeviation();
		final double[] bound = new double[std.length];
		for (int i = 0; i < std.length; i++)
			bound[i] = getMean()[i] + beta * std[i];
		return bound;
	}

}
