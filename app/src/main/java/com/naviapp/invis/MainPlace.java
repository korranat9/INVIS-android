package com.naviapp.invis;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.naviapp.invis.app.Place;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class MainPlace extends AppCompatActivity implements View.OnClickListener {
    TextView placeText;
    TextView mainText;
    String url;
    ImageView imagePlace;
    AppCompatButton mapButton;
    Place p;
    RequestQueue requestQueue;
    JsonObjectRequest jsonObject;
    JSONObject jsonBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_place);
        url =getResources().getString(R.string.server_address)+"/get_text.php";
        placeText = (TextView)findViewById(R.id.tvMainPlace);
        mainText= (TextView)findViewById(R.id.tvMainPlaceTop);
        imagePlace = (ImageView)findViewById(R.id.ivMainPlace);
        final Intent[] i = {getIntent()};
        p = (Place) i[0].getSerializableExtra("place");
        mainText.setText(p.getName());
        mapButton  = (AppCompatButton)findViewById(R.id.btMainPlaceMap);
        mapButton.setOnClickListener(this);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Picasso.with(getApplicationContext()).load(p.getPhotoFile()).into(imagePlace);

        final String[] allText = {""};
        //get JSON
        try {
            jsonBody = new JSONObject("{'placeID':'" +p.getPlaceID()+ "','1stL':'e'}");
            Log.v("POST!!!!",jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonObject = new JsonObjectRequest(Request.Method.POST, url,jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray= new JSONArray();
                String xx="";
                Log.v("OnResponse","RECIEVE");
                try {
                    jsonArray=response.getJSONArray("text");
                    Log.v("GETJSON",jsonArray.toString());
                        JSONObject textObject = jsonArray.getJSONObject(0);
                        if(textObject.toString().equals("")){

                        }else{
                            xx=textObject.getString("textFile");
                            xx=getResources().getString(R.string.server_address)+"/res/textPlace/"+xx;

                            try {
                                URL textURL =new URL(xx);
                                BufferedReader in = new BufferedReader(new InputStreamReader(textURL.openStream()));
                                String str;

                                while ((str = in.readLine()) != null) {
                                    allText[0] +=str;
                                    Log.v("SERVERGETTEXT",allText[0]);
                                    // str is one line of text; readLine() strips the newline character(s)
                                }
                                placeText.setText(allText[0]);
                                in.close();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObject);

     //   placeText.setText(p.toString());
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.btMainPlaceMap:
                i = new Intent(MainPlace.this,Map.class);
                i.putExtra("main",p.getName());
                i.putExtra("lattitude",p.getLattitude());
                i.putExtra("longtitude",p.getLongtitude());
                startActivity(i);
                break;
        }
    }
}
