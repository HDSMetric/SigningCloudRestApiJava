/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.util.ArrayList;
import java.util.List;

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

public class sendAuthCode {

	public static JSONObject verifySMS(String apikey, String apiSecret) {
		JSONObject retJson = null ;
		JSONObject jsonAccessToken = null;
		String data = null;
		String mac = null;
		
		String phonesn=" "; //user phone number
		String email=" "; //user email address
		String contractnum = " ";//uploaded contract number
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		
		 JSONObject jsonData = new JSONObject();
	     JSONObject signinfo1 = new JSONObject();
	     
	      signinfo1.put("phonesn",phonesn); 
	      signinfo1.put("email",email); 
	      
	      jsonData.put("signer", signinfo1);
	      jsonData.put("contractnum",contractnum);
	      jsonData.put("type","1");
	      System.out.println(jsonData);
	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 
		//CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		 CloseableHttpClient httpclient=Utils.getHttpClient();
		 
		try {
		  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/contract/authcode");  
		
	      
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
