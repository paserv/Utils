package it.osg.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import it.osg.model.Color;
import it.osg.model.Edge;
import it.osg.model.GraphElement.ElementType;
import it.osg.model.Node;

public class GephiUtils {

	private static final Color PAGEID_COLOR = new Color("40", "160", "255");
	private static final Color AUTHOR_COLOR = new Color("40", "170", "20");
	private static final Color PAGEID_EDGE_COLOR = new Color("0", "0", "255");
	private static final Color AUTHOR_EDGE_COLOR = new Color("0", "255", "0");
	private static String description = "";

	public static String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<gexf xmlns=\"http://www.gexf.net/1.2draft\" version=\"1.2\" xmlns:viz=\"http://www.gexf.net/1.2draft/viz\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\">\n" +
			"\t<meta lastmodifieddate=\"2013-07-15\">\n" +
			"\t\t<creator>Don Pablo</creator>\n" +
			"\t\t<description>" + description + "</description>\n" +
			"\t</meta>\n" +
			"\t<graph defaultedgetype=\"directed\" mode=\"static\">\n";
	public static String FOOTER = "\t</graph>\n" +
			"</gexf>";

	public static String insertNode(Node nod) {
		return "\t\t<node id=\"" + nod.id + "\" label=\"" + nod.label + "\">\n" +
				"\t\t\t<attvalues></attvalues>\n" +
				"\t\t\t<viz:size value=\"" + nod.size + "\"></viz:size>\n" +
				"\t\t\t<viz:position x=\"0.0\" y=\"0.0\" z=\"0.0\"></viz:position>\n" +
				"\t\t\t<viz:color r=\"" + nod.color.R + "\" g=\"" + nod.color.G + "\" b=\"" + nod.color.B + "\"></viz:color>\n" +
				"\t\t</node>\n";
	}

	public static String insertEdge(Edge currEdge) {
		return "\t\t<edge source=\"" + currEdge.source + "\" target=\"" + currEdge.target + "\" weight=\"" + currEdge.weight + "\">\n" +
				"\t\t\t<viz:color r=\"" + currEdge.color.R + "\" g=\"" + currEdge.color.G + "\" b=\"" + currEdge.color.B + "\"></viz:color>\n" +
				"\t\t\t<attvalues></attvalues>\n" +
				"\t\t</edge>\n";
	}

	public static String createGraph(Hashtable<String,Node> nodes, Hashtable<String,Edge> edges, String descr) {
		description = descr;
		String result = GephiUtils.HEADER;
		Enumeration<Node> iterNodes = nodes.elements();

		result = result + "\t\t<nodes>\n";
		while (iterNodes.hasMoreElements()) {
			Node currNode = iterNodes.nextElement();
			if (currNode.type.equals(ElementType.PAGEID)) {
				currNode.color = PAGEID_COLOR;
			} else if (currNode.type.equals(ElementType.AUTHOR)) {
				currNode.color = AUTHOR_COLOR;
			}
			result = result + GephiUtils.insertNode(currNode);
		}
		result = result + "\t\t</nodes>\n";
		result = result + "\t\t<edges>\n";
		Enumeration<Edge> iterEdge = edges.elements();
		while (iterEdge.hasMoreElements()) {
			Edge currEdge = iterEdge.nextElement();
			if (currEdge.type.equals(ElementType.PAGEID)) {
				currEdge.color = PAGEID_EDGE_COLOR;
			} else if (currEdge.type.equals(ElementType.AUTHOR)) {
				currEdge.color = AUTHOR_EDGE_COLOR;
			}
			result = result + GephiUtils.insertEdge(currEdge);
		}
		result = result + "\t\t</edges>\n";
		result = result + GephiUtils.FOOTER;
		return result;
	}

	public static String createGraph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
		String result = GephiUtils.HEADER;

		result = result + "<nodes>\n";
		Iterator<Node> iterNodes = nodes.iterator();		
		while (iterNodes.hasNext()) {
			Node currNode = iterNodes.next();
			if (currNode.type.equals(ElementType.PAGEID)) {
				currNode.color = PAGEID_COLOR;
			} else if (currNode.type.equals(ElementType.AUTHOR)) {
				currNode.color = AUTHOR_COLOR;
			}
			result = result + GephiUtils.insertNode(currNode);
		}
		result = result + "</nodes>\n";

		result = result + "<edges>\n";
		Iterator<Edge> iterEdges = edges.iterator();		
		while (iterEdges.hasNext()) {
			Edge currEdge = iterEdges.next();
			if (currEdge.type.equals(ElementType.PAGEID)) {
				currEdge.color = PAGEID_EDGE_COLOR;
			} else if (currEdge.type.equals(ElementType.AUTHOR)) {
				currEdge.color = AUTHOR_EDGE_COLOR;
			}
			result = result + GephiUtils.insertEdge(currEdge);
		}
		result = result + "</edges>\n";
		result = result + GephiUtils.FOOTER;
		return result;
	}

}
