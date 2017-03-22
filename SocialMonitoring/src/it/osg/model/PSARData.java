package it.osg.model;

import java.util.ArrayList;

public class PSARData {

	public String idTransaction = "";
	public String pageId = "";
	public double postFromPageCount;
	public double postFromFanCount;
	public double commentsCount;
	public ArrayList<String> authors = new ArrayList<String>();
	public double likesCount;
	public double sharesCount;
	
	public double viewCount;
	public double dislikesCount;
	public double favouriteCount;
	
	public double commentsToPostFromFan;
	public double commnetsFromPageToPostFromPage;
	public double commnetsFromPageToPostFromFan;
	
}
