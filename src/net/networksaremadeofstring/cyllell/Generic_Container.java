package net.networksaremadeofstring.cyllell;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.TextView;

public class Generic_Container extends FragmentActivity
{
	FragmentManager fm;
	Fragment fragment;
	String URI = null;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		//Log.e("onSaveInstanceState","Called from Generic Container");
		savedInstanceState.putBoolean("fragmentSet", true);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_container);
        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragment = null;
        if(savedInstanceState != null && savedInstanceState.getBoolean("fragmentSet"))
        {
        	//Log.e("onCreate","We've already got a fragment");
        }
        else
        {
        	//Log.e("onCreate","No fragments or savedInstanceState so lets eval what we are doing");
        	
	        if(getIntent().getStringExtra("fragment").equals("viewnodes"))
	        {
	        	fragment = new ViewNodes_Fragment();
	        }
	        else if(getIntent().getStringExtra("fragment").equals("viewcookbooks"))
	        {
	        	fragment = new ViewCookbooks_Fragment();
	        }
	        else if(getIntent().getStringExtra("fragment").equals("viewcookbook"))
	        {
	        	if(URI == null)
	        		URI = getIntent().getStringExtra("cookbookURI");
	        	
	        	fragment = new ViewCookbook_Fragment(URI);
	        }
	        else if(getIntent().getStringExtra("fragment").equals("viewroles"))
	        {
	        	fragment = new ViewRoles_Fragment();
	        }
	        else if(getIntent().getStringExtra("fragment").equals("viewenvironments"))
	        {
	        	fragment = new ViewEnvironments_Fragment();
	        }
	        else if(getIntent().getStringExtra("fragment").equals("viewenvironment"))
	        {
	        	if(URI == null)
	        		URI = getIntent().getStringExtra("envURI");
	        	
	        	fragment = new ViewEnvironment_Fragment(URI);
	        }
	        else if(getIntent().getStringExtra("fragment").equals("viewrole"))
	        {
	        	if(URI == null)
	        		URI = getIntent().getStringExtra("roleURI");
	        	
	        	fragment = new ViewRole_Fragment(URI);
	        }
	        else
	        {
	        	//There should probably be some generic holder
	        	fragment = new ViewNodes_Fragment();
	        }
	        fragment.setRetainInstance(true);
	        
	        fragmentTransaction.replace(R.id.generic_fragment, fragment);
	        fragmentTransaction.commit();
        }
        
        if(((TextView) findViewById(R.id.TitleBarText)) != null)
        {
        	((TextView) findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        }
    }
}
