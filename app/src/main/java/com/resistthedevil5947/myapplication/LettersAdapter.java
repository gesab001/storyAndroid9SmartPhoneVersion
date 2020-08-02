package com.resistthedevil5947.myapplication;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class LettersAdapter extends RecyclerView.Adapter<LettersAdapter.MyViewHolder> {

    private ArrayList<DataModel2> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.textView = (TextView) itemView.findViewById(R.id.letter);
        }

        public void onClick(View view) {
            Log.i("test", textView.getText().toString());
            String filename = textView.getText().toString();
            if(filename.length()>2){
                Context context = view.getContext();
                Intent intent = new Intent(context, FullscreenActivity.class);
                intent.putExtra("filename", filename);
                context.startActivity(intent);
            }


        }
    }

    public LettersAdapter(ArrayList<DataModel2> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards2_layout, parent, false);

        view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textView = holder.textView;
        String groupletter = dataSet.get(listPosition).getLetter();
        String title = dataSet.get(listPosition).getTitle();
        if (groupletter!=null){
            textView.setText(groupletter);
        }
        if(title!=null){
            textView.setText(title);
            Log.i("titlerecycler", title);
        }

//        JSONArray jsonArray = new JSONArray();
//        jsonArray = dataSet.get(listPosition).getNames();
//        for (int x=0; x<jsonArray.length(); x++){
//            try {
//                String title = (String) jsonArray.get(x);
//                letter.append(title);
//                letter.append("\n");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}