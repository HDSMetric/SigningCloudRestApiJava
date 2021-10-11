package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
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
import org.json.simple.JSONObject;

public class UploadImage{

	public static JSONObject uploadimg(String apikey, String apiSecret)throws Exception  {
		// TODO Auto-generated method stub
		JSONObject retJson = null ;
		JSONObject jsonAccessToken = null;
		String data = null;
		String mac = null;
		
		String phonesn="";
		String email=" ";//user email
		String name=" ";// username
		String img="C:\\Users\\.....\\Sign.jpg"; //image location
		String imgHex;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		
		byte[] bImg = Utils.readBytesFromFile(img);
		imgHex = Utils.bytes2HexString(bImg);
		
		
		 JSONObject jsonData = new JSONObject();
	     JSONObject signinfo1 = new JSONObject();
	  
	      signinfo1.put("email", email);
	      jsonData.put("img", imgHex);
	      jsonData.put("transparency", "0");
	      jsonData.put("signer", signinfo1);
	      System.out.println(jsonData);
	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 
		 CloseableHttpClient httpclient=Utils.getHttpClient();
		 
		try {
		  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/user/signimg");  	
	      
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
	    	  
	      }
	      
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	      return retJson;
	}
	
	public static JSONObject uploadseal(String apikey, String apiSecret)throws Exception  {
		// TODO Auto-generated method stub
		JSONObject retJson = null ;
		JSONObject jsonAccessToken = null;
		String data = null;
		String mac = null;
		
		String phonesn="";
		String email=" ";//user email
		String name=" ";// username
		String img="C:\\Users\\.....\\Seal.jpg"; //image location
		String imgHex;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		
		byte[] bImg = Utils.readBytesFromFile(img);
		imgHex = Utils.bytes2HexString(bImg);
		
		
		 JSONObject jsonData = new JSONObject();
	     JSONObject signinfo1 = new JSONObject();
	  
	      signinfo1.put("email", email);
	      jsonData.put("img", imgHex);
	      jsonData.put("transparency", "0");
	      jsonData.put("signer", signinfo1);
	      System.out.println(jsonData);
	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 
		 CloseableHttpClient httpclient=Utils.getHttpClient();
		 
		try {
		  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/user/stampimg");  	
	      
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
	    	  
	      }
	      
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	      return retJson;
	}
	
	public static JSONObject uploadcompany(String apikey, String apiSecret)throws Exception  {
		// TODO Auto-generated method stub
		JSONObject retJson = null ;
		JSONObject jsonAccessToken = null;
		String data = null;
		String mac = null;
		
		String img="C:\\Users\\.....\\sm.jpg"; //company image
		String imgHex;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		
		byte[] bImg = Utils.readBytesFromFile(img);
		imgHex = Utils.bytes2HexString(bImg);
		
		
		 JSONObject jsonData = new JSONObject();
	     
	      jsonData.put("img", imgHex);
	      jsonData.put("transparency", "1");
	      
	      System.out.println(jsonData);
	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking 
		 mac = Utils.calculateMac(data,apiSecret);
		 
		 CloseableHttpClient httpclient=Utils.getHttpClient();
		 
		try {
		  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/user/companyimg");  
		
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
	      }
	      
		} catch (Exception e) {
	    	e.printStackTrace();
	    }
	      return retJson;
	}

	

}
