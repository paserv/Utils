package it.osg.utils;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

public class GSpreadService {

	/** Our view of Google Spreadsheets as an authenticated Google user. */
	  private SpreadsheetService service;
	  
	  /** A factory that generates the appropriate feed URLs. */
	  private FeedURLFactory factory;
	  
	  
	  public GSpreadService(String username, String password) throws Exception {
		    factory = FeedURLFactory.getDefault();
		    service = new SpreadsheetService("spreadsheetservice");
		    // Authenticate
		    service.setUserCredentials(username, password);
		  }


	public SpreadsheetService getService() {
		return service;
	}


	public void setService(SpreadsheetService service) {
		this.service = service;
	}


	public FeedURLFactory getFactory() {
		return factory;
	}


	public void setFactory(FeedURLFactory factory) {
		this.factory = factory;
	}
	  
	  
	
}
