package com.example.sahilnishal.tech_o_hunt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(5000);
                    Intent i = new Intent(getBaseContext(),Team_Id.class);
                    startActivity(i);
                    finish();
                }
                catch(InterruptedException e){
                }
            }
        };

        SharedPreferences sharedPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("finished", false)){
            Intent i = new Intent(getBaseContext(),Finish.class);
            startActivity(i);
            finish();
        }
        else if(sharedPreferences.getBoolean("team_id_submitted", false)){
            Intent i = new Intent(getBaseContext(),Question.class);
            startActivity(i);
            finish();
        }
        else {
            timer.start();
        }

    }
}
