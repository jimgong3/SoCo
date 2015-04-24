package com.soco.SoCoClient.view.dashboard;

//import info.androidhive.tabsswipe.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.soco.SoCoClient.control.config.GeneralConfig;
import com.soco.SoCoClient.control.http.task.AddFriendTaskAsync;
import com.soco.SoCoClient.view.contacts.ContactDetailsActivity;
import com.soco.SoCoClient.R;
import com.soco.SoCoClient.control.SocoApp;
import com.soco.SoCoClient.control.db.DBManagerSoco;
import com.soco.SoCoClient.model.Profile;
import com.soco.SoCoClient.view.common.sectionlist.EntryItem;
import com.soco.SoCoClient.view.common.sectionlist.Item;
import com.soco.SoCoClient.view.common.sectionlist.SectionEntryListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashboardContactsFragment extends Fragment implements View.OnClickListener {

    String tag = "ContactsFragment";
    View rootView;

    int pid;
    String pid_onserver;
    SocoApp socoApp;
    Profile profile;
    DBManagerSoco dbManagerSoco;
    ArrayList<Item> contactItems;
    SectionEntryListAdapter contactsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        socoApp = (SocoApp)(getActivity().getApplication());
        profile = socoApp.profile;
        dbManagerSoco = socoApp.dbManagerSoco;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(tag, "create project members fragment view.....");
        rootView = inflater.inflate(R.layout.fragment_dashboard_contacts, container, false);

        ((ListView)rootView.findViewById(R.id.listview_contacts)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //new
                if (!contactItems.get(position).isSection()) {  //click on item/name
                    EntryItem item = (EntryItem) contactItems.get(position);
                    Log.d(tag, "You clicked: " + item.title);

                    String name = item.title;
                    String email = item.subtitle;
//                    int pid = ProjectUtil.findPidByPname(projects, name);
//                    socoApp.pid = pid;
//                    String pid_onserver = dbmgrSoco.findActivityIdOnserver(pid);
//                    socoApp.pid_onserver = pid_onserver;
//                    Log.i(tag, "pid/pid_onserver: " + pid + ", " + pid_onserver);

                    //new fragment-based activity
                    Intent i = new Intent(view.getContext(), ContactDetailsActivity.class);
                    i.putExtra(GeneralConfig.INTENT_KEY_NAME, name);
                    i.putExtra(GeneralConfig.INTENT_KEY_EMAIL, email);
                    startActivity(i);
                }
            }
        });

        rootView.findViewById(R.id.add).setOnClickListener(this);
        listContacts();

        return rootView;
    }

//    public void updateContactName(final String email) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
//        alert.setTitle("New contact name");
//        alert.setMessage("So I want to ...");
//        final EditText input = new EditText(getActivity());
//        alert.setView(input);
//
//        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                String name = input.getText().toString();
//                dbManagerSoco.updateContactName(email, name);
//            }
//        });
//
//        alert.show();
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                add();
                break;
        }
    }


    public void add(){
        String email = ((EditText) rootView.findViewById(R.id.email)).getText().toString();
        Log.i(tag, "save into db new member " + email);
        dbManagerSoco.saveContact(email);

        //todo: send invitation to server and save contact id onserver
        Log.d(tag, "send add friend request to server: " + email);
        String url = profile.getAddFriendUrl(getActivity());
        AddFriendTaskAsync task = new AddFriendTaskAsync(url, email, getActivity().getApplicationContext());
        task.execute();

        Toast.makeText(getActivity().getApplicationContext(), "Sent invitation success",
                Toast.LENGTH_SHORT).show();

        listContacts();
    }

    public void listContacts() {
        Log.d(tag, "List contacts");

        contactItems = new ArrayList<Item>();
        HashMap<String, String> map = dbManagerSoco.getContacts();

        for(Map.Entry<String, String> e : map.entrySet()){
            Log.d(tag, "found contact: " + e.getValue() + ", " + e.getKey());
            contactItems.add(new EntryItem(e.getValue(), e.getKey()));
        }

        contactsAdapter = new SectionEntryListAdapter(getActivity(), contactItems);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_contacts);
        lv.setAdapter(contactsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(tag, "onResume start, reload project attribute for pid: ");
    }

}