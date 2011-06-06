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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/* A knife is used for cutting so that's what this little class does.
 * I'm tempted to make Cuts() used for GETs and Slash() used for POSTS but
 * we'll just have to wait and see
 */
public class Cuts 
{
	private SharedPreferences settings = null;
	private String ChefURL = "https://api.opscode.com/organizations/namos";//This should probably be null or something
	private String UserID = "Android"; //The name of the API client
	
	public Cuts(Context thisContext)
	{
		settings = thisContext.getSharedPreferences("Cyllell", 0);
		
		//TODO ensure that there is no trailing slash
		this.ChefURL = settings.getString("ChefURL", ChefURL);
		
		this.UserID = settings.getString("UserID", UserID);
	}
	
	public String[] GetNodes()
	{
		Authentication ChefAuth = new Authentication(ChefURL + "/nodes");//Lack of variables makes me sad
		Log.e("API", "Resulting Chef URL is: " + ChefURL);
		URLConnection conn = ChefAuth.AddHeaders(UserID);
		
		InputStream is = null;
		int current = 0; 
		ByteArrayBuffer baf = new ByteArrayBuffer(50);  
		
		try 
		{
			conn = ChefAuth.chefURL.openConnection();
		} 
		catch (IOException e) 
		{
			return null;
		}  
		
		
		try 
		{
			is = conn.getInputStream();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}  
		BufferedInputStream bis = new BufferedInputStream(is);  
		
		 
		try 
		{
			while((current = bis.read()) != -1)
			{  
				baf.append((byte)current);  
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}  
		
		/* Convert the Bytes read to a String. */  
		String ChefJSONString = new String(baf.toByteArray());  
		
		Log.e("API", "assigning the string to a JSON object failed: " + ChefJSONString);
		
		try 
		{
			JSONObject ChefJSON = new JSONObject(ChefJSONString);
		} 
		catch (JSONException e) 
		{
			//Log.e("API", "assigning the string to a JSON object failed: " + ChefJSONString);
			return null;
		}
		
		return null;
	}
}
