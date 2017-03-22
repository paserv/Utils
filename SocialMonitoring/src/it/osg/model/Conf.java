package it.osg.model;

import com.google.appengine.api.datastore.Key;

public class Conf {

	private Key datastoreId;

	private String id;
	private String url;


	public Conf() {

	}

	public Conf(Key datastoreId, String id, String domandaDeploy) {

	}


	public Key getDatastoreId() {
		return datastoreId;
	}


	public void setDatastoreId(Key datastoreId) {
		this.datastoreId = datastoreId;
	}



	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Conf [datastoreId=" + datastoreId + ", url=" + url + "]";
	}

}
