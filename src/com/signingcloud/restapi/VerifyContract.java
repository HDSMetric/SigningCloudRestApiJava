package com.signingcloud.restapi;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class VerifyContract {
	
	public static JSONObject verify1(String apiKey, String apiSecret) throws Exception {
		JSONObject retJson = null ;
		JSONObject jsonAccessToken = null;
		JSONObject jsonData = new JSONObject();
		
		String at = null;
		
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apiKey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		at = jsonAccessToken.get("at").toString();
		
		CloseableHttpClient httpclient=Utils.getHttpClient();
	      
		try {
		      // specify the host, protocol, and port
		      HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
		      
		      // specify the get request
		      HttpPost postRequest = new HttpPost("/signserver/v1/contract/signature/verify");      
		      File uploadFile = new File("C:\\.....\\demo.pdf");
		  				
		      String uploadFileHash = Utils.calculateHash(Files.readAllBytes(uploadFile.toPath()));
		      System.out.println(uploadFileHash);
		        
		      jsonData = new JSONObject();
		      jsonData.put("verifyFileHash", uploadFileHash);
		          
		      System.out.println(jsonData.toString());
		      
		      String uploadFileData = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		      String uploadFileMac = Utils.calculateMac(uploadFileData,apiSecret);
		      
		      FileBody fileBody = new FileBody(uploadFile, ContentType.DEFAULT_BINARY);
		      StringBody stringBody1 = new StringBody(uploadFileMac, ContentType.MULTIPART_FORM_DATA);
		  	  StringBody stringBody2 = new StringBody(uploadFileData, ContentType.MULTIPART_FORM_DATA);
		  	  StringBody stringBody3 = new StringBody(at, ContentType.MULTIPART_FORM_DATA);
		  	  MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		  	  builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		  	  builder.addPart("verifyFile", fileBody);
		  	  builder.addPart("accesstoken", stringBody3);
		  	  builder.addPart("mac", stringBody1);
		  	  builder.addPart("data", stringBody2);
		  	  HttpEntity entity = builder.build();
		  	
		  	  postRequest.setEntity(entity);
		      HttpResponse httpResponse = httpclient.execute(target,postRequest);
		      entity = httpResponse.getEntity();
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
