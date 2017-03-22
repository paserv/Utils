package it.osg.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "graph")
public class Graph {

	String axis;
	double ordinate;

	public Graph () {

	}

		
	public Graph (String axisIn, double ordIn) {
		setAxis(axisIn);
		setOrdinate(ordIn);
	}

	public String getAxis() {
		return axis;
	}


	public void setAxis(String axis) {
		this.axis = axis;
	}



	
	public double getOrdinate() {
		return ordinate;
	}

	public void setOrdinate(double ordinate) {
		this.ordinate = ordinate;
	}

	@Override
	public String toString() {
		return axis.toString() + " " + String.valueOf(ordinate);
	}



}
