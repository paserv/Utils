package it.osg.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;


public class SpreadsheetUtils {

	private static SpreadsheetService service;
	private FeedURLFactory factory;
	
	private static String spreadsheetID;
	private static String spreadsheetRootURL = "http://spreadsheets.google.com/feeds/spreadsheets/";
//	private static String spreadsheetRootURL = "https://docs.google.com/spreadsheets/d/";

	public SpreadsheetUtils (String user, String psw, String spreadID) {
		try {
			factory = FeedURLFactory.getDefault();
		    service = new SpreadsheetService("spreadsheetservice");
		    // Authenticate
		    service.setUserCredentials(user, psw);
		    
		    spreadsheetID = spreadsheetRootURL + spreadID;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private String getCellValue (int row, int column, int ws) {
		URL metafeedUrl;
		try {
			metafeedUrl = new URL(spreadsheetID);
			SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl, SpreadsheetEntry.class);
			URL cellFeedUrl = spreadsheet.getWorksheets().get(ws).getCellFeedUrl();
			CellQuery cq = new CellQuery(cellFeedUrl);
			cq.setMinimumRow(row);
			cq.setMaximumRow(row);
			cq.setMinimumCol(column);
			cq.setMaximumCol(column);
			CellFeed result = service.query(cq, CellFeed.class);
			List<CellEntry> cells = result.getEntries();
			return cells.get(0).getPlainTextContent();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<CellEntry> getRange (int minRow, int maxRow, int minCol, int maxCol, int ws) {
		URL metafeedUrl;
		try {
			metafeedUrl = new URL(spreadsheetID);
			SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl, SpreadsheetEntry.class);
			URL cellFeedUrl = spreadsheet.getWorksheets().get(ws).getCellFeedUrl();
			CellQuery cq = new CellQuery(cellFeedUrl);
			cq.setMinimumRow(minRow);
			cq.setMaximumRow(maxRow);
			cq.setMinimumCol(minCol);
			cq.setMaximumCol(maxCol);
			CellFeed result = service.query(cq, CellFeed.class);
			List<CellEntry> cells = result.getEntries();
			return cells;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Hashtable<String, String> getHashrange() {
		Hashtable<String, String> result = new Hashtable<String, String>();
		
		List<CellEntry> cells = getRange(1, 100, 1, 2, 0);
		for (int i = 0; i < cells.size(); i++) {
			String name = cells.get(i).getPlainTextContent().replace(" ", "%20");
			result.put(cells.get(i + 1).getPlainTextContent(), name);
			i++;
		}
		return result;
	}
	
	public static void main(String[] args) {

		SpreadsheetUtils su = new SpreadsheetUtils(Constants.GMAIL_USER, Constants.GMAIL_PSW, "0ApFZC7m0E5ixdGViY2RoQlJXRHFxTnlmeHZ3MzYyUnc");
		Hashtable<String,String> cells = su.getHashrange();
		Enumeration<String> enumer = cells.keys();
		while (enumer.hasMoreElements()) {
			String key = enumer.nextElement();
			System.out.println(key + " " + cells.get(key));
		}
		

	}

}
