package it.postemen.bean;

public class Slice {

	private Coordinate leftUpCorner;
	private Coordinate rightDownCorner;
	
	public Slice(Coordinate leftUp, Coordinate rightDown) {
		this.leftUpCorner = leftUp;
		this.rightDownCorner = rightDown;
	}
	
	public int getArea() {
		return (this.getRightDownCorner().getX() - this.getLeftUpCorner().getX() + 1) * (this.getRightDownCorner().getY() - this.getLeftUpCorner().getY() + 1); 
	}
	
	public Coordinate getLeftUpCorner() {
		return leftUpCorner;
	}
	public void setLeftUpCorner(Coordinate leftUpCorner) {
		this.leftUpCorner = leftUpCorner;
	}
	public Coordinate getRightDownCorner() {
		return rightDownCorner;
	}
	public void setRightDownCorner(Coordinate rightDownCorner) {
		this.rightDownCorner = rightDownCorner;
	}
	
	public void print() {
		System.out.println("LeftUp: (" + this.getLeftUpCorner().getX() + "," + this.getLeftUpCorner().getY() + ")");
		System.out.println("RightDown: (" + this.getRightDownCorner().getX() + "," + this.getRightDownCorner().getY() + ")");
	}
	
	
	
}
