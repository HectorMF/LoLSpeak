package org.perfectplay.com.lolspeak;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import dto.Team.Roster;


public class MainMenu extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        View gameInfoButton = (Button) findViewById(R.id.gameInfoButton);
        View summonerInfoButton = (Button) findViewById(R.id.summonerReviewButton);
        View friendsButton = (Button) findViewById(R.id.friendsButton);

        gameInfoButton.setOnClickListener(this);
        summonerInfoButton.setOnClickListener(this);
        friendsButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
        switch
                (view.getId())
        {
            case R.id.gameInfoButton:
                startActivity(new Intent(this, GameInfoTabbed.class));
                break;
            case R.id.friendsButton:
                startActivity(new Intent(this, RosterActivity.class));
                break;
            case R.id.summonerReviewButton:
                startActivity(new Intent(this, SummonerReview.class));
                break;
        }
    }
}
