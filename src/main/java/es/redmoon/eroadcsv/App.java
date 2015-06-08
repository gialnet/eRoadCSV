package es.redmoon.eroadcsv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Read a CSV with a UTC datetime
 * Use GeoNames Web Services
 * 
 */
public class App 
{
    
    public String get(String url) throws ClientProtocolException, IOException {
        return execute(new HttpGet(url));
    }
    
    private String execute(HttpRequestBase request) throws ClientProtocolException, IOException {
     
        HttpClient httpClient = new DefaultHttpClient();
        org.apache.http.HttpResponse response = httpClient.execute(request);
      
        HttpEntity entity = response.getEntity();
  
        String body = EntityUtils.toString(entity);
 
        if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Expected 200 but got " + response.getStatusLine().getStatusCode() + ", with body " + body);
        }
 
        return body;
    }
    
    public String post(String url, Map<String,String> formParameters) throws ClientProtocolException, IOException {
        HttpPost request = new HttpPost(url);
   
        List <NameValuePair> nvps = new ArrayList <>();
   
        for (String key : formParameters.keySet()) {
            nvps.add(new BasicNameValuePair(key, formParameters.get(key)));
        }
 
  request.setEntity(new UrlEncodedFormEntity(nvps));
   
  return execute(request);
 }
    public static void main( String[] args ) throws FileNotFoundException, IOException, org.json.simple.parser.ParseException
    {
        String service ="http://api.geonames.org/timezoneJSON?";
        FileReader fr = new FileReader("/home/antonio/NetBeansProjects/eRoadCSV/data.csv");
        BufferedReader bf = new BufferedReader(fr);
        String sCadena;
        String sASC;
        String sResto;
        String Lati;
        String Longi;
        int donde;
        
        String CallService;
        String jsonDatosPer = null;
        String TimezoneId;
        String LocalTime;
        
        App MyApp = new App();
        
        while ((sCadena = bf.readLine())!=null){
            
            // UTC Date Time
            donde=sCadena.indexOf(",");
            sASC = sCadena.substring(0, donde);
            System.out.println(sASC);
            
            sResto=sCadena.substring(donde+1, sCadena.length());
            
            donde=sResto.indexOf(",");
            Lati=sResto.substring(0, donde);
            System.out.println(Lati);
            
            Longi=sResto.substring(donde+1, sResto.length());
            
            System.out.println(Longi);
            
            CallService=service+"lat="+Lati+"&lng="+Longi+"&username=user";
            
            String respuestaSecure = MyApp.get(CallService);
            
            if (respuestaSecure.equals("Error"))
                {
                    System.out.println("Service GeoName unavaible");
                }
                    
            JSONObject jsonObject = null;

            // {"sunrise":"2015-06-08 05:24","lng":10.2,"countryCode":"AT","gmtOffset":1,"rawOffset":1,"sunset":"2015-06-08 21:11",
            // "timezoneId":"Europe/Vienna","dstOffset":2,"countryName":"Austria","time":"2015-06-08 19:21","lat":47.01}
            
            try {
             jsonObject = (JSONObject) new JSONParser().parse(jsonDatosPer);
            } catch (ParseException e) {
             throw new RuntimeException("Unable to parse json " + jsonDatosPer);
            }
            
            TimezoneId = (String) jsonObject.get("timezoneId");
            LocalTime = (String) jsonObject.get("time");
            
            System.out.println(sCadena+","+TimezoneId+","+LocalTime);
            
            // free memory
            jsonObject = null;
        }
        
        fr.close();
        
    } ///home/antonio/NetBeansProjects/eRoadCSV
}
