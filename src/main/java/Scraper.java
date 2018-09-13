import java.io.IOException;
import java.io.*;
import java.io.FileNotFoundException;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection;

import org.json.simple.JSONObject;

public class Scraper {
    private List<String> merchant_names = new ArrayList<String>();
    private List<String> states_names = new ArrayList<String>();
    private List<String> city_url = new ArrayList<String>();
    private List<String> merchants_url = new ArrayList<String>();
    private JSONObject merchant = new JSONObject();

    public void scrapeData() throws  IOException, FileNotFoundException, InterruptedException
    {
        Connection.Response city_res = null;
        try {
            city_res = Jsoup
                    .connect("https://www.bitesquad.com/")
                    .proxy("216.158.192.210", 8800)            // Proxy used for reference from https://www.us-proxy.org/
                    .userAgent(getUserAgent())
                    .header("authority", "www.bitesquad.com")
                    .header("upgrade-insecure-requests", "1")
                    .header("Accept-Encoding", "gzip,deflate,sdch")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
                    .header("compressed", "")
                    .execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000);

        Map<String, String> home_page_cookies =  city_res.cookies();

        Document doc = city_res.parse();

        Elements cities = doc.getElementsByClass("zone-state");

        for(Element city: cities)
        {
            states_names.add(city.getElementsByClass("zone-link").text());
            city_url.add("https://www.bitesquad.com"+city.getElementsByClass("zone-link").select("a").attr("href"));
        }
        System.out.println("Processing city"+city_url.get(0));
        Connection.Response merchants_res = null;
        try {
//            String ur = city_url.get(0);
            merchants_res = Jsoup
                    .connect("https://www.bitesquad.com/phoenix-restaurant-delivery/")       // Here we are testing only one city url link.
                    .proxy("216.158.192.210", 8800)
                    .cookies(home_page_cookies)
                    .userAgent(getUserAgent())
                    .header("authority", "www.bitesquad.com")
                    .header("upgrade-insecure-requests", "1")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("referer", "https://www.bitesquad.com/")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "en-GB,en-US;q=0.9,en;q=0.8")
                    .header("compressed", "")
                    .execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);
        Map<String, String> city_page_cookies =  merchants_res.cookies();
        doc = merchants_res.parse();

        Elements merchants = doc.getElementsByClass("location preOrder");
        System.out.println("Merchants processing - "+merchants);

        for(Element merchant: merchants)
        {
            String merchant_name = merchant.getElementsByClass("localTitle").text();
            merchant_names.add(merchant.getElementsByClass("zone-link").text());
            merchants_url.add(merchant.getElementsByClass("location-link").select("a").attr("href"));
        }
        System.out.println(merchant_names);
        System.out.println(merchants_url);
    }

    public String getUserAgent() throws IOException{
        List<String> list = new ArrayList<String>();
        Properties properties = new Properties();
        properties.load(this.getClass().getResourceAsStream("/useragents.properties"));
        Enumeration en = properties.propertyNames();
        while(en.hasMoreElements()) {
            list.add((String)en.nextElement());
        }
        String userAgentKey = list.get((int) (Math.random() * list.size()));
        String userAgentValue = properties.getProperty(userAgentKey);
        return userAgentValue;
    }
}
