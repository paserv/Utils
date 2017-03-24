package it.poste.vsphere.bean;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TopologyBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<VmBean> vms = new ArrayList<VmBean>();
	private ArrayList<ServerBean> serversInfo = new ArrayList<ServerBean>();
	
	public ArrayList<VmBean> getVms() {
		return vms;
	}
	public ArrayList<ServerBean> getServersInfo() {
		return serversInfo;
	}
	public void setVms(ArrayList<VmBean> vms) {
		this.vms = vms;
	}
	public void setServersInfo(ArrayList<ServerBean> serversInfo) {
		this.serversInfo = serversInfo;
	}
	
	
	
}
