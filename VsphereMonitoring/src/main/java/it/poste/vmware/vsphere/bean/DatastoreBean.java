package it.poste.vmware.vsphere.bean;

public class DatastoreBean {

	private String name;
	private long capacity;
	private long freeSpace;
	
	public DatastoreBean(String name, long capacity, long freeSpace) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.freeSpace = freeSpace;
	}
	public String getName() {
		return name;
	}
	public long getCapacity() {
		return capacity;
	}
	public long getFreeSpace() {
		return freeSpace;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
	public void setFreeSpace(long freeSpace) {
		this.freeSpace = freeSpace;
	}
	
	public void printInfo() {
		System.out.println("name: " + this.name);
		System.out.println("capacity : " + this.capacity);
		System.out.println("freeSpace: " + this.freeSpace);
	}

	
}
