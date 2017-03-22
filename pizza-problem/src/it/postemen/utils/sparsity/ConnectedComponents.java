package it.postemen.utils.sparsity;

public abstract class ConnectedComponents <T>{

	
	private NeighborStrategy ns;
	protected ScoreStrategy ss;

	public ConnectedComponents(NeighborStrategy ns, ScoreStrategy ss) {
		this.ns = ns;
		this.ss = ss;
	}

	private boolean isSafe(T matrix[][], T toFind, int row, int col, boolean visited[][]) {
		return (row >= 0) && (row < matrix.length) && (col >= 0) && (col < matrix[0].length)
				&& (matrix[row][col].equals(toFind) && !visited[row][col]);
	}

	private boolean isInRange(T matrix[][], T toFind, int row, int col) {
		// row number is in range, column number is in range
		return (row >= 0) && (row < matrix.length) && (col >= 0) && (col < matrix[0].length) && (matrix[row][col].equals(toFind));
	}

	void DFS(T M[][], T toFind, int row, int col, boolean visited[][], int[][] scoreM) {

		// Mark this cell as visited
		visited[row][col] = true;

		// Recur for all connected neighbours
		for (int k = 0; k < ns.getRowNbr().length; ++k) {
			if (isInRange(M, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k]))
				scoreM[row][col] += (M[row + ns.getRowNbr()[k]][col + ns.getColNbr()[k]].equals(toFind) ? 1 : 0);
			if (isSafe(M, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k], visited)) {
				DFS(M, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k], visited, scoreM);
			}
		}
	}

	int computeDepth(T matrix[][], T toFind, int row, int col, boolean visited[][]) {
		int depth = 1;
		visited[row][col] = true;

		// for all connected neighbors
		for (int k = 0; k < ns.getRowNbr().length; ++k)
			if (isSafe(matrix, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k], visited)) {
				depth += computeDepth(matrix, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k], visited);
			}

		return depth;
	}

	void assignValue(T M[][], T toFind, int row, int col, boolean visited[][], int[][] scoreM, int val) {
		scoreM[row][col] = val;
		visited[row][col] = true;
		// for all connected neighbors
		for (int k = 0; k < ns.getRowNbr().length; ++k) {
			if (isSafe(M, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k], visited)) {
				scoreM[row + ns.getRowNbr()[k]][col + ns.getColNbr()[k]] = val;
				assignValue(M, toFind, row + ns.getRowNbr()[k], col + ns.getColNbr()[k], visited, scoreM, val);
			}
		}

	}

	public abstract float getScore(T matrix[][], T placeholdVal);

}