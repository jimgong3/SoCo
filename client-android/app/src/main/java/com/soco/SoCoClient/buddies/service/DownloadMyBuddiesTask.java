package com.soco.SoCoClient.buddies.service;

import android.os.AsyncTask;
import android.util.Log;

import com.soco.SoCoClient.buddies.allbuddies.ui.MyBuddiesListEntryItem;
import com.soco.SoCoClient.common.HttpStatus;
import com.soco.SoCoClient.common.TaskCallBack;
import com.soco.SoCoClient.common.http.HttpUtil;
import com.soco.SoCoClient.common.http.JsonKeys;
import com.soco.SoCoClient.common.http.UrlUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by David_WANG on 12/06/2015.
 */
public class DownloadMyBuddiesTask extends AsyncTask<String, Void, ArrayList<MyBuddiesListEntryItem>> {

    String tag = "DownloadMyBuddiesTask";
    String user_id;
    String token;
//    String[] paramNames;
    TaskCallBack callBack;

    public DownloadMyBuddiesTask(String user_id, String token, TaskCallBack cb){
        Log.v(tag, "user event task: " + user_id);
        this.user_id=user_id;
        this.token=token;
        callBack = cb;
    }
    protected ArrayList<MyBuddiesListEntryItem> doInBackground(String... params) {
        Log.v(tag, "validate data");

        String url = UrlUtil.getMyBuddiesUrl();
        Object response = request(
                url,
                user_id,
                token,
                params
        );

        if (response != null) {
            Log.v(tag, "parse response");
            return parse(response);
        }
        else {
            Log.e(tag, "response is null, cannot parse");
        }
        return null;
    }

    Object request(
            String url,
            String user_id,
            String token,
            String... inputs){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair(JsonKeys.USER_ID, user_id));
        params.add(new BasicNameValuePair(JsonKeys.TOKEN, token));
        if(inputs!=null&&inputs.length>0) {
           params.add(new BasicNameValuePair(JsonKeys.START_USER_ID, inputs[0]));
        }
        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        Log.d(tag, "request url: " + url);

        return HttpUtil.executeHttpGet(url);
    }

    ArrayList<MyBuddiesListEntryItem> parse(Object response) {
        Log.d(tag, "parse response: " + response.toString());
        Log.v(tag, "clear my match");
        try {
            JSONObject json;
            json = new JSONObject(response.toString());
            Log.d(tag, "converted json: " + json);


            int status = json.getInt(JsonKeys.STATUS);
            if(status == HttpStatus.SUCCESS) {
                String allBuddiesString = json.getString(JsonKeys.USER);
                Log.v(tag, "all buddies string: " + allBuddiesString);
                JSONArray allBuddies = new JSONArray(allBuddiesString);
                Log.d(tag, "retrieve buddy list: " + allBuddies.length() + " users downloaded");
                ArrayList<MyBuddiesListEntryItem> result = new ArrayList<>();
                for(int i=0; i<allBuddies.length(); i++){
                    MyBuddiesListEntryItem u = new MyBuddiesListEntryItem();
                    JSONObject obj = allBuddies.getJSONObject(i);
                    Log.v(tag, "current buddy json: " + obj.toString());
                    parseUserBasics(u, obj);
                   result.add(u);
                }
                return result;
            }
            else {
                String error_code = json.getString(JsonKeys.ERROR_CODE);
                String message = json.getString(JsonKeys.MESSAGE);
                String more_info = json.getString(JsonKeys.MORE_INFO);
                Log.d(tag, "create buddy fail, " +
                                "error code: " + error_code + ", message: " + message + ", more info: " + more_info
                );
            }
        } catch (Exception e) {
            Log.e(tag, "cannot convert parse to json object: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

    private void parseUserBasics(MyBuddiesListEntryItem u, JSONObject obj) throws JSONException {
        u.setUser_id(obj.getString(JsonKeys.USER_ID));
        u.setUser_name(obj.getString(JsonKeys.USER_NAME));
        u.setLocation(obj.getString(JsonKeys.LOCATION));
        Log.v(tag, "user id, name, location: " + u.getUser_id() + ", " + u.getUser_name() + ", " + u.getLocation());
    }
    protected void onPostExecute(ArrayList<MyBuddiesListEntryItem> result) {
        callBack.doneTask(result);
    }
}
