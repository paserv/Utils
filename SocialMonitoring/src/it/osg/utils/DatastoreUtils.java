package it.osg.utils;

import it.osg.model.Edge;
import it.osg.model.GraphElement;
import it.osg.model.Node;
import it.osg.model.PSARData;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

public class DatastoreUtils {

	private static DatastoreService DS = DatastoreServiceFactory.getDatastoreService();

	public static ArrayList<String> getKeyNamesFromTable(String tableName) {
		ArrayList<String> result = new ArrayList<String>();

		Query q;
		PreparedQuery pq;
		q = new Query(tableName).setKeysOnly();
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			String currKey = String.valueOf(ent.getKey().getName());
			result.add(currKey);
		}
		
		return result;

	}
	
	public static ArrayList<String> getPropertyList(String tableName, String idKind) {
		ArrayList<String> result = new ArrayList<String>();

		Query q;
		PreparedQuery pq;
		q = new Query(tableName);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			if (ent.getProperty(idKind) != null) {
				String currKey = String.valueOf(ent.getProperty(idKind));
				result.add(currKey);
			}
		}

		
		
		return result;
	}

	public static void saveEntity (Entity ent) {
		DS.put(ent);
	}

	
	public static void saveNode (String table, String idTransaction, Node node) {
		Entity currEntity = new Entity(table);
		currEntity.setProperty("nodeID", node.id);
		currEntity.setProperty("nodeLabel", node.label);
		currEntity.setProperty("nodeSize", node.size);
		currEntity.setProperty("nodeType", node.type.toString());
		currEntity.setProperty("idTransaction", idTransaction);
		//currEntity.setProperty("pageId", pageId);
		DS.put(currEntity);
	}

	public static void saveEdge (String table, String idTransaction, Edge edge) {
		Entity currEntity = new Entity(table);
		currEntity.setProperty("edgeSource", edge.source);
		currEntity.setProperty("edgeTarget", edge.target);
		currEntity.setProperty("edgeWeight", edge.weight);
		currEntity.setProperty("edgeType", edge.type.toString());
		currEntity.setProperty("idTransaction", idTransaction);
		//currEntity.setProperty("pageId", pageId);
		DS.put(currEntity);
	}


	public static void saveNodes (String table, String idTransaction, ArrayList<Node> nodes) {
		Iterator<Node> iter = nodes.iterator();
		while (iter.hasNext()) {
			Node currNode = iter.next();
			saveNode(table, idTransaction, currNode);
		}
	}

	public static void saveNodes (String table, String idTransaction, Hashtable<String, Node> nodes) {
		Enumeration<String> keys = nodes.keys();
		while(keys.hasMoreElements()) {
			Node currNode = nodes.get(keys.nextElement());
			saveNode(table, idTransaction, currNode);
		}
	}

	public static void saveEdges (String table, String idTransaction, ArrayList<Edge> edges) {
		Iterator<Edge> iter = edges.iterator();
		while (iter.hasNext()) {
			Edge currEdge = iter.next();
			saveEdge(table, idTransaction, currEdge);
		}
	}

	public static void saveEdges (String table, String idTransaction, Hashtable<String, Edge> edges) {
		Enumeration<String> keys = edges.keys();
		while(keys.hasMoreElements()) {
			Edge currEdge = edges.get(keys.nextElement());
			saveEdge(table, idTransaction, currEdge);
		}
	}
	
	public static ArrayList<PSARData> getPsarDataFB(String table, String idTransaction) {
		ArrayList<PSARData> result = new ArrayList<PSARData>();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, idTransaction);
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			PSARData curr = new PSARData();
			curr.idTransaction = (String) ent.getProperty("idTransaction");
			curr.pageId = (String) ent.getProperty("pageId");
			curr.postFromPageCount = (Double) ent.getProperty("totParzPostFromPage");
			curr.postFromFanCount = (Double) ent.getProperty("totParzPostFromFan");
			curr.commentsCount = (Double) ent.getProperty("totParzComments");
			curr.likesCount = (Double) ent.getProperty("totParzLikes");
			curr.sharesCount = (Double) ent.getProperty("totParzShares");
			//String authors = ((Text) ent.getProperty("authors")).getValue();
			//curr.authors = ArrayUtils.splitAndAdd(authors, ",");
//			if ((Text) ent.getProperty("authors") != null && !((Text) ent.getProperty("authors")).getValue().equalsIgnoreCase("")){
//				String authors = ((Text) ent.getProperty("authors")).getValue();
//				curr.authors = ArrayUtils.splitAndAdd(authors, ",");
//			}
			
			curr.commentsToPostFromFan = (Double) ent.getProperty("totParzCommentsToPostFromFan");
			curr.commnetsFromPageToPostFromFan = (Double) ent.getProperty("totParzCommnetsFromPageToPostFromFan");
			curr.commnetsFromPageToPostFromPage = (Double) ent.getProperty("totParzCommnetsFromPageToPostFromPage");
//			String[] splittedAuthors = authors.split(",");
//			for (int i = 0; i < splittedAuthors.length; i++) {
//				String currAuth = splittedAuthors[i];
//				curr.authors.add(currAuth);
//			}
			result.add(curr);
		}
		return result;
	}
	
	public static ArrayList<PSARData> getPsarDataPL(String table, String idTransaction) {
		ArrayList<PSARData> result = new ArrayList<PSARData>();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, idTransaction);
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			PSARData curr = new PSARData();
			curr.idTransaction = (String) ent.getProperty("idTransaction");
			curr.pageId = (String) ent.getProperty("pageId");
			curr.postFromPageCount = (Double) ent.getProperty("totParzActFromPage");
			curr.commentsCount = (Double) ent.getProperty("totParzComments");
			curr.likesCount = (Double) ent.getProperty("totParzPluses");
			curr.sharesCount = (Double) ent.getProperty("totParzShares");
			if ((Text) ent.getProperty("authors") != null && !((Text) ent.getProperty("authors")).getValue().equalsIgnoreCase("")){
				String authors = ((Text) ent.getProperty("authors")).getValue();
				curr.authors = ArrayUtils.splitAndAdd(authors, ",");
			}
			result.add(curr);
		}
		return result;
	}
	
	public static ArrayList<PSARData> getPsarDataYT(String table, String idTransaction) {
		ArrayList<PSARData> result = new ArrayList<PSARData>();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, idTransaction);
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			PSARData curr = new PSARData();
			curr.idTransaction = (String) ent.getProperty("idTransaction");
			curr.pageId = (String) ent.getProperty("pageId");
			curr.postFromPageCount = (Double) ent.getProperty("activitiescount");
			curr.viewCount = (Double) ent.getProperty("viewcount");
			curr.likesCount = (Double) ent.getProperty("likecount");
			curr.dislikesCount = (Double) ent.getProperty("dislikecount");
			curr.favouriteCount = (Double) ent.getProperty("favouritecount");
			curr.commentsCount = (Double) ent.getProperty("commentcount");
			
			result.add(curr);
		}
		return result;
	}


	public static ArrayList<Node> getNodes (String table, String idTransaction) {
		ArrayList<Node> result = new ArrayList<Node>();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, idTransaction);
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			String nodeID = (String) ent.getProperty("nodeID");
			String nodeLabel = (String) ent.getProperty("nodeLabel");
			double nodeSize = (Double) ent.getProperty("nodeSize");
			String tipo = (String) ent.getProperty("nodeType");
			Node currNode = new Node(nodeID, nodeLabel, nodeSize, GraphElement.getType(tipo));
			result.add(currNode);
		}
		return result;

	}

	public static ArrayList<Edge> getEdges (String table, String idTransaction) {
		ArrayList<Edge> result = new ArrayList<Edge>();
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate("idTransaction", FilterOperator.EQUAL, idTransaction);
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			String edgeSource = (String) ent.getProperty("edgeSource");
			String edgeTarget = (String) ent.getProperty("edgeTarget");
			double edgeWeight = (Double) ent.getProperty("edgeWeight");
			String edgeType = (String) ent.getProperty("edgeType");
			Edge currEdge = new Edge(edgeSource, edgeTarget, edgeWeight, GraphElement.getType(edgeType));
			result.add(currEdge);
		}
		return result;

	}

	public static void incrementTask(String table, String idTransaction) {
		Query q;
		PreparedQuery pq;
		Filter idFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, KeyFactory.createKey(table, idTransaction));
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			Long exexutedTask = (Long) ent.getProperty("executedtask");
			ent.setProperty("executedtask", exexutedTask + 1L);
			DS.put(ent);
			return;
		}
		Entity newEnt = new Entity("task", idTransaction);
		newEnt.setProperty("executedtask", 1L);
		DS.put(newEnt);
	}

	public static Object getValue(String table, String filterPropertyName, String filterPropertyValue, String returnProperty) {
		Query q;
		PreparedQuery pq;
		Filter idFilter = null;
		if (filterPropertyName.equalsIgnoreCase("key")) {
			idFilter = new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, KeyFactory.createKey(table, filterPropertyValue));
		} else {
			idFilter = new FilterPredicate(filterPropertyName, FilterOperator.EQUAL, filterPropertyValue);
		} 
		q = new Query(table).setFilter(idFilter);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			Object returnValue = ent.getProperty(returnProperty);
			return returnValue;
		}
		return null;
	}
	
	public static String getSetting(String settingName) {
		return (String) getValue(Constants.SETTINGS_TABLE, "property", settingName, "value");
	}
	
	public static void addRow(String table, String propertyName, Object propertyValue) {
		Entity newEnt = new Entity(table);
		newEnt.setProperty(propertyName, propertyValue);
		DS.put(newEnt);
	}



	public static Hashtable<String, Node> saveNodes(String table, String idTransaction, Hashtable<String, Node> nodes, long value) {
		Enumeration<String> keys = nodes.keys();
		Hashtable<String, Node> result = new Hashtable<String, Node>();
		while(keys.hasMoreElements()) {
			Node currNode = nodes.get(keys.nextElement());
			if (currNode.size >= value) {
				saveNode(table, idTransaction, currNode);
				result.put(currNode.id, currNode);
			}
		}
		return result;
	}



	public static void saveEdges(String table, String idTransaction, Hashtable<String, Edge> edges, Hashtable<String, Node> nodes) {
		Enumeration<String> keys = edges.keys();
		while(keys.hasMoreElements()) {
			Edge currEdge = edges.get(keys.nextElement());
			if (nodes.containsKey(currEdge.source) && nodes.containsKey(currEdge.target)) {
				saveEdge(table, idTransaction, currEdge);
			}
		}		
	}
	
	public static Object getProperty(Entity ent, String prop) {
		if(ent.getProperty(prop) == null) {
			return "null";
		}
		if(ent.getProperty(prop).equals("")) {
			return "null";
		}
		return ent.getProperty(prop);
	}



}
