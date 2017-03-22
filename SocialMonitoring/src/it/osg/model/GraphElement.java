package it.osg.model;


public class GraphElement {

	public Color color;
	public ElementType type;
	public enum ElementType {PAGEID, AUTHOR};
	
	public static ElementType getType(String type) {
		if (type.equalsIgnoreCase(ElementType.PAGEID.toString())) {
			return ElementType.PAGEID;
		} else {
			return ElementType.AUTHOR;
		}
	}
}
