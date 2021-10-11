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

public class GetContractList  {
	public static JSONObject contractlist(String apiKey, String apiSecret) throws Exception{
		JSONObject retJson = null ;
		JSONObject jsonObj = null;
		String plainData = null;
		String data = null;
		String mac = null;
		long result;
		String macCheck = null; 
		String output = null;
		String startIndex = "0";
		String pageSize = "0";  //return template, max 50, min 0 
		String rDetail = "0"; //  0: Do not return, 1: Return only the document list information, 2: Return the document list information and document signer information
		String state="1"; // 1: In progress, 4: Completed or signed by all signers, 6: Refused by one of the signers, 7: Cancelled, 20: Document signed but deleted, 21: In progress document deleted
		String date="";// yyyy-MM-dd. Use empty string “” if do not want to filter by date.
		
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
		String param = "{\"startIndex\":\"" + startIndex + "\",\"pageSize\":\"" + pageSize + "\",\"rDetail\":\"" + rDetail + "\",\"contractState\":\"" + state + "\",\"date\":\"" + date+"\"}";
		System.out.println(param);
		byte[] encRequestBody = Utils.aesEcbPkcs5PaddingEncrypt(param, apiSecret);
		data = Utils.bytes2HexString(encRequestBody);
		
		// create a mac for checking
		mac = Utils.SHA256(new StringBuilder(data).append(apiSecret).toString().getBytes()); 
		
		
		// REST API call
		URL url;
		HttpsURLConnection conn;
		    try {
				url = new URL("https://stg-env.signingcloud.com/signserver/v1/contract/list?accesstoken="+accessToken+"&data="+data+"&mac="+mac);
			
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
						System.out.println("Mac contract list verified OK!");
						System.out.print("The data is: ");
	
						byte[] jsonData = Utils.aesEcbPkcs5PaddingDecrypt(Utils.hexString2Bytes(data), apiSecret);
						
						plainData = new String(jsonData).toString();
						System.out.println(plainData);
						
						JSONParser parser = new JSONParser();
						retJson = (JSONObject) parser.parse(plainData);
					} else {
						System.out.println("Mac contract list mismatch!");
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
