package it.poste.vsphere.controller;

import it.poste.vsphere.bean.VmBean;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

public class Test {

	public static void main(String[] args) {
		VmInfo vi;
		try {
			vi = new VmInfo();
			
			for (VmBean curr : vi.getVms()) {
				System.out.println(curr.getName() + " - " + curr.getHostedBy());
			}
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}
}
