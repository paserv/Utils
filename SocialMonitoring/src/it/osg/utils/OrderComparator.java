package it.osg.utils;

import it.osg.model.Graph;

import java.util.Comparator;

public class OrderComparator implements Comparator {

	@Override
	public int compare(Object o1, Object o2) {
		Graph obj1 = (Graph) o1;
		Graph obj2 = (Graph) o2;
		return (int) (obj2.getOrdinate() - obj1.getOrdinate());
	}

}
