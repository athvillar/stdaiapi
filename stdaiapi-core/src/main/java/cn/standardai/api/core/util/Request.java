package cn.standardai.api.core.util;
import java.io.IOException;  
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.ArrayList;  
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;  
import java.util.Map;  
import java.util.Map.Entry;
import java.util.Set;  

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
  
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;  
import org.apache.http.NameValuePair;  
import org.apache.http.ParseException;  
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;  
import org.apache.http.client.methods.HttpGet;  
import org.apache.http.client.methods.HttpPost;  
import org.apache.http.client.methods.HttpUriRequest;  
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;  
import org.apache.http.protocol.HTTP;  
import org.apache.http.util.EntityUtils;

public class Request {
	
    /** 
     * 使用 HMAC-SHA1 签名方法对对encryptText进行签名 
     * @param encryptText 被签名的字符串 
     * @param encryptKey  密钥 
     * @return 
     */  
    public static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException  
    {         
    	byte[] data=encryptKey.getBytes("UTF-8");
    	//根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, "HmacSHA1"); 
        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = Mac.getInstance("HmacSHA1"); 
        //用给定密钥初始化 Mac 对象
        mac.init(secretKey);  
        
        byte[] text = encryptText.getBytes("UTF-8");  
        //完成 Mac 操作 
        byte[] finalText = mac.doFinal(text);  
       
        // base64加密再输出
        byte[] bytes = Base64.encodeBase64(finalText);
        return new String(bytes);
    }  
	
	public static String hashMapToJson(HashMap map) {  
        String string = "{";  
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {  
            Entry e = (Entry) it.next();  
            string += "\"" + e.getKey() + "\":";  
            string += "\"" + e.getValue() + "\",";  
        }  
        string = string.substring(0, string.lastIndexOf(","));  
        string += "}";  
        return string;  
    }  
	
	final static String APP_ID = "apple_test";
	final static String APP_KEY = "apple_test&";
	final static String URL_STRING = "http://123.56.160.244/cgi-bin/api/pile_state_notify.php";


	/*
	public static void main(String args[]) throws IOException, URISyntaxException, HttpException { 
        System.out.println("Request begin:"); 
        
        // Infos
        HashMap<String, String> infos = new HashMap<String, String>();
        infos.put("pile_code", "0000000000000222");
        infos.put("inter_no", "1");
        infos.put("inter_type", "2");
        infos.put("inter_conn_state", "1");
        infos.put("inter_work_state", "2");
        infos.put("inter_order_state", "1");
        infos.put("elect_type", "1");
        infos.put("elect_address", "asfda");
        infos.put("elect_rate", "1");
        infos.put("active_power", "10");
        infos.put("reactive_power", "20");
        infos.put("active_energy", "10");
        infos.put("reactive_energy", "20");
        infos.put("voltage", "10");
        infos.put("current", "20");
        infos.put("fault_code", "0");
        infos.put("err_code", "0");
        infos.put("parking_state", "2");
        infos.put("soc", "11");
        infos.put("res_time", "2220");
        infos.put("time", "100");// ""+System.currentTimeMillis());
        String infosString = hashMapToJson(infos);
        
        System.out.println(infosString);
        
        // Signature        
        String hamcSource = "app_id=" + APP_ID + "&info=" + infosString;
        String infoSignature = null;
        try {
			infoSignature = hmacSHA1Encrypt(hamcSource, APP_KEY);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (infoSignature != null) {
            
            List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
            data.add(new BasicNameValuePair("app_id", APP_ID));
            data.add(new BasicNameValuePair("info", infosString));
            data.add(new BasicNameValuePair("sig", infoSignature));
            
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(URL_STRING);
            //post.setEntity(new UrlEncodedFormEntity(data));
            
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            
            System.out.println("Response:: " + EntityUtils.toString(entity));  

		}
    } 
    */
}
