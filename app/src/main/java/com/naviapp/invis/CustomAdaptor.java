package com.naviapp.invis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.naviapp.invis.app.Place;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Korranat on 4/25/2016.
 */
public class CustomAdaptor extends BaseAdapter {
    private ArrayList<Place> places;
    private Context context;

    public Context getContext() {
        return context;
    }

    public ArrayList<Place> getPlaces(int position) {
        return places;
    }

    public CustomAdaptor(Context context, ArrayList<Place>places){
        this.places = places;

        this.context= context;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("POSITION", String.valueOf(position));
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null) convertView = layoutInflater.inflate(R.layout.list_row_places,parent,false);

        TextView placeName = (TextView)convertView.findViewById(R.id.tvListRowPlaceName);
        TextView placeDistance = (TextView) convertView.findViewById(R.id.tvListRowPlaceDistance);
        final RelativeLayout rowLayout = (RelativeLayout) convertView.findViewById(R.id.loListRowMain);

        placeName.setText(places.get(position).getName());
        double dis =  places.get(position).getDist();

        placeDistance.setText(""+String.format("%.2f",dis)+" km");
        String url =places.get(position).getPhotoFile();
        if(url!="0");
        Picasso.with(context).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    rowLayout.setBackground(new BitmapDrawable(context.getResources(),bitmap));
                    rowLayout.getBackground().setAlpha(150);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        return convertView;
    }
}
