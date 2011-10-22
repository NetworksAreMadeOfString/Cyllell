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
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.conn.ssl.SSLSocketFactory;

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
	private String ChefURL = "https://api.opscode.com";//This won't work but hey ho
	private String UserID = "Cyllell"; //The name of the API client
	private String PrivateKey = null;
	
	private ResponseHandler<String> responseHandler;
	private String JSONReturn = "";
	private Authentication ChefAuth = null;
	
	//These don't get used directly
	private DefaultHttpClient client;
	private SingleClientConnManager mgr;
	
	//This one is the HttpClient we do use;
	private DefaultHttpClient httpClient;
	
	public Cuts(Context thisContext) throws Exception
	{
		settings = thisContext.getSharedPreferences("Cyllell", 0);
		
		responseHandler = new BasicResponseHandler();
		
		if(settings.getString("URL", "--").equals("--") == false && settings.getString("ClientName", "--").equals("--") == false &&  settings.getString("PrivateKey", "--").equals("--") == false)
		{
			ChefAuth = new Authentication(settings.getString("URL", "--"),settings.getString("ClientName", "--"),settings.getString("PrivateKey", "--"));
		}
		else
		{
			Log.i("URL",settings.getString("URL", "--"));
			Log.i("ClientName",settings.getString("ClientName", "--"));
			Log.i("PrivateKey",settings.getString("PrivateKey", "--"));
			throw new Exception("Chef URL is not set");
		}
		
		//Create the httpclient that trusts everything!
		this.PrepareSSLHTTPClient();
	}
	
	private void PrepareSSLHTTPClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException
	{
		client = new DefaultHttpClient(); 
		
        SchemeRegistry registry = new SchemeRegistry();
        //HostnameVerifier hostnameVerifier;
        SocketFactory socketFactory = null;
        
        //Check whether people are self signing or not
        if(settings.getBoolean("SelfSigned", true))
        {
        	Log.i("SelfSigned","Allowing Self Signed Certificates");
        	//hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			socketFactory = TrustAllSSLSocketFactory.getDefault();
        }
        else
        {
        	Log.i("SelfSigned","Enforcing Certificate checks");
        	//hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
        	socketFactory = SSLSocketFactory.getSocketFactory();
        }
        
        //socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
        registry.register(new Scheme("https", socketFactory, 443));
        //HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        mgr = new SingleClientConnManager(client.getParams(), registry); 

        httpClient = new DefaultHttpClient(mgr, client.getParams());
	}
	
	public String[] GetNodes()
	{
		ChefAuth.SetHeaders("/search/nodes");
		
	    try 
		{
	    	JSONReturn = httpClient.execute(ChefAuth.httpget, responseHandler);
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			Log.i("Cuts ClientProtocolException",e.getLocalizedMessage());
			Log.i("Cuts ClientProtocolException",e.getMessage());
		} 
		catch (IOException e) 
		{
			Log.i("Cuts IOException",responseHandler.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("Cuts",JSONReturn);
		
		return null;
	}
}
