package it.pipe.transformers;

import java.util.ArrayList;

import it.pipe.core.Transformer;

public class DummyTransformer extends Transformer {

	public DummyTransformer() {
		super();
	}
	
	public DummyTransformer(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {
		return input;
	}

}
