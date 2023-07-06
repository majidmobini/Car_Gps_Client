package home.com.internet;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class Get_class 
{

	String user;
	String pass;
	public Get_class(String user , String pass) {
			this.user = user;
			this.pass = pass;
	}

	public String GetUrl(String requestURL)
	{
		URL url;
		String response = "";
		try
		{
			url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//	HttpsURLConnection.setDefaultSSLSocketFactory(engine);
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			//conn.setDoOutput(true);


			String encoded = android.util.Base64.encodeToString((user+":"+pass).getBytes(), Base64.NO_WRAP);;  //Java 8
			conn.setRequestProperty("Authorization", "Basic "+encoded);
			conn.setRequestMethod("GET");
			/*OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));

			writer.flush();
			writer.close();
			os.close();*/
			int responseCode=conn.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line=br.readLine()) != null) {
					response+=line;
				}
			}
			else {
				response="";

			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Error";
		}

		return response;
	}

	


}

