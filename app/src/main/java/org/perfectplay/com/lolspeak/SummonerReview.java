package org.perfectplay.com.lolspeak;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import constant.Region;
import constant.Season;
import dto.Stats.AggregatedStats;
import dto.Stats.PlayerStatsSummary;
import dto.Stats.PlayerStatsSummaryList;
import dto.Summoner.Summoner;
import dto.Summoner.SummonerName;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;


public class SummonerReview extends Activity implements Button.OnClickListener{
    private PlayerStatsSummaryList data;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summoner_review);

        Button btn = (Button) findViewById(R.id.searchButton);
        btn.setOnClickListener(this);
        handler = new Handler();
    }

    public void PopulatePage(String name, PlayerStatsSummaryList data)
    {
        List<PlayerStatsSummary> stats = data.getPlayerStatSummaries();

        int i = 0;

        while(!stats.get(i).getPlayerStatSummaryType().equalsIgnoreCase("unranked"))
            i++;

        AggregatedStats aStat = stats.get(i).getAggregatedStats();
        TextView nameLbl = (TextView) findViewById(R.id.summonerName);
        nameLbl.setText(name);

        TextView won = (TextView) findViewById(R.id.won);
        TextView kills = (TextView) findViewById(R.id.kills);
        TextView minions = (TextView) findViewById(R.id.minion);
        TextView turrets = (TextView) findViewById(R.id.towers);

        won.setText(stats.get(i).getWins() + "");
        kills.setText(aStat.getTotalChampionKills() + "");
        minions.setText(aStat.getTotalMinionKills() + "");
        turrets.setText(aStat.getTotalTurretsKilled() + "");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.summoner_review, menu);
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
    public void onClick(View view) {
        Thread t = new Thread()
        {
            @Override
            public void run() {
                RiotApi api = new RiotApi("a29fee68-0e2a-446e-b1ec-211dba50aedc");
                api.setRegion(Region.NA);

                //Collection<Champion> shittyList = api.getDataChampionList().getData().values();
                //Champion[] champs = new Champion[shittyList.size()];
                //api.getDataChampionList().getData().values().toArray(champs);
                Map<String, Summoner> summoner = null;
                try {
                    EditText nameText = (EditText) findViewById(R.id.searchText);
                    summoner = api.getSummonerByName(nameText.getText().toString());
                } catch (RiotApiException e) {
                    e.printStackTrace();
                }
                
                Summoner[] summoners = new Summoner[summoner.size()];
                summoner.values().toArray(summoners);
                if(summoners.length == 0) data = null;
                else {
                    long id = summoners[0].getId();
                    try {
                        data = api.getPlayerStatsSummary(Region.NA, Season.Season4, id);
                    } catch (RiotApiException e) {
                        e.printStackTrace();
                    }
                }

                final String name = summoners[0].getName();

                final Runnable r = new Runnable()
                {
                    public void run()
                    {
                        PopulatePage(name, data);
                    }
                };

                handler.post(r);
            }};

        t.start();
    }
}
