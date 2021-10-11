/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.time.Instant;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GetSignatureURL {

	public static JSONObject auto(String apiKey, String apiSecret)  throws Exception{
		
		String name="...";//signer name
		String email="....";//signer email
		String verifycode="";
		String signkeyword="please sign";// sign keyword on document
		String sealkeyword="";
		
		String contractnum = "....";//contract number
	
		String callUrl;
		String data = null;
		String mac = null;
		JSONObject retJson = null;
		JSONObject jsonData = new JSONObject();
	    JSONObject signinfo1 = new JSONObject();
	  
	    JSONObject jsonObj = null;
		JSONObject jsonAccessToken = null;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apiKey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		
		callUrl="";
		
	    signinfo1.put("name",name); 
	    signinfo1.put("email", email);
	    signinfo1.put("verifycode", verifycode);
	    signinfo1.put("signkeyword", signkeyword);
	    
	    jsonData.put("signerInfo", signinfo1);
	    jsonData.put("contractnum", contractnum);
	    jsonData.put("callUrl", callUrl);
	      
	      
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 
		 CloseableHttpClient httpclient = Utils.getHttpClient();
		 
		try {
		  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/contract/signature/automatic");  
	      List<NameValuePair> params = new ArrayList<NameValuePair>();
	      params.add(new BasicNameValuePair("accesstoken", accessToken));
	      params.add(new BasicNameValuePair("mac", mac));
	      params.add(new BasicNameValuePair("data", data));
	  	  postRequest.setEntity(new UrlEncodedFormEntity(params));
	      HttpResponse httpResponse = httpclient.execute(target,postRequest);
	      System.out.println("params:"+params);
	      HttpEntity entity = httpResponse.getEntity();
	     
	      if (entity != null) {
	    	  String response=EntityUtils.toString(entity);
	    	  System.out.println("response:"+response);
	    	  jsonObj = Utils.getJsonObjectFromString(response);
				
	      }
	      
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		return retJson;
		// TODO Auto-generated method stub
		
	}

	public static JSONObject manual2(String apikey, String apiSecret) throws Exception {
	
		String idcardnum="...";
		String name="...."; //signer name
		String email="..."; //signer email
		
		/*
		String idcardnum="";
		String name="2tupai";
		String email="duatupai@yopmail.com";*/
			
		String contractnum = "....";// contract number
		
		String callUrl,backUrl;
		String data = null;
		String mac = null;
		JSONObject retJson = null;
		JSONObject jsonData = new JSONObject();
	    JSONObject signinfo1 = new JSONObject();
	   
	    JSONObject jsonObj = null;
		JSONObject jsonAccessToken = null;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		
		callUrl="";
		backUrl="";
		
		//System.out.println("callUrl: "+callUrl);
		
	      signinfo1.put("name",name); 
	      signinfo1.put("email", email);
	  
		  jsonData.put("contractnum", contractnum);
	      jsonData.put("callUrl", callUrl);
	      jsonData.put("backUrl", backUrl);
		  jsonData.put("signerInfo", signinfo1);
	     
	      
	      System.out.println(jsonData.toString());
	      
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 
		//CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		CloseableHttpClient httpclient = Utils.getHttpClient();
		try {
		  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/contract/signature/manual");  
	      List<NameValuePair> params = new ArrayList<NameValuePair>();
	      params.add(new BasicNameValuePair("accesstoken", accessToken));
	      params.add(new BasicNameValuePair("mac", mac));
	      params.add(new BasicNameValuePair("data", data));
	  	  postRequest.setEntity(new UrlEncodedFormEntity(params));
	      HttpResponse httpResponse = httpclient.execute(target,postRequest);
	      System.out.println("params:"+params);
	      HttpEntity entity = httpResponse.getEntity();
	     
	      if (entity != null) {
	    	  String response=EntityUtils.toString(entity);
	    	  System.out.println("response:"+response);
	    	  jsonObj = Utils.getJsonObjectFromString(response);
				String data1 = jsonObj.get("data").toString();
				byte[] jsonData1 = Utils.aesEcbPkcs5PaddingDecrypt(Utils.hexString2Bytes(data1), apiSecret);
				String plainData = new String(jsonData1).toString();
				System.out.println("Output:"+plainData);
	    	  
	      }
	      
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		return retJson;
		// TODO Auto-generated method stub
		
	}
	
	
	
}
