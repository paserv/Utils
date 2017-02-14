package it.pipe.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.csvreader.CsvReader;

public class PipelineEngine {

	private String configFilePath;
	private static String methodName = "getOutput";
	
	private static ArrayList<PipeModule> allModules;
	private static ArrayList<PipeBlock> allBlocks;
	private static ArrayList<String> input;
	
	private static Logger LOGGER = Logger.getLogger(PipelineEngine.class.getName());

	public static void main(String[] args) {

		PipelineEngine eng = new PipelineEngine("conf/pipeline/tagPipeline.txt");
		eng.run();
				
	}

	public PipelineEngine() {

	}
	
	public PipelineEngine(String configEngineFilePath) {
		setConfigFilePath(configEngineFilePath);
		allModules = getModules();
	}
	
	
	private ArrayList<String> runBlocks() {
		
		ArrayList<String> output = new ArrayList<String>();
		if (input != null) {
			output = input;
		}
		
		ArrayList<String> temp = new ArrayList<String>();
				
		int size = allBlocks.size();
		for (int i = 0; i < size; i++){
			
			PipeBlock currBlock = allBlocks.get(i);
			LOGGER.info("\t\t\tStep " + i + ": " + currBlock.getClass().getName());
			int initialSize = output.size();
			LOGGER.info("\t\t\tInitial size: " + initialSize);
			temp = currBlock.getOutput(output);
			output = temp;
			int finalSize = output.size();
			if ((initialSize - finalSize) > 0) {
				LOGGER.info("\t\t\tRemoved: " + (initialSize - finalSize) + " Items");
			} else {
				LOGGER.info("\t\t\tAdded: " + (finalSize - initialSize) + " Items");
			}
			
			LOGGER.info("\t\t\tFinal size: " + finalSize);
			
		}
		
		LOGGER.info("\t\t\tFinal size is " + output.size());
		return output;
	}
	
	public ArrayList<String> run() {
		
		if (this.getConfigFilePath() == null) {
			return runBlocks();
		} else {
			ArrayList<String> output = new ArrayList<String>();
			ArrayList<String> temp = new ArrayList<String>();
			
			int size = allModules.size();
			for (int i = 0; i < size; i++){
					String currModule = allModules.get(i).getModuleName();
					try {
						Class currMod = Class.forName(currModule);
						Constructor constr = currMod.getConstructor(String.class, String.class);
						Object currObj = constr.newInstance(allModules.get(i).getModuleName(), allModules.get(i).getConfPath());
						
						Method getName = currMod.getMethod("getModuleName", null);
						
						Method[] met = currMod.getMethods();
						for (int j=0; j < met.length; j++) {
							Method currMet = met[j];
							if (currMet.getName().equalsIgnoreCase(methodName)) {
								LOGGER.info("\t\t\tStep " + i + ": " + getName.invoke(currObj));
								int initialSize = output.size();
								LOGGER.info("\t\t\tInitial size: " + initialSize);
								temp = (ArrayList<String>) currMet.invoke(currObj, output);
								output = temp;
								int finalSize = output.size();
								if ((initialSize - finalSize) > 0) {
									LOGGER.info("\t\t\tRemoved: " + (initialSize - finalSize) + " Items");
								} else {
									LOGGER.info("\t\t\tAdded: " + (finalSize - initialSize) + " Items");
								}
								LOGGER.info("\t\t\tFinal size: " + finalSize);
							}
						}
						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				
			}
			
			LOGGER.info("\t\t\tFinal size is " + output.size());
			return output;
		}
		
		
	}

	
	

	public ArrayList<PipeModule> getModules() {
		ArrayList<PipeModule> result = new ArrayList<PipeModule>();
		CsvReader reader;
		try {
			reader = new CsvReader(configFilePath);
			reader.readHeaders();
			while (reader.readRecord()){
				String currModule = reader.get("module");
				String currConf = reader.get("conf");
				PipeModule currPM = new PipeModule(currModule, currConf);
				result.add(currPM);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public static ArrayList<PipeModule> getAllModules() {
		return allModules;
	}

	public static void setAllModules(ArrayList<PipeModule> allModules) {
		PipelineEngine.allModules = allModules;
	}
	
	public static void addAllModules(PipeModule module) {
		PipelineEngine.allModules.add(module);
	}

	public ArrayList<String> getInput() {
		return input;
	}

	public void setInput(ArrayList<String> input) {
		PipelineEngine.input = input;
	}
	
	
	
	public void addBlock(PipeBlock block) {
		if (this.allBlocks == null) {
			this.allBlocks = new ArrayList<PipeBlock>();
		}
		this.allBlocks.add(block);
	}

}
