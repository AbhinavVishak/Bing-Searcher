import java.util.Scanner;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import javax.json.JsonObject ; 
import javax.json.JsonReader ; 
import javax.json.Json ;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URLDecoder;
/**
 * This is the class which serves as the start point for the dMetrics 
 * Java Application that takes a search query and returns the top result
 */
class Entry {
    public static void main(String[] args) {
        System.out.print("Enter Your Search Query:");
        Scanner scanner = new Scanner(System.in) ; 
        String searchQuery = scanner.nextLine() ; 

        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            URIBuilder builder = new URIBuilder("https://api.cognitive.microsoft.com/bing/v5.0/search");

            builder.setParameter("q", searchQuery);
            builder.setParameter("count", "1");
            builder.setParameter("mkt", "en-us");
            builder.setParameter("safesearch", "Moderate");
            builder.setParameter("responseFilter", "Webpages");

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", "e9c83e8502014231b672f1a1b9697d0d");
            
            HttpEntity entity =null ; 
            try {
                HttpResponse response = httpclient.execute(request);
                entity = response.getEntity();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            if (entity != null) 
            {
                String temp = EntityUtils.toString(entity) ;
                JsonReader jsonReader = Json.createReader(new StringReader(temp));
                JsonObject object = jsonReader.readObject();
                jsonReader.close();
                if ( object.getJsonObject("rankingResponse").size() == 0 ){
                    System.out.print("No Search Results");
                    return ; 
                }
                String topResultName = object.getJsonObject("webPages").getJsonArray("value").getJsonObject(0).getString("name");
                String topResultUrl = object.getJsonObject("webPages").getJsonArray("value").getJsonObject(0).getString("url");

                String[] a = new URL(topResultUrl).getQuery().split("&");
                String q = a[a.length-2].split("=")[1] ; 
                String q_d = URLDecoder.decode(q, "UTF-8");

                System.out.println("TITLE: " + topResultName);
                System.out.println("URL: " + q_d);
                //java.awt.Desktop.getDesktop().browse(URI.create(q_d));
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception");
            System.out.println(e.getMessage());
        }
    }    
}
