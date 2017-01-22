package com.geniusnine.android.geniusnineframework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendsList extends AppCompatActivity {


    JSONArray friendslist;
    private ListView listView;
    ArrayList<String> friends = new ArrayList <String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        String jsondata;
        jsondata = intent.getStringExtra("jsondata");

        try {
            friendslist = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++)
            {

                friends.add(friendslist.getJSONObject(l).getString("name"));

            }
        }
        catch (JSONException e)
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        ArrayAdapter adapter = new ArrayAdapter <String>(this, R.layout.activity_friends_list, R.id.textViewfriend, friends);

        listView.setAdapter(adapter);
    }
}
