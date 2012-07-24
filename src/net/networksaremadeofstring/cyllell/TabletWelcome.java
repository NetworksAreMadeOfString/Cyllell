package net.networksaremadeofstring.cyllell;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TabletWelcome extends SherlockFragment
{
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.welcome, container, false);
    }
    
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    	((WebView) getView().findViewById(R.id.cyllellWelcome)).loadUrl("http://blog.networksaremadeofstring.co.uk/projects/cyllell/");
    }
}
