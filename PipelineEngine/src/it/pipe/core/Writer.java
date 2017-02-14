package it.pipe.core;

import java.util.ArrayList;

public abstract class Writer extends PipeBlock {

	public abstract void write(ArrayList<String> input);
	
	
	public Writer() {
		super();
	}
	
	
	public Writer(String modName, String conFilePath) {
		super(modName, conFilePath);
	}
	

	public ArrayList<String> getOutput(ArrayList<String> input) {
		write(input);
		return input;
	}
	

	
}
