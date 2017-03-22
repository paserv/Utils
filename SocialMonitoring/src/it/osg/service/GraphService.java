package it.osg.service;

import it.osg.datasource.GraphSourceGenerator;
import it.osg.model.Graph;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;



public abstract class GraphService {

	public enum graphType {DATA,TIME};
	

	public static Response getGraphData(String className, Object[] objects, String gt) {
		
		ArrayList<Graph> result = GraphSourceGenerator.getData(className, objects);
		
		final GenericEntity<List<Graph>> entity = new GenericEntity<List<Graph>>(result) { };
		return Response.ok().entity(entity).build();

	} 



}



