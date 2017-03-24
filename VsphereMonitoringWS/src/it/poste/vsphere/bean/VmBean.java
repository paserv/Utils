package it.poste.vsphere.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VmBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private long usedSpace;
	private String powerState;
	private String annotation;
	private Integer allocatedRam;
	private String path;
	private String hostedBy;
	
	public VmBean(String name, long usedSpace, String powerState,
			String annotation, Integer allocatedRam, String path, String hostedBy) {
		super();
		this.name = name;
		this.usedSpace = usedSpace;
		this.powerState = powerState;
		this.annotation = annotation;
		this.allocatedRam = allocatedRam;
		this.path = path;
		this.hostedBy = hostedBy;
	}
	
	public String getName() {
		return name;
	}
	public long getUsedSpace() {
		return usedSpace;
	}
	public String getPowerState() {
		return powerState;
	}
	public String getAnnotation() {
		return annotation;
	}
	public Integer getAllocatedRam() {
		return allocatedRam;
	}
	public String getPath() {
		return path;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}
	public void setPowerState(String powerState) {
		this.powerState = powerState;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public void setAllocatedRam(Integer allocatedRam) {
		this.allocatedRam = allocatedRam;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getHostedBy() {
		return hostedBy;
	}

	public void setHostedBy(String hostedBy) {
		this.hostedBy = hostedBy;
	}	
	public void printInfo() {
		System.out.println("name: " + this.name);
		System.out.println("usedSpace : " + this.usedSpace);
		System.out.println("powerState: " + this.powerState);
		System.out.println("annotation: " + this.annotation);
		System.out.println("allocatedRam: " + this.allocatedRam);
		System.out.println("path: " + this.path);
		System.out.println("hostedBy: " + this.hostedBy);
	}
	
}
