package com.soco.SoCoClient.onboarding.login.service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.facebook.GraphResponse;
import com.soco.SoCoClient.common.http.HttpUtil;
import com.soco.SoCoClient.common.http.UrlUtil;
import com.soco.SoCoClient.common.util.SocoApp;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginViaFacebookService extends IntentService {

    static final String tag = "LoginViaFacebookService";

    static final String TYPE = "type";
    static final String FACEBOOK = "facebook";
    static final String FACEBOOK_FIELD_ID = "id";
    static final String FACEBOOK_FIELD_NAME = "name";
    static final String FACEBOOK_FIELD_EMAIL = "email";

    static final String STATUS = "status";
    static final String USER_ID = "user_id";

    static final int WAIT_INTERVAL_IN_SECOND = 1;
    static final int WAIT_ITERATION = 10;
    static final int THOUSAND = 1000;

    String userId;
    String userName;
    String userEmail;

    SocoApp socoApp;
    boolean requestStatus;
    GraphResponse requestResponse;

    public LoginViaFacebookService() {
        super("LoginViaFacebook");
    }

    @Override
    public void onCreate() {
        super.onCreate();   //important

        socoApp = (SocoApp)getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(tag, "login via facebook, handle intent:" + intent);

        waitForResponse();

        if(requestStatus) {
            requestResponse = socoApp.facebookUserinfoResponse;
            retrieveUserinfo();
            loginToServer();
        }
        else{
            Log.e(tag, "cannot retrieve userinfo, skip login to server");
        }

        return;
    }

    private void waitForResponse() {
        Log.v(tag, "wait for response");

        int count = 0;
        requestStatus = socoApp.facebookUserinfoReady;
        requestResponse = null;

        while(!requestStatus && count < WAIT_ITERATION) {   //wait for 10s
            Log.d(tag, "wait for response: " + count * WAIT_INTERVAL_IN_SECOND + "s");
            long endTime = System.currentTimeMillis() + WAIT_INTERVAL_IN_SECOND*THOUSAND;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                        Log.e(tag, "Error in waiting");
                    }
                }
            }

            count++;
            requestStatus = socoApp.facebookUserinfoReady;
        }

        return;
    }

    private void retrieveUserinfo() {
        Log.d(tag, "retrieve userinfo");

        JSONObject userinfo = requestResponse.getJSONObject();
        try {
            userId = userinfo.get(FACEBOOK_FIELD_ID).toString();
            Log.d(tag, "user id: " + userId);

            userEmail = userinfo.get(FACEBOOK_FIELD_EMAIL).toString();
            Log.d(tag, "user email: " + userEmail);

            userName = userinfo.get(FACEBOOK_FIELD_NAME).toString();
            Log.d(tag, "user name: " + userName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return;
    }

    private void loginToServer(){
        Log.d(tag, "login to server");

        String url = UrlUtil.getSocialLoginUrl();
        Object response = request(
                url,
                FACEBOOK,
                userId,
                userName,
                userEmail
        );

        if (response != null)
            parse(response);

        return;
    }

    public Object request(
            String url,
            String type,
            String id,
            String name,
            String email
    ) {
        Log.v(tag, "create json request");

        JSONObject data = new JSONObject();
        try {
            data.put(TYPE, type);
            data.put(FACEBOOK_FIELD_ID, id);
            data.put(FACEBOOK_FIELD_NAME, name);
            data.put(FACEBOOK_FIELD_EMAIL, email);
            Log.d(tag, "created json: " + data);
        } catch (Exception e) {
            Log.e(tag, "cannot create Login Json post data");
            e.printStackTrace();
        }

        return HttpUtil.executeHttpPost(url, data);
    }

    public boolean parse(Object response) {
        Log.d(tag, "parse social login response: " + response.toString());

        try {
            JSONObject json = new JSONObject(response.toString());
            String status = json.getString(STATUS);
            String user_id = json.getString(USER_ID);
            Log.d(tag, "social login response, status: " + status + ", user_id: " + user_id);
        } catch (Exception e) {
            Log.e(tag, "cannot convert parse to json object: " + e.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}