/* Copyright (c) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package it.osg.utils;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Using this demo, you can see how GData can read and write to individual cells
 * based on their position or send a batch of update commands in one HTTP
 * request.
 * 
 * Usage: java CellDemo --username [user] --password [pass] 
 */
public class Cell {


	/** The URL of the cells feed. */
	private URL cellFeedUrl;

	/** Our view of Google Spreadsheets as an authenticated Google user. */
	private SpreadsheetService service;

	/** A factory that generates the appropriate feed URLs. */
	private FeedURLFactory factory;

	public Cell(GSpreadService gs) {
		factory = gs.getFactory();
		service = gs.getService();
	}


	public void getSpreadsheet () {
		URL sheetListURL = factory.getSpreadsheetsFeedUrl();
		SpreadsheetQuery query = new SpreadsheetQuery(sheetListURL);
		query.setTitleQuery("Accounts");
		try {
			SpreadsheetFeed feed = service.getFeed(query, SpreadsheetFeed.class);
			System.out.println(feed.getEntries());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	

	public void loadWorksheet(String SpreadsheetKey, int ws) throws IOException,	ServiceException {
		
		// Load sheet
		URL metafeedUrl = new URL(SpreadsheetKey);
		
		SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl, SpreadsheetEntry.class);
		
		cellFeedUrl = spreadsheet.getWorksheets().get(ws).getCellFeedUrl();
		
		CellFeed assa = service.getFeed(cellFeedUrl, CellFeed.class);
		
		for (CellEntry cell : assa.getEntries()) {
			System.out.println(cell.getTitle().getPlainText());
		}
		
		CellQuery qqq = new CellQuery(cellFeedUrl);
		qqq.setMinimumRow(2);
		qqq.setMaximumRow(2);
		qqq.setMinimumCol(2);
		qqq.setMaximumCol(2);
		
		CellFeed result = service.query(qqq, CellFeed.class);
		List<CellEntry> cells = result.getEntries();
		System.out.println("qqq" + cells.get(0).getPlainTextContent());
		System.out.println(cellFeedUrl);
		
	}
	

	/**
	 * Sets the particular cell at row, col to the specified formula or value.
	 * 
	 * @param row the row number, starting with 1
	 * @param col the column number, starting with 1
	 * @param formulaOrValue the value if it doesn't start with an '=' sign; if it
	 *        is a formula, be careful that cells are specified in R1C1 format
	 *        instead of A1 format.
	 * @throws ServiceException when the request causes an error in the Google
	 *         Spreadsheets service.
	 * @throws IOException when an error occurs in communication with the Google
	 *         Spreadsheets service.
	 */
	
	public void setCell(int row, int col, String formulaOrValue)
			throws IOException, ServiceException {

		CellEntry newEntry = new CellEntry(row, col, formulaOrValue);
		
		service.insert(cellFeedUrl, newEntry);
	}

	
	
	/**
	 * Prints out the specified cell.
	 * 
	 * @param cell the cell to print
	 */
	public void printCell(int row, int col, String formulaOrValue) {
		
		CellEntry cell = new CellEntry(row, col, formulaOrValue);
		String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
		System.out.println(" -- Cell(" + shortId + "/" + cell.getTitle().getPlainText()
				+ ") formula(" + cell.getCell().getInputValue() + ") numeric("
				+ cell.getCell().getNumericValue() + ") value("
				+ cell.getCell().getValue() + ")");
	}

	



	

}
