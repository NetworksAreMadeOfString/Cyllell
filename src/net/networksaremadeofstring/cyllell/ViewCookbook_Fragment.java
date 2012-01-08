package net.networksaremadeofstring.cyllell;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewCookbook_Fragment extends Fragment
{
	private String URI = "";
	
	public ViewCookbook_Fragment(String _URI)
	{
		this.URI = _URI;
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.cookbook_view_details, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	((TextView) getView().findViewById(R.id.textView1)).setText(this.URI);
    }
}
