package net.networksaremadeofstring.cyllell;

import java.lang.reflect.Method;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;

public class MainLanding extends SherlockFragmentActivity
{
	private SharedPreferences settings = null;
	FragmentManager fm;
	int requestCode; //Used for evaluating what the settings Activity returned (Should always be 1)
	Fragment viewNodes = null;
	Fragment viewCookbooks = null;
	Fragment viewRoles = null;
	Fragment viewEnvironments = null;
	Fragment viewSearch = null;
	Fragment viewSettings = null;
	private Boolean enableTabListeners = false;
	private Boolean fragmentSet = false;
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) 
	{
		savedInstanceState.putBoolean("fragmentSet", true);
	    super.onSaveInstanceState(savedInstanceState);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	
    	if(savedInstanceState != null && savedInstanceState.getBoolean("fragmentSet"))
    		fragmentSet = savedInstanceState.getBoolean("fragmentSet");
    	
        BugSenseHandler.setup(this, "84aff884");
        settings = getSharedPreferences("Cyllell", 0);
        
        //See if this is the first time the app has run (and subsequently have no Chef details)
        if(settings.getBoolean("FirstRun", true) == true)
        {
        	Toast.makeText(MainLanding.this, "Before continuing you will need to configure your settings...", Toast.LENGTH_LONG).show();
        	Intent LauncherIntent = new Intent(MainLanding.this, SettingsLanding.class);
        	MainLanding.this.startActivityForResult(LauncherIntent, requestCode);
        }
        else
        {
        	CheckDatabase();
        }
        
        fm = getSupportFragmentManager();
        
        if(isTabletDevice())
        {
        	setContentView(R.layout.main);
        	if(!fragmentSet)
        	{
	        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
	        	Fragment fragment = new TabletWelcome();
	            fragmentTransaction.replace(R.id.MainFragment, fragment);
	            fragmentTransaction.commit();
        	}
        }
        else
        {
        	setContentView(R.layout.main);
        	//((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        }
	     
		 ActionBar actionBar = getSupportActionBar();
		 actionBar.setTitle("");
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		 AddTabs();
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
    
    /*public boolean onCreateOptionsMenu(Menu menu) 
	{
		if(isTabletDevice())
		{
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.tablet_menu, menu);
			return true;
		}
		else
		{
			return false;
		}
	}*/
	
    private void AddTabs()
    {
    	ActionBar actionBar = getSupportActionBar();
    	
    	/*Tab HomeTab = actionBar.newTab().setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) 
			{
				Fragment welcome = new TabletWelcome();		
		        ft.replace(R.id.MainFragment, welcome);
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) 
			{
				Fragment welcome = new TabletWelcome();		
		        ft.replace(R.id.MainFragment, welcome);
			}});
    	HomeTab.setIcon(R.drawable.ab_home);
    	actionBar.addTab(HomeTab);*/
    	
    	Tab NodesTab = actionBar.newTab().setText("Nodes").setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) 
			{
				//I don't start on the first tab
				if(enableTabListeners)
				{
					if(viewNodes == null)
						viewNodes = new ViewNodes_Fragment();
					
		        	ft.replace(R.id.MainFragment, viewNodes);
		        	//ft.commit();
				}
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) 
			{
				viewNodes = new ViewNodes_Fragment();
	        	ft.replace(R.id.MainFragment, viewNodes);
			}});
    	NodesTab.setIcon(R.drawable.ab_nodes);
    	actionBar.addTab(NodesTab, false);

		Tab cookbooksTab = actionBar.newTab().setText("Cookbooks").setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				if(viewCookbooks == null)
					viewCookbooks = new ViewCookbooks_Fragment();
				
	        	ft.replace(R.id.MainFragment, viewCookbooks);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) 
			{
				//We should probably ask if they want to refresh?
				viewCookbooks = new ViewCookbooks_Fragment();
	        	ft.replace(R.id.MainFragment, viewCookbooks);
				
			}});
		
    	cookbooksTab.setIcon(R.drawable.ab_cookbooks);
    	actionBar.addTab(cookbooksTab,false);

    	Tab rolesTab = actionBar.newTab().setText("Roles").setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) 
			{
				if(viewRoles == null)
					viewRoles = new ViewRoles_Fragment();
				
	        	ft.replace(R.id.MainFragment, viewRoles);
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) 
			{
				viewRoles = new ViewRoles_Fragment();
	        	ft.replace(R.id.MainFragment, viewRoles);	
			}});
    	rolesTab.setIcon(R.drawable.ab_roles);
    	actionBar.addTab(rolesTab,false);

    	Tab envTab = actionBar.newTab().setText("Environments").setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				if(viewEnvironments == null)
					viewEnvironments = new ViewEnvironments_Fragment();
				
	        	ft.replace(R.id.MainFragment, viewEnvironments);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				viewEnvironments = new ViewEnvironments_Fragment();
	        	ft.replace(R.id.MainFragment, viewEnvironments);
				
			}});
    	envTab.setIcon(R.drawable.ab_environments);
    	actionBar.addTab(envTab,false);
    	
    	Tab searchTab = actionBar.newTab().setText("Search").setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				if(viewSearch == null)
					viewSearch = new Search_Fragment();
				
	        	ft.replace(R.id.MainFragment, viewSearch);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) 
			{
				viewSearch = new Search_Fragment();
	        	ft.replace(R.id.MainFragment, viewSearch);
			}});
    	searchTab.setIcon(R.drawable.ab_search);
    	actionBar.addTab(searchTab,false);
    }
    
    
   
    private void CheckDatabase()
    {
    	if(settings.getBoolean("DatabaseCreated", false) == true)
    	{
    		/*Intent MainLandingIntent = new Intent(launcher.this, MainLanding.class);
    		launcher.this.startActivity(MainLandingIntent);
    		finish();*/
    	}
    	else
    	{
    		Intent DatabaseIntent = new Intent(MainLanding.this, CreateDatabase.class);
    		MainLanding.this.startActivity(DatabaseIntent);
    		//finish();
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	//Check what the result was from the Settings Activity
    	//In theory the Settings activity should perform validation and only finish() if the settings pass validation
    	if(resultCode == 1)
    	{
    		SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("FirstRun", false);
        	editor.commit();
        	
    		//Toast.makeText(launcher.this, "Settings validated!\r\nLaunching Cyllell...", Toast.LENGTH_SHORT).show();
    		
    		/*Intent MainIntent = new Intent(launcher.this, Main.class);
    		launcher.this.startActivity(MainIntent);
    		finish();*/
        	CheckDatabase();
    	}
    	else if(resultCode == 2)
    	{
    		Toast.makeText(MainLanding.this, "Cyllell cannot start without configured settings.\n\nExiting....", Toast.LENGTH_LONG).show();
    		finish();
    	}
    	else //There is the potential for an infinite loop of unhappiness here but I doubt it'll happen
    	{
    		Toast.makeText(MainLanding.this, "Settings did not validate, returning to the settings screen.", Toast.LENGTH_LONG).show();
    		Intent LauncherIntent = new Intent(MainLanding.this, SettingsLanding.class);
    		MainLanding.this.startActivityForResult(LauncherIntent, requestCode);
    	}
    }
    
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_settings:
			{
				Intent ViewSettingsIntent = new Intent(MainLanding.this, ViewSettings.class);
	             MainLanding.this.startActivity(ViewSettingsIntent);
				return true;
			}
		}
		return false;
	}
	
	public void EnableTabs(Boolean enable)
	{
		enableTabListeners = enable;
	}
	
	private void EnableImageButtons()
	{
		/*((ImageView)findViewById(R.id.NodeImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	((ActionBar) getSupportActionBar()).setTitle("View Nodes");
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
    	            
    	            //((ActionBar) getSupportActionBar()).setTitle("View Nodes");
                }
                else
                {
                	Intent GenericIntent = new Intent(MainLanding.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewnodes");
                	MainLanding.this.startActivity(GenericIntent);
                }
            }
        });
        
        ((ImageView)findViewById(R.id.CookbookImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	((ActionBar) getSupportActionBar()).setTitle("View Cookbooks");
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
    	            
    	            //((ActionBar) getActionBar()).setTitle("View Cookbooks");
                }
                else
                {
                	Intent GenericIntent = new Intent(MainLanding.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewcookbooks");
                	MainLanding.this.startActivity(GenericIntent);
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
    	            
    	            ((ActionBar) getSupportActionBar()).setTitle("View Roles");
                }
                else
                {
                	Intent GenericIntent = new Intent(MainLanding.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewroles");
                	MainLanding.this.startActivity(GenericIntent);
                }
            }
        });
        
        ((ImageView)findViewById(R.id.EnvironmentImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	if(isTabletDevice())
                {
            		((ImageView) findViewById(R.id.NodesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.CookbookTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.RolesTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		((ImageView) findViewById(R.id.EnvironmentTab)).setImageResource(R.drawable.tablet_tab);
            		((ImageView) findViewById(R.id.SearchTab)).setImageResource(R.drawable.tablet_tab_inactive);
            		
    	        	FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	        	Fragment fragment = new ViewEnvironments_Fragment();
    	            fragmentTransaction.replace(R.id.MainFragment, fragment);
    	            fragmentTransaction.commit();
    	            
    	            ((ActionBar) getSupportActionBar()).setTitle("View Environments");
                }
                else
                {
                	Intent GenericIntent = new Intent(MainLanding.this, Generic_Container.class);
                	GenericIntent.putExtra("fragment", "viewenvironments");
                	MainLanding.this.startActivity(GenericIntent);
                }
            }
        });
        
        ((ImageView)findViewById(R.id.SettingsImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
	             Intent ViewSettingsIntent = new Intent(MainLanding.this, ViewSettings.class);
	             MainLanding.this.startActivity(ViewSettingsIntent);
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
    	            
    	            ((ActionBar) getSupportActionBar()).setTitle("Chef Search");
                }
                else
                {
                	Intent SearchIntent = new Intent(MainLanding.this, Search.class);
   	             	MainLanding.this.startActivity(SearchIntent);
                }
            }
        });*/
	}

}
