package it.postemen.bean;

public class Geometry {

	@Override
	public String toString() {
		return "Geometry [numRow=" + numRow + ", numCol=" + numCol + "]";
	}
	private int numRow;
	private int numCol;
	
	
	public Geometry() {

	}
	
	public Geometry(int rows, int cols) {
		this.numRow = rows;
		this.numCol = cols;
	}
	
	public int getNumRow() {
		return numRow;
	}
	public void setNumRow(int numRow) {
		this.numRow = numRow;
	}
	public int getNumCol() {
		return numCol;
	}
	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}
	
	@Override
	public int hashCode() {
		return (numRow*100)+numCol;
	}
	
	@Override
	public boolean equals(Object obj) {
		Geometry g = (Geometry)obj;
		
		return (this.getNumCol()==g.getNumCol() && this.getNumRow()==g.getNumRow());
	}
}
