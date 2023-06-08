package com.together.traveler.webScraping;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.together.traveler.context.AppContext;
import com.together.traveler.model.ParsedEvent;
import com.together.traveler.ui.main.home.HomeViewModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebScraping {
    private final String TAG = "WebScraping";
    private Document doc;
    private Thread secThread;
    private Runnable runnable;
    private Context context = AppContext.getContext();
    private HomeViewModel homeViewModel;

    private ArrayList<ParsedEvent> events = new ArrayList<>();

    public WebScraping(HomeViewModel homeViewModel) {
        Log.i(TAG, "WebScraping: ");
        this.homeViewModel = homeViewModel;
    }


    HashMap<String, String> TKT_links = new HashMap<String, String>(){{
        put("Theater", "https://tkt.am/en/%D0%A2%D0%B5%D0%B0%D1%82%D1%80/gid/5");
        put("Cinema", "https://tkt.am/en/%D0%9A%D0%B8%D0%BD%D0%BE/gid/6");
        put("Concert", "https://tkt.am/en/concert/gid/2");
        put("For kids", "https://tkt.am/en/%D0%94%D0%B5%D1%82%D1%81%D0%BA%D0%BE%D0%B5%20%D0%BC%D0%B5%D1%80%D0%BE%D0%BF%D1%80%D0%B8%D1%8F%D1%82%D0%B8%D0%B5/gid/9");

    }};


    public void startScraping() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(this::getTomsarkgh);
        executorService.submit(this::getTKT);

        executorService.shutdown();
    }

    private void getTomsarkgh() {
        Log.d("Web", "getTomsarkgh: ");
        try {
            doc = Jsoup.connect("https://tomsarkgh.am/en").get();
            Elements hrefs = doc.getElementsByAttributeValueContaining("href", "event/");
            LinkedHashSet<String> links = new LinkedHashSet<>();
            for (Element href : hrefs) {
                links.add(href.absUrl("href"));
            }
            for (String link : links) {

                ParsedEvent event = getTomsarkghEventInfo(link);
                Log.d("Web", "getTomsarkgh: " + homeViewModel.getCategories().getValue());
                if(eventIsValid(event)) homeViewModel.addParsedEvents(event);

            }
        } catch (IOException e) {
            Log.e("Jsoup/getTomsarkgh", "Connection problem: " + e.getMessage());
        }
    }

    private ParsedEvent getTomsarkghEventInfo(String link) {

        Document event = null;
        try {
            event = Jsoup.connect(link).get();
        } catch (IOException e) {
            Log.e("Jsoup", "Connection problem");
        }
        ParsedEvent eventInfo = new ParsedEvent();
        try {
            eventInfo.setLink(link);
            eventInfo.setTitle(event.getElementById("eventName").text().replaceAll("/", " "));
            eventInfo.setDescription(event.getElementById("eventDesc").text());
            eventInfo.setImg_url(event.getElementById("single_default").absUrl("href"));
            eventInfo.setLocation(event.getElementsByClass("occurrence_venue").get(0).text());
            eventInfo.setMore_images(new ArrayList<String>());
            eventInfo.setTags(new ArrayList<String>());
            eventInfo.setDates(new ArrayList<Date>());
            eventInfo.setPrices(new ArrayList<Integer>());
            eventInfo.setGeoPoint(getLocationFromAddress(eventInfo.getLocation(), context));

            Elements tags_el = event.getElementsByClass("event_type alpha60");
            Elements images = event.getElementsByAttributeValueContaining("href", "/thumbnails/Photo/bigimage/");


            eventInfo.getPrices().addAll(extractPrice(eventInfo.getDescription(), false));

            String tag = tags_el.first().text();
            eventInfo.getTags().add(tag);
            if(new ArrayList<>(Arrays.asList("Jazz", "Classical", "Rock")).contains(tag))
                eventInfo.getTags().add(0,"Concert");
            else if(eventInfo.getTags().size()==1 &&
                    !new ArrayList<>(Arrays.asList("Theater","Clubs & pubs","Cinema","Concert","Other")).contains(tag))
                eventInfo.getTags().add(0, "Other");


            for (int i = 1; i < images.size(); i++) {
                eventInfo.getMore_images().add(images.get(i).absUrl("href"));
            }

            HashSet<String> date_times = new HashSet<String>();

            Elements time = event.getElementsByClass("oc_time");
            if (time.size() == 1) {
                String day = event.getElementsByClass("oc_date").text();
                String month = event.getElementsByClass("oc_month").text();
                String weekday = event.getElementsByClass("oc_weekday").text();
                String full_date = day + " " + month + ", " + weekday + "  " + time.text();
                date_times.add(full_date);
            } else {
                Elements full_date = event.getElementsByClass("oc_fulldate");
                time.remove(0);
                for (int i = 0; i < time.size(); i++) {
                    date_times.add(full_date.get(i).text() + "  " + time.get(i).text());
                }
            }

            TreeSet<Date> dateTreeSet = new TreeSet<>();
            for (String dateString: date_times) {
                dateTreeSet.add(stringToDate(dateString));
            }
            eventInfo.getDates().addAll(dateTreeSet);
            Log.i(TAG, "getTomsarkghEventInfo: " + dateTreeSet.first()+" "+dateTreeSet.last());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy, HH:mm");
            eventInfo.setStartDate(dateFormat.format(dateTreeSet.first()));
            eventInfo.setEndDate(dateFormat.format(dateTreeSet.last()));
            eventInfo.setUsername("Tomsarkgh.am");
            eventInfo.setUserAvatar("https://is2-ssl.mzstatic.com/image/thumb/Purple128/v4/8b/d0/24/8bd0240c-9f79-8d83-ef7e-f3e264607e18/source/256x256bb.jpg");

        }catch (Exception e){
            Log.e("Jsoup/getTomsarkghEventInfo", link+ "  " + e.getMessage());
        }

        return eventInfo;
    }

    private void getTKT() {
        Log.d(TAG, "getTKT: ");
        for (Map.Entry<String, String> tkt_link: TKT_links.entrySet()) {
            try {
                doc = Jsoup.connect(tkt_link.getValue()).get();
                Elements cards = doc.getElementsByClass("more__item");
                for (Element card: cards) {
                    Element href = card.getElementsByAttributeValueContaining("href", "/web/event/event_id").get(0);
                    String link = "https://tkt.am/en//eid/"+ extractID(href.absUrl("href"));
                    String price = card.getElementsByClass("more__info-text").last().text();
                    ParsedEvent event = getTKTEventInfo(link, tkt_link.getKey(), price);
                    if(eventIsValid(event)) homeViewModel.addParsedEvents(event);
                    Log.i(TAG, "getTKT: " + event.getTitle());
                }

            } catch (IOException e) {
                Log.e("Jsoup/getTKT", "Connection problem: " + e.getMessage());
            }
        }


    }

    private ParsedEvent getTKTEventInfo(String link, String tag, String price) {

        Document event = null;
        try {
            event = Jsoup.connect(link).get();
        } catch (IOException e) {
            Log.e("Jsoup", "Connection problem");
        }
        ParsedEvent eventInfo = new ParsedEvent();

        try {
            eventInfo.setLink(link);
            eventInfo.setTitle(event.getElementsByClass("info__title title").get(0).text());
            String img_url = "https://tkt.am"+ event.getElementsByClass("event-image").attr("src");
            eventInfo.setImg_url(img_url);
            eventInfo.setMore_images(new ArrayList<String>(Collections.singleton(img_url)));
            String full_address = event.getElementsByClass("place__place-more").get(0).text();
            eventInfo.setGeoPoint(getLocationFromAddress(full_address, context));
            eventInfo.setLocation(full_address.split(",")[0]);
            eventInfo.setDates(new ArrayList<>());
            Date date = new Date(event.getElementsByClass("info__date").text());
            eventInfo.getDates().add(date);
            eventInfo.setPrices(new ArrayList<>(extractPrice(price, true)));
            ArrayList<String> tags = new ArrayList<>();
            if(tag.equals("For kids")) tags.add("Theater");
            tags.add(tag);
            eventInfo.setTags(tags);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy, HH:mm");
            eventInfo.setStartDate(dateFormat.format(date));
            eventInfo.setEndDate(dateFormat.format(date));
            eventInfo.setDescription(event.getElementsByClass("info__inner").last().wholeText().trim());
            eventInfo.setUsername("TKT.am");
            eventInfo.setUserAvatar("https://tkt.am/images/timg/site_logo_file_small.png");
            //#TODO map description en, am, ru
            if(eventInfo.getDescription().isEmpty()){
                Document eventRu = Jsoup.connect(link.replace("en", "ru")).get();
                eventInfo.setDescription(eventRu.getElementsByClass("info__inner").last().wholeText().trim());
            }
            if(eventInfo.getDescription().isEmpty()){
                Document eventAm = Jsoup.connect(link.replace("en", "am")).get();
                eventInfo.setDescription(eventAm.getElementsByClass("info__inner").last().wholeText().trim());
            }


        }catch (Exception e){
            Log.e("Jsoup/getTKTEventInfo", link+ "  " + e.getMessage());
        }

        return eventInfo;
    }





    private String extractID(String href) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(href);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }


    private TreeSet<Integer> extractPrice(String description, boolean tkt){
        description = description.replaceAll("\\." , "");
        description = description.replaceAll("\\s" , "");

        TreeSet<Integer> prices = new TreeSet<>();
        String regex = "\\d{4}?\\d{0,2}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(description);
        while (matcher.find()) {
            int price = Integer.parseInt(matcher.group().replaceAll("[\\.,\\s]", ""));
            if (price % 100 == 0) {
                if(tkt) price /= 100;
                prices.add(price);
            }
        }

        return prices;
    }

    private Date stringToDate(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("dd MMM, EEE HH:mm", Locale.ENGLISH);
        Date parsedDate = null;

        try {
            parsedDate = format.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            parsedDate = calendar.getTime();

        } catch (ParseException e) {
            Log.e("stringToDate", "format problem " + e.getMessage() +" " + dateString);
        }
        return parsedDate;
    }

    private boolean eventIsValid(ParsedEvent event){
        boolean valid = true;
        if(event.getTitle() == null || event.getTitle().isEmpty()) valid = false;
        if(event.getDates() == null || event.getDates().isEmpty()) valid = false;
        if(event.getTags() == null || event.getTags().isEmpty()) valid = false;
        if(event.getPrices() == null) valid = false;

        return valid;
    }


    private GeoPoint getLocationFromAddress(String strAddress, Context context) {

        Geocoder coder = new Geocoder(context);
        GeoPoint gp = null;
        try {
            List<Address> address = coder.getFromLocationName(strAddress, 1);
            if (!address.isEmpty()) {
                Address location = address.get(0);
                gp = new GeoPoint(location.getLatitude(), location.getLongitude());
            } else {
                Log.e("WS/Geocoder", "Location not found: " + strAddress);
            }
        } catch (IOException ex) {
            Log.e("WS/Geocoder", "getLocationFromAddress failed "+ ex.getMessage());
        }
        return gp;
    }

}
