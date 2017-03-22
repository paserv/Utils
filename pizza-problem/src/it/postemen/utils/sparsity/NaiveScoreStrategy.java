package it.postemen.utils.sparsity;

public class NaiveScoreStrategy implements ScoreStrategy{

	@Override
	public float computeScore(int[][] m) {
		float tot = 0;;
		for (int i = 0; i < m.length; ++i)
			for (int j = 0; j < m[0].length; ++j)
				tot  += m[i][j];
		return tot;
	}

}
