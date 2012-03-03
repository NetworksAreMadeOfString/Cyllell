package net.networksaremadeofstring.cyllell;

import java.lang.reflect.Method;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends FragmentActivity
{
	private SharedPreferences settings = null;
	FragmentManager fm;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
        
        fm = getSupportFragmentManager();
        
        if(isTabletDevice())
        {
        	setContentView(R.layout.main);
        	
        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
        	Fragment fragment = new TabletWelcome();
            fragmentTransaction.replace(R.id.MainFragment, fragment);
            fragmentTransaction.commit();
            
        	//Sort out the action bar
            ActionBar actionBar = getActionBar();
            if(actionBar != null)
            {
            	actionBar.setDisplayUseLogoEnabled(true);
            	//actionBar.setDisplayShowTitleEnabled(false);
            	actionBar.setTitle("Welcome to Cyllell");
            }
            else
            {
            	Log.e("ActionBar","Still fucking broken");
            }
        }
        else
        {
        	requestWindowFeature(Window.FEATURE_NO_TITLE);
        	setContentView(R.layout.main);
        	((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        	
        }
        
        //Populate the settings so we can get out some common info
        settings = getSharedPreferences("Cyllell", 0);
        
        ((ImageView)findViewById(R.id.NodeImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	if(isTabletDevice())
                {
            		((ImageView) findViewById(R.id.NodesTab)).setImageResource(R.drawable.tablet_tab);
            		((ImageView) findViewById(R.id.CookbookTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.RolesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.EnvironmentTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.SearchTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		
    	        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	        	Fragment fragment = new ViewNodes_Fragment();
    	            fragmentTransaction.replace(R.id.MainFragment, fragment);
    	            fragmentTransaction.commit();
    	            
    	            ((ActionBar) getActionBar()).setTitle("View Nodes");
                }
                else
                {
                	Intent GenericIntent = new Intent(Main.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewnodes");
                	Main.this.startActivity(GenericIntent);
                }
            }
        });
        
        ((ImageView)findViewById(R.id.CookbookImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	if(isTabletDevice())
                {
            		((ImageView) findViewById(R.id.NodesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.CookbookTab)).setImageResource(R.drawable.tablet_tab);
            		((ImageView) findViewById(R.id.RolesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.EnvironmentTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.SearchTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		
    	        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	        	Fragment fragment = new ViewCookbooks_Fragment();
    	            fragmentTransaction.replace(R.id.MainFragment, fragment);
    	            fragmentTransaction.commit();
    	            
    	            ((ActionBar) getActionBar()).setTitle("View Cookbooks");
                }
                else
                {
                	Intent GenericIntent = new Intent(Main.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewcookbooks");
                	Main.this.startActivity(GenericIntent);
                }
            }
        });
        
        ((ImageView)findViewById(R.id.RoleImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	if(isTabletDevice())
                {
            		((ImageView) findViewById(R.id.NodesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.CookbookTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.RolesTab)).setImageResource(R.drawable.tablet_tab);
            		((ImageView) findViewById(R.id.EnvironmentTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.SearchTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		
    	        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	        	Fragment fragment = new ViewRoles_Fragment();
    	            fragmentTransaction.replace(R.id.MainFragment, fragment);
    	            fragmentTransaction.commit();
    	            
    	            ((ActionBar) getActionBar()).setTitle("View Cookbooks");
                }
                else
                {
                	Intent GenericIntent = new Intent(Main.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewroles");
                	Main.this.startActivity(GenericIntent);
                }
            }
        });
        
        ((ImageView)findViewById(R.id.SettingsImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
	             Intent ViewSettingsIntent = new Intent(Main.this, ViewSettings.class);
	             Main.this.startActivity(ViewSettingsIntent);
            }
        });
        
        ((ImageView)findViewById(R.id.SearchImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	if(isTabletDevice())
                {
            		((ImageView) findViewById(R.id.NodesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.CookbookTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.RolesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.EnvironmentTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.SearchTab)).setImageResource(R.drawable.tablet_tab);
            		
    	        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	        	Fragment fragment = new Search_Fragment();
    	            fragmentTransaction.replace(R.id.MainFragment, fragment);
    	            fragmentTransaction.commit();
    	            
    	            ((ActionBar) getActionBar()).setTitle("Chef Search");
                }
                else
                {
                	Intent SearchIntent = new Intent(Main.this, Search.class);
   	             	Main.this.startActivity(SearchIntent);
                }
            }
        });
        
        //See if this is the first time the app has run so we can give the user an intro
        if(settings.getBoolean("FirstRun", true) == true)
        {
        	Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
        }
    }
    
    private boolean isTabletDevice() 
    {
        if (android.os.Build.VERSION.SDK_INT >= 11) // honeycomb
        { 
            // test screen size, use reflection because isLayoutSizeAtLeast is only available since 11
            Configuration con = getResources().getConfiguration();
            try 
            {
                Method mIsLayoutSizeAtLeast = con.getClass().getMethod("isLayoutSizeAtLeast", int.class);
                Boolean r = (Boolean) mIsLayoutSizeAtLeast.invoke(con, 0x00000004); // Configuration.SCREENLAYOUT_SIZE_XLARGE
                return r;
            } 
            catch (Exception x) 
            {
                x.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
