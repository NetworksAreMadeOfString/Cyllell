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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsLanding extends Activity 
{
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingslanding);
        
        Button SaveButton = (Button) findViewById(R.id.saveSettingsButton);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
            	//Eventually I'll actually do some sanity testing on this URL but since 
            	//I'm doing everything statically at the moment it can wait
            	
            	//TODO Actually do some verification of the settings the user has passed in
            	
            	Intent in = new Intent();
                setResult(1,in);
                finish();
            }
        });
    }
}
