package it.test;
import java.util.ArrayList;

import com.independentsoft.exchange.ResolveNamesResponse;
import com.independentsoft.exchange.Service;
import com.independentsoft.exchange.ServiceException;

public class NameResolver {

	private String url;
	private String user;
	private String psw;

	private static String DEFAULT_URL = "https://scretecas01/ews/Exchange.asmx";
	private static String DEFAULT_USER = "servill7";
	private static String DEFAULT_PSW = "Paolos24"; 

	public static void main(String[] args) {
		NameResolver nr = new NameResolver();
		nr.resolve("servillo");
	}
	
	public NameResolver(){
		this.url = DEFAULT_URL;
		this.user = DEFAULT_USER;
		this.psw = DEFAULT_PSW;
	}

	public NameResolver(String url, String user, String psw){
		this.url = url;
		this.user = user;
		this.psw = psw;
	}

	public NameResolver(String user, String psw){
		this.url = DEFAULT_URL;
		this.user = user;
		this.psw = psw;
	}

	public ArrayList<String> resolve(String name) {
		ArrayList<String> result = new ArrayList<String>();

		Service service = new Service(this.url, this.user, this.psw);

		ResolveNamesResponse response;
		try {
			response = service.resolveNames(name);
			for (int i = 0; i < response.getResolutions().size(); i++) {
				response.getResolutions().get(i).getContact();
				if (response.getResolutions().get(i).getMailbox() != null && response.getResolutions().get(i).getMailbox().getEmailAddress() != null) {
					String mailAddress = response.getResolutions().get(i).getMailbox().getEmailAddress();
					System.out.println(mailAddress);
					result.add(mailAddress);
				}
			}
			return result;
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
}

