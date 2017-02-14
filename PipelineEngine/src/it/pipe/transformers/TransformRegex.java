package it.pipe.transformers;

import it.pipe.core.Filter;

import java.util.ArrayList;

public class TransformRegex extends Filter {

	public TransformRegex() {
		super();
	}
	
	public TransformRegex(String modName, String conFilePath) {
		super(modName, conFilePath);
	}

	@Override
	public ArrayList<String> getOutput(ArrayList<String> input) {

		ArrayList<String> result = new ArrayList<String>();
				
		for (int i = 0; i < input.size(); i++){
			String currToken = input.get(i);
			
			String resultToken = "";
			
			String[] splittedToken = currToken.split(" ");
			
			for (int j=0; j<splittedToken.length; j++){
				String currSplittedToken = splittedToken[j];
				if (!currSplittedToken.matches(config.get("regex"))){
					resultToken = resultToken + " " + currSplittedToken;
				} else {
					//System.out.println("Mail found: " + currSplittedToken);
				}
			}
			
			result.add(resultToken);
						
		}
		return result;
	}

	

}
