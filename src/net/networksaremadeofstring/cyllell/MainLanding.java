package net.networksaremadeofstring.cyllell;

import java.lang.reflect.Method;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
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
	Boolean fragmentSet = false;
	ActionMode mActionMode;
	public CyllellCache cacheDB;
	
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
        setContentView(R.layout.main);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
    	Fragment fragment = new TabletWelcome();
        fragmentTransaction.replace(R.id.MainFragment, fragment);
        fragmentTransaction.commit();
        ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("");
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		AddTabs();
		 
        if(!isTabletDevice())
        {
        	actionBar.setTitle("Cyllell - Knife for Android");
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
  
    
    private void AddTabs()
    {
    	ActionBar actionBar = getSupportActionBar();
    	
    	Tab NodesTab = actionBar.newTab().setText(R.string.TabsNodes).setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) 
			{
				if(viewNodes == null)
					viewNodes = new ViewNodes_Fragment();
				
				viewNodes.setHasOptionsMenu(true);
	        	ft.replace(R.id.MainFragment, viewNodes);
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) 
			{
				viewNodes = new ViewNodes_Fragment();
				viewNodes.setHasOptionsMenu(true);
	        	ft.replace(R.id.MainFragment, viewNodes);
			}});
    	NodesTab.setIcon(R.drawable.ic_action_nodes);
    	actionBar.addTab(NodesTab, false);

		Tab cookbooksTab = actionBar.newTab().setText(R.string.TabsCookbooks).setTabListener(new TabListener(){

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
		
    	cookbooksTab.setIcon(R.drawable.ic_action_cookbook);
    	actionBar.addTab(cookbooksTab,false);

    	Tab rolesTab = actionBar.newTab().setText(R.string.TabsRoles).setTabListener(new TabListener(){

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) 
			{
				if(viewRoles == null)
				{
					viewRoles = new ViewRoles_Fragment();
				}
				
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
    	rolesTab.setIcon(R.drawable.ic_action_roles);
    	actionBar.addTab(rolesTab,false);

    	Tab envTab = actionBar.newTab().setText(R.string.TabsEnv).setTabListener(new TabListener(){

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
    	envTab.setIcon(R.drawable.ic_action_environments);
    	actionBar.addTab(envTab,false);
    	
    	Tab searchTab = actionBar.newTab().setText(R.string.TabsSearch).setTabListener(new TabListener(){

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
    	searchTab.setIcon(R.drawable.ic_action_search);
    	actionBar.addTab(searchTab,false);
    }
    
    
   
    private void CheckDatabase()
    {
    	if(settings.getBoolean("DatabaseCreated", false) == true)
    	{
    		/*Intent MainLandingIntent = new Intent(launcher.this, MainLanding.class);
    		launcher.this.startActivity(MainLandingIntent);
    		finish();*/
    		cacheDB = new CyllellCache(MainLanding.this.getApplicationContext());
    		try
    		{
	        	Cuts Cut = new Cuts(MainLanding.this.getApplicationContext());
	    		cacheDB.RefreshCache(Cut);
    		}
    		catch(Exception e)
    		{
    			BugSenseHandler.log("CheckDataBase", e);
    		}
    	}
    	else
    	{
    		Intent DatabaseIntent = new Intent(MainLanding.this, CreateDatabase.class);
    		MainLanding.this.startActivity(DatabaseIntent);
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
    
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
    	MenuInflater inflater = getSupportMenuInflater();
    	inflater.inflate(R.menu.tablet_menu, menu);
    	return true;
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
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		cacheDB.close();
	}
}
