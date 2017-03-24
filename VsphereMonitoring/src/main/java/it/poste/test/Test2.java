package it.poste.test;

import it.poste.vmware.vsphere.DatastoreInfo;
import it.poste.vmware.vsphere.VmInfo;
import it.poste.vmware.vsphere.conf.Conf;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class Test2 {

	public static void main(String[] args) {
		try {
			DatastoreInfo di = new DatastoreInfo();
			di.printInfo();
			
			VmInfo vi = new VmInfo(Conf.URLS[1]);
			vi.printInfo();
			
			VmInfo vi2 = new VmInfo(Conf.URLS[0]);
			vi2.printInfo();
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
}
