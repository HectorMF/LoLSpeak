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
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import dto.Static.Passive;
import dto.Static.Stats;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;

public class ChampionInfo extends Activity implements ListView.OnItemClickListener{

    private ArrayAdapter<String> mAdapter;
    private Champion data;
    private Handler handler;
    private ListView spellList;
    private ScrollView loreDesc;
    private GridLayout stats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_info);
        handler = new Handler();



        // Set the adapter
        spellList = (ListView) findViewById(R.id.spellListContent);
        loreDesc = (ScrollView) findViewById(R.id.loreDesc);
        stats = (GridLayout) findViewById(R.id.statsContent);

        loreDesc.setVisibility(View.GONE);
        spellList.setVisibility(View.GONE);
        stats.setVisibility(View.GONE);
        List<String> emptyList = new ArrayList<String>();

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
                    data = api.getDataChampion(266, "en_US", "4.19.2", true, ChampData.ALL);
                    final Runnable r = new Runnable()
                    {
                        public void run()
                        {
                            if(data == null)
                                handler.postDelayed(this, 500);
                            PopulatePage(data);
                        }
                    };

                    handler.post(r);
                } catch (RiotApiException e) {
                    e.printStackTrace();
                }
            }};

        t.start();
    }

    public void toggle_spellContents(View v)
    {
        /**
         * onClick handler
         */
        if(!spellList.isShown()){
            spellList.setVisibility(View.VISIBLE);
            SlidingHelper.slide_down(this, spellList);
            SlidingHelper.slide_up(this, loreDesc);
            loreDesc.setVisibility(View.GONE);
            SlidingHelper.slide_up(this, stats);
            stats.setVisibility(View.GONE);
        }
    }

    public void toggle_loreContents(View v)
    {
        /**
         * onClick handler
         */
        if(!loreDesc.isShown()){
            loreDesc.setVisibility(View.VISIBLE);
            SlidingHelper.slide_down(this, loreDesc);
            SlidingHelper.slide_up(this, stats);
            stats.setVisibility(View.GONE);
            SlidingHelper.slide_up(this, spellList);
            spellList.setVisibility(View.GONE);
        }
    }

    public void toggle_statsContents(View v)
    {
        /**
         * onClick handler
         */
        if(!stats.isShown()){
            stats.setVisibility(View.VISIBLE);
            SlidingHelper.slide_down(this, stats);
            SlidingHelper.slide_up(this, loreDesc);
            loreDesc.setVisibility(View.GONE);
            SlidingHelper.slide_up(this, spellList);
            spellList.setVisibility(View.GONE);
        }
    }

    public void PopulatePage(Champion champ)
    {
        TextView name = (TextView) findViewById(R.id.champName);
        TextView title = (TextView) findViewById(R.id.champTitle);
        TextView lore = (TextView) findViewById(R.id.loreDescLabel);

        name.setText(champ.getName());
        title.setText(champ.getTitle());
        lore.setText(champ.getLore());


        Stats stats = champ.getStats();

        TextView health = (TextView) findViewById(R.id.healthContent);
        TextView mana = (TextView) findViewById(R.id.manaContent);
        TextView damage = (TextView) findViewById(R.id.damage);
        TextView as = (TextView) findViewById(R.id.attackSpeed);
        TextView ms = (TextView) findViewById(R.id.movementSpeed);
        TextView healthRegen = (TextView) findViewById(R.id.healthRegen);
        TextView manaRegen = (TextView) findViewById(R.id.manaRegen);
        TextView armor = (TextView) findViewById(R.id.armor);
        TextView mr = (TextView) findViewById(R.id.magicResist);

        health.setText(getStatDisplay(stats.getHp(), stats.getHpperlevel()));
        mana.setText(getStatDisplay(stats.getMp(), stats.getMpperlevel()));
        damage.setText(getStatDisplay(stats.getAttackdamage(), stats.getAttackdamageperlevel()));
        as.setText(getStatDisplay(stats.getAttackspeedoffset(), stats.getAttackspeedperlevel()));
        ms.setText(stats.getMovespeed() + "");
        healthRegen.setText(getStatDisplay(stats.getHpregen(), stats.getHpregenperlevel()));
        manaRegen.setText(getStatDisplay(stats.getMpregen(), stats.getMpregenperlevel()));
        armor.setText(getStatDisplay(stats.getArmor(), stats.getArmorperlevel()));
        mr.setText(getStatDisplay(stats.getSpellblock(), stats.getSpellblockperlevel()));

        //Handle Spells
        List<ChampionSpell> spells = champ.getSpells();
        List<String> spellNames = new ArrayList<String>();
        Passive passive = champ.getPassive();
        spellNames.add(passive.getName());

        for(int i = 0; i < spells.size(); i++)
            spellNames.add(spells.get(i).getName());

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, spellNames);

        spellList.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    private String getStatDisplay(double flat, double perLevel)
    {
        return flat + "(+" + perLevel + " per level)";
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
