package it.osg.service.rest.impl;

import it.osg.model.IdName;
import it.osg.service.GraphService;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;



@Path("/table/")
public class ServiceImpl extends GraphService {

	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("resource/{table}/{field}")
	public Response getMonitoredEntities(@PathParam("table") String table, @PathParam("field") String field) {

		ArrayList<IdName> result = new ArrayList<IdName>();
		DatastoreService DS = DatastoreServiceFactory.getDatastoreService();
		Query q;
		PreparedQuery pq;
		q = new Query(table);
		pq = DS.prepare(q);
		for (Entity ent : pq.asIterable()) {
			IdName curr = new IdName();
			if (ent.getProperty(field) != null && !((String)ent.getProperty(field)).equalsIgnoreCase("")) {
				curr.setIdentificativo((String) ent.getProperty(field));
				curr.setNome((String) ent.getProperty("nome"));
				result.add(curr);
			}
		}
		final GenericEntity<List<IdName>> entity = new GenericEntity<List<IdName>>(result) { };
		return Response.ok().entity(entity).build();
	}
	

	
}
