package it.postemen.fitness;

import java.util.ArrayList;

import it.postemen.bean.Slice;

public interface FitnessCalculator {

	public float getFitness(boolean[][] matrix, ArrayList<Slice> slices);
}
