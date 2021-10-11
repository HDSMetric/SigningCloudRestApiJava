/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DownloadContract  {

	public static JSONObject download(String apiKey, String apiSecret) throws Exception{
		// TODO Auto-generated method stub
		JSONObject retJson = null ;
		JSONObject jsonObj = null;
		String plainData = null;
		String data = null;
		String mac = null;
		long result;
		String macCheck = null; 
		String output = null;
		String contractnum = "B62D00801C6350ED61C50044A9058374"; // contract number
		String targetPath="C:\\Users\\......contracts\\auto-signed.pdf"; //target download path for contract
		
		// Request access token
		JSONObject jsonAccessToken = null;
		try {
			jsonAccessToken = AccessToken.acquireAccessToken(apiKey, apiSecret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String accessToken = jsonAccessToken.get("at").toString();
		System.out.println(accessToken);

		// form data request in JSON
		String param = "{\"contractnum\":\"" + contractnum +"\"}";
		System.out.println("DSD:"+param);
		byte[] encRequestBody = Utils.aesEcbPkcs5PaddingEncrypt(param, apiSecret);
		data = Utils.bytes2HexString(encRequestBody);
		
		// create a mac for checking
		mac = Utils.SHA256(new StringBuilder(data).append(apiSecret).toString().getBytes()); 
		
		// REST API call
				URL url;
				HttpsURLConnection conn;
				    try {
						url = new URL("https://stg-env.signingcloud.com/signserver/v1/contract/file?accesstoken="+accessToken+"&data="+data+"&mac="+mac);
					  
						conn = (HttpsURLConnection) url.openConnection();
						conn.setRequestMethod("GET");
						conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
						
						if (conn.getResponseCode() != 200) {
							throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
						}
						
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						while ((output = br.readLine())!= null) {
							jsonObj = Utils.getJsonObjectFromString(output);
						}
						
						conn.disconnect();
						
						result = (long) jsonObj.get("result");
						data = jsonObj.get("data").toString();
						mac = jsonObj.get("mac").toString();
						macCheck = Utils.SHA256(new StringBuilder(data).append(apiSecret).toString().getBytes());
						if (result == 0) {
							if(mac.equals(macCheck)) {
								System.out.println("Mac download doc verified OK!");
								System.out.print("The data is: ");
			
								byte[] jsonData = Utils.aesEcbPkcs5PaddingDecrypt(Utils.hexString2Bytes(data), apiSecret);
								
								plainData = new String(jsonData).toString();
								System.out.println(plainData);
								
								JSONParser parser = new JSONParser();
								retJson = (JSONObject) parser.parse(plainData);
								
								//download file
								String hexdata= (String) retJson.get("pdfdata");
								byte[] bytedate=Utils.hexString2Bytes(hexdata);
								
								File file = new File(targetPath); 
								try { 
						            OutputStream os = new FileOutputStream(file); 
						            os.write(bytedate); 
						            System.out.println("Successfully byte inserted"); 
						            os.close(); 
						        } 
						        catch (Exception e) { 
						            System.out.println("Exception: " + e); 
						        } 
								    
							} else {
								System.out.println("Mac download doc mismatch!");
							}
						} else {
							System.out.println("response error with code: " + result);
							return null;
						}
				    } catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
		    
		return retJson;
	}

}
