package it.poste.vmware.vsphere;

import it.poste.vmware.vsphere.bean.DatastoreBean;
import it.poste.vmware.vsphere.conf.Conf;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

public class DatastoreInfo {

	private static final String MANAGED_ENTITY = "Datastore";

	private ArrayList<DatastoreBean> datastores = new ArrayList<DatastoreBean>();
	
	public DatastoreInfo() throws RemoteException, MalformedURLException {
		ServiceInstance si;
		si = new ServiceInstance(new URL("https://" + Conf.URLS[1] + "/sdk"), Conf.USERNAME, Conf.PASSWORD, true);
		Folder rootFolder = si.getRootFolder();
		
		ManagedEntity[] datastores = new InventoryNavigator(rootFolder).searchManagedEntities(MANAGED_ENTITY);

		for (ManagedEntity curr : datastores) {
			Datastore currDatastore = (Datastore) new InventoryNavigator(rootFolder).searchManagedEntity(MANAGED_ENTITY, curr.getName());
			this.datastores.add(new DatastoreBean(currDatastore.getName(), currDatastore.getSummary().getCapacity()/1073741824, currDatastore.getSummary().getFreeSpace()/1073741824));
//			System.out.println("Datastore name: " + currDatastore.getName() + " Capacity: " + currDatastore.getSummary().getCapacity()/1073741824 + " Free Space: " + currDatastore.getSummary().getFreeSpace()/1073741824);
		}
		
	}
	
	public void printInfo() {
		for (DatastoreBean curr : datastores) {
			curr.printInfo();
		}
	}
}
