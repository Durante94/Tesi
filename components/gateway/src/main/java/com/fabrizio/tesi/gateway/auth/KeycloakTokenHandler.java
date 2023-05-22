package com.fabrizio.tesi.gateway.auth;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class KeycloakTokenHandler {

	@Value("${keycloack.feingress}")
	String HOST;

	@Value("${keycloack.oidcaddr}")
	String OIDCHOST;

	@Value("${keycloack.clientid}")
	String clientid;

	@Value("${auth.secret}")
	String CLIENT_SECRET;

	@Value("${keycloack.realm}")
	String realm;

	protected String postData;

	public KeycloakTokenHandler() {
		this.postData = "";
		disableCertificateValidation();
	}

	protected abstract String getGrantType();

	protected abstract void setPostDataParam(String value);

	private void disableCertificateValidation() {
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			throw new RuntimeException("Failed to disable certificate validation", e);
		}
	}

	public String getToken(String value) throws IOException {
		setPostDataParam(value);
		URL url = new URL(OIDCHOST + "/realms/" + realm + "/protocol/openid-connect/token");
		// HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		String postData = "client_secret=" + CLIENT_SECRET + "&client_id=" + clientid + "&grant_type=" + getGrantType()
				+ "&" + this.postData + "&redirect_uri=" + HOST + "/gateway/auth&scope=openid";
		byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
		con.setDoOutput(true);
		try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
			dos.write(postDataBytes);
		}

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				return response.toString();
			}
		} else {
			throw new RuntimeException(
					"Failed to retrieve token from Keycloak, response code: " + con.getResponseMessage());
		}
	}

	public boolean deleteToken(String idToken) throws IOException {
		URL url = new URL(OIDCHOST + "/realms/" + realm + "/protocol/openid-connect/logout");
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		String postData = "client_secret=" + CLIENT_SECRET + "&refresh_token=" + idToken + "&client_id=" + clientid;
		byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
		con.setDoOutput(true);
		try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
			dos.write(postDataBytes);
		}

		int responseCode = con.getResponseCode();
		return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
	}
}
