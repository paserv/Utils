package it.poste.ntlm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class NTLMPageDownloader {

	private CloseableHttpClient httpClient;
	private CloseableHttpResponse response;
	
	private static final String AUTH_HOST = "smwauthpi.noidiposte.poste";
	private static final String AUTH_DOMAIN = "rete.poste";
	private static final int AUTH_PORT = 443;
	
	private String USERNAME;
	private String PASSWORD;
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
	private static final String HOST = "intranet.postepernoi.poste";

	
	public static void main( String[] args ) {
		NTLMPageDownloader pd = new NTLMPageDownloader("servill7", "Paolos50");
		try {
			String result = pd.downloadPage("https://noidiposte.poste/il-consiglio-di-amministrazione-approva-i-risultati-al-31-marzo-2016/");
			System.out.println(result);
			
//			result = pd.downloadPage("https://intranet.postepernoi.poste/intervista-allad-francesco-caio-investendo-per-le-poste-2-0-riusciremo-a-digitalizzare-gli-italiani/");
//			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public NTLMPageDownloader(String username, String password) {
			this.USERNAME = username;
			this.PASSWORD = password;
			
			try {
				this.httpClient = HttpClientBuilder
						.create()
						.setRedirectStrategy(new LaxRedirectStrategy())
						.setDefaultCookieStore(new BasicCookieStore())
						.setSSLSocketFactory(builConnectionSocketFactory())
						.setDefaultCredentialsProvider(getProxyAuthCredentialsProvider())
						.build();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	

	private CredentialsProvider getProxyAuthCredentialsProvider() throws UnknownHostException {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
				new AuthScope(AUTH_HOST, AUTH_PORT, AuthScope.ANY_REALM, "ntlm"),
				new NTCredentials(this.USERNAME, this.PASSWORD,
				InetAddress.getLocalHost().getHostName(), AUTH_DOMAIN));
		return credentialsProvider;
	}

	private static SSLConnectionSocketFactory builConnectionSocketFactory() throws Exception {
		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
					public boolean isTrusted(final X509Certificate[] chain,
						final String authType) throws CertificateException {
						return true;
					}
				}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return sslsf;
	}
	
	private static void setConfig(HttpRequestBase request) {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(0).setSocketTimeout(0).build();
		request.setConfig(config);
	}
	
	private void closeConnection() {
		try {
			this.response.close();
			this.httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String downloadPage(String url) throws Exception {
		HttpGet request = new HttpGet(url);
		setConfig(request);
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Host", HOST);
		request.setHeader("DNT", "1");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "it-IT,it;q=0.8,en-US;q=0.6,en;q=0.4");

		this.response = httpClient.execute(request);
		
		int responseCode = response.getStatusLine().getStatusCode();
		System.out.println("responseCode: " + responseCode);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null)
			result.append(String.format("%s\n", line));

		return result.toString();
	}
	
	public String downloadPage(String url, String lightUrl4Auth) throws Exception {
		downloadPage(lightUrl4Auth);
		String result = downloadPage(url);
		closeConnection();
		return result;
	}
}


