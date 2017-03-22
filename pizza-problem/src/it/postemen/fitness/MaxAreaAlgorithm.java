package it.postemen.fitness;

import java.util.ArrayList;

import it.postemen.bean.Slice;
import it.postemen.utils.ScoreCalculator;

public class MaxAreaAlgorithm implements FitnessCalculator {

	@Override
	public float getFitness(boolean[][] matrix, ArrayList<Slice> slices) {
		return ScoreCalculator.getScore(slices);
	}

}
