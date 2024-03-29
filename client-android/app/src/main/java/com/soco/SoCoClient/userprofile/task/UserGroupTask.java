package com.soco.SoCoClient.userprofile.task;


import android.os.AsyncTask;
import android.util.Log;

import com.soco.SoCoClient.common.HttpStatus;
import com.soco.SoCoClient.common.TaskCallBack;
import com.soco.SoCoClient.common.http.HttpUtil;
import com.soco.SoCoClient.common.http.JsonKeys;
import com.soco.SoCoClient.common.http.UrlUtil;
import com.soco.SoCoClient.common.util.SocoApp;
import com.soco.SoCoClient.groups.model.Group;
import com.soco.SoCoClient.common.util.GroupsReponseUtil;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserGroupTask extends AsyncTask<String, Void, ArrayList<Group> >{

    String tag = "UserGroupTask";
    String user_id;
    String token;

    TaskCallBack callBack;

    public UserGroupTask(String user_id, String token, TaskCallBack cb){
        Log.v(tag, "user group task: " );
        this.user_id = user_id;
        this.token = token;
        callBack = cb;

    }


    protected ArrayList<Group>  doInBackground(String... params) {
        Log.v(tag, "validate data");
        String url = UrlUtil.getUserGroupUrl();
        Object response = request(
                url,
                SocoApp.user_id,
                SocoApp.token,
                params[0]
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
            String buddy_id){

        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair(JsonKeys.USER_ID, user_id));
        params.add(new BasicNameValuePair(JsonKeys.TOKEN, token));

        params.add(new BasicNameValuePair(JsonKeys.BUDDY_ID, buddy_id));
        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        Log.d(tag, "request url: " + url);

        return HttpUtil.executeHttpGet(url);
    }

    ArrayList<Group>  parse(Object response) {
        Log.d(tag, "parse response: " + response.toString());

        try {
            JSONObject json = new JSONObject(response.toString());

            int status = json.getInt(JsonKeys.STATUS);
            if(status == HttpStatus.SUCCESS) {
                return GroupsReponseUtil.parseGroupsResponse(json);
            }
            else {
                String error_code = json.getString(JsonKeys.ERROR_CODE);
                String message = json.getString(JsonKeys.MESSAGE);
                String more_info = json.getString(JsonKeys.MORE_INFO);
                Log.d(tag, "request fail, "
                                + "error code: " + error_code
                                + ", message: " + message
                                + ", more info: " + more_info
                );
            }
        } catch (Exception e) {
            Log.e(tag, "cannot convert parse to json object: " + e.toString());
            e.printStackTrace();

        }

        return null;
    }

    protected void onPostExecute(ArrayList<Group>  result) {
        callBack.doneTask(result);
    }

}
