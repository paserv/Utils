package it.postemen.fitness;

import java.util.ArrayList;

import it.postemen.bean.Slice;
import it.postemen.utils.MatrixUtils;
import it.postemen.utils.sparsity.ConnectedComponents;

public class SparsityCalculator implements FitnessCalculator {

	private ConnectedComponents<Boolean> cc;

	public SparsityCalculator(ConnectedComponents<Boolean> _cc) {
		cc = _cc;
	}
	
	@Override
	public float getFitness(boolean[][] matrix, ArrayList<Slice> slices) {
		float result = 0;
		Boolean[][] copyBool = MatrixUtils.copy2(matrix);
		result = cc.getScore(copyBool, new Boolean(true));
		return result;
	}
}
