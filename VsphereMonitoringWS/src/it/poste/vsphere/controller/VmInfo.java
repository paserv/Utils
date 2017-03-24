package it.poste.vsphere.controller;

import it.poste.vsphere.bean.ServerBean;
import it.poste.vsphere.bean.TopologyBean;
import it.poste.vsphere.bean.VmBean;
import it.poste.vsphere.conf.Conf;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class VmInfo {
	
	private static final String MANAGED_ENTITY = "VirtualMachine";
	
	private ArrayList<VmBean> vms = new ArrayList<VmBean>();
	private Hashtable<String, ServerBean> serversInfo = new Hashtable<String, ServerBean>();
	
	public VmInfo() throws RemoteException, MalformedURLException {
		Conf cf = new Conf();
		HostInfo hi = new HostInfo();
		for (String currUrl : cf.URLS) {
			ServiceInstance si;
			si = new ServiceInstance(new URL("https://" + currUrl + "/sdk"), cf.USERNAME, cf.PASSWORD, true);
			Folder rootFolder = si.getRootFolder();
			
			ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities(MANAGED_ENTITY);
			
			for (ManagedEntity curr : vms) {
				VirtualMachine currVm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", curr.getName());
				this.vms.add(new VmBean(currVm.getName(), currVm.getSummary().getStorage().getCommitted()/1073741824, currVm.getSummary().getRuntime().getPowerState().toString(), currVm.getSummary().getConfig().getAnnotation(), currVm.getSummary().getConfig().getMemorySizeMB(), currVm.getSummary().getConfig().getVmPathName().split(" ")[0], hi.getHostCodes().get(currVm.getSummary().getRuntime().getHost().get_value())));
				updateServerInfo(hi, currVm);
			}
		}
		
		
	}
	
	public VmInfo(String url) throws RemoteException, MalformedURLException {
		Conf cf = new Conf();
		HostInfo hi = new HostInfo();
			ServiceInstance si;
			si = new ServiceInstance(new URL("https://" + url + "/sdk"), cf.USERNAME, cf.PASSWORD, true);
			Folder rootFolder = si.getRootFolder();
			
			ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities(MANAGED_ENTITY);
			
			for (ManagedEntity curr : vms) {
				VirtualMachine currVm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", curr.getName());
				this.vms.add(new VmBean(currVm.getName(), currVm.getSummary().getStorage().getCommitted()/1073741824, currVm.getSummary().getRuntime().getPowerState().toString(), currVm.getSummary().getConfig().getAnnotation(), currVm.getSummary().getConfig().getMemorySizeMB(), currVm.getSummary().getConfig().getVmPathName().split(" ")[0], hi.getHostCodes().get(currVm.getSummary().getRuntime().getHost().get_value())));
				System.out.println(hi.getHostCodes().get(currVm.getSummary().getRuntime().getHost().get_value()));
				updateServerInfo(hi, currVm);
			}
	}
	
	private void updateServerInfo(HostInfo hi, VirtualMachine currVm) {
		if (currVm.getSummary().getRuntime().getPowerState().toString().equalsIgnoreCase("poweredOn")) {
			String ipServer = hi.getHostCodes().get(currVm.getSummary().getRuntime().getHost().get_value());
			ServerBean server = serversInfo.get(ipServer);
			if (server == null) {
				ServerBean newServer = new ServerBean(hi.getHostCodes().get(currVm.getSummary().getRuntime().getHost().get_value()), currVm.getSummary().getConfig().getNumCpu(), currVm.getSummary().getConfig().getMemorySizeMB());
				serversInfo.put(ipServer, newServer);
			} else {
				server.setAllocatedRam(server.getAllocatedRam() + currVm.getSummary().getConfig().getMemorySizeMB());
				server.setAllocatedCpu(server.getAllocatedCpu() + currVm.getSummary().getConfig().getNumCpu());
			}
		}
		
		
	}

	
	
	public void printInfo() {
		for (VmBean curr : vms) {
			curr.printInfo();
		}
		
		for (String curr : serversInfo.keySet()) {
			ServerBean sb = serversInfo.get(curr);
			sb.printInfo();
		}
	}

	public ArrayList<VmBean> getVms() {
		return vms;
	}

	public Hashtable<String, ServerBean> getServersInfo() {
		return serversInfo;
	}

	public void setVms(ArrayList<VmBean> vms) {
		this.vms = vms;
	}

	public void setServersInfo(Hashtable<String, ServerBean> serversInfo) {
		this.serversInfo = serversInfo;
	}
	
	public TopologyBean getTopologyBean() {
		TopologyBean tb = new TopologyBean();
		tb.setVms(getVms());
		tb.setServersInfo(new ArrayList<ServerBean>(getServersInfo().values()));
		return tb;
	}
}
