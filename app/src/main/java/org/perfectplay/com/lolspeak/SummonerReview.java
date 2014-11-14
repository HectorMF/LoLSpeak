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

import java.util.Map;

import constant.Region;
import dto.Summoner.Summoner;
import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;


public class SummonerReview extends Activity implements Button.OnClickListener{
    private Summoner data;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summoner_review);

        Button btn = (Button) findViewById(R.id.searchButton);
        btn.setOnClickListener(this);
        handler = new Handler();
    }

    public void PopulatePage(Summoner data)
    {
        TextView name = (TextView) findViewById(R.id.summonerName);
        name.setText(data.getName());
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
                data = summoners[0];

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
            }};

        t.start();
    }
}
