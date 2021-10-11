/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import android.util.Log; 

public class AccessToken {

	public static JSONObject acquireAccessToken(String apikey, String apisecret) throws Exception {
		JSONObject retJson = null ;
		JSONObject jsonObj = null;
		String data = null;
		String mac = null; 
		String state = "";
		String at = null;
		String macCheck = null; 
		String output = null;
		String plainData = null;
	
		CloseableHttpClient httpclient=Utils.getHttpClient();
		
	    try {
	      // specify the host, protocol, and port
	      HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
	     
	      // specify the get request
	      HttpGet getRequest = new HttpGet("/signserver/v1/accesstoken"+"?client_id="+apikey+"&state="+state);
	      
	      HttpResponse httpResponse = httpclient.execute(target, getRequest);
	      HttpEntity entity = httpResponse.getEntity();
	      if (entity != null) {
	    	  String response=EntityUtils.toString(entity);
	    	//  System.out.println(response);
	    	  jsonObj = Utils.getJsonObjectFromString(response);
	    	  System.out.println(jsonObj.toString());
	    	  mac = jsonObj.get("mac").toString();
	    	  data = jsonObj.get("data").toString();
	    	
	    	  //System.out.println(data);
	    	  if (mac.equals(Utils.SHA256(new StringBuilder(data).append(apisecret).toString().getBytes()))) {
	    		  String jsonResponseString = new String(Utils.aesEcbPkcs5PaddingDecrypt(Hex.decodeHex(data),apisecret));
	    		  System.out.println(jsonResponseString);
	    		  retJson = Utils.getJsonObjectFromString(jsonResponseString);
	    		  at = retJson.get("at").toString();
	    		  //System.out.println(at);
	    	  }
	      }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		return retJson;
	}




}