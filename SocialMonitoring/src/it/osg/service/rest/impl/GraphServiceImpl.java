package it.osg.service.rest.impl;

import it.osg.service.GraphService;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



@Path("/resource/")
public class GraphServiceImpl extends GraphService {

	
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("time/likes/{transmission}/{from}/{to}")
	public Response getLikesCount(@PathParam("transmission") String transmission, @PathParam("from") String from, @PathParam("to") String to) {

		return getGraphData("it.osg.datasource.facebook.time.PickedData", new Object[]{transmission, from, to, "date", "like_count"}, "TIME");

	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("time/dlikes/{transmission}/{from}/{to}")
	public Response getDLikesCount(@PathParam("transmission") String transmission, @PathParam("from") String from, @PathParam("to") String to) {

		return getGraphData("it.osg.datasource.facebook.time.DPickedData", new Object[]{transmission, from, to, "date", "like_count"}, "TIME");

	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("time/talkingabout/{transmission}/{from}/{to}")
	public Response getTalkingAboutCount(@PathParam("transmission") String transmission, @PathParam("from") String from, @PathParam("to") String to) {

		return getGraphData("it.osg.datasource.facebook.time.PickedData", new Object[]{transmission, from, to, "date", "talking_about_count"}, "TIME");

	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("time/dtalkingabout/{transmission}/{from}/{to}")
	public Response getDTalkingAboutCount(@PathParam("transmission") String transmission, @PathParam("from") String from, @PathParam("to") String to) {

		return getGraphData("it.osg.datasource.facebook.time.DPickedData", new Object[]{transmission, from, to, "date", "talking_about_count"}, "TIME");

	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("time/post/{transmission}/{from}/{to}")
	public Response getCommentCount(@PathParam("transmission") String transmission, @PathParam("from") String from, @PathParam("to") String to) {
		
		return getGraphData("it.osg.datasource.facebook.time.PostCount", new Object[]{transmission, from, to}, "TIME");

	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("label/wordfrequency/{transmission}/{from}/{to}/{limit}")
	public Response getWordFrequencyCount(@PathParam("transmission") String transmission, @PathParam("from") String from, @PathParam("to") String to, @PathParam("limit") String limit) {
		
		return getGraphData("it.osg.datasource.facebook.label.WordFrequencyCalculator", new Object[]{transmission, from, to, limit}, "DATA");

	}
	

}
