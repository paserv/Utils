package it.poste.ntlm;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

public class Main {
	
	private static final String url = "https://noidiposte.poste";
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
	private static final String PROXY_HOST = "smwauthpi.noidiposte.poste";
	private static final String PROXY_DOMAIN = "rete";
	private static final int PROXY_PORT = 443;
	private static final String PROXY_USERNAME = "servill7";
	private static final String PROXY_PASSWORD = "Paolos50";
	private static final PrintWriter pw = new PrintWriter(System.out, true);

	public static void main(String[] args) throws Exception {
		Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
		        .register(AuthSchemes.NTLM, new JCIFSNTLMSchemeFactory())
		        .register(AuthSchemes.BASIC, new BasicSchemeFactory())
		        .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
		        .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
		        .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
		        .build();
		CloseableHttpClient httpClient = HttpClients.custom()
		        .setDefaultAuthSchemeRegistry(authSchemeRegistry)
		        .setRedirectStrategy(new LaxRedirectStrategy())
				.setDefaultCookieStore(new BasicCookieStore())
				.setSSLSocketFactory(builConnectionSocketFactory())
		        .setDefaultCredentialsProvider(getProxyAuthCredentialsProvider())
		        .build();

		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Host", "intranet.postepernoi.poste");
		request.setHeader("DNT", "1");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "it-IT,it;q=0.8,en-US;q=0.6,en;q=0.4");

		HttpResponse response = httpClient.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();
		pw.printf("Response Code : %d\n", responseCode);
	}
	
	private static CredentialsProvider getProxyAuthCredentialsProvider() throws UnknownHostException {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
				new AuthScope(PROXY_HOST, PROXY_PORT, AuthScope.ANY_REALM, "ntlm"),
				new NTCredentials(PROXY_USERNAME, PROXY_PASSWORD,
				InetAddress.getLocalHost().getHostName(), PROXY_DOMAIN));
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
}
