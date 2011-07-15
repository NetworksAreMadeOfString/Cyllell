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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class NodesLanding extends Activity
{
	Cuts Cut = null;
	String[] Nodes = null;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nodeslanding);
        
        Cut = new Cuts(this);
        final ProgressDialog dialog = ProgressDialog.show(this, "Contacting Chef", "Please wait: loading data....", true);
        
    	final Handler handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{
    			Toast.makeText(NodesLanding.this, "If this code was any good I'd have written a ListView and stuff like that", Toast.LENGTH_LONG).show();
    			
    			//Once we've checked the data is good to use start processing it
    			if(true == true)//TODO sanity check
    			{
    				//TODO ListView population
    				
    				//Close the Progress dialog
        			dialog.dismiss();
    			}
    			else
    			{
    				//Close the Progress dialog
    				dialog.dismiss();
    				
    				AlertDialog alertDialog = new AlertDialog.Builder(NodesLanding.this).create();
    				alertDialog.setTitle("API Error");
    				alertDialog.setMessage("There was an error communicating with the API.");
    				/*alertDialog.setButton2("Exit", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int which) {
    					   NodesLanding.this.finish();
    				   }
    				});*/
    				alertDialog.setIcon(R.drawable.icon);
    				alertDialog.show();
    			}
    			
    			
    		}
    	};
    	
    	Thread dataPreload = new Thread() 
    	{  
    		public void run() 
    		{
    			Nodes = Cut.GetNodes();
    			handler.sendEmptyMessage(0);
    			return;
    		}
    	};
    	
    	dataPreload.start();
    }
}
