package net.networksaremadeofstring.cyllell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabletWelcome extends Fragment
{
	/*
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        
        setContentView(R.layout.tablet_welcome);
        super.onCreate(savedInstanceState);
    }*/
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		Log.i("CDV", "View Created");
        return inflater.inflate(R.layout.tablet_welcome, container, false);
    }
}
