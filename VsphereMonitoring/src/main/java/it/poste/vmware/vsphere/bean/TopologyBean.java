package it.poste.vmware.vsphere.bean;

import java.util.ArrayList;

public class TopologyBean {

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
