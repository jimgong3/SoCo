package com.soco.SoCoClient.groups.task;

import android.os.AsyncTask;
import android.util.Log;

import com.soco.SoCoClient.common.HttpStatus;
import com.soco.SoCoClient.common.TaskCallBack;
import com.soco.SoCoClient.common.http.HttpUtil;
import com.soco.SoCoClient.common.http.JsonKeys;
import com.soco.SoCoClient.common.http.UrlUtil;
import com.soco.SoCoClient.common.util.GroupsReponseUtil;
import com.soco.SoCoClient.groups.model.Group;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class GroupMembersTask extends AsyncTask<String, Void, Group> {

    String tag = "GroupMembersTask";
//    public static final String START_MEMBER_ID = JsonKeys.MEMBER_ID;
    String user_id;
    String token;
    String group_id;
    TaskCallBack callBack;

    public GroupMembersTask(String user_id, String token,String groupId, TaskCallBack cb){
        Log.v(tag, "group details task with userid: " + user_id);
        this.user_id=user_id;
        this.token=token;
        this.group_id=groupId;
        callBack = cb;
    }


    protected Group doInBackground(String... params) {
        Log.v(tag, "validate data");

        String url = UrlUtil.getGroupMembersUrl();
        Object response = request(
                url,
                user_id,
                token,
                group_id,
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
            String group_id,
            String... input){
        if(!url.endsWith("?"))
            url += "?";

        List<NameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair(JsonKeys.USER_ID, user_id));
        params.add(new BasicNameValuePair(JsonKeys.TOKEN, token));
        params.add(new BasicNameValuePair(JsonKeys.GROUP_ID, group_id));
        if(null!=input&&input.length>0){
            params.add(new BasicNameValuePair(JsonKeys.MEMBER_ID, input[0]));
        }
        String paramString = URLEncodedUtils.format(params, "utf-8");

        url += paramString;
        Log.d(tag, "request url: " + url);

        return HttpUtil.executeHttpGet(url);
    }

    Group parse(Object response) {
        Log.d(tag, "parse response: " + response.toString());

        try {
            JSONObject json = new JSONObject(response.toString());

            int status = json.getInt(JsonKeys.STATUS);
            if(status == HttpStatus.SUCCESS) {
                String gs = json.getString(JsonKeys.GROUP);
                JSONObject gobj = new JSONObject(gs);
                Log.v(tag, "group json: " + gobj);
                return GroupsReponseUtil.parseGroupResponse(gobj);
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

    protected void onPostExecute(Group result) {
        callBack.doneTask(result);
    }
}
