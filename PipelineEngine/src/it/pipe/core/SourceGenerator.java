package it.pipe.core;

import java.util.ArrayList;

public abstract class SourceGenerator extends PipeBlock {

	public abstract ArrayList<String> add();
	
	public SourceGenerator() {
		super();
	}
	
	public SourceGenerator(String modName, String conFilePath) {
		super(modName, conFilePath);
	}
	

	public ArrayList<String> getOutput(ArrayList<String> input) {
		input.addAll(add());
		return input;
	}


}
