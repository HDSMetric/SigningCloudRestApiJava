/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.util.ArrayList;
import java.util.Base64;
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

public class UploadEkycImg {

	public static JSONObject upload(String apikey, String apiSecret) throws Exception {
		JSONObject retJson = null;
		JSONObject jsonAccessToken = null;
		JSONObject jsonData = new JSONObject();
	    JSONObject signinfo1 = new JSONObject();
	     
		String data = null;
		String mac = null;
		
		String email="tammy.yam@disposeamail.com";
		String idcardnum="600606066006";
		String name="Tammy Yam";
		String icfront="C:\\Users\\....";
		String icback="C:\\Users\\....";
		String icfrontBs64,icbackBs64;
		
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
		byte[] bImg = Utils.readBytesFromFile(icback);
		icbackBs64 = Base64.getEncoder().encodeToString(bImg);
		 
	      
	      signinfo1.put("name",name); 
	      signinfo1.put("email", email);
	      jsonData.put("idFront", icfrontBs64);
	      jsonData.put("idBack", icbackBs64);
	      jsonData.put("signerInfo", signinfo1);

	
		// form data request in JSON
		data = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		
		// create a mac for checking
		 mac = Utils.calculateMac(data,apiSecret);
		 
		 CloseableHttpClient httpclient=Utils.getHttpClient();
		 
		try {
		  HttpHost target = new HttpHost("demo.securemetric.com", 447, "https");
		// REST API call
		 HttpPost postRequest = new HttpPost("/signserver/v1/user/ekycimages");  
		
	      
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
		// TODO Auto-generated method stub
		
	}

}
