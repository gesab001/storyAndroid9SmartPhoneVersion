package com.resistthedevil5947.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static RecyclerView.Adapter adapter;
    private static RecyclerView.Adapter adapter2;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManager2;

    private static RecyclerView recyclerView2;
    private static RecyclerView recyclerView;

    private static ArrayList<DataModel> data;
    private static ArrayList<DataModel2> data2;

    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myOnClickListener = new MyOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        layoutManager2 = new LinearLayoutManager(this);
//        recyclerView2.setLayoutManager(layoutManager);
//        recyclerView2.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<DataModel>();
        data2 = new ArrayList<DataModel2>();
        String filename = "stories.json";
        String githuburl = "https://gesab001.github.io/assets/story/stories.json";
        String localurl = "http://192.168.1.70/assets/story/stories.json";

        MyData2 myData2 = new MyData2(this, filename, githuburl, localurl);
        myData2.getFromGithubStorage();
        JSONArray jsonArray = myData2.getFromLocalStorage(filename);
        Log.i("localstoragetest: ", jsonArray.toString());




        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = (JSONObject) jsonArray.get(i);
                String letter = jsonObject.getString("letter");
                JSONArray names = jsonObject.getJSONArray("names");
                int total = 0;
                DataModel2 item = new DataModel2(letter, total);
                data2.add(item);
                if (names.length()>0){
                    for (int x=0; x<names.length(); x++){
                        String title = (String) names.get(x);
                        Log.i("title", title);
                        DataModel2 itemtitle = new DataModel2(title);
                        data2.add(itemtitle);
                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        removedItems = new ArrayList<Integer>();

//        adapter = new CustomAdapter(data);
        Log.i("data2", data2.toString());
        adapter2 = new LettersAdapter(data2);

//        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter2);

    }


    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            removeItem(v);
        }

        private void removeItem(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.textViewName);
            String selectedName = (String) textViewName.getText();
            int selectedItemId = -1;
            for (int i = 0; i < MyData.nameArray.length; i++) {
                if (selectedName.equals(MyData.nameArray[i])) {
                    selectedItemId = MyData.id_[i];
                }
            }
            removedItems.add(selectedItemId);
            data.remove(selectedItemPosition);
            adapter.notifyItemRemoved(selectedItemPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_item) {
            //check if any items to add
            if (removedItems.size() != 0) {
                addRemovedItemToList();
            } else {
                Toast.makeText(this, "Nothing to add", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void addRemovedItemToList() {
        int addItemAtListPosition = 3;
        data.add(addItemAtListPosition, new DataModel(
                MyData.nameArray[removedItems.get(0)],
                MyData.versionArray[removedItems.get(0)],
                MyData.id_[removedItems.get(0)],
                MyData.drawableArray[removedItems.get(0)]
        ));
        adapter.notifyItemInserted(addItemAtListPosition);
        removedItems.remove(0);
    }
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager layoutManager;
//    private static RecyclerView.Adapter adapter;
//    private static ArrayList<DataModel> data;
//    static View.OnClickListener myOnClickListener;
//    private static ArrayList<Integer> removedItems;
//    TextView textView;
//    JSONArray story = new JSONArray();
//    JSONObject article = new JSONObject();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        String[] mobileArray = {"Android","IPhone","WindowsMobile","Blackberry",
//                "WebOS","Ubuntu","Windows7","Max OS X"};
//        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        recyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//
//        // specify an adapter (see also next example)
//        mAdapter = new MyAdapter(mobileArray);
//        recyclerView.setAdapter(mAdapter);
//
//
//        textView = (TextView) findViewById(R.id.textView);
//
//        // Array of strings...
//
//
//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                R.layout.activity_listview, mobileArray);
//
//        ListView listView = (ListView) findViewById(R.id.story_list);
//        listView.setAdapter(adapter);
//// ...
//
//// Instantiate the RequestQueue.
//
//        String url ="https://gesab001.github.io/assets/story/stories.json";
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//
//                    @Override
//                    public void onResponse(JSONArray response) {
//
//                            JSONArray story = (JSONArray) response;
//
//                        try {
//                            getStory(story);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
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


//    public void getStory(JSONArray response) throws JSONException {
//        story = response;
//        JSONObject titles = (JSONObject) story.get(0);
//        JSONArray names = (JSONArray) titles.get("names");
//        String url = "https://gesab001.github.io/assets/story/articles/"+names.get(0).toString().replace(" ", "_") + ".json";
//        textView.setText(url);
//
//        getArticle(url);
//
//    }
//
//    public void getArticle(String url){
//
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
//    }
//
//    public void loadArticle(JSONObject response) throws JSONException {
//        article = response;
//        JSONArray slides = (JSONArray) article.get("slides");
//        JSONObject slide = (JSONObject) slides.get(0);
//        String imgurl = (String) slide.get("image");
//        textView.setText(imgurl);
//        getImage(imgurl);
//
//
//
//    }
//
//    public void getImage(String imageUrl){
//        ImageView imageView = findViewById(R.id.imageView2);
//
//        //Loading image using Picasso
//        Picasso.get().load(imageUrl).into(imageView);
//    }


}
