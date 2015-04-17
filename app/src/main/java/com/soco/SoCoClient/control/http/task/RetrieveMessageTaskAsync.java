package com.soco.SoCoClient.control.http.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.soco.SoCoClient.control.SocoApp;
import com.soco.SoCoClient.control.config.HttpConfig;
import com.soco.SoCoClient.control.db.DBManagerSoco;
import com.soco.SoCoClient.control.http.HttpUtil;
import com.soco.SoCoClient.control.util.ProfileUtil;
import com.soco.SoCoClient.model.Project;

import org.json.JSONArray;
import org.json.JSONObject;

public class RetrieveMessageTaskAsync extends AsyncTask<Void, Void, Boolean> {

    static String tag = "RetrieveMessageTask";

    String url;
    Context context;

    public RetrieveMessageTaskAsync(
            String url,
            Context context
    ){
        Log.i(tag, "Create new HttpTask: " + url);
        this.url = url;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(url == null || url.isEmpty() ){
            Log.e(tag, "Cannot get url");
            return false;
        }

        execute(url, context);
        return true;
    }

    public void execute(String url, Context context){
        Object response = request(url);
        if (response != null)
            parse(response, context);
    }

    public Object request(String url) {
        JSONObject data = new JSONObject();
        //no data needed for retrieve message
        return HttpUtil.executeHttpPost(url, data);
    }

    /*
    Sample response - success:
        {status:"success",
        message:[
            {from_type:1, from_id:"test@test.com", to_type:2, to_id:1,
            send_date_time:"2015-04-05 12:12:12", content_type:1, signature:"XYZABC123",
            content:"hi how are you"},
            {another message}
        ],
        finish:1}
    Sample response - failure:
        {status:"failure"}
    */
    public static boolean parse(Object response, Context context) {
        try {
            String str = response.toString();
            Log.i(tag, "Server response string: " + str);

            JSONObject json = new JSONObject(response.toString());
            String isSuccess = json.getString(HttpConfig.JSON_KEY_RESPONSE_STATUS);

            if(isSuccess.equals(HttpConfig.JSON_VALUE_RESPONSE_STATUS_SUCCESS)) {
                Log.i(tag, "Server response status success");

                //extract messages
                JSONArray messageArray = new JSONArray(
                        json.getString(HttpConfig.JSON_KEY_MESSAGE));

                //process each message
                for (int i = 0; i < messageArray.length(); i++) {
                    JSONObject message = messageArray.getJSONObject(i);

                    int from_type = message.getInt(HttpConfig.JSON_KEY_FROM_TYPE);
                    String from_id = message.getString(HttpConfig.JSON_KEY_FROM_ID);
                    Log.i(tag, "Get message, from: " + from_type + ", " + from_id);

                    int to_type = message.getInt(HttpConfig.JSON_KEY_TO_TYPE);
                    String to_id = message.getString(HttpConfig.JSON_KEY_TO_ID);
                    Log.i(tag, "Get message, to: "   + to_type + ", " + to_id);

                    String send_date_time = message.getString(HttpConfig.JSON_KEY_SEND_DATE_TIME);
                    Log.i(tag, "Get message, timestamp: " + send_date_time);

                    int content_type = message.getInt(HttpConfig.JSON_KEY_CONTENT_TYPE);
                    String content = message.getString(HttpConfig.JSON_KEY_CONTENT);
                    Log.i(tag, "Get message, content: " + content_type + ", " + content);

                    //todo: add message into database
                    if(to_type == HttpConfig.MESSAGE_TO_TYPE_2) {   //type 2: send to activity
                        DBManagerSoco dbManagerSoco = ((SocoApp) context).dbManagerSoco;
                        int aid_local = dbManagerSoco.findLocalAidByServerAid(Integer.parseInt(to_id));
                        Log.i(tag, "add comment to project: " + aid_local + ", " + content
                                + ", " + from_id);
                        dbManagerSoco.addCommentToProject(content, aid_local, from_id);
                    }
                    else if(to_type == HttpConfig.MESSAGE_TO_TYPE_1){   //type 1: send to member
                        Log.e(tag, "no available function yet for sending message to member");
                    }

                    //todo: send ack to server
                }
            }
            else {
                Log.e(tag, "Parse result not in success status");
                return false;
            }
        } catch (Exception e) {
            Log.e(tag, "Cannot convert parse to Json object: " + e.toString());
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
