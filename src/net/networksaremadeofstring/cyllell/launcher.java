/*
* Copyright (C) 2011 - Gareth Llewellyn
*
* This file is part of Cyllell - http://blog.NetworksAreMadeOfString.co.uk/cyllell/
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>
*/
package net.networksaremadeofstring.cyllell;

import com.bugsense.trace.BugSenseHandler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
public class launcher extends Activity 
{
	private SharedPreferences settings = null;
    int requestCode; //Used for evaluating what the settings Activity returned (Should always be 1)
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        
        //Enable BugSense
        BugSenseHandler.setup(this, "84aff884");
        
        //Populate the settings so we can get out some common info
        settings = getSharedPreferences("Cyllell", 0);
        
        //See if this is the first time the app has run (and subsequently has no Chef details)
        if(settings.getBoolean("FirstRun", true) == true)
        {
        	Toast.makeText(launcher.this, "Before continuing you will need to configure your settings...", Toast.LENGTH_LONG).show();
        	Intent LauncherIntent = new Intent(launcher.this, SettingsLanding.class);
    		launcher.this.startActivityForResult(LauncherIntent, requestCode);
        }
        else
        {
        	CheckDatabase();
        }
    }
    
    private void CheckDatabase()
    {
    	if(settings.getBoolean("DatabaseCreated", false) == true)
    	{
    		Intent MainLandingIntent = new Intent(launcher.this, MainLanding.class);
    		launcher.this.startActivity(MainLandingIntent);
    		finish();
    	}
    	else
    	{
    		Intent DatabaseIntent = new Intent(launcher.this, CreateDatabase.class);
    		launcher.this.startActivity(DatabaseIntent);
    		finish();
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
    		Toast.makeText(launcher.this, "Cyllell cannot start without configured settings.\n\nExiting....", Toast.LENGTH_LONG).show();
    		finish();
    	}
    	else //There is the potential for an infinite loop of unhappiness here but I doubt it'll happen
    	{
    		Toast.makeText(launcher.this, "Settings did not validate, returning to the settings screen.", Toast.LENGTH_LONG).show();
    		Intent LauncherIntent = new Intent(launcher.this, SettingsLanding.class);
    		launcher.this.startActivityForResult(LauncherIntent, requestCode);
    	}
    }
}