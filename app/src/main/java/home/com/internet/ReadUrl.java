package home.com.internet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Majid on 5/8/2018.
 */

public class ReadUrl {

    public String read(String url)
    {
        String response = "";
        try {
            URL inUrl = new URL(url);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            inUrl.openStream()));

            String inputLine="";


            while ((inputLine = in.readLine()) != null)
                response = response + inputLine;

            in.close();
            return response;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "Error";
        }
    }
}
