package it.postemen.fitness;

import java.util.ArrayList;

import it.postemen.bean.Slice;
import it.postemen.utils.ScoreCalculator;

public class RandomCalculator implements FitnessCalculator {

	@Override
	public float getFitness(boolean[][] matrix, ArrayList<Slice> slices) {
		return (float) Math.random();
	}

}
