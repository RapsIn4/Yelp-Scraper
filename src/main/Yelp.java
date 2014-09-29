package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp {

  OAuthService service;
  Token accessToken;

  /**
   * Setup the Yelp API OAuth credentials.
   *
   * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
   *
   * @param consumerKey Consumer key
   * @param consumerSecret Consumer secret
   * @param token Token
   * @param tokenSecret Token secret
   */
  public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
    this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
    this.accessToken = new Token(token, tokenSecret);
  }


  /**
   * Search with term and location.
   *
   * @param term Search term
   * @param latitude Latitude
   * @param longitude Longitude
   * @return JSON string response
   */
  public void search(String term, double latitude, double longitude) {
    OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
    request.addQuerystringParameter("term", term);
    request.addQuerystringParameter("ll", latitude + "," + longitude);
    
    int offset = 0;
    request.addQuerystringParameter("offset", Integer.toString(offset));
    this.service.signRequest(this.accessToken, request);
    Response response = request.send();
    
  JSONObject temp = (JSONObject) JSONValue.parse(response.getBody());
  
  String businesses = temp.get("businesses").toString();
  businesses = businesses.substring(1, businesses.length() - 1);

  try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("business.txt", true)))) {
        out.println("[" + businesses + ",");
    }catch (IOException e) {
        //exception handling left as an exercise for the reader
      System.err.println("error writing to file");
    }
  
  
    System.out.println(offset);
    System.out.println(businesses);
    
    int total = Integer.parseInt(temp.get("total").toString());
    System.out.println("Number of restaurants: " + total);
    
    
    for (offset=20;offset<total; offset+=20) {
      System.out.println( request.getQueryStringParams());
      request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
      request.addQuerystringParameter("term", term);
      request.addQuerystringParameter("ll", latitude + "," + longitude);
      request.addQuerystringParameter("limit", "20");

      request.addQuerystringParameter("offset", Integer.toString(offset));
      this.service.signRequest(this.accessToken, request);
      response = request.send();
      
      temp = (JSONObject) JSONValue.parse(response.getBody());
      businesses = temp.get("businesses").toString();
      businesses = businesses.substring(1, businesses.length() - 1);
      
      if (offset+20 < total) businesses += ",";
      
      try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("business.txt", true)))) {
            out.println(businesses);
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
          System.err.println("error writing to file");
        }
      
      System.out.println(offset);
      System.out.println(businesses);
      
    }
    
    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("business.txt", true)))) {
        out.println("]");
    }catch (IOException e) {
        //exception handling left as an exercise for the reader
      System.err.println("error writing to file");
    }

  }


public void searchLocation(String term, String location) throws ParseException {
    
      OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
      request.addQuerystringParameter("term", term);
      request.addQuerystringParameter("location", location);
      request.addQuerystringParameter("limit", "20");
      
      int offset = 0;
      request.addQuerystringParameter("offset", Integer.toString(offset));
      this.service.signRequest(this.accessToken, request);
      Response response = request.send();
      
    JSONObject temp = (JSONObject) JSONValue.parse(response.getBody());
    
    String businesses = temp.get("businesses").toString();
    businesses = businesses.substring(1, businesses.length() - 1);

    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("business.txt", true)))) {
            out.println("[" + businesses + ",");
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
          System.err.println("error writing to file");
        }
      
    
      System.out.println(offset);
      System.out.println(businesses);
      
      int total = Integer.parseInt(temp.get("total").toString());
      System.out.println("Number of restaurants: " + total);
      
      
      for (offset=20;offset<total; offset+=20) {
        System.out.println( request.getQueryStringParams());
        request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", "20");

        request.addQuerystringParameter("offset", Integer.toString(offset));
        this.service.signRequest(this.accessToken, request);
        response = request.send();
        
        temp = (JSONObject) JSONValue.parse(response.getBody());
        businesses = temp.get("businesses").toString();
        businesses = businesses.substring(1, businesses.length() - 1);
        
        if (offset+20 < total) businesses += ",";
        
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("business.txt", true)))) {
              out.println(businesses);
          }catch (IOException e) {
              //exception handling left as an exercise for the reader
            System.err.println("error writing to file");
          }
        
        System.out.println(offset);
        System.out.println(businesses);
        
      }
      
      try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("business.txt", true)))) {
            out.println("]");
        }catch (IOException e) {
            //exception handling left as an exercise for the reader
          System.err.println("error writing to file");
        }
      
      
    }
  
  // CLI
  public static void main(String[] args) throws IOException, ParseException {
    // Update tokens here from Yelp developers site, Manage API access.
    String consumerKey = "";
    String consumerSecret = "";
    String token = "";
    String tokenSecret = "";

    // clean out old file
    File json = new File("business.txt");
    json.delete();
    
    Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
    
    yelp.searchLocation("burritos", "toronto");
    
  }
}