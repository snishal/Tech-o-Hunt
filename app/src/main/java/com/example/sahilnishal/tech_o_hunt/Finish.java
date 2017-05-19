package com.example.sahilnishal.tech_o_hunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

public class Finish extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        ImageView i = (ImageView)findViewById(R.id.imageView2);
        i.setImageResource(R.drawable.acm_logo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Result");
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if((item.toString()).equals("Result")) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Key");
            alert.setMessage("Enter key if you wish to continue");
            final EditText key = new EditText(this);
            alert.setView(key);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (key.getText().toString().equals("admin")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                        Intent i = new Intent(getBaseContext(),Result.class);
                        i.putExtra("timings", sharedPreferences.getString("result", ""));
                        startActivity(i);
                    }
                }
            });
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
