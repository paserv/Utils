package it.postemen.slice;

import java.util.ArrayList;

import it.postemen.bean.Coordinate;
import it.postemen.bean.Geometry;
import it.postemen.bean.Slice;
import it.postemen.bean.Ties;
import it.postemen.utils.InputReader;
import it.postemen.utils.MatrixUtils;

public class LeftDownStartingPoint extends SliceGenerator {

	public static void main(String[] args) {
		Ties vincoli = InputReader.getTies("input/small.in");
		boolean[][] matrixWithSlice = new boolean[6][7];
		Geometry brick = new Geometry(2, 2);
		
		SliceGenerator sg = new LeftDownStartingPoint();
		sg.fitSlicesIntoMatrix(brick, vincoli, matrixWithSlice);
	}
	
	@Override
	public ArrayList<Slice> fitSlicesIntoMatrix(Geometry brick, Ties vincoli, boolean[][] matrixWithSlice) {
		ArrayList<Slice> result = new ArrayList<Slice>();
		
		boolean[][] copyMatrix = MatrixUtils.copy(matrixWithSlice);
		
		int verticalStep = brick.getNumRow();
		int stopRow = vincoli.getMatrixRows() - 1;
		int startRow = stopRow - verticalStep + 1;		
		
		int horizontalStep = brick.getNumCol();
		int startCol = 0;
		int stopCol = horizontalStep - 1;
		
		while (startRow >= 0) {
			
			while (stopCol < vincoli.getMatrixCols()) {
				
				Slice currentSlice = new Slice(new Coordinate(startRow, startCol), new Coordinate(stopRow, stopCol));
//				currentSlice.print();
				
				if (checkSliceOk(currentSlice, vincoli, copyMatrix)) {
					result.add(currentSlice);
					MatrixUtils.fillMatrixWithSlice(copyMatrix, currentSlice);
					startCol = stopCol + 1;
					stopCol = stopCol + horizontalStep;
				} else {
					startCol = startCol + 1;
					stopCol = stopCol + 1;
				}
			}
			startCol = 0;
			stopCol = horizontalStep - 1;
			
			startRow = startRow - 1;
			stopRow = stopRow - 1;
		}
		return result;
	}

}
