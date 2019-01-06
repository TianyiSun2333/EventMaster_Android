package com.tianyisun.eventsearch.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tianyisun.eventsearch.artifact.User;

public class DataService {

    private static DataService mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private static final String URL_PREFIX = "http://eventsearch.us-west-1.elasticbeanstalk.com";
    private static final String RECOMMENDATION_ENDPOINT = "/recommendation";
    private static final String EVENTS_ENDPOINT = "/events";
    private static final String DETAIL_ENDPOINT = "/detail";
    private static final String ARTIST_ENDPOINT = "/artist";
    private static final String VENUE_ENDPOINT = "/venue";
    private static final String UPCOMINGEVENTS_ENDPOINT = "/upcomingevents";
    private static final String IMAGES_ENDPOINT = "/images";

    public DataService(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized DataService getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataService(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void getRecommendation(Context ctx, String keyword, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        keyword = keyword.replaceAll("~|!|@|#|\\$|%|/|\\|_|&|^|\\(|\\)|\\*|\\+|\\^|\\?", "");
        String url = URL_PREFIX + RECOMMENDATION_ENDPOINT + "?keyword=" + keyword;
        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    public static void getEvents(Context ctx, User userInput, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String keyword = userInput.getKeyword();
        keyword = keyword.replaceAll("~|!|@|#|\\$|%|/|\\|_|&|^|\\(|\\)|\\*|\\+|\\^|\\?", "");
        String category = userInput.getCategory();
        int radius = userInput.getDistance();
        radius = radius > 19999 ? 19999 : radius;
        String unit = userInput.getUnit();
        unit = unit.equals("kilometers") ? "km" : unit;
        String address = "";
        double lat = 0;
        double lng = 0;
        if (userInput.isHere()) {
            lat = userInput.getLat();
            lng = userInput.getLng();
        } else {
            address = userInput.getAddress();
            address = address.replaceAll("~|!|@|\\$|%|/|\\|_|&|^|\\*|\\+|\\^|\\?", "");
        }

        String url = URL_PREFIX + EVENTS_ENDPOINT + "?keyword=" + keyword + "&unit=" + unit + "&radius=" + radius + "&category=" + category;
        if (userInput.isHere()) {
            url += "&lat=" + lat + "&lng=" + lng;
        } else {
            url += "&address=" + address;
        }

        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    public static void getDetail(Context ctx, String id, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = URL_PREFIX + DETAIL_ENDPOINT + "?id=" + id;
        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    public static void getArtist(Context ctx, String name, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = URL_PREFIX + ARTIST_ENDPOINT + "?name=" + name;
        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    public static void getVenue(Context ctx, String keyword, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = URL_PREFIX + VENUE_ENDPOINT + "?keyword=" + keyword;
        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    public static void getUpComingEvents(Context ctx, String name, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = URL_PREFIX + UPCOMINGEVENTS_ENDPOINT + "?name=" + name;
        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    public static void getImages(Context ctx, String keyword, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        int number = 8;
        String size = "large";
        String url = URL_PREFIX + IMAGES_ENDPOINT + "?keyword=" + keyword + "&number=" + number + "&size=" + size;
        addtoRequestQueue(ctx, url, listener, errorListener);
    }

    private static void addtoRequestQueue(Context ctx, String url,
                                          Response.Listener<String> listener,
                                          Response.ErrorListener errorListener) {
        Log.d("request data from, ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        DataService.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
