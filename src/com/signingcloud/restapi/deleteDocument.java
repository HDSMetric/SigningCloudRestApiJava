/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

public class deleteDocument {

	public static JSONObject delete(String apikey, String apiSecret) throws Exception {
		JSONObject retJson = null;
		
		JSONObject jsonAccessToken = null;
				
				try {
					jsonAccessToken = AccessToken.acquireAccessToken(apikey, apiSecret);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String accessToken = jsonAccessToken.get("at").toString();
				System.out.println(accessToken);
				
				CloseableHttpClient httpclient = Utils.getHttpClient();
				    try {
				      // specify the host, protocol, and port
				    	  HttpHost target = new HttpHost("stg-env.signingcloud.com", 443, "https");
				      
				      // specify the get request
				      
				      JSONObject jsonData = new JSONObject();
				      jsonData.put("contractnum","8B1803EA85C9795FE86E040EB9655265");

				      String deleteDocumentsData = Utils.bytes2HexString(Utils.aesEcbPkcs5PaddingEncrypt(jsonData.toString(),apiSecret));
				      String deleteDocumentsMac = Utils.SHA256(new StringBuilder(deleteDocumentsData).append(apiSecret).toString().getBytes());
				      
				      
				      HttpDelete deleteRequest = new HttpDelete("/signserver/v1/contract/list"+"?mac="+deleteDocumentsMac+"&data="+deleteDocumentsData+"&accesstoken="+accessToken);
				      HttpResponse httpResponse = httpclient.execute(target, deleteRequest);
				      HttpEntity entity = httpResponse.getEntity();
				      if (entity != null) {
				    	  String response=EntityUtils.toString(entity);
				    	  System.out.println("Result:"+response);
				      }
				    } catch (Exception e) {
				    	e.printStackTrace();
				    }
		return retJson;
		// TODO Auto-generated method stub
		
	}

}
