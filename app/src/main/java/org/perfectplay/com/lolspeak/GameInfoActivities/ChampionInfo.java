package org.perfectplay.com.lolspeak.GameInfoActivities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.perfectplay.com.lolspeak.R;
import org.perfectplay.com.lolspeak.Utility.SlidingHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import constant.Region;
import constant.staticdata.ChampData;
import constant.staticdata.SpellData;
import dto.Static.Champion;
import dto.Static.ChampionList;
import dto.Static.ChampionSpell;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

public class ChampionInfo extends Activity implements ListView.OnItemClickListener{

    private ArrayAdapter<String> mAdapter;
    private ArrayList<String> data;
    private Handler handler;
    private ListView spellList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_info);
        handler = new Handler();



        // Set the adapter
        spellList = (ListView) findViewById(R.id.spellListContent);
        spellList.setVisibility(View.GONE);
        data = new ArrayList<String>();

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, data);

        spellList.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        spellList.setOnItemClickListener(this);

        Thread t = new Thread()
        {
            @Override
            public void run() {
                RiotApi api = new RiotApi("a29fee68-0e2a-446e-b1ec-211dba50aedc");
                api.setRegion(Region.NA);

                try {
                    //Collection<Champion> shittyList = api.getDataChampionList().getData().values();
                    //Champion[] champs = new Champion[shittyList.size()];
                    //api.getDataChampionList().getData().values().toArray(champs);
                    dto.Static.Champion champ = api.getDataChampion(266, "en_US", "4.19.2", true, ChampData.ALL);
                    PopulatePage(champ);
                } catch (RiotApiException e) {
                    e.printStackTrace();
                }
            }};

        t.start();

        final Runnable r = new Runnable()
        {
            public void run()
            {
                if(mAdapter.getCount() == 0)
                    handler.postDelayed(this, 500);
                mAdapter.notifyDataSetChanged();
            }
        };

        handler.post(r);
    }

    public void toggle_spellContents(View v)
    {
        /**
         * onClick handler
         */
        if(spellList.isShown()){
            SlidingHelper.slide_up(this, spellList);
            spellList.setVisibility(View.GONE);
        }
        else{
            spellList.setVisibility(View.VISIBLE);
            SlidingHelper.slide_down(this, spellList);
        }
    }

    public void PopulatePage(Champion champ)
    {
        List<ChampionSpell> spells = champ.getSpells();

        data.clear();

        for(int i = 0; i < spells.size(); i++)
            data.add(spells.get(i).getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.champion_info, menu);
        return true;
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
}
