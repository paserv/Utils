package it.osg.data;

public class FacebookCredential {

	private String appId;
	private String appKey;
	private String appAccessToken;
	
	
	
	public FacebookCredential(String appId, String appKey, String appAccessToken) {
		this.appId = appId;
		this.appKey = appKey;
		this.appAccessToken = appAccessToken;
	}
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getAppAccessToken() {
		return appAccessToken;
	}
	public void setAppAccessToken(String appAccessToken) {
		this.appAccessToken = appAccessToken;
	}
	
	
	
	
}
