package com.signingcloud.restapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Hex;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.crypto.KeyGenerator;
import org.apache.commons.httpclient.HttpClient;
import javax.net.ssl.SSLSocketFactory;

public class Utils {

	public static String calculateHash(byte[] inputBytes) {
		 try {
		    	MessageDigest digest = MessageDigest.getInstance("SHA-256");	  
	    		return Hex.encodeHexString(digest.digest(inputBytes));	    		
		    } catch (Exception e) {
		    }
		    return null;
	}
	
	public static String calculateMac(String content, String key) {
		 try {
		    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
		    	StringBuilder sb = new StringBuilder(content);
		    	sb.append(key);		  
	    		return Hex.encodeHexString(digest.digest(sb.toString().getBytes()));	    		
		    } catch (Exception e) {
		    }
		    return null;
	}
	
	public static String SHA256(final byte[] pbytes) {
		return SHA(pbytes, "SHA-256");
	}
	
	private static String SHA(final byte[] pbytes, final String digestType) {
		String strResult = null; 

		if (pbytes != null && pbytes.length > 0) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance(digestType);
				messageDigest.update(pbytes);
				byte byteBuffer[] = messageDigest.digest();

				StringBuffer strHexString = new StringBuffer();
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		return strResult;
	}
	
	public static String bytes2HexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
	
	public static byte[] hexString2Bytes(String hexString) {
        hexString = hexString.toUpperCase();
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            b[i] = (byte) (char2Byte(hexChars[pos]) << 4 | char2Byte(hexChars[pos + 1]));
        }
        return b;
    }
	
	public static byte char2Byte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
	

	public static byte[] aesEcbPkcs5PaddingEncrypt(String content, String key) {    	
	    try {
	    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
    		byte[] aesKey = digest.digest(key.getBytes());
	    	SecretKey secretKey = new SecretKeySpec(aesKey,"AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	        return cipher.doFinal(content.getBytes("utf-8"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	    return null;
    }
	
	
	public static byte[] aesEcbPkcs5PaddingDecrypt(byte[] content, String key) {
	    try {
	    	MessageDigest digest = MessageDigest.getInstance("SHA-256");
    		byte[] aesKey = digest.digest(key.getBytes());
	    	SecretKey secretKey = new SecretKeySpec(aesKey,"AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
	        return cipher.doFinal(content);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return null;
	}

	
	public static SSLContext httpsCall() {
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// trusted ca
		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			KeyStore tks = KeyStore.getInstance("JKS");
			tks.load(new FileInputStream("resources/certs/117.jks"), "12345678".toCharArray());
		    tmf.init(tks);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 
		try {
			//sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			sc.init(null, tmf.getTrustManagers(), null);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sc;
	}
	
	public static JSONObject getJsonObjectFromString(String jsonString) {
		JSONParser parse = new JSONParser();
        if (null != jsonString && !"".equals(jsonString.trim())) {
            try {
                JSONObject jsonObject = (JSONObject)parse.parse(jsonString);
                if (null != jsonObject) {
                    return jsonObject;
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
	
	public static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytesArray;
    }

	
public static void hostIgnoringClient() throws NoSuchAlgorithmException, KeyManagementException {
	TrustManager TrustAllX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    };

    // Create SSLContext and set the socket factory as default
    try {
        SSLContext sslc = SSLContext.getInstance("TLS");
        sslc.init(null, new TrustManager[]{TrustAllX509TrustManager},
                new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslc
                .getSocketFactory());
  	  HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
  		  public boolean verify(String string,SSLSession ssls) {
  			  return true;
  		  }
  	  });
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (KeyManagementException e) {
        e.printStackTrace();
    }
}

public static CloseableHttpClient getHttpClient() {

    try {
        SSLContext sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null,
                new TrustManager[]{new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {

                        return null;
                    }

                    public void checkClientTrusted(
                            X509Certificate[] certs, String authType) {

                    }

                    public void checkServerTrusted(
                            X509Certificate[] certs, String authType) {

                    }
                }}, new SecureRandom());

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);



        CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLSocketFactory(socketFactory).build();

        return httpClient;

    } catch (Exception e) {
        e.printStackTrace();
        return HttpClientBuilder.create().build();
    }
}

}
