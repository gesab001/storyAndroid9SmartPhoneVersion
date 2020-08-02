package com.resistthedevil5947.myapplication;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyData2 {

    JSONArray jsonArray;
    JSONArray story = new JSONArray();
    int status = 0;
    JSONObject jsonObject;
    Context context;
    String filename;
    String githuburl;
    String homeurl;

    public MyData2(Context context, String filename, String githuburl, String homeurl){
        this.context = context;
        this.filename = filename;
        this.githuburl = githuburl;
        this.homeurl = homeurl;

    }
    //    static String[] letters = {"A", "B", "C", "D", "E", "F", "G","H", "I", "J", "K"};
//
//    static String[] names = {"Abraham", "Isaac", "Isaac", "Isaac", "Isaac", "Isaac", "Isaac", "Isaac", "Isaac", "Isaac", "Isaac"};
    public JSONArray getArray(){

        return jsonArray;

    }

    public JSONObject getJsonObject(){

        return jsonObject;

    }

    public JSONArray getFromLocalStorage(String filename){

        FileWriter fileWriter = new FileWriter(context){};
        String jsondata = fileWriter.readFromFile(filename);
        Log.i("localstorage", jsondata);
        try {
            jsonArray = new JSONArray(jsondata);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public JSONObject getArticleFromLocalStorage(String filename){

        FileWriter fileWriter = new FileWriter(context){};
        String jsondata = fileWriter.readFromFile(filename);
        Log.i("articlelocalstorage", jsondata);
        try {
            jsonObject = new JSONObject(jsondata);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void saveJsonFile(String data, String filename){
        FileWriter fileWriter = new FileWriter(context){};
        fileWriter.writeToFile(data, filename);
    }

    public void getArticleFromGithubStorage(){
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, githuburl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("articleremote:", response.toString());
                        saveArticleToLocalStroage(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error downloading:", error.toString() + "getArticleFromgithubstorage");
                        getArticleFromHomeServer();


                    }
                });

// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    public void getArticleFromHomeServer(){
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, githuburl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("articleremote:", response.toString());
                        saveArticleToLocalStroage(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error downloading:", error.toString());


                    }
                });

// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

    public void getFromGithubStorage(){
        Log.i("GITHUB STORAGE", "getting stories.json");

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, githuburl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        status = response.length();
                        if(status>0)
                            setJsonArray(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error", error.toString());
                        getFromHomeServer();

                    }
                });

// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);



    }

    public void getFromHomeServer(){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                (Request.Method.GET, homeurl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        status = response.length();
                        if(status>0)
                            setJsonArray(response);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("error", error.toString());

                    }
                });

// Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
        Log.i("status", Integer.toString(status));



    }

    public void setJsonArray(JSONArray response){
        jsonArray = response;
        Log.i("updating stories.json:", jsonArray.toString());
        saveJsonFile(jsonArray.toString(), filename);
    }

    public void saveArticleToLocalStroage(JSONObject response){
        Log.i("updating "+filename, response.toString());
        saveJsonFile(response.toString(), filename);
    }


}