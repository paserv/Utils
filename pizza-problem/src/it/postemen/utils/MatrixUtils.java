package it.postemen.utils;

import java.util.ArrayList;

import it.postemen.bean.Slice;

public class MatrixUtils {

	public static void fillMatrixWithSlices(boolean[][] existentMatrix, ArrayList<Slice> slices) {
		for (Slice currSlice : slices) {
			fillMatrixWithSlice(existentMatrix, currSlice);
		}
	}

	public static void fillMatrixWithSlice(boolean[][] existentMatrix, Slice slice) {
		for (int i = slice.getLeftUpCorner().getX(); i <= slice.getRightDownCorner().getX(); i++) {
			for (int j = slice.getLeftUpCorner().getY(); j <= slice.getRightDownCorner().getY(); j++) {
				existentMatrix[i][j] = true;
			}
		}
	}

	public static boolean[][] copy(boolean[][] matrixWithSlice) {
		boolean[][] copyMatrix = new boolean[matrixWithSlice.length][matrixWithSlice[0].length];
		for (int i = 0; i < matrixWithSlice.length; i++) {
			for (int j = 0; j < matrixWithSlice[0].length; j++) {
				copyMatrix[i][j] = matrixWithSlice[i][j];
			}
		}
		return copyMatrix;
	}
	
	public static Boolean[][] copy2(boolean[][] matrixWithSlice) {
		Boolean[][] copyMatrix = new Boolean[matrixWithSlice.length][matrixWithSlice[0].length];
		for (int i = 0; i < matrixWithSlice.length; i++) {
			for (int j = 0; j < matrixWithSlice[0].length; j++) {
				copyMatrix[i][j] = new Boolean(matrixWithSlice[i][j]);
			}
		}
		return copyMatrix;
	}

	public static void print(boolean[][] matrixWithSlice) {
		for (int i = 0; i < matrixWithSlice.length; i++) {
			for (int j = 0; j < matrixWithSlice[0].length; j++) {
				System.out.print(matrixWithSlice[i][j]?"X":".");
			}
			System.out.println();
		}
	}
	
}