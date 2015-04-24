package com.soco.SoCoClient.view.activities;

//import info.androidhive.tabsswipe.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.soco.SoCoClient.R;
import com.soco.SoCoClient.control.SocoApp;
import com.soco.SoCoClient.control.config.HttpConfig;
import com.soco.SoCoClient.control.db.DBManagerSoco;
import com.soco.SoCoClient.control.http.task.InviteActivityMemberTaskAsync;
import com.soco.SoCoClient.model.Profile;
import com.soco.SoCoClient.view.common.sectionlist.SectionEntryListAdapter;
import com.soco.SoCoClient.view.common.sectionlist.EntryItem;
import com.soco.SoCoClient.view.common.sectionlist.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityMembersFragment extends Fragment implements View.OnClickListener {

    String tag = "ProjectMembersFragment";
    View rootView;

    int pid;
    String pid_onserver;
    SocoApp socoApp;
    Profile profile;
    DBManagerSoco dbManagerSoco;
    SectionEntryListAdapter membersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        socoApp = (SocoApp)(getActivity().getApplication());
        profile = socoApp.profile;
        dbManagerSoco = socoApp.dbManagerSoco;

        pid = socoApp.pid;
        pid_onserver = socoApp.pid_onserver;
        Log.d(tag, "pid is " + pid + ", pid_onserver is " + pid_onserver);

        //todo: test script
//        if(pid_onserver == null)
//            pid_onserver = "1";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(tag, "create project members fragment view");
        rootView = inflater.inflate(R.layout.fragment_activity_members, container, false);

        rootView.findViewById(R.id.bt_add).setOnClickListener(this);
        listMembers();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                add();
                break;
        }
    }

    public void add(){
        String url = profile.getInviteProjectMemberUrl(getActivity());
        String email = ((EditText) rootView.findViewById(R.id.et_add_member)).getText().toString();
        InviteActivityMemberTaskAsync httpTask = new InviteActivityMemberTaskAsync(
                        url,
                        String.valueOf(pid_onserver),
                        email                                       //invite email
        );
        httpTask.execute();

        Toast.makeText(getActivity().getApplicationContext(), "Sent invitation success",
                Toast.LENGTH_SHORT).show();

//        String userEmail = profile.email;
//        String userName = profile.username;
        Log.i(tag, "save into db new member " + email + "/" + HttpConfig.TEST_UNKNOWN_USERNAME
                + " into project " + pid);
        dbManagerSoco.addMemberToActivity(email, HttpConfig.TEST_UNKNOWN_USERNAME, pid);
        listMembers();

    }

    public void listMembers() {
        Log.d(tag, "List project members");
        ArrayList<Item> memberItems = new ArrayList<Item>();
        HashMap<String, String> map = dbManagerSoco.getMembersOfActivity(pid);

        for(Map.Entry<String, String> e : map.entrySet()){
            Log.d(tag, "found member: " + e.getValue() + ", " + e.getKey());
            memberItems.add(new EntryItem(e.getValue(), e.getKey()));
        }

        membersAdapter = new SectionEntryListAdapter(getActivity(), memberItems);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_members);
        lv.setAdapter(membersAdapter);
    }

}