package net.networksaremadeofstring.cyllell;

import android.content.Intent;
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_container);
        fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fragment = null;
        
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
        	fragment = new ViewCookbook_Fragment(getIntent().getStringExtra("cookbookURI"));
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
        	fragment = new ViewEnvironment_Fragment(getIntent().getStringExtra("roleURI"));
        }
        else if(getIntent().getStringExtra("fragment").equals("viewrole"))
        {
        	String roleURI = getIntent().getStringExtra("roleURI");
        	fragment = new ViewRole_Fragment(roleURI);
        }
        else
        {
        	//There should probably be some generic holder
        	fragment = new ViewNodes_Fragment();
        }
        
        fragmentTransaction.replace(R.id.generic_fragment, fragment);
        fragmentTransaction.commit();
        
        if(((TextView) findViewById(R.id.TitleBarText)) != null)
        {
        	((TextView) findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        }
    }
}
