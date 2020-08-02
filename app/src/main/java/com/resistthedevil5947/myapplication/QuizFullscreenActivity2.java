package com.resistthedevil5947.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class QuizFullscreenActivity2 extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    String[] mobileArray = {"Android","IPhone","WindowsMobile", "Linux"};
    JSONObject questionitem;
    JSONArray questions;
    String question;
    String answer;
    String userchoice;
    ArrayList<String> choices;
    TextView question_tv;
    int questionnumber = 0;
    Button submit_button;
    Button tryagain_button;
    Button exit_button;
    MediaPlayer mediaPlayer = new MediaPlayer();
    int mistakes = 0;
    ListView listView;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    public QuizFullscreenActivity2() {
        listView = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_quiz_fullscreen2);
        Bundle bundle = getIntent().getExtras();
        try {
            questions = new JSONArray (bundle.getString("questions"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        question_tv = (TextView)findViewById(R.id.question_tv);
        loadQuestion();
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        submit_button = (Button) findViewById(R.id.submit_button);
        tryagain_button = (Button) findViewById(R.id.tryagain_button);
        exit_button = (Button) findViewById(R.id.exit_button);
        submit_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                if (questionnumber<5){
                    if(answer.equals(userchoice)){
                        Log.i("correct", userchoice+answer);
                        try{
                            playCorrectSound();

                        }catch (Exception err){
                            Log.i("correctsounderror:", err.toString());
                        }
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "CORRECT",
                                Toast.LENGTH_SHORT);

                        toast.show();
                        questionnumber = questionnumber + 1;
                        loadQuestion();
                        if (questionnumber==5){
                            question_tv.setText("YOU MADE " + Integer.toString(mistakes) + " mistakes");
                            listView.setVisibility(View.GONE);
                            tryagain_button.setVisibility(View.VISIBLE);
                            submit_button.setVisibility(View.GONE);


                        }

                    }else{
                        Log.i("wrong. correct answer ", answer + userchoice);
                        try{
                            playWrongSound();

                        }catch (Exception err){
                            Log.i("correctsounderror:", err.toString());
                        }
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "WRONG",
                                Toast.LENGTH_SHORT);

                        toast.show();

                    }



                }




            }
        });
        exit_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void loadQuestion(){
        try {
            questionitem = (JSONObject) questions.get(questionnumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            question = (String) questionitem.getString("question");
            answer = (String) questionitem.getString("answer");
            JSONArray choicearray = (JSONArray) questionitem.getJSONArray("choices");
            choices = new ArrayList<String>();
            String[] abc = {"A", "B", "C", "D"};
            for (int x=0; x<choicearray.length();x ++){
                String choice = (String) choicearray.get(x);
                choices.add(choice);
            }
            Collections.shuffle(choices);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        question_tv.setText(Integer.toString(questionnumber+1) + ". " + question );
        final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, choices);

        listView = (ListView) findViewById(R.id.choices_list);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userchoice = (String) choices.get(position);
                userchoice = (String) parent.getItemAtPosition(position);
            }
        });


    }

    public void playCorrectSound(){
        mediaPlayer=MediaPlayer.create(this, R.raw.correct2);
        mediaPlayer.start();
//        Uri myUri = Uri.parse("https://gesab001.github.io/assets/soundeffects/correct2.mp3");
//        try {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource(this, myUri);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
//            mediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//            playCorrectSound2();
//        }
    }


    public void playCorrectSound2(){
        Uri myUri = Uri.parse("http://192.168.1.70/assets/soundeffects/soundeffects/correct2.mp3");
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, myUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer=MediaPlayer.create(this, R.raw.correct2);
            mediaPlayer.start();
        }
    }

    public void playWrongSound(){
//        Uri githuburl = Uri.parse("https://gesab001.github.io/assets/soundeffects/wrong2.mp3");
        mediaPlayer=MediaPlayer.create(this, R.raw.wrong2);
        mediaPlayer.start();
        mistakes = mistakes + 1;
//        try {
//            mediaPlayer.setDataSource(this, githuburl);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//            playWrongSound2();
//
//        }
    }
    public void playWrongSound2(){
        mediaPlayer=MediaPlayer.create(this, R.raw.wrong2);
        mediaPlayer.start();

//        Uri homeurl = Uri.parse("http://192.168.1.70/assets/soundeffects/soundeffects/wrong2.mp3");
//
//        try {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setDataSource(this, homeurl);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//
//            mediaPlayer=MediaPlayer.create(this, R.raw.wrong2);
//            mediaPlayer.start();
//
//        }
    }


}
