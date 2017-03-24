package it.poste.vsphere.controller;

import it.poste.vsphere.bean.ServerBean;
import it.poste.vsphere.conf.Conf;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

public class HostInfo {

	private static final String MANAGED_ENTITY = "HostSystem";
	
	private Hashtable<String, String> hostCodes = new Hashtable<String, String>();
	private ArrayList<ServerBean> serversInfo = new ArrayList<ServerBean>();
	
	public HostInfo() throws RemoteException, MalformedURLException {
		Conf cf = new Conf();
		for (String currUrl : cf.URLS) {
			ServiceInstance si;
			si = new ServiceInstance(new URL("https://" + currUrl + "/sdk"), cf.USERNAME, cf.PASSWORD, true);
			Folder rootFolder = si.getRootFolder();
			
			ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities(MANAGED_ENTITY);

			for (ManagedEntity curr : hosts) {
				HostSystem currHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity(MANAGED_ENTITY, curr.getName());
				hostCodes.put(currHost.getSummary().getHost().get_value(), currHost.getName());
				serversInfo.add(new ServerBean(currHost.getName(), currHost.getSummary().getQuickStats().getOverallCpuUsage(), currHost.getSummary().getQuickStats().getOverallMemoryUsage()));
			}
		}
		
	}

	public Hashtable<String, String> getHostCodes() {
		return hostCodes;
	}

	public void setHostCodes(Hashtable<String, String> hostCodes) {
		this.hostCodes = hostCodes;
	}
	
	public ArrayList<ServerBean> getServersInfo() {
		return serversInfo;
	}

	public void setServersInfo(ArrayList<ServerBean> serversInfo) {
		this.serversInfo = serversInfo;
	}
	
	public void printInfo(){
		for (String curr : hostCodes.keySet()) {
			System.out.println("Key: " + curr + " Value: " + hostCodes.get(curr));
		}
	}

}
