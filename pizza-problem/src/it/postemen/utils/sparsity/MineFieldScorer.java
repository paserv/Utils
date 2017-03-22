package it.postemen.utils.sparsity;

public class MineFieldScorer<T> extends ConnectedComponents<T> {

	public MineFieldScorer(NeighborStrategy ns, ScoreStrategy ss) {
		super(ns, ss);
	}

	@Override
	public float getScore(T[][] matrix, T toFind) {
		int R = matrix.length;
		int C = matrix[0].length;
		int[][] scoreM = new int[R][C];
		boolean visited[][] = new boolean[R][C];

		for (int i = 0; i < R; ++i)
			for (int j = 0; j < C; ++j)
				if (matrix[i][j].equals(toFind) && !visited[i][j]) {
					DFS(matrix, toFind, i, j, visited, scoreM);
				}

		return ss.computeScore(scoreM);
	}

}
