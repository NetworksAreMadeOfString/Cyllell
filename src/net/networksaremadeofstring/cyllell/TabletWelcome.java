package net.networksaremadeofstring.cyllell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class TabletWelcome extends Fragment
{
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.tablet_welcome, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	((WebView) getView().findViewById(R.id.webView1)).loadUrl("http://blog.networksaremadeofstring.co.uk/projects/cyllell/");
    }
}
