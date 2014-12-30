package com.soco.SoCoClient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.soco.SoCoClient.db.DBManagerSoco;
import com.soco.SoCoClient.db.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private DBManagerSoco dbmgrSoco;
    private ListView listViewSoco;
    private List<Program> programs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbmgrSoco = new DBManagerSoco(this);
        programs = dbmgrSoco.loadProgram();

        listViewSoco = (ListView) findViewById(R.id.listView);
        listPrograms(null);

        listViewSoco.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView)parent;
                HashMap<String, String> map = (HashMap<String, String>)
                        listView.getItemAtPosition(position);
                String name = map.get(Program.PROGRAM_PNAME);

                Intent intent = new Intent(view.getContext(), ShowProgramActivity.class);
                intent.putExtra(Program.PROGRAM_PNAME, name);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cleanDB(View view) {
        dbmgrSoco.cleanDB();
        Toast.makeText(getApplicationContext(), "Database cleaned.", Toast.LENGTH_SHORT).show();
    }

    public void createProgram(View view) {
        Intent intent = new Intent(this, CreateProgramActivity.class);
        startActivity(intent);
    }

    public void listPrograms(View view) {

        ArrayList<Map<String, String>> list = new ArrayList<>();
        for (Program program : programs) {
            HashMap<String, String> map = new HashMap<>();
            map.put(Program.PROGRAM_PNAME, program.pname);
            map.put(Program.PROGRAM_PINFO, program.getMoreInfo());
            list.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2,
                new String[]{Program.PROGRAM_PNAME, Program.PROGRAM_PINFO},
                new int[]{android.R.id.text1, android.R.id.text2});
        listViewSoco.setAdapter(adapter);
    }

    public void showCompletedPrograms(View view) {
        Intent intent = new Intent(this, ShowCompletedProgramsActivity.class);
        startActivity(intent);
    }


}
