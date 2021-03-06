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
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ViewSettings extends Activity
{
	private SharedPreferences settings = null;
	 /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
       super.onCreate(savedInstanceState);
       //setContentView(R.layout.settings_main);
       setContentView(R.layout.settings_initial_hosted);
       
       settings = getSharedPreferences("Cyllell", 0);
       ((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
       
       EditText chefURL = (EditText) findViewById(R.id.chefServerURL);
       EditText chefClientName = (EditText) findViewById(R.id.chefClientName);
       EditText chefPrivateKey = (EditText) findViewById(R.id.chefPrivateKey);
       RadioButton OpenSourceChef = (RadioButton) findViewById(R.id.OpenSourceChef);
       RadioButton HostedChef = (RadioButton) findViewById(R.id.HostedChefRadio);
       
       if(settings.getBoolean("OpenSourceChef", true) == true)
       {
       	OpenSourceChef.setChecked(true);
       	HostedChef.setChecked(false);
       	((CheckBox) findViewById(R.id.chefSelfSigned)).setVisibility(0);
       }
       else
       {
       	OpenSourceChef.setChecked(false);
       	HostedChef.setChecked(true);
       	((CheckBox) findViewById(R.id.chefSelfSigned)).setVisibility(8);
       }
       
       if(settings.getString("URL", "--").equals("--") == false)
       {
    	   if(settings.getBoolean("OpenSourceChef", true) == true)
           {
    		   chefURL.setText(settings.getString("URL",""));
           }
    	   else
    	   {
    		   chefURL.setText(settings.getString("Suffix",""));
    	   }
       }
       
       if(settings.getString("ClientName", "--").equals("--") == false)
       	chefClientName.setText(settings.getString("ClientName",""));
       
       if(settings.getString("PrivateKey", "--").equals("--") == false)
       	chefPrivateKey.setText(settings.getString("PrivateKey",""));
       
       ((RadioGroup) findViewById(R.id.chefTypeRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				Log.i("RadioGroup",Integer.toString(checkedId));
				if(checkedId == ((RadioButton) findViewById(R.id.HostedChefRadio)).getId())
				{
					((EditText) findViewById(R.id.chefServerURL)).setHint("/organizations/orgname");
					((EditText) findViewById(R.id.chefServerURL)).setText("");
					((CheckBox) findViewById(R.id.chefSelfSigned)).setVisibility(8);
				}
				else
				{
					((EditText) findViewById(R.id.chefServerURL)).setHint("https://chef.server.com");
					((CheckBox) findViewById(R.id.chefSelfSigned)).setVisibility(0);
					((EditText) findViewById(R.id.chefServerURL)).setText("");
				}

			}
		});
       
       Button SaveButton = (Button) findViewById(R.id.saveSettingsButton);
       SaveButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) 
           {
           	EditText chefURL = (EditText) findViewById(R.id.chefServerURL);
               EditText chefClientName = (EditText) findViewById(R.id.chefClientName);
               EditText chefPrivateKey = (EditText) findViewById(R.id.chefPrivateKey);
               CheckBox chefSelfSigned = (CheckBox) findViewById(R.id.chefSelfSigned);
               RadioButton OpenSourceChef = (RadioButton) findViewById(R.id.OpenSourceChef);
               RadioButton HostedChef = (RadioButton) findViewById(R.id.HostedChefRadio);
               
               SharedPreferences.Editor editor = settings.edit();
               String ChefURL = "";
               String PathSuffix = "";
               if(OpenSourceChef.isChecked())
               {
               	ChefURL = chefURL.getText().toString();
               	PathSuffix = "";
               }
               else
               {
               	ChefURL = "https://api.opscode.com";
               	if(chefURL.getText().toString().contains("/organizations/"))
               	{
               		PathSuffix = chefURL.getText().toString();
               	}
               	else
               	{
               		PathSuffix = "/organizations/" + chefURL.getText().toString();
               	}
               }
               
               if(ChefURL.endsWith("/"))
               	ChefURL = ChefURL.substring(0, ChefURL.length()-1);
               
               if(PathSuffix.endsWith("/"))
               	PathSuffix = PathSuffix.substring(0, PathSuffix.length()-1);

               editor.putString("URL", ChefURL);
               editor.putString("Suffix", PathSuffix);
               
               editor.putBoolean("OpenSourceChef", OpenSourceChef.isChecked());
               
               //Name doesn't need any validation really
               editor.putString("ClientName", chefClientName.getText().toString());
               
               //Strip crap out of private key
               String tmpPK = chefPrivateKey.getText().toString().replace("-----BEGIN RSA PRIVATE KEY-----", "");
               tmpPK = tmpPK.replace("-----END RSA PRIVATE KEY-----", "");
               tmpPK = tmpPK.replace("\n", "");
               //tmpPK = tmpPK.replaceAll(regularExpression, replacement); //REGEX one day!
               editor.putString("PrivateKey", tmpPK);
               

               //Save selfsigned settings
               editor.putBoolean("SelfSigned", chefSelfSigned.isChecked());
               
               
           	//TODO Actually do some verification of the settings the user has passed in
               editor.commit();
               
               finish();
           }
       });
       
       Button SettingsButton = (Button) findViewById(R.id.LoadTimeSettings);
       SettingsButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) 
           {
           	startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS),0);
           }
       });
   }
}
