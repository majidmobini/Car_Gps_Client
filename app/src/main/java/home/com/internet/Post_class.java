package home.com.internet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Post_class {


	String user;
	String pass;
	public Post_class(String user , String pass) {
		this.user = user;
		this.pass = pass;
	}

	public String PostUrl(String requestURL,HashMap<String, String> params)
	{
		String data = getPostDataString(params);
		URL url;
		String response = "";
		try
		{
			url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);


			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8"));
			writer.write(data);

			writer.flush();
			writer.close();
			os.close();
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



	private String getPostDataString(HashMap<String, String> params) {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for(Map.Entry<String, String> entry : params.entrySet()){
			if (first)
				first = false;
			else
				result.append("&");

			try {
				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return result.toString();
	}

}
