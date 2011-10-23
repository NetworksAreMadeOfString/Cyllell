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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class ViewSettings extends Activity
{
	private SharedPreferences settings = null;
	 /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.settings_main);
       
       settings = getSharedPreferences("Cyllell", 0);
       ((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
       
       EditText chefURL = (EditText) findViewById(R.id.chefServerURL);
       EditText chefClientName = (EditText) findViewById(R.id.chefClientName);
       EditText chefPrivateKey = (EditText) findViewById(R.id.chefPrivateKey);
       
       if(settings.getString("URL", "--").equals("--") == false)
       	chefURL.setText(settings.getString("URL",""));
       
       if(settings.getString("ClientName", "--").equals("--") == false)
       	chefClientName.setText(settings.getString("ClientName",""));
       
       if(settings.getString("PrivateKey", "--").equals("--") == false)
       	chefPrivateKey.setText(settings.getString("PrivateKey",""));
       
       
       Button SaveButton = (Button) findViewById(R.id.saveSettingsButton);
       SaveButton.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) 
           {
           	EditText chefURL = (EditText) findViewById(R.id.chefServerURL);
               EditText chefClientName = (EditText) findViewById(R.id.chefClientName);
               EditText chefPrivateKey = (EditText) findViewById(R.id.chefPrivateKey);
               CheckBox chefSelfSigned = (CheckBox) findViewById(R.id.chefSelfSigned);
               
               SharedPreferences.Editor editor = settings.edit();
               //TODO remove trailing slash from a URL
               editor.putString("URL", chefURL.getText().toString());
               
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
