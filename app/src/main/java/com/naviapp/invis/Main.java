package com.naviapp.invis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.naviapp.invis.app.Place;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;
    GoogleApiClient mGoogleApiClient;
    String url;
    TextView mainText;
    JSONObject jsonBody=new JSONObject();
    JsonObjectRequest jsonObject;
    RequestQueue requestQueue;
    ArrayList<Place> places ;
    ListView listViewMain;
    CustomAdaptor customAdaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        sharedPreferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        listViewMain = (ListView)findViewById(R.id.lvMainPlace);
        url = getResources().getString(R.string.server_address)+"/get_place.php";
        places = new ArrayList<Place>();
       // placesAdap  = new ArrayAdapter<Place>(this,android.R.layout.simple_list_item_1,places);
       // mainText.setText("Hello");
     //   url = "localhost/invis/dump.php";
        //get JSON
        requestQueue = Volley.newRequestQueue(getApplicationContext());
         jsonObject = new JsonObjectRequest(Request.Method.POST, url,jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jsonArray= new JSONArray();
                String xx="";

                try {
                    jsonArray=response.getJSONArray("place");
                    int i=0;
                    while(true){
                        JSONObject placeObject = jsonArray.getJSONObject(i);
                        if(placeObject.toString().equals("")){
                            break;
                        }else{
                            i++;
                            Place place = new Place(
                                    placeObject.getString("placeID"),
                                    placeObject.getString("name"),
                                    placeObject.getString("photoFile"),
                                    placeObject.getDouble("lattitude"),
                                    placeObject.getDouble("longtitude"),
                                    placeObject.getDouble("dist")
                            );
                            xx+=place.toString();
                            places.add(place);

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


      //  listViewMain.setAdapter(placesAdap);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(Plus.API)
                                .addScope(Plus.SCOPE_PLUS_LOGIN)
                                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView emailText = (TextView) header.findViewById(R.id.textView);
        emailText.setText(sharedPreferences.getString("email",""));
        TextView nameText = (TextView) header.findViewById(R.id.nameTextView);
        nameText.setText(sharedPreferences.getString("name","anonymous"));
        ImageView photo = (ImageView)header.findViewById(R.id.imageView);
        String photo_url_str=sharedPreferences.getString("photo","");
        Log.v("Photo",photo_url_str);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Picasso.with(this.getApplicationContext()).load(photo_url_str).into(photo);

        customAdaptor = new CustomAdaptor(getApplicationContext(),places);
        listViewMain.setAdapter(customAdaptor);
        listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place itemAtPosition = (Place) parent.getItemAtPosition(position);
                Intent i = new Intent(Main.this,MainPlace.class);
                i.putExtra("place",itemAtPosition);
                startActivity(i);
            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.btMenuProfile) {
            // Handle the camera action
        } else if (id == R.id.btMenuSetting) {

        } else if (id == R.id.btMenuHelp) {

        } else if (id == R.id.btMenuLogOut) {
            LoginManager mLoginManager = LoginManager.getInstance();
            mLoginManager.logOut();
            editor.putBoolean("isLogin",false);
            editor.commit();
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
               Log.v("Google Logout","Success");
                // mGoogleApiClient.connect();  //may not be needed
            }
            Intent i = new Intent(Main.this,MainActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public Drawable getResource(String url) throws MalformedURLException, IOException
    {
        return Drawable.createFromStream((InputStream)new URL(url).getContent(), "src");
    }

}
