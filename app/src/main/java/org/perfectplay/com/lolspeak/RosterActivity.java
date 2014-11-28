package org.perfectplay.com.lolspeak;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import org.perfectplay.com.lolspeak.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class RosterActivity extends Activity {
    private ArrayAdapter<Spanned> adapter;
    private Spinner spinner1;
    private ListView listview;
    private HashMap<String, String> nameLookup = new HashMap<String, String>();
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roster);

       // spinner1 = (Spinner) findViewById(R.id.presence);
        listview = (ListView) findViewById(R.id.listRoster);

        TextView text = (TextView) findViewById(R.id.logo);
        text.setTypeface(LoginActivity.font);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parentView, View childView,
                                       int position, long id)
            {
                Spanned selectedFromList = (Spanned)(listview.getItemAtPosition(position));
                startChat(selectedFromList.toString());
            }

            public void onNothingSelected(AdapterView parentView) {

            }
        });

        handler = new Handler();
        setListAdapter();

        final Runnable r = new Runnable()
        {
            public void run()
            {
                handler.postDelayed(this, 5000);
                updateListAdapter();
            }
        };

        handler.post(r);
    }
    private void startChat(String user) {
        if(!nameLookup.containsKey(user)) return;
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("User", nameLookup.get(user));

        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.roster, menu);
        return true;
    }

    private void setListAdapter() {
        Collection<RosterEntry> entries = LoginActivity.roster.getEntries();
        ArrayList<Spanned> roster = new ArrayList<Spanned>();
        nameLookup.clear();
        //ArrayAdapter<Spanned> adapter;
        for (RosterEntry entry : entries) {
            Presence p = LoginActivity.roster.getPresence(entry.getUser());
            if(p.getType() == Presence.Type.available) {
                roster.add(Html.fromHtml("<b>" + entry.getName() + "</b>"));
                nameLookup.put(entry.getName(), entry.getUser());
            }
        }
        for (RosterEntry entry : entries) {
            Presence p = LoginActivity.roster.getPresence(entry.getUser());
            if(p.getType() == Presence.Type.unavailable) {
                roster.add(Html.fromHtml("<font color=" +"#666666" + "><b>" + entry.getName() + "</b></font>"));
                nameLookup.put(entry.getName(), entry.getUser());
            }
        }
        adapter = new ArrayAdapter<Spanned>(this, R.layout.listitem, roster);
        listview.setAdapter(adapter);
        listview.setSelection(0);
    }


    private void updateListAdapter() {
        Collection<RosterEntry> entries = LoginActivity.roster.getEntries();
        ArrayList<Spanned> roster = new ArrayList<Spanned>();
        nameLookup.clear();
        //ArrayAdapter<Spanned> adapter;
        for (RosterEntry entry : entries) {
            Presence p = LoginActivity.roster.getPresence(entry.getUser());
            if(p.getType() == Presence.Type.available) {
                roster.add(Html.fromHtml("<b>" + entry.getName() + "</b>"));
                nameLookup.put(entry.getName(), entry.getUser());
            }
        }
        for (RosterEntry entry : entries) {
            Presence p = LoginActivity.roster.getPresence(entry.getUser());
            if(p.getType() == Presence.Type.unavailable) {
                roster.add(Html.fromHtml("<font color=" +"#666666" + "><b>" + entry.getName() + "</b></font>"));
                nameLookup.put(entry.getName(), entry.getUser());
            }
        }
        adapter.clear();
        adapter.addAll(roster);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
