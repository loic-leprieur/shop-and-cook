package be.vives.loic.shopandcook.controllers;

/**
 * Created by LOIC on 04/12/2016.
 */

import com.loopj.android.http.*;

public class Food2ForkApiClient {
    private final static String API_KEY = "d73bc57bb507293fcd95b6c383ce59ca";

    private String QUERY = "";

    private String REQUEST_ENDPOINT = "search";

    private String BASE_URL = "https://community-food2fork.p.mashape.com/"+REQUEST_ENDPOINT+"?key="+API_KEY+"&q=";

    private static AsyncHttpClient client = new AsyncHttpClient();


    public Food2ForkApiClient(String requestEndpoint, String query) {
        this.REQUEST_ENDPOINT = requestEndpoint;
        this.QUERY = query;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
