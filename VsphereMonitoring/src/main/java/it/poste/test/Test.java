package it.poste.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class Test {

	private static String url = "https://10.24.5.52/sdk";
	private static String username = "Administrator@vsphere.local";
	private static String password = "Password.01";
	
	public static void main(String[] args) {
		System.getProperties().put("http.proxyHost", "10.204.68.5");
		System.getProperties().put("http.proxyPort", "8080");
		System.getProperties().put("http.proxyUser", "rete\\servill7");
		System.getProperties().put("http.proxyPassword", "Paolos51");
		HttpsConnectionUtil.trustAllHosts();
		ServiceInstance si;
		try {
			si = new ServiceInstance(new URL(url), username, password, true);
			System.out.println(si);
			
			Folder rootFolder = si.getRootFolder();

			//Note: It is expected to pass all the parameters as per your environment

			//Getting hold of a datacenter in vCenter server
//			String dcName = "IND-BLR";
//			Datacenter datacenter = null;
//			datacenter = (Datacenter) new InventoryNavigator(rootFolder).searchManagedEntity("Datacenter", dcName);
//			System.out.println("Data center Name::" + datacenter.getName());

			//Getting hold of All datacenters in vCenter server
//			ManagedEntity[] dcenters = new InventoryNavigator(rootFolder).searchManagedEntities("Datacenter");
//			System.out.println("Number of Datacenters in vCenter::" + dcenters.length);

			//Getting hold of a host in vCenter server
//			String hostName = "10.192.34.2";
//			HostSystem host = null;
//			host = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", hostName);
//			System.out.println("Host Name::" + host.getName());

//			Getting hold of  All hosts in vCenter server
			ManagedEntity[] hosts = new InventoryNavigator(rootFolder).searchManagedEntities("HostSystem");
			System.out.println("Number of hosts in vCenter ::" + hosts.length); 

			for (ManagedEntity curr : hosts) {
				HostSystem currHost = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", curr.getName());
				System.out.println(currHost.getName());
				System.out.println(currHost.getSummary().getHost().get_value());
				System.out.println(currHost.getSummary().getQuickStats().getOverallCpuUsage());
				System.out.println(currHost.getSummary().getQuickStats().getOverallMemoryUsage());
			}

			//Getting hold of a cluster in vCenter server
//			String clusterName = "My-Cluster";
//			ClusterComputeResource cluster = null;
//			cluster = (ClusterComputeResource) new InventoryNavigator(rootFolder)
//			.searchManagedEntity("ClusterComputeResource", hostName);
//			System.out.println("Cluster Name::" + cluster.getName());

			//Getting hold of All clusters in vCenter server
//			ManagedEntity[] clusters = new InventoryNavigator(rootFolder)
//			.searchManagedEntities("ClusterComputeResource");
//			System.out.println("Number of clusters in vCenter ::" + clusters.length);

			//Getting hold of a datastore in vCenter server

//			String DSName = "NAS_1";
//			Datastore datastore = null;
//			datastore = (Datastore) new InventoryNavigator(rootFolder).searchManagedEntity("Datastore", DSName);
//			System.out.println("Datastore Name::" + datastore.getName());
//			System.out.println(datastore.getSummary().getCapacity());
//			
//			//Getting hold of All datastores in vCenter server
//			ManagedEntity[] datastores = new InventoryNavigator(rootFolder).searchManagedEntities("Datastore");
//			System.out.println("Number of datastores in vCenter ::"+ datastores.length);
//			for (ManagedEntity curr : datastores) {
//				Datastore currDatastore = (Datastore) new InventoryNavigator(rootFolder).searchManagedEntity("Datastore", curr.getName());
//				System.out.println(currDatastore.getName() + " " + currDatastore.getSummary().getCapacity()/1073741824 + " GB " + currDatastore.getSummary().getFreeSpace()/1073741824);
//				
//			}

			//Getting hold of a VM in vCenter server
//			String VMName = "RICEVO_LOADBALANCER";
//			VirtualMachine vm = null;
//			vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", VMName);
//			System.out.println("VM Name::" + vm.getName());
//			System.out.println("VM Host::" + vm.getRuntime().getHost().get_value());

			//Getting hold of  All VMs in vCenter server
			ManagedEntity[] vms = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
			System.out.println("Number of VMs in vCenter ::"+ vms.length);

			for (ManagedEntity curr : vms) {
				VirtualMachine currVm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", curr.getName());
				System.out.println(currVm.getName() + " " + currVm.getSummary().getStorage().getCommitted()/1073741824 + " GB ");
				System.out.println(currVm.getSummary().getRuntime().getPowerState().toString());
				System.out.println(currVm.getSummary().getConfig().getAnnotation());
				System.out.println(currVm.getSummary().getConfig().getMemorySizeMB());
				System.out.println(currVm.getSummary().getConfig().getVmPathName().split(" ")[0]);
				System.out.println("");
			}
			
			//Getting hold of a Resource pool in vCenter server
//			String ResourcePoolName = "My-RP";
//			VirtualMachine rpool = null;
//			rpool = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("ResourcePool", ResourcePoolName);
//			System.out.println("VM Name::" + rpool.getName());

			//Getting hold of All resource pool in vCenter server
//			ManagedEntity[] rpools = new InventoryNavigator(rootFolder).searchManagedEntities("ResourcePool");
//			System.out.println("Number of VMs in vCenter ::"+ rpools.length);

			si.getServerConnection().logout();
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}
}
