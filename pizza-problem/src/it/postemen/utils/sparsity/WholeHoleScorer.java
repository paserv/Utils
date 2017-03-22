package it.postemen.utils.sparsity;

public class WholeHoleScorer<T> extends ConnectedComponents<T> {

	public WholeHoleScorer(NeighborStrategy ns, ScoreStrategy ss) {
		super(ns,ss);
	}

	@Override
	public float getScore(T[][] matrix, T toFind) {
		
		int R = matrix.length;
		int C = matrix[0].length;
		int[][] scoreM = new int[R][C];
		boolean visited[][] = new boolean[R][C];
		boolean visited2[][] = new boolean[R][C];

		for (int i = 0; i < R; ++i)
			for (int j = 0; j < C; ++j)
				if (matrix[i][j].equals(toFind) && !visited[i][j]) {
					int d = computeDepth(matrix, toFind, i, j, visited);
					if (d > 0)
						assignValue(matrix, toFind, i, j, visited2, scoreM, d);
				}
		return ss.computeScore(scoreM);
		
	}

	
}
