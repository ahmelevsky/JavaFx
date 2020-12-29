package web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Faceapp {
	static Map<String,String> cookies;
	static Map<String,String> headers;
	static Map<String,String> headers1;
	static Map<String,String> payload;
	static String API_BASE_URL = "https://phv3f.faceapp.io:443"; 
	static String API_USER_AGENT = "FaceApp/3.2.1 (Linux; Android 4.4)";
	static File testImage = new File("D:\\14336016954829.jpg");
	static String deviceId = "1234567890ABCDEF";
	
	static {
		headers1 = new HashMap<String,String>(){{
			   put("Content-Type", "application/json");
			   put("User-Agent", API_USER_AGENT);
			   put("X-FaceApp-DeviceID", deviceId);
			}};
		headers = new HashMap<String,String>(){{
				   put("User-Agent", API_USER_AGENT);
				   put("X-FaceApp-DeviceID", deviceId);
				}};
		cookies = new HashMap<String,String>(){{
			}};
	   payload = new HashMap<String,String>(){{
		   	put("app_version", "3.2.1");
		   	put("device_id", deviceId);
		   	put("registration_id", deviceId);
		   	put("device_model", "ZTE U950");
		   	put("lang_code", "en-US");
		   	put("sandbox", "False");
		   	put("system_version", "4.4.2");
		   	put("token_type", "fcm");
	   		}};
	}
	
	
	public static void test() throws IOException {
		
		
		JSONObject jobj = new JSONObject(payload);
		System.out.println(jobj.toString());
		String resp = Jsoup.connect("https://api.faceapp.io/api/v3.0/devices/register")
		  .headers(headers1)
		  .followRedirects(false)
		  .cookies(cookies)
		  .method(Connection.Method.POST)
		  .validateTLSCertificates(false)
		  .ignoreContentType(true)
		  .requestBody(jobj.toString())
		  .execute().body();
		System.out.println(resp);
		
		String resp2 = Jsoup.connect(API_BASE_URL + "/api/v3.8/photos")
		  .headers(headers)
		  .followRedirects(false)
		   .cookies(cookies)
		  .method(Connection.Method.POST)
		  .validateTLSCertificates(false)
		  .ignoreContentType(true)
		  .data("file", testImage.getName(), new FileInputStream(testImage))
		  .execute().body();
		System.out.println(resp2);
	}
	
	
}
