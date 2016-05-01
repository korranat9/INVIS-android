package com.naviapp.invis.app;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.Serializable;

/**
 * Created by Korranat on 4/24/2016.
 */
public class Place implements Serializable {
    private String placeID,name,photoFile;
    private double lattitude,longtitude,dist;
    public Place(){
        this.placeID="";
        this.name="";
        this.lattitude =0;
        this.longtitude = 0;
        this.photoFile = "";
        this.dist = 0;
    };
    public Place(String placeID,String name, String photoFile,double lattitude,double longtitude,double dist){
        this.placeID=placeID;
        this.name=name;
        this.lattitude =lattitude;
        this.longtitude = longtitude;
        this.photoFile = photoFile;
        this.dist = dist;

    }

    public String getPlaceID() {
        return placeID;
    }

    public String getName() {
        return name;
    }


    public double getLattitude() {
        return lattitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public double getDist() {
        return dist;
    }

    public String getPhotoFile() {
        return photoFile;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeID='" + placeID + '\'' +
                ", name='" + name + '\'' +
                ", photoFile='" + photoFile + '\'' +
                ", lattitude=" + lattitude +
                ", longtitude=" + longtitude +
                ", dist=" + dist +
                "}\n=========================\n";
    }


}
