package it.pipe.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.csvreader.CsvReader;

public abstract class PipeBlock {

	public String moduleName;
	public String confPath;

	public Hashtable<String, String> config = new Hashtable<String, String>();

	public abstract ArrayList<String> getOutput(ArrayList<String> input);

	
	public PipeBlock() {
	}
	
	public void setProperty (String propertyName, String propertyValue) {
		this.config.put(propertyName, propertyValue);
	}
	
	
	public PipeBlock(String modName, String conFilePath) {
		if (conFilePath != null) {
			this.confPath = conFilePath;
			this.config = getConfig();
		}
		this.moduleName = modName;
	}

	public Hashtable<String, String> getConfig() {
		Hashtable<String, String> configMap = new Hashtable<String, String>();
		CsvReader reader;

		try {
			reader = new CsvReader(this.confPath);
			reader.readHeaders();
			while (reader.readRecord()){
				String currParam = reader.get("Param");
				String currValue = reader.get("Value");
				configMap.put(currParam, currValue);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return configMap;
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
	public void addConfiguration(String name, String prop) {
		this.config.put(name, prop);
	}


}
