package it.osg.model;

public class Edge extends GraphElement {

	public String source;
	public String target;
	public double weight;
	
	public Edge(String sour, String tar, double weig, ElementType nt) {
		this.source = sour;
		this.target = tar;
		this.weight = weig;
		this.type = nt;
	}
	


	
	
}
