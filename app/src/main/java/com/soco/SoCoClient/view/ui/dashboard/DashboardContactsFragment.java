package com.soco.SoCoClient.view.ui.dashboard;

//import info.androidhive.tabsswipe.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soco.SoCoClient.R;
import com.soco.SoCoClient.control.SocoApp;

public class DashboardContactsFragment extends Fragment implements View.OnClickListener {

    String tag = "ContactsFragment";
    View rootView;

    int pid;
    String pid_onserver;
    SocoApp socoApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        socoApp = (SocoApp)(getActivity().getApplication());
        pid = socoApp.pid;
        pid_onserver = socoApp.pid_onserver;
        Log.d(tag, "pid is " + pid + ", pid_onserver is " + pid_onserver);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(tag, "create project members fragment view");
        rootView = inflater.inflate(R.layout.fragment_dashboard_contacts, container, false);

//        rootView.findViewById(R.id.bt_add).setOnClickListener(this);
//        listMembers();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                //todo: do something
                break;
        }
    }



}