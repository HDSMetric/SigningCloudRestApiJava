/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class verifyEKYC {
	public static JSONObject ocr(String apikey, String apiSecret) throws Exception {
		JSONObject retJson = null;
		JSONObject jsonAccessToken = null;
		JSONObject jsonData = new JSONObject();
	    JSONObject signinfo1 = new JSONObject();
	     
		String data = null;
		String mac = null;
		String macCheck = null; 
		String output = null;
		String plainData = null;
		JSONObject jsonObj = null;
		
		String email=" "; //user email
		String name=" "; // user real name
		String icfront="C:\\Users\\.....\\icFront.jpg"; //ic front image location
		String icback="C:\\Users\\......\\icBack.jpg"; //ic back image location
		String icfrontflash="C:\\Users\\.....\\icFrontFlash.jpg"; //ic front image with flash location
		String doctype = "mykad";
	
		String icfrontBs64,icbackBs64,icfrontflashBs64;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		 System.out.println("accessToken:"+accessToken);
		
		byte[] fImg = Utils.readBytesFromFile(icfront);
		icfrontBs64 = Base64.getEncoder().encodeToString(fImg);
		byte[] ffImg = Utils.readBytesFromFile(icfrontflash);
		icfrontflashBs64 = Base64.getEncoder().encodeToString(ffImg);
		byte[] bImg = Utils.readBytesFromFile(icback);
		icbackBs64 = Base64.getEncoder().encodeToString(bImg);
		 
	      signinfo1.put("name",name); 
	      signinfo1.put("email", email);
	      jsonData.put("docType", doctype);
	      jsonData.put("idFront", icfrontBs64);
	      jsonData.put("idFrontFlash", icfrontflashBs64);
	      jsonData.put("idBack", icbackBs64);
	      jsonData.put("signerInfo", signinfo1);
	      System.out.println(jsonData.toString());
	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 try {
			CloseableHttpClient httpclient = getCloseableHttpClient();

			HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
			// REST API call
			 HttpPost postRequest = new HttpPost("/signserver/v1/user/ekycimages/ocr");  
			
		      
		      List<NameValuePair> params = new ArrayList<NameValuePair>();
		      params.add(new BasicNameValuePair("accesstoken", accessToken));
		      params.add(new BasicNameValuePair("mac", mac));
		      params.add(new BasicNameValuePair("data", data));
		  	  postRequest.setEntity(new UrlEncodedFormEntity(params));
		      HttpResponse httpResponse = httpclient.execute(target,postRequest);	     
		      HttpEntity entity = httpResponse.getEntity();
		     
		      if (entity != null) {
		    	  String response=EntityUtils.toString(entity);
		    	  System.out.println("response:"+response);
		    	  
		      }
		      
			} catch (Exception e) {
		    	e.printStackTrace();
		    }
		
		return retJson;
		// TODO Auto-generated method stub
		
	}
	
	public static JSONObject verify(String apikey, String apiSecret) throws Exception {
		JSONObject retJson = null;
		JSONObject jsonAccessToken = null;
		JSONObject jsonData = new JSONObject();
	    JSONObject signinfo1 = new JSONObject();
	     
		String data = null;
		String mac = null;
		String macCheck = null; 
		String output = null;
		String plainData = null;
		JSONObject jsonObj = null;
		
		String email=" "; // user email
		String name=" "; //user real name
		String doctype = "mykad";
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		 System.out.println("accessToken:"+accessToken);
		
	      signinfo1.put("name",name); 
	      signinfo1.put("email", email);
	      jsonData.put("docType", doctype);
	      jsonData.put("signerInfo", signinfo1);

	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 		
		 try {
				CloseableHttpClient httpclient = getCloseableHttpClient();

				HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
				// REST API call
				 HttpPost postRequest = new HttpPost("/signserver/v1/user/ekycimages/verify");  
				
			      
			      List<NameValuePair> params = new ArrayList<NameValuePair>();
			      params.add(new BasicNameValuePair("accesstoken", accessToken));
			      params.add(new BasicNameValuePair("mac", mac));
			      params.add(new BasicNameValuePair("data", data));
			  	  postRequest.setEntity(new UrlEncodedFormEntity(params));
			      HttpResponse httpResponse = httpclient.execute(target,postRequest);
			      HttpEntity entity = httpResponse.getEntity();
			     
			      if (entity != null) {
			    	  String response=EntityUtils.toString(entity);
			    	  System.out.println("response:"+response);
			    	  
			      }
			      
				} catch (Exception e) {
			    	e.printStackTrace();
			    }
		
		return retJson;
		// TODO Auto-generated method stub
		
	}
	
	public static JSONObject face(String apikey, String apiSecret) throws Exception {
		JSONObject retJson = null;
		JSONObject jsonAccessToken = null;
		JSONObject jsonData = new JSONObject();
	    JSONObject signinfo1 = new JSONObject();
	     
		String data = null;
		String mac = null;
		String macCheck = null; 
		String output = null;
		String plainData = null;
		JSONObject jsonObj = null;
		
		String email=" "; //user email address
		String name=" "; //user real name
		String selfie="C:\\Users\\....\\originalselfie.jpg"; //selfie photo location 

		String selfieBs64;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		 System.out.println("accessToken:"+accessToken);
		
		byte[] sImg = Utils.readBytesFromFile(selfie);
		selfieBs64 = Base64.getEncoder().encodeToString(sImg);
		 
	      signinfo1.put("name",name); 
	      signinfo1.put("email", email);
	      jsonData.put("selfie", selfieBs64);
	      jsonData.put("signerInfo", signinfo1);

	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking 
		 mac = Utils.calculateMac(data,apiSecret);
		 
		
		 try {
				CloseableHttpClient httpclient = getCloseableHttpClient();

				HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
				// REST API call
				 HttpPost postRequest = new HttpPost("/signserver/v1/user/ekycimages/face");  
				
			      
			      List<NameValuePair> params = new ArrayList<NameValuePair>();
			      params.add(new BasicNameValuePair("accesstoken", accessToken));
			      params.add(new BasicNameValuePair("mac", mac));
			      params.add(new BasicNameValuePair("data", data));
			  	  postRequest.setEntity(new UrlEncodedFormEntity(params));
			      HttpResponse httpResponse = httpclient.execute(target,postRequest);
			      HttpEntity entity = httpResponse.getEntity();
			     
			      if (entity != null) {
			    	  String response=EntityUtils.toString(entity);
			    	  System.out.println("response:"+response);
			    	  
			      }
			      
				} catch (Exception e) {
			    	e.printStackTrace();
			    }
		
		return retJson;
		// TODO Auto-generated method stub
		
	}
	
	private static CloseableHttpClient getCloseableHttpClient()	{
	    CloseableHttpClient httpClient = null;
	    try {
	        httpClient = HttpClients.custom().
	                setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).
	                setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
	                {
	                    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException
	                    {
	                        return true;
	                    }
	                }).build()).build();
	    } catch (KeyManagementException e) {
	        System.out.println("KeyManagementException in creating http client instance: " + e);
	    } catch (NoSuchAlgorithmException e) {
	    	System.out.println("NoSuchAlgorithmException in creating http client instance"+e);
	    } catch (KeyStoreException e) {
	    	System.out.println("KeyStoreException in creating http client instance"+ e);
	    }
	    return httpClient;
	}
}
