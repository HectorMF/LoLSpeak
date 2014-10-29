package org.perfectplay.com.lolspeak.GameInfoActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import org.perfectplay.com.lolspeak.R;

public class SummonerGameInfo extends Activity {
    private TabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summoner_game_info);

        mTabHost = (TabHost) findViewById(R.id.summonerGameInfoTabHost);

        TabHost.TabSpec masteriesTab = mTabHost.newTabSpec("Masteries");
        TabHost.TabSpec runesTab = mTabHost.newTabSpec("Runes");
        TabHost.TabSpec spellsTab = mTabHost.newTabSpec("Summoner Spells");

        masteriesTab.setIndicator("Masteries");
        masteriesTab.setContent(new Intent(this, Masteries.class));

        runesTab.setIndicator("Runes");
        runesTab.setContent(new Intent(this, RuneList.class));

        spellsTab.setIndicator("Summoner Spells");
        spellsTab.setContent(new Intent(this, SummonerSpellList.class));

        mTabHost.addTab(masteriesTab);
        mTabHost.addTab(runesTab);
        mTabHost.addTab(spellsTab);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summoner_game_info, menu);
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
