package com.example.gilad.fp;

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
}