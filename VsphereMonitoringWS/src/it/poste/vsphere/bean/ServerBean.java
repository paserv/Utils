package it.poste.vsphere.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServerBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ip;
	private int allocatedCpu;
	private int allocatedRam;
	
	public ServerBean(String ip, int allocatedCpu, int allocatedRam) {
		super();
		this.ip = ip;
		this.allocatedCpu = allocatedCpu;
		this.allocatedRam = allocatedRam;
	}
	
	public String getIp() {
		return ip;
	}
	public int getAllocatedCpu() {
		return allocatedCpu;
	}
	public int getAllocatedRam() {
		return allocatedRam;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setAllocatedCpu(int allocatedCpu) {
		this.allocatedCpu = allocatedCpu;
	}
	public void setAllocatedRam(int allocatedRam) {
		this.allocatedRam = allocatedRam;
	}

	public void printInfo() {
		System.out.println("ip: " + this.ip);
		System.out.println("allocatedCpu: " + this.allocatedCpu);
		System.out.println("allocatedRam: " + this.allocatedRam);
		
	}
	
	
	
}
