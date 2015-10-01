package com.soco.SoCoClient.view.events;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.soco.SoCoClient.R;
import com.soco.SoCoClient.control.config.DataConfig;
import com.soco.SoCoClient.control.database.DataLoader;
import com.soco.SoCoClient.model.Event;
import com.soco.SoCoClient.view.common.Item;

import java.util.ArrayList;

public class ActivityAllEvents extends ActionBarActivity {

    static String tag = "AllEvents";

    ListView lv_active_programs;
    EditText et_quick_add;

    Context context;
    DataLoader dataLoader;
    ArrayList<Event> events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);

        context = getApplicationContext();
        dataLoader = new DataLoader(context);
        events = dataLoader.loadEvents();

        lv_active_programs = (ListView) findViewById(R.id.all_events);
        et_quick_add = ((EditText) findViewById(R.id.et_quickadd));
        findViewById(R.id.add).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                add();
            }
        });


        showEvents(events);

        lv_active_programs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e = events.get(position);
                Log.d(tag, "tap on event: " + e.toString());

                Intent i = new Intent(view.getContext(), ActivityEventDetailV1.class);
                i.putExtra(DataConfig.EXTRA_EVENT_SEQ, e.getSeq());
                startActivity(i);
            }
        });
    }

    void showEvents(ArrayList<Event> events) {
        Log.v(tag, "show events to ui");
        ArrayList<Item> allListItems = new ArrayList<>();

        for(Event e : events){
            allListItems.add(new EventListEntryItem(e.getName(), e.getDesc(), e.getDate()));
        }

//        Log.d(tag, "refresh UI");
        EventListAdapter adapter = new EventListAdapter(this, allListItems);
        lv_active_programs.setAdapter(adapter);
    }

    public void add(){
        String name = et_quick_add.getText().toString();
        Log.d(tag, "quick add event: " + name);

        Log.v(tag, "create new event");
        Event e = new Event(getApplicationContext());
        e.setName(name);
//        e.addContext(context);
        e.save();

        DataLoader dataLoader = new DataLoader(context);
        events = dataLoader.loadEvents();
        showEvents(events);

        //clean up
        et_quick_add.setText("", TextView.BufferType.EDITABLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow((findViewById(R.id.et_quickadd)).getWindowToken(), 0);
    }

    public void onResume() {
        super.onResume();
//        Log.i(tag, "onResume start, reload active projects");
//        activities = dbmgrSoco.loadActivitiessByActiveness(DataConfigV1.VALUE_ACTIVITY_ACTIVE);
//        activities = dbmgrSoco.loadActiveActivitiesByPath(socoApp.currentPath);
//        refreshList();
        events = dataLoader.loadEvents();
        showEvents(events);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_all_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            //insert code
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}