package it.poste.vsphere.conf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Conf {

	public String[] URLS;
	public String USERNAME;
	public String PASSWORD;
	
	public Conf() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			prop.load(input);
			this.URLS = prop.getProperty("urls").split(",");
			this.USERNAME = prop.getProperty("username");
			this.PASSWORD = prop.getProperty("password");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
