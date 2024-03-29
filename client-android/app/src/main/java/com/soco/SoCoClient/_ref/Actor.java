package com.soco.SoCoClient._ref;

import android.content.Context;

@Deprecated
public class Actor
{
    String name;

    String picName;

    public Actor(String name, String picName)
    {
        this.name = name;
        this.picName = picName;
    }

    public int getImageResourceId( Context context )
    {
        try
        {
            return context.getResources().getIdentifier(this.picName, "drawable", context.getPackageName());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }
}