package org.perfectplay.com.lolspeak;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import org.perfectplay.com.lolspeak.GameInfoActivities.ChampionList;
import org.perfectplay.com.lolspeak.GameInfoActivities.ItemList;
import org.perfectplay.com.lolspeak.GameInfoActivities.SummonerGameInfo;

public class GameInfo extends Activity {
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabHost.TabSpec championsTab = mTabHost.newTabSpec("Champions");
        TabHost.TabSpec itemsTab = mTabHost.newTabSpec("Items");
        TabHost.TabSpec summonersTab = mTabHost.newTabSpec("Summoners");

        championsTab.setIndicator("Champions");
        championsTab.setContent(new Intent(this, ChampionList.class));

        itemsTab.setIndicator("Items");
        itemsTab.setContent(new Intent(this, ItemList.class));

        summonersTab.setIndicator("Champions");
        summonersTab.setContent(new Intent(this, SummonerGameInfo.class));

        mTabHost.addTab(championsTab);
        mTabHost.addTab(itemsTab);
        mTabHost.addTab(summonersTab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_info, menu);
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
}
