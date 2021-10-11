package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UploadContract {

	public static JSONObject upload_doc(String apiKey, String apiSecret) throws Exception {
		JSONObject retJson = null ;
		JSONObject jsonAccessToken = null;
		JSONObject jsonObj = null;
		JSONArray signinfos = new JSONArray();
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
		      HttpPost postRequest = new HttpPost("/signserver/v1/contract/file");      
		      File uploadFile = new File("C:\\Users\\....\\demo.docx"); //upload document location
		      String uploadFileHash = Utils.calculateHash(Files.readAllBytes(uploadFile.toPath()));
		      System.out.println(uploadFileHash);
		      
		      JSONObject jsonContractInfo = new JSONObject();
		      jsonContractInfo.put("contractnum","");
		      jsonContractInfo.put("isWatermark", false);
		      jsonContractInfo.put("contractname", "");
		      jsonContractInfo.put("signernum", 1); 

		      JSONObject signinfo1 = new JSONObject();
		      signinfo1.put("name"," "); //username
	          signinfo1.put("authtype",0); 
	          signinfo1.put("caprovide","1"); 
		      signinfo1.put("email", " ");//user email address
		      signinfos.add(signinfo1);
		      	      
		      /* JSONObject signinfo2 = new JSONObject();
		      signinfo2.put("email", "...");
		      signinfo2.put("Name","...");
	          signinfo2.put("authtype",0); 
	          signinfo2.put("caprovide","1"); 
		     
		      signinfos.add(signinfo2);*/
		      
		      jsonContractInfo.put("signerinfo", signinfos);
		      
		      jsonData = new JSONObject();
		      jsonData.put("uploadFileHash", uploadFileHash);
		      jsonData.put("type","docx");
		      jsonData.put("contractInfo", jsonContractInfo);
		      
		      System.out.println(jsonData.toString());
		      
		      String uploadFileData = Hex.encodeHexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
		      String uploadFileMac = Utils.calculateMac(uploadFileData,apiSecret);
		      
		      FileBody fileBody = new FileBody(uploadFile, ContentType.DEFAULT_BINARY);
		      StringBody stringBody1 = new StringBody(uploadFileMac, ContentType.MULTIPART_FORM_DATA);
		  	  StringBody stringBody2 = new StringBody(uploadFileData, ContentType.MULTIPART_FORM_DATA);
		  	  StringBody stringBody3 = new StringBody(at, ContentType.MULTIPART_FORM_DATA);
		  	  MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		  	  builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		  	  builder.addPart("uploadFile", fileBody);
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
	}
	
}
