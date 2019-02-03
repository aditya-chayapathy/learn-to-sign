package com.example.amine.learn2sign;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.stetho.Stetho;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Utils;

import static android.provider.MediaStore.EXTRA_DURATION_LIMIT;
import static android.provider.MediaStore.EXTRA_MEDIA_TITLE;
import static com.example.amine.learn2sign.LoginActivity.INTENT_EMAIL;
import static com.example.amine.learn2sign.LoginActivity.INTENT_ID;
import static com.example.amine.learn2sign.LoginActivity.INTENT_SERVER_ADDRESS;
import static com.example.amine.learn2sign.LoginActivity.INTENT_TIME_WATCHED;
import static com.example.amine.learn2sign.LoginActivity.INTENT_TIME_WATCHED_VIDEO;
import static com.example.amine.learn2sign.LoginActivity.INTENT_URI;
import static com.example.amine.learn2sign.LoginActivity.INTENT_WORD;

public class MainActivity extends AppCompatActivity {


    static final int REQUEST_VIDEO_CAPTURE = 1;

    @BindView(R.id.rg_practice_learn)
    RadioGroup rg_practice_learn;

    @BindView(R.id.rb_learn)
    RadioButton rb_learn;

    @BindView(R.id.rb_practice)
    RadioButton rb_practice;

    @BindView(R.id.rb_precision)
    RadioButton rb_precision;

    @BindView(R.id.sp_words)
    Spinner sp_words;

    @BindView(R.id.sp_ip_address)
    Spinner sp_ip_address;

    @BindView(R.id.vv_video_learn)
    VideoView vv_video_learn;

    @BindView(R.id.vv_record)
    VideoView vv_record;

    @BindView(R.id.bt_record)
    Button bt_record;

    @BindView(R.id.bt_father)
    Button bt_father;

    @BindView(R.id.scores_heading)
    TextView scores_heading;

    @BindView(R.id.scores_values)
    TextView scores_values;

//    @BindView(R.id.bt_about)
//    Button bt_about;

    @BindView(R.id.bt_send)
    Button bt_send;

    @BindView(R.id.bt_cancel)
    Button bt_cancel;

    @BindView(R.id.ll_after_record)
    LinearLayout ll_after_record;

    @BindView(R.id.ratingText)
    TextView ratingText;

    String path;
    String returnedURI;
    String old_text = "";
    SharedPreferences sharedPreferences;
    long time_started = 0;
    long time_started_return = 0;
    Activity mainActivity;
    SeekBar ratingSeek;
    int rating_val = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind xml to activity
        ButterKnife.bind(this);
        Stetho.initializeWithDefaults(this);

        rb_learn.setChecked(true);
        bt_cancel.setVisibility(View.GONE);
        bt_send.setVisibility(View.GONE);
        bt_father.setVisibility(View.GONE);
        //bt_about.setVisibility(View.GONE);
        scores_heading.setVisibility(View.GONE);
        scores_values.setVisibility(View.GONE);
        rg_practice_learn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                HashSet<String> radioButtonClickSet = (HashSet<String>) sharedPreferences.getStringSet("RADIO_BUTTON_CLICK", new HashSet<String>());
                radioButtonClickSet.add("RADIO_BUTTON_CLICK_" + checkedId + "_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
                sharedPreferences.edit().putStringSet("RADIO_BUTTON_CLICK", radioButtonClickSet).apply();

                if(checkedId==rb_learn.getId()) {
                    sharedPreferences.edit().putString("mode","learn").apply();
                    Toast.makeText(getApplicationContext(),"Learn",Toast.LENGTH_SHORT).show();
                    vv_video_learn.setVisibility(View.VISIBLE);
                    vv_video_learn.start();
                    time_started = System.currentTimeMillis();
                    sp_words.setEnabled(true);
                    sp_words.setVisibility(View.GONE);
                    sp_ip_address.setEnabled(true);
                    bt_father.setVisibility(View.GONE);
                    //bt_about.setVisibility(View.GONE);
                    scores_heading.setVisibility(View.GONE);
                    scores_values.setVisibility(View.GONE);
                } else if ( checkedId==rb_practice.getId()) {
                    sharedPreferences.edit().putString("mode","practice").apply();
                    Toast.makeText(getApplicationContext(),"Practice",Toast.LENGTH_SHORT).show();
                    vv_video_learn.setVisibility(View.GONE);
                    int randomVal = (int)(Math.random() * 25);
                    sp_words.setSelection(randomVal);
                    //sp_words.setMinimumHeight(50);
                    sp_words.setEnabled(false);
                    sp_words.setVisibility(View.GONE);
                    sp_ip_address.setEnabled(false);
                    bt_father.setVisibility(View.GONE);
                    //bt_about.setVisibility(View.GONE);
                    scores_heading.setVisibility(View.GONE);
                    scores_values.setVisibility(View.GONE);
                } else if ( checkedId==rb_precision.getId()) {
                    sharedPreferences.edit().putString("mode","precision").apply();
                    Toast.makeText(getApplicationContext(),"Checking Prescision",Toast.LENGTH_SHORT).show();
                    int randomVal = (int)(Math.random() * 25);
                    sp_words.setSelection(randomVal);
                    //sp_words.setMinimumHeight(50);
                    vv_video_learn.setVisibility(View.GONE);
                    bt_record.setVisibility(View.GONE);
                    sp_words.setEnabled(false);
                    sp_words.setVisibility(View.GONE);
                    bt_send.setVisibility(View.GONE);
                    bt_cancel.setVisibility(View.GONE);
                    ratingText.setVisibility(View.GONE);
                    ratingSeek.setVisibility(View.GONE);
                    scores_heading.setVisibility(View.GONE);
                    scores_values.setVisibility(View.GONE);
                    sp_ip_address.setEnabled(false);
                    bt_father.setVisibility(View.VISIBLE);
                    //bt_about.setVisibility(View.VISIBLE);

                }

            }
        });

        sp_words.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                HashSet<String> wordsDropdownSet = (HashSet<String>) sharedPreferences.getStringSet("WORDS_DROPDOWN_CLICK", new HashSet<String>());
                wordsDropdownSet.add("WORDS_DROPDOWN_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
                sharedPreferences.edit().putStringSet("WORDS_DROPDOWN_CLICK", wordsDropdownSet).apply();

                String text = sp_words.getSelectedItem().toString();
                if(!old_text.equals(text)) {
                    path = "";
                    time_started = System.currentTimeMillis();
                    play_video(text);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_ip_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPreferences.edit().putString(INTENT_SERVER_ADDRESS, sp_ip_address.getSelectedItem().toString()).apply();

                HashSet<String> ipsDropdownSet = (HashSet<String>) sharedPreferences.getStringSet("IPS_DROPDOWN_CLICK", new HashSet<String>());
                ipsDropdownSet.add("IPS_DROPDOWN_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
                sharedPreferences.edit().putStringSet("IPS_DROPDOWN_CLICK", ipsDropdownSet).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.start();

                }

             }
        };
        vv_record.setOnCompletionListener(onCompletionListener);
        vv_video_learn.setOnCompletionListener(onCompletionListener);
        vv_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashSet<String> ipsDropdownSet = (HashSet<String>) sharedPreferences.getStringSet("VIDEO_PREVIEW_CLICK", new HashSet<String>());
                ipsDropdownSet.add("VIDEO_PREVIEW_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
                sharedPreferences.edit().putStringSet("VIDEO_PREVIEW_CLICK", ipsDropdownSet).apply();

                vv_record.start();
            }
        });
        vv_video_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashSet<String> ipsDropdownSet = (HashSet<String>) sharedPreferences.getStringSet("VIDEO_PREVIEW_CLICK", new HashSet<String>());
                ipsDropdownSet.add("VIDEO_PREVIEW_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
                sharedPreferences.edit().putStringSet("VIDEO_PREVIEW_CLICK", ipsDropdownSet).apply();

                if(!vv_video_learn.isPlaying()) {
                    vv_video_learn.start();
                }
            }
        });
        time_started = System.currentTimeMillis();
        sharedPreferences =  this.getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("mode","learn").apply();
        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_EMAIL) && intent.hasExtra(INTENT_ID)) {
            Toast.makeText(this,"User : " + intent.getStringExtra(INTENT_EMAIL),Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this,"Already Logged In",Toast.LENGTH_SHORT).show();

        }

        ratingSeek = (SeekBar) findViewById(R.id.ratingBar); // initiate the progress bar
        ratingSeek.setVisibility(View.GONE);
        ratingText.setVisibility(View.GONE);
        ratingSeek.setMax(10); // 200 maximum value for the Seek bar
        ratingSeek.setProgress(0); // 50 default progress value

        ratingSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                HashSet<String> ratingBarSet = (HashSet<String>) sharedPreferences.getStringSet("RATING_BAR_CLICK", new HashSet<String>());
                ratingBarSet.add("RATING_BAR_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
                sharedPreferences.edit().putStringSet("RATING_BAR_CLICK", ratingBarSet).apply();

                ratingText.setText("Rating:" + i);
                rating_val = i;
                sharedPreferences.edit().putInt("rating",rating_val).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        HashSet<String> backCLickSet = (HashSet<String>) sharedPreferences.getStringSet("BACK_CLICK", new HashSet<String>());
        backCLickSet.add("BACK_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
        sharedPreferences.edit().putStringSet("BACK_CLICK", backCLickSet).apply();

        moveTaskToBack(true);
        ratingSeek.setVisibility(View.GONE);
        ratingText.setVisibility(View.GONE);
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onResume() {

        vv_video_learn.start();
        time_started = System.currentTimeMillis();
        super.onResume();

    }

    public void play_video(String text) {
        old_text = text;
        if(text.equals("About")) {

             path = "android.resource://" + getPackageName() + "/" + R.raw._about;
        } else if(text.equals("And")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._and;
        } else if (text.equals("Can")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._can;
        }else if (text.equals("Cat")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._cat;
        }else if (text.equals("Cop")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._cop;
        }else if (text.equals("Cost")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._cost;
        }else if (text.equals("Day")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._day;
        }else if (text.equals("Deaf")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._deaf;
        }else if (text.equals("Decide")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._decide;
        }else if (text.equals("Father")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._father;
        }else if (text.equals("Find")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._find;
        }else if (text.equals("Go Out")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._go_out;
        }else if (text.equals("Gold")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._gold;
        }else if (text.equals("Goodnight")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._good_night;
        }else if (text.equals("Hearing")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._hearing;
        }else if (text.equals("Here")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._here;
        }else if (text.equals("Hospital")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._hospital;
        }else if (text.equals("Hurt")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._hurt;
        }else if (text.equals("If")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._if;
        }else if (text.equals("Large")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._large;
        }else if (text.equals("Hello")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._hello;
        }else if (text.equals("Help")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._help;
        }else if (text.equals("Sorry")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._sorry;
        }else if (text.equals("After")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._after;
        }else if (text.equals("Tiger")) {
            path = "android.resource://" + getPackageName() + "/" + R.raw._tiger;
        }
        if(!path.isEmpty()) {
            Uri uri = Uri.parse(path);
            vv_video_learn.setVideoURI(uri);
            vv_video_learn.start();
        }

    }

    @OnClick(R.id.bt_father)
    public void send_father_data() {
        ArrayList<String> filepaths = new ArrayList<String>();
        ArrayList<String> outputs;
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot+"/Learn2Sign/about_father");

        String[] paths= yourDir.list();
        List<String> tempList = new ArrayList<String>(Arrays.asList(paths));
        Collections.shuffle(tempList);
        int numberOfFiles = paths.length;
        int testSetLength = (int)(Math.floor(numberOfFiles*0.2));

        try {

            if (paths != null) {
                for (int i=0;i<testSetLength;i++) {
                    String name = tempList.get(i);
                    //if (name.toLowerCase().contains("father")) {
                        System.out.println(name);
                        filepaths.add(sdCardRoot+"/Learn2Sign/about_father/"+name);
                    //}
                }
            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Data Issue");
                alertDialog.setMessage("Could not find files in folder /Learn2Sign/about_father/ in the root directory");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
            ClassifierService cc = new ClassifierService();
            cc.initializeClassifier();
            outputs = new ArrayList<String>(cc.getPredictionForFiles(filepaths));
            scores_heading.setVisibility(View.VISIBLE);
            scores_values.setVisibility(View.VISIBLE);
            String values="";
            for(int i=0;i<outputs.size();i++) {
                values="\t"+values+tempList.get(i)+"\t\t\t"+outputs.get(i)+"\n";
            }
            scores_values.setText(values);
        }
        catch (Exception ex) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("File Access Issue");
            alertDialog.setMessage("Issue with files access");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.show();
        }
    }

//    @OnClick(R.id.bt_about)
//    public void send_about_data(){
//        ArrayList<String> filepaths = new ArrayList<String>();
//        ArrayList<String> outputs;
//        File sdCardRoot = Environment.getExternalStorageDirectory();
//        File yourDir = new File(sdCardRoot+"/Learn2Sign/about_father");
//        String[] paths= yourDir.list();
//        try {
//
//            if (paths != null) {
//                for (String name : paths) {
//                    if (name.toLowerCase().contains("about")) {
//                            System.out.println(name);
//                            filepaths.add(sdCardRoot+"/Learn2Sign/about_father/"+name);
//                    }
//                }
//            } else {
//                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//                alertDialog.setTitle("Data Issue");
//                alertDialog.setMessage("Could not find files in folder /Learn2Sign/about_father/ in the root directory");
//                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                alertDialog.show();
//            }
//            ClassifierService cc = new ClassifierService();
//            cc.initializeClassifier();
//            outputs = new ArrayList<String>(cc.getPredictionForFiles(filepaths));
//            scores_heading.setVisibility(View.VISIBLE);
//            scores_values.setVisibility(View.VISIBLE);
//            String values="";
//            for(int i=0;i<outputs.size();i++) {
//                values=values+outputs.get(i)+"\n";
//            }
//            scores_values.setText(values);
//
//        }
//        catch (Exception ex) {
//            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//            alertDialog.setTitle("File Access Issue");
//            alertDialog.setMessage("Issue with files");
//            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                }
//            });
//            alertDialog.show();
//        }
//
//
//    }

    @OnClick(R.id.bt_record)
    public void record_video() {

        HashSet<String> buttonClickSet = (HashSet<String>) sharedPreferences.getStringSet("RECORD_BUTTON_CLICK", new HashSet<String>());
        buttonClickSet.add("RECORD_BUTTON_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
        sharedPreferences.edit().putStringSet("RECORD_BUTTON_CLICK", buttonClickSet).apply();

         if( ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ) {

             // Permission is not granted
             // Should we show an explanation?

             if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                     Manifest.permission.CAMERA)) {
                 // Show an explanation to the user *asynchronously* -- don't block
                 // this thread waiting for the user's response! After the user
                 // sees the explanation, try again to request the permission.
             } else {
                 // No explanation needed; request the permission
                 ActivityCompat.requestPermissions(this,
                         new String[]{Manifest.permission.CAMERA},
                         101);

                 // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                 // app-defined int constant. The callback method gets the
                 // result of the request.
             }
         }


         if ( ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            // Permission is not granted
            // Should we show an explanation?


            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        } else {
            // Permission has already been granted
             File f = new File(Environment.getExternalStorageDirectory(), "Learn2Sign");

             if (!f.exists()) {
                 f.mkdirs();
             }

             time_started = System.currentTimeMillis() - time_started;

             Intent t = new Intent(this,VideoActivity.class);
             t.putExtra(INTENT_WORD,sp_words.getSelectedItem().toString());
             t.putExtra(INTENT_TIME_WATCHED, time_started);
             startActivityForResult(t,9999);





 /*           File m = new File(Environment.getExternalStorageDirectory().getPath() + "/Learn2Sign");
            if(!m.exists()) {
                if(m.mkdir()) {
                    Toast.makeText(this,"Directory Created",Toast.LENGTH_SHORT).show();
                }
            }

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(EXTRA_DURATION_LIMIT, 10);

            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }*/
        }
    }

    @OnClick(R.id.bt_send)
    public void sendToServer() {
        HashSet<String> buttonClickSet = (HashSet<String>) sharedPreferences.getStringSet("SEND_BUTTON_CLICK", new HashSet<String>());
        buttonClickSet.add("SEND_BUTTON_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
        sharedPreferences.edit().putStringSet("SEND_BUTTON_CLICK", buttonClickSet).apply();

        Toast.makeText(this,"Send to Server",Toast.LENGTH_SHORT).show();
        Intent t = new Intent(this,UploadActivity.class);
        startActivityForResult(t,2000);
        ratingSeek.setVisibility(View.GONE);
        ratingText.setVisibility(View.GONE);

    }

    @OnClick(R.id.bt_cancel)
    public void cancel() {
        HashSet<String> buttonClickSet = (HashSet<String>) sharedPreferences.getStringSet("CANCEL_BUTTON_CLICK", new HashSet<String>());
        buttonClickSet.add("CANCEL_BUTTON_CLICK_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
        sharedPreferences.edit().putStringSet("CANCEL_BUTTON_CLICK", buttonClickSet).apply();

        vv_record.setVisibility(View.GONE);
        if(rb_learn.isSelected()) {
            vv_video_learn.setVisibility(View.VISIBLE);
        }
        bt_record.setVisibility(View.VISIBLE);
        bt_send.setVisibility(View.GONE);
        bt_cancel.setVisibility(View.GONE);
        ratingSeek.setVisibility(View.GONE);
        ratingText.setVisibility(View.GONE);

        sp_words.setEnabled(true);

        rb_learn.setEnabled(true);
        rb_practice.setEnabled(true);
        time_started = System.currentTimeMillis();


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

    Log.e("OnActivityresult",requestCode+" "+resultCode);
        if(requestCode==2000 ) {
            //from video activity
            vv_record.setVisibility(View.GONE);
            rb_learn.setChecked(true);
            bt_cancel.setVisibility(View.GONE);
            bt_send.setVisibility(View.GONE);
            bt_record.setVisibility(View.VISIBLE);
            sp_words.setEnabled(true);
            rb_learn.setEnabled(true);
            rb_practice.setEnabled(true);
            sp_ip_address.setEnabled(true);


        }
        if(requestCode==9999 && resultCode == 8888) {
            if(intent.hasExtra(INTENT_URI) && intent.hasExtra(INTENT_TIME_WATCHED_VIDEO)) {
                returnedURI = intent.getStringExtra(INTENT_URI);
                time_started_return = intent.getLongExtra(INTENT_TIME_WATCHED_VIDEO,0);

                vv_record.setVisibility(View.VISIBLE);
                bt_record.setVisibility(View.GONE);
                bt_send.setVisibility(View.VISIBLE);
                bt_cancel.setVisibility(View.VISIBLE);
                ratingSeek.setVisibility(View.VISIBLE);
                ratingText.setVisibility(View.VISIBLE);
                sp_words.setEnabled(false);
                rb_learn.setEnabled(false);
                rb_practice.setEnabled(false);
                vv_record.setVideoURI(Uri.parse(returnedURI));
                vv_record.start();
                int try_number = sharedPreferences.getInt("record_"+sp_words.getSelectedItem().toString(),0);
                try_number++;
                String toAdd  = sp_words.getSelectedItem().toString()+"_"+try_number+"_"+time_started_return + "";
                HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("RECORDED",new HashSet<String>());
                set.add(toAdd);
                sharedPreferences.edit().putStringSet("RECORDED",set).apply();
                sharedPreferences.edit().putInt("record_"+sp_words.getSelectedItem().toString(), try_number).apply();
                //find a way to know which radio button is checked and then set this to true or false
                //it should be visible for practice and invisible for learn module
                if(rb_practice.isChecked()) {
                    vv_video_learn.setVisibility(View.VISIBLE);
                    vv_video_learn.start();
                }
                else {
                    vv_video_learn.setVisibility(View.GONE);
                }
            }
            if(sharedPreferences.getString("mode","learn").equalsIgnoreCase("learn")) {
                ratingSeek.setVisibility(View.GONE);
                ratingText.setVisibility(View.GONE);
            }

        }

        if(requestCode==9999 && resultCode==7777)
        {
            if(intent!=null) {
                //create folder
                if(intent.hasExtra(INTENT_URI) && intent.hasExtra(INTENT_TIME_WATCHED_VIDEO)) {
                    returnedURI = intent.getStringExtra(INTENT_URI);
                    time_started_return = intent.getLongExtra(INTENT_TIME_WATCHED_VIDEO,0);
                    File f = new File(returnedURI);
                    f.delete();
                  //  int try_number = sharedPreferences.getInt("record_"+sp_words.getSelectedItem().toString(),0);
                   // try_number++;
                    //String toAdd  = sp_words.getSelectedItem().toString()+"_"+try_number+"_"+time_started_return + "_cancelled";
                    //HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("RECORDED",new HashSet<String>());
                   // set.add(toAdd);
                  //  sharedPreferences.edit().putStringSet("RECORDED",set).apply();
                 //   sharedPreferences.edit().putInt("record_"+sp_words.getSelectedItem().toString(), try_number).apply();




                    time_started = System.currentTimeMillis();
                    vv_video_learn.start();
                }
            }

        }

        /*if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            final Uri videoUri = intent.getData();


            vv_record.setVisibility(View.VISIBLE);
            vv_record.setVideoURI(videoUri);
            vv_record.start();
            play_video(sp_words.getSelectedItem().toString());
            bt_record.setVisibility(View.GONE);
            int i=0;
            File n = new File(Environment.getExternalStorageDirectory().getPath() + "/Learn2Sign/"
                    + sharedPreferences.getString(INTENT_ID,"0000")+"_"+sp_words.getSelectedItem().toString()+"_0" + ".mp4");
            while(n.exists()) {
                i++;
                n = new File(Environment.getExternalStorageDirectory().getPath() + "/Learn2Sign/"
                        + sharedPreferences.getString(INTENT_ID,"0000")+"_"+sp_words.getSelectedItem().toString()+"_"+i + ".mp4");
            }
            SaveFile saveFile = new SaveFile();
            saveFile.execute(n.getPath(),videoUri.toString());

            bt_send.setVisibility(View.VISIBLE);
            bt_cancel.setVisibility(View.VISIBLE);

            sp_words.setEnabled(false);
            rb_learn.setEnabled(false);
            rb_practice.setEnabled(false);
        }*/
    }

    //Menu Item for logging out
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        HashSet<String> menuClickSet = (HashSet<String>) sharedPreferences.getStringSet("MENU_CLICK", new HashSet<String>());
        menuClickSet.add("MENU_CLICK_" + item.getItemId() + "_" + sharedPreferences.getString(INTENT_ID, "") + "_" + sharedPreferences.getString(INTENT_EMAIL, "") + "_" + String.valueOf(System.currentTimeMillis()));
        sharedPreferences.edit().putStringSet("MENU_CLICK", menuClickSet).apply();

        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.menu_logout:
                mainActivity = this;
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("ALERT");
                    alertDialog.setMessage("Logging out will delete all the data!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sharedPreferences.edit().clear().apply();
                                    File f = new File(Environment.getExternalStorageDirectory(), "Learn2Sign");
                                    if (f.isDirectory())
                                    {
                                        String[] children = f.list();
                                        for (int i = 0; i < children.length; i++)
                                        {
                                            new File(f, children[i]).delete();
                                        }
                                    }
                                    startActivity(new Intent(mainActivity,LoginActivity.class));
                                    mainActivity.finish();

                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();



                    return true;
            case R.id.menu_upload_server:
                sharedPreferences.edit().putInt(getString(R.string.gotoupload), sharedPreferences.getInt(getString(R.string.gotoupload),0)+1).apply();
                Intent t = new Intent(this,UploadActivity.class);
                startActivityForResult(t,2000);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class SaveFile extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            FileOutputStream fileOutputStream = null;
            FileInputStream fileInputStream = null;
            try {
                fileOutputStream = new FileOutputStream(strings[0]);
                fileInputStream = (FileInputStream) getContentResolver().openInputStream(Uri.parse(strings[1]));
                Log.d("msg", fileInputStream.available() + " ");
                byte[] buffer = new byte[1024];
                while (fileInputStream.available() > 0) {

                    fileInputStream.read(buffer);
                    fileOutputStream.write(buffer);
                    publishProgress(fileInputStream.available()+"");
                }

                fileInputStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getApplicationContext(),"Video Saved Successfully",Toast.LENGTH_SHORT).show();
        }
    }
}
