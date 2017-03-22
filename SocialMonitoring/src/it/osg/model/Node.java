package it.osg.model;

public class Node extends GraphElement {

	public String id;
	public String label;
	public double size;

	
	public Node(String ident, String lab, double siz, ElementType tp) {
		this.id = ident;
		this.label = lab;
		this.size = siz;
		this.type = tp;
	}
	 @Override
	 public boolean equals(Object object) {
		 if (object != null && object instanceof Node) {
			 if (((Node) object).id.equalsIgnoreCase(this.id)) {
				 return true;
			 }
		 }
		 return false;
	 }
	
	
}
