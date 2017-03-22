package it.postemen.bean;

import java.util.Hashtable;

public class Ties {

	private Hashtable<Character, Integer> minIngredient;
	private int maxSliceArea;
	private int matrixRows;
	private int matrixCols;
	private char[][] matrix;
	
	public char[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(char[][] matrix) {
		this.matrix = matrix;
	}

	public int getMinIngr(Character ingredient) {
		return minIngredient.get(ingredient);
	}
	
	public Hashtable<Character, Integer> getMinIngredient() {
		return minIngredient;
	}
	public void setMinIngredient(Hashtable<Character, Integer> minIngredient) {
		this.minIngredient = minIngredient;
	}
	public int getMaxSliceArea() {
		return maxSliceArea;
	}
	public void setMaxSliceArea(int maxSliceArea) {
		this.maxSliceArea = maxSliceArea;
	}

	public int getMatrixRows() {
		return matrixRows;
	}

	public void setMatrixRows(int matrixRows) {
		this.matrixRows = matrixRows;
	}

	public int getMatrixCols() {
		return matrixCols;
	}

	public void setMatrixCols(int matrixCols) {
		this.matrixCols = matrixCols;
	}
	
	
	
}
