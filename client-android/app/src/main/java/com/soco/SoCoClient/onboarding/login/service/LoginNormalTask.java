package com.soco.SoCoClient.onboarding.login.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.soco.SoCoClient.common.HttpStatus;
import com.soco.SoCoClient.common.ReturnCode;
import com.soco.SoCoClient.common.TaskCallBack;
import com.soco.SoCoClient.common.http.HttpUtil;
import com.soco.SoCoClient.common.http.JsonKeys;
import com.soco.SoCoClient.common.http.UrlUtil;
import com.soco.SoCoClient.common.util.SocoApp;
import com.soco.SoCoClient.events.model.Event;
import com.soco.SoCoClient.groups.model.Group;
import com.soco.SoCoClient.userprofile.model.User;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class LoginNormalTask extends AsyncTask<String, Void, Boolean> {

    String tag = "LoginNormalTask";

    static final String PERFS_NAME = "EVENT_BUDDY_PERFS";
    static final String USER_ID = "user_id";
    static final String TOKEN = "token";

    Context context;
    SocoApp socoApp;
    TaskCallBack callBack;

    public LoginNormalTask(Context context, TaskCallBack cb){
        this.context = context;
        this.socoApp = (SocoApp) context;
        this.callBack = cb;
    }

    protected Boolean doInBackground(String... params) {
        Log.d(tag, "login normal task:"
                        + ", login email " + socoApp.loginEmail
                        + ", logig password " + socoApp.loginPassword
        );

        Log.v(tag, "login to server");

        String url = UrlUtil.getLoginUrl();
        String name = "";   //not available from UI
        String email = socoApp.loginEmail;
        String phone = "";  //not available from UI
        String password = socoApp.loginPassword;
        Object response = request(
                url,
                name,
                email,
                phone,
                password
        );

        if (response != null) {
            Log.v(tag, "set response flag as true");
            socoApp.loginNormalResponse= true;

            Log.v(tag, "parse response");
            parse(response);
        }
        else {
            Log.e(tag, "response is null, cannot parse");
            return false;
        }

        return true;
    }


    private Object request(
            String url,
            String name,
            String email,
            String phone,
            String password
    ) {
        Log.v(tag, "create json request");

        JSONObject data = new JSONObject();
        try {
            if(!name.isEmpty())
                data.put(JsonKeys.NAME, name);
            data.put(JsonKeys.EMAIL, email);
            if(!phone.isEmpty())
                data.put(JsonKeys.PHONE, phone);
            data.put(JsonKeys.PASSWORD, password);
            Log.d(tag, "normal login request json: " + data);
        } catch (Exception e) {
            Log.e(tag, "cannot create json post data");
            e.printStackTrace();
        }

        return HttpUtil.executeHttpPost(url, data);
    }

    private int parse(Object response) {
        Log.d(tag, "parse login normal response: " + response.toString());

        try {
            JSONObject json = new JSONObject(response.toString());

            int status = json.getInt(JsonKeys.STATUS);
            if(status == HttpStatus.SUCCESS) {
                Log.v(tag, "login success, retrieve user id and token");
                socoApp.loginNormalResult = true;

                String user_id = json.getString(JsonKeys.USER_ID);
                String token = json.getString(JsonKeys.TOKEN);
//                String verified = json.getString(JsonKeys.VERIFIED);
                Log.d(tag, "login success, " +
                                "user id: " + user_id + ", token: " + token
                );

                Log.v(tag, "set app userid, save userid and token: " + user_id + ", " + token);
                socoApp.user_id = user_id;
                socoApp.token = token;

                Log.v(tag, "save userid/token to shared preference");
                SharedPreferences settings = context.getSharedPreferences(PERFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(USER_ID, user_id);
                editor.putString(TOKEN, token);
                editor.commit();
            }
            else {
                Log.v(tag, "login fail");
                socoApp.loginNormalResult = false;

                String error_code = json.getString(JsonKeys.ERROR_CODE);
                String property = json.getString(JsonKeys.PROPERTY);
                String message = json.getString(JsonKeys.MESSAGE);
                String more_info = json.getString(JsonKeys.MORE_INFO);
                Log.e(tag, "login fail, error code: " + error_code + ", message: " + message
                        + ", more info: " + more_info);
            }
        } catch (Exception e) {
            Log.e(tag, "cannot convert parse to json object: " + e.toString());
            e.printStackTrace();
            socoApp.loginNormalResult = false;
            return ReturnCode.JSON_PARSE_ERROR;
        }

        return ReturnCode.SUCCESS;
    }

    protected void onPostExecute(Boolean result){
        Log.v(tag, "post execute");
        callBack.doneTask(null);
    }
}