package it.pipe.core;

public class PipeModule {

	public String moduleName;
	public String confPath;
	
	
	public PipeModule(String modName, String confPath) {
		this.confPath = confPath;
		this.moduleName = modName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getConfPath() {
		return confPath;
	}
	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}
	
	
	
}
