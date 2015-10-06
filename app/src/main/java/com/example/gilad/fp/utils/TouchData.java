package com.example.gilad.fp.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.method.Touch;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gilad on 8/30/2015.
 */
public class TouchData
{
    public long time;
    public int type;
    public int index;
    public String choice;

    public TouchData(long time, int type, int index, String choice)
    {
        this.time = time;
        this.type = type;
        this.index = index;
        this.choice = choice;
    }

    public static JSONArray toJSONArray(ArrayList<TouchData> list)
    {
        JSONArray retVal = new JSONArray();
        for (TouchData elem:list)
        {
            JSONObject toAdd = new JSONObject();
            try
            {
                toAdd.put("time", elem.time);
                toAdd.put("type", elem.type);
                toAdd.put("index", elem.index);
                toAdd.put("choice", elem.choice);
            }
            catch (Exception e)
            {

            }
            retVal.put(toAdd);
        }
        return retVal;
    }
}