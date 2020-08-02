package com.resistthedevil5947.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    JSONArray story = new JSONArray();
    JSONObject article = new JSONObject();
    JSONArray slides = new JSONArray();
    JSONArray questions =new JSONArray();
    TextView textView;
    TextView indicator;
    ImageView imageView;
    String welcomeText;
    String title;
    int slideNumber = -1;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("filename");
        String filename = title.replace(" ", "_") + ".json";

        textView = findViewById(R.id.textView);
        indicator = findViewById(R.id.indicator);
        final TextView textViewTitle = (TextView) findViewById(R.id.textviewTitle);
        textViewTitle.setText(title.toUpperCase());
        welcomeText = "Hello there.  In this story, we are going to talk about " + title + ".  Let's begin.";
        textView.setText(welcomeText);
        imageView = findViewById(R.id.imageView2);
        try {
            getStartingImage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Instantiate the RequestQueue.
        Button prev = (Button) findViewById(R.id.button);
        Button next = (Button) findViewById(R.id.button2);
        String githuburl = "https://gesab001.github.io/assets/story/articles/"+filename;
        String localurl = "http://192.168.1.70/assets/story/articles/"+filename;



        try{
            getArticle(filename, githuburl, localurl);

        }catch (Exception err){
            textView.setText("not connected to the internet");
        }

        prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (slideNumber>-2){
                    slideNumber = slideNumber - 1;
                    try {
                        if(slideNumber==-1){
                            getStartingImage();
                            textViewTitle.setVisibility(View.VISIBLE);
                            textView.setText(welcomeText);

                        }else{
                            getImage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



            }
        });

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                textViewTitle.setVisibility(View.GONE);
                if (slideNumber<10){
                    slideNumber = slideNumber + 1;
                    try {
                        if(slideNumber==10){
                            getQuizImage();
                            textView.setText("Its quiz time! Click on the image above to start the quiz.");
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Context context = v.getContext();
                                    Intent intent = new Intent(context, QuizFullscreenActivity2.class);

                                    intent.putExtra("questions", getQuestions());
                                    context.startActivity(intent);
                                }
                            });
                        }else{
                            getImage();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

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

    public void getArticle(String filename, String githuburl, String homeurl){
        MyData2 myData2 = new MyData2(this, filename, githuburl, homeurl);
        myData2.getArticleFromGithubStorage();
        myData2.getArticleFromLocalStorage(filename);
        JSONObject jsonObject = myData2.getJsonObject();
        Log.i("localstoragetest: ", jsonObject.toString());
        try {
            loadArticle(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            loadArticle(response);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        textView.setText(error.toString());
//
//                    }
//                });
//
//// Access the RequestQueue through your singleton class.
//        queue.add(jsonObjectRequest);
    }

    public void loadArticle(JSONObject response) throws JSONException {
        article = response;
        loadSlides();
//        getImage();

    }

    public void loadSlides() {
        try {
            slides = (JSONArray) article.get("slides");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getImage() throws JSONException {
        JSONObject slide = (JSONObject) slides.get(slideNumber);
        final String githuburl = (String) slide.get("image");
        String caption = (String) slide.get("text");
        indicator.setText((slideNumber +1)+ "/10");
        textView.setText(caption);


        //Loading image using Picasso
//        Picasso.get().load(imgurl).error(R.drawable.ic_portable_wifi_off_black_24dp).into(imageView);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                String folder = title.toLowerCase().replace(" ", "");
                String filename = githuburl.substring(githuburl.lastIndexOf("/")+1);
                String homeurl = "http://192.168.1.70/assets/public/images/"+folder+"/"+filename;
                Log.i("imageloadfaile", "getting slide image from home server");

                getImagefromHomeServer(homeurl);
            }
        });
        Log.i("getImage", "getting slide image from github");
        builder.build().load(githuburl).into(imageView);
    }

    public void getStartingImage() throws JSONException {
        String githuburl = "https://gesab001.github.io/assets/images/sunset.jpg";
        final String homeurl = "http://192.168.1.70/assets/public/images/sunset.jpg";

        //Loading image using Picasso
//        Picasso.get().load(githuburl).error(R.drawable.ic_portable_wifi_off_black_24dp).into(imageView);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                Log.i("imageloadfaile", "getting slide image from home server");

                getImagefromHomeServer(homeurl);
            }
        });
        Log.i("getImage", "getting slide image from github");

        builder.build().load(githuburl).into(imageView);
    }

    public void getImagefromHomeServer(String url){
//        Picasso.get().load(url).error(R.drawable.ic_portable_wifi_off_black_24dp).into(imageView);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                textView.setText("please connect to the internet");
                imageView.setImageResource(R.drawable.ic_portable_wifi_off_black_24dp);
                Log.i("imageloadfaile", "cannot load image, connect to the internet");

            }
        });
        builder.build().load(url).into(imageView);

    }
    public void getQuizImage(){
        String githuburl = "https://gesab001.github.io/assets/images/quiztime.jpg";


        Picasso.Builder builder = new Picasso.Builder(this);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                String homeurl = "http://192.168.1.70/assets/public/images/quiztime.jpg";
                Log.i("imageloadfaile", "getting slide image from home server");

                getImagefromHomeServer(homeurl);
            }
        });
        Log.i("getImage", "getting slide image from github");

        builder.build().load(githuburl).into(imageView);
    }

    public String getQuestions(){
        try {
            questions = (JSONArray) article.get("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questions.toString();
    }

    public void getPrevSlide(){

    }

    public void getNextSlide(){

    }
}
