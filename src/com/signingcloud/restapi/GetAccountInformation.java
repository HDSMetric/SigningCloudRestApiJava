/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetAccountInformation  {
	public static JSONObject accinfo (String apiKey, String apiSecret) throws Exception {
		
		JSONObject retJson = null ;
		JSONObject jsonObj = null;
		String data = null;
		String plainData = null;
		String mac = null;
		String output = null;
		long result;
		String macCheck = null; 
		
		// Request access token
				JSONObject jsonAccessToken = null;
				try {
					jsonAccessToken = AccessToken.acquireAccessToken(apiKey, apiSecret);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String accessToken = jsonAccessToken.get("at").toString();
				
				URL url;
				HttpsURLConnection conn;
				    try {
						url = new URL("https://stg-env.signingcloud.com7/signserver/v1/account/infomation?accesstoken="+accessToken);
					
						conn = (HttpsURLConnection) url.openConnection();
						conn.setRequestMethod("GET");
						conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
						
						if (conn.getResponseCode() != 200) {
							throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
						}
						
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						while ((output = br.readLine())!= null) {
							//System.out.println("Output:");
							System.out.println("Output:"+output);
							jsonObj = Utils.getJsonObjectFromString(output);
						}
						
						conn.disconnect();
						
						result = (long) jsonObj.get("result");
						data = jsonObj.get("data").toString();
						mac = jsonObj.get("mac").toString();
						macCheck = Utils.SHA256(new StringBuilder(data).append(apiSecret).toString().getBytes());
						if (result == 0) {
							if(mac.equals(macCheck)) {
								System.out.println("Mac account info verified OK!");
								System.out.print("The data is: ");
			
								byte[] jsonData = Utils.aesEcbPkcs5PaddingDecrypt(Utils.hexString2Bytes(data), apiSecret);
								
								plainData = new String(jsonData).toString();
								System.out.println(plainData);
								
								JSONParser parser = new JSONParser();
								retJson = (JSONObject) parser.parse(plainData);
							} else {
								System.out.println("Mac account info mismatch!");
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
