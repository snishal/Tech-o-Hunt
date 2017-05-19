package com.example.sahilnishal.tech_o_hunt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Question extends Activity {

    Time t;
    TextView textView;
    Button button;

    int penalty_count = 0;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    static int question = 1;
    static int answer = 1;
    static int first = 2;
    int current_question;
    String time;
    int team_id;
    static final String questions[] = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        timerValue = (TextView) findViewById(R.id.timer);

        customHandler.postDelayed(updateTimerThread, 0);


        textView = (TextView) findViewById(R.id.question);
        textView.setMovementMethod(new ScrollingMovementMethod());
        button = (Button) findViewById(R.id.scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Scan.class);
                startActivityForResult(i, question);
            }
        });
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        current_question = pref.getInt(getString(R.string.cq),-1);
        team_id = pref.getInt(getString(R.string.td),-2);
        time = pref.getString(getString(R.string.time),"Team_ID :");
        for(int i=0; i<7; i++)
            questions[i] = pref.getString("array_" + i, null);

        if (team_id == -2) {
            SharedPreferences sharedPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("team_id_submitted", true);
            editor.commit();
            Intent i = getIntent();
            team_id = i.getExtras().getInt("team_ID") - 1;
            populate();
            startTime = SystemClock.uptimeMillis();
            current_question = 0;
            textView.setText(questions[current_question]);
            t = new Time();
            t.setToNow();
            time += (team_id+1) + "\n\n";
            time += t.toString().substring(9, 11) + " : " + t.toString().substring(11, 13) + " : " + t.toString().substring(13, 15) + "\n\n";
            SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(getString(R.string.td), team_id);
            edit.putString(getString(R.string.time), time);
            edit.putLong(getString(R.string.startTime), startTime);
            edit.putInt(getString(R.string.penalty_count), penalty_count);
            edit.commit();
        }else{
            textView.setText(questions[current_question]);
            startTime = pref.getLong("0L",0L);
            penalty_count = pref.getInt(getString(R.string.penalty_count),0);
        }

    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hrs = mins/60;
            mins = mins % 60;
            secs = secs % 60;
            timerValue.setText(hrs + ":"+
                    mins + ":"
                    + String.format("%02d", secs)
            );
            customHandler.postDelayed(this, 0);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == question) {
            if (resultCode == answer) {
                final String qr_code = data.getExtras().getString("code");
                if (qr_code.length() > 0) {
                    if (current_question == 6) {
                        if(qr_code.equals("Finish")) {
                            SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
                            t = new Time();
                            t.setToNow();
                            time += t.toString().substring(9, 11) + " : " + t.toString().substring(11, 13) + " : " + t.toString().substring(13, 15) + "\n\n";
                            time += "\nTotal Time : " + timerValue.getText();
                            time += "\nPenalty : " + penalty_count;
                            time += "\nInternet Connected : " + prefs.getBoolean("Internet Connect", false);
                            SharedPreferences sharedPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("finished", true);
                            editor.putString("result", time);
                            editor.commit();
                            Intent i = new Intent(getBaseContext(), Final.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            penalty_count++;
                            Toast.makeText(getApplicationContext(), "Wrong Location", Toast.LENGTH_SHORT).show();
                        }
                    } else if (qr_code.equals(questions[current_question + 1])) {
                        current_question++;
                        textView.setText(questions[current_question]);
                        t = new Time();
                        t.setToNow();
                        time += t.toString().substring(9, 11) + " : " + t.toString().substring(11, 13) + " : " + t.toString().substring(13, 15) + "\n\n";
                        Toast.makeText(getApplicationContext(), "Congrats Next Level", Toast.LENGTH_SHORT).show();
                    } else{
                        penalty_count++;
                        Toast.makeText(getApplicationContext(), "Wrong Location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if(requestCode == 2){
            final String qr_code = data.getExtras().getString("code");
            if(qr_code.length() > 0){
                Toast.makeText(getApplicationContext(),"QR Scanner Working properly",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int item_id = item.getItemId();
        if(item_id == R.id.time || item_id == R.id.reset) {
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
                        int id = item.getItemId();
                        if (id == R.id.time) {
                            Intent i = new Intent(getBaseContext(), Result.class);
                            i.putExtra("timings", time);
                            startActivity(i);
                        } else if (id == R.id.reset) {
                            SharedPreferences sharedPreferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = sharedPreferences.edit();
                            ed.putBoolean("team_id_submitted", false);
                            ed.commit();
                            time = "Team_ID : ";
                            startTime = 0L;
                            penalty_count = 0;
                            question = 0;
                            team_id = -2;
                            finish();
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"Passcode Incorrect!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alert.show();
        }

        else if(item_id == R.id.contact){
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Contact Us\n");
            alert.setMessage("Sahil : 9467918415\n\nRashad : 7289975258\n\nShikha Lal : 8375996223");
            alert.setPositiveButton("Continue", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.show();
        }

        else if(item_id == R.id.testscan){
            Intent intent_scan = new Intent(getBaseContext(), Scan.class);
            startActivityForResult(intent_scan, 2);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(getString(R.string.td),team_id);
        edit.putInt(getString(R.string.cq),current_question);
        edit.putString(getString(R.string.time),time);
        edit.putInt(getString(R.string.penalty_count),penalty_count);
        edit.putLong(getString(R.string.startTime),startTime);
        for(int i=0;i<7; i++)
            edit.putString("array_" + i, questions[i]);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected) {
            edit.putBoolean("Internet Connect", true);
        }
        edit.commit();
    }

    protected void populate(){
        for(int i=0;i<7;i++) {

            Bitmap mybitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.set1question1 + (7*team_id) + i);
            BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.QR_CODE).build();
            if (!detector.isOperational()) {
                Toast.makeText(this, "Could not setup the detector", Toast.LENGTH_SHORT).show();
                return;
            }

            Frame frame = new Frame.Builder().setBitmap(mybitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);

            Barcode thisCode = barcodes.valueAt(0);
            questions[i] = thisCode.rawValue;
        }
    }
}
