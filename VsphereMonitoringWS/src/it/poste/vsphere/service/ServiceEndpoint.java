package it.poste.vsphere.service;

import it.poste.vsphere.bean.DatastoreBean;
import it.poste.vsphere.bean.ServerBean;
import it.poste.vsphere.bean.TopologyBean;
import it.poste.vsphere.bean.VmBean;
import it.poste.vsphere.controller.DatastoreInfo;
import it.poste.vsphere.controller.HostInfo;
import it.poste.vsphere.controller.VmInfo;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/vsphere")
public class ServiceEndpoint {

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getMsg(@PathParam("param") String msg) {
		return Response.status(200).entity("pong").build();
	}

	@GET
	@Path("/datastoreinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDatastoreInfo() {
		try {
			DatastoreInfo di = new DatastoreInfo();
			List<DatastoreBean> result = di.getDatastores();
			return Response.ok().entity(new GenericEntity<List<DatastoreBean>>(result) {}).build();
		} catch (RemoteException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		}
	}

	@GET
	@Path("/vminfo/{url}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVmInfo(@PathParam("url") String url) {
		try {
			VmInfo vi = new VmInfo(url);
			List<VmBean> result = vi.getVms();
			return Response.ok().entity(new GenericEntity<List<VmBean>>(result) {}).build();
		} catch (RemoteException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		}
	}

	@GET
	@Path("/vminfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllVmInfo() {
		try {
			VmInfo vi = new VmInfo();
			List<VmBean> result = vi.getVms();
			return Response.ok().entity(new GenericEntity<List<VmBean>>(result) {}).build();
		} catch (RemoteException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		}
	}
	
	@GET
	@Path("/topology")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTopology() {
		try {
			VmInfo vi = new VmInfo();
			TopologyBean result = vi.getTopologyBean();
			return Response.ok().entity(new GenericEntity<TopologyBean>(result) {}).build();
		} catch (RemoteException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		}
	}
	
	@GET
	@Path("/topology/{url}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTopology(@PathParam("url") String url) {
		try {
			VmInfo vi = new VmInfo(url);
			TopologyBean result = vi.getTopologyBean();
			return Response.ok().entity(new GenericEntity<TopologyBean>(result) {}).build();
		} catch (RemoteException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		}
	}
	
	@GET
	@Path("/serverinfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServerInfo() {
		try {
			HostInfo hi = new HostInfo();
			List<ServerBean> result = hi.getServersInfo();
			return Response.ok().entity(new GenericEntity<List<ServerBean>>(result) {}).build();
		} catch (RemoteException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return Response.status(200).entity("ERROR").build();
		}
	}

}
