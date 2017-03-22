package it.postemen.utils.sparsity;

public class ThreeNStrategy implements NeighborStrategy {
	int rowNbr[] = new int[] { 0, 1, 1};
	int colNbr[] = new int[] { 1, 0, 1};
	@Override
	public int[] getRowNbr() {
	return rowNbr;

	}

	@Override
	public int[] getColNbr() {
		return colNbr;
	}

}
