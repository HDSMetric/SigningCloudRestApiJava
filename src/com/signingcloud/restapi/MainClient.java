/*
Copyright © 2021 Signing Cloud Sdn. Bhd @ https://www.signingcloud.com/
All rights reserved
*/

package com.signingcloud.restapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainClient {
	
	
	public static void main(String[] args) throws Exception {
	
		int  i = 0;

		String apikey = ""; //api key
		String apiSecret = " ";//api secret
		
	
	    Utils.hostIgnoringClient();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
		System.out.print("1.get token  2.get account information  3.get document list  4. get document detail");
        System.out.print("\n5.Upload document   6.Auto Sign  7.Manual Sign  8.Download document ");
        System.out.print("\n9.Upload Signature Image  10.Upload Seal Image  11. Upload company image 12.Get SMS code   ");
        System.out.print("\n13.Delete document  14.Upload EKCY Image   15.Verify Ekyc   16.Verify Selfie ");
        System.out.print("\nSelect an operation: ");
        
        try {
            i = Integer.parseInt(br.readLine());
        } catch(NumberFormatException nfe) {
            System.err.println("Invalid Format!");
        }
        
        switch (i) {
        	case 1:
        		AccessToken.acquireAccessToken(apikey, apiSecret);
        		break;

        	case 2:
        		GetAccountInformation.accinfo(apikey, apiSecret);
			break;
            
        	case 3:
        		GetContractList.contractlist(apikey,apiSecret);
        		break;
            
        	case 4:
        		GetContractDetail.detail(apikey,apiSecret);
        		break;
        	
        	case 5:
        		UploadContract.upload_doc(apikey,apiSecret);
        		break;
        		
        	case 6:
        		GetSignatureURL.auto(apikey,apiSecret);
        		break;
        		
        	case 7:
        		GetSignatureURL.manual2(apikey,apiSecret);
        		break;
        		
        		
        	case 8:
        		DownloadContract.download(apikey,apiSecret);
        		break;
        		
        	case 9:
        		UploadImage.uploadimg(apikey,apiSecret);
        		break;
        		
        	case 10:
        		UploadImage.uploadseal(apikey,apiSecret);
        		break;
        		
        	case 11:
        		UploadImage.uploadcompany(apikey,apiSecret);
        		break;
        		
        	case 12:
        		sendAuthCode.verifySMS(apikey,apiSecret);
        		break;
        		
        	case 13:
        		deleteDocument.delete(apikey,apiSecret);
        		break;
        		
        	case 14:
        		UploadEkycImg.upload(apikey, apiSecret);
        		//verifyEKYC.ocr(apikey, apiSecret);
        		break;

        	case 15:
        		verifyEKYC.verify(apikey, apiSecret);
        		break;
        		
        	case 16:
        		verifyEKYC.face(apikey, apiSecret);
        		break;
        }
	}
}
