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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONArray;
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
	private String ChefURL = "https://api.opscode.com"; //Hosted Chef
	private String PathSuffix = "/organizations/namos"; //Hosted Chef
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
	public HttpGet httpget = null;
	public HttpPut httpput = null;
	
	public Cuts(Context thisContext) throws Exception
	{
		settings = thisContext.getSharedPreferences("Cyllell", 0);
		
		responseHandler = new BasicResponseHandler();
		
		if(settings.getString("URL", "--").equals("--") == false && settings.getString("ClientName", "--").equals("--") == false &&  settings.getString("PrivateKey", "--").equals("--") == false)
		{
			ChefAuth = new Authentication(settings.getString("ClientName", "--"),settings.getString("PrivateKey", "--"));
			this.ChefURL = settings.getString("URL", "--");
			this.PathSuffix = settings.getString("Suffix", "");
		}
		else
		{
			//Log.i("URL",settings.getString("URL", "--"));
			//Log.i("ClientName",settings.getString("ClientName", "--"));
			//Log.i("PrivateKey",settings.getString("PrivateKey", "--"));
			throw new Exception("Chef URL is not set");
		}
		
		//Create the httpclient that trusts everything!
		this.PrepareSSLHTTPClient();
	}
	
	private void PrepareSSLHTTPClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException
	{
		client = new DefaultHttpClient(); 
		
        SchemeRegistry registry = new SchemeRegistry();
        SocketFactory socketFactory = null;
        
        //Check whether people are self signing or not
        if(settings.getBoolean("SelfSigned", true))
        {
        	//Log.i("SelfSigned","Allowing Self Signed Certificates");
			socketFactory = TrustAllSSLSocketFactory.getDefault();
        }
        else
        {
        	//Log.i("SelfSigned","Enforcing Certificate checks");
        	socketFactory = SSLSocketFactory.getSocketFactory();
        }
        registry.register(new Scheme("https", socketFactory, 443));
        mgr = new SingleClientConnManager(client.getParams(), registry); 

        httpClient = new DefaultHttpClient(mgr, client.getParams());
	}
	
	/**
	 * Gets a list of all Nodes in the system along with the URI's to get additional details
	 * @return - JSONObject - Returns a hash of uri's for the nodes.
	 * @throws Exception
	 */
	public JSONObject GetCookbooks() throws Exception
	{
		String Path = this.PathSuffix + "/cookbooks";
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	/**
	 * Gets a list of all Nodes in the system along with the URI's to get additional details
	 * @return - JSONObject - Returns a hash of uri's for the nodes.
	 * @throws Exception
	 */
	public JSONObject GetCookbook(String CookbookURI) throws Exception
	{
		String Path = this.PathSuffix + "/cookbooks/" + CookbookURI + "/_latest";
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	/**
	 * Gets a list of all Nodes in the system along with the URI's to get additional details
	 * @return - JSONObject - Returns a hash of uri's for the nodes.
	 * @throws Exception
	 */
	public JSONObject GetNodes() throws Exception
	{
		String Path = this.PathSuffix + "/nodes";
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	/**
	 * Gets a list of all Roles in the system along with the URI's to get additional details
	 * @return - JSONObject - Returns a hash of uri's for the roles.
	 * @throws Exception
	 */
	public JSONObject GetRoles() throws Exception
	{
		String Path = this.PathSuffix + "/roles";
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	/**
	 * Gets a specific role and it details
	 * @return - JSONObject - Returns details of a role
	 * @throws Exception
	 */
	public JSONObject GetRole(String URI) throws Exception
	{
		String Path = this.PathSuffix + "/roles/"+URI;
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	/**
	 * Gets a list of all environments in the system along with the URI's to get additional details
	 * @return - JSONObject - Returns a hash of uri's for the environments.
	 * @throws Exception
	 */
	public JSONObject GetEnvironments() throws Exception
	{
		String Path = this.PathSuffix + "/environments";
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	/**
	 * Gets a specific environemnt and its details
	 * @return - JSONObject - Returns details of a role
	 * @throws Exception
	 */
	public JSONObject GetEnvironment(String URI) throws Exception
	{
		String Path = this.PathSuffix + "/environments/"+URI;
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	public Boolean SetEnvironment(String NodeURI, String Environment) throws Exception
	{
		JSONObject NodeDetails = this.GetNode(NodeURI);
		NodeDetails.put("chef_environment", Environment);

		String Path = this.PathSuffix + "/nodes/"+NodeURI;
		this.httpput = new HttpPut(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, NodeDetails.toString(),"PUT");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpput.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	this.httpput.setEntity(new StringEntity(NodeDetails.toString()));
    	
    	String jsonTempString = httpClient.execute(this.httpput, responseHandler);
    	Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
		
    	if(json.get("chef_environment").equals(Environment))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public Boolean AddRunList(String NodeURI, String Role) throws Exception
	{
		JSONObject NodeDetails = this.GetNode(NodeURI);
		Log.i("NodeDetails",NodeDetails.toString(3));
		
		
		//TODO Replace with JSONObject.accumulate() at some point
		JSONArray RunList = NodeDetails.getJSONArray("run_list");
		RunList.put("role["+Role+"]");
		NodeDetails.put("run_list", RunList);
		
		Log.i("NodeDetails2",NodeDetails.toString(3));
		
		String Path = this.PathSuffix + "/nodes/"+NodeURI;
		this.httpput = new HttpPut(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, NodeDetails.toString(),"PUT");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpput.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	this.httpput.setEntity(new StringEntity(NodeDetails.toString()));
    	
    	String jsonTempString = httpClient.execute(this.httpput, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	//JSONObject json = new JSONObject(jsonTempString);
		
		return true;
	}
	
	public Boolean UpdateNodewithRawJSON(String NodeURI, JSONObject JSON) throws Exception
	{
		String Path = this.PathSuffix + "/nodes/"+NodeURI;
		this.httpput = new HttpPut(this.ChefURL + Path);
		Log.i("UpdateNodewithRawJSON",Path);
		Log.i("UpdateNodewithRawJSON",JSON.toString(3));
    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, JSON.toString(),"PUT");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpput.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	this.httpput.setEntity(new StringEntity(JSON.toString()));
    	
    	httpClient.execute(this.httpput, responseHandler);

		return true;
	}
	/**
	 * Try and perform a chef function - it'll either succeed or throw an exception / return false
	 * @return - Boolean - whether we logged in or not
	 * @throws Exception
	 */
	public Boolean ConfirmLogin() throws Exception
	{
		String Path = this.PathSuffix + "/clients/" + settings.getString("ClientName", "--");
		this.httpget = new HttpGet(this.ChefURL + Path);
		Log.i("ConfirmLogin",Path);
    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		Log.i("Headers",Headers.get(i).getName()+":"+Headers.get(i).getValue());
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
          
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    
    	JSONObject json = new JSONObject(jsonTempString);
    	
    	if(settings.getBoolean("OpenSourceChef", true) == true)
    	{
	    	if(json.getString("chef_type").equals("client"))
	    	{
	    		return true;
	    	}
	    	else
	    	{
	    		return false;
	    	}
    	}
    	else
    	{
    		if(json.getString("clientname").equals(settings.getString("ClientName", "--")))
	    	{
	    		return true;
	    	}
	    	else
	    	{
	    		return false;
	    	}
    	}
	}
	
	/**
	 * Get all the details about a particular node
	 * @param URI - The unique name of the node in question
	 * @return - JSONObject - Complete list of cookbook attributes, definitions, libraries and recipes that are required for this node
	 * @throws Exception
	 */
	public JSONObject GetNode(String URI) throws Exception
	{
		String Path = this.PathSuffix + "/nodes/"+URI;
		this.httpget = new HttpGet(this.ChefURL + Path);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	public HashMap<String,String> CanonicalizeNode(JSONObject Node)
	{
		HashMap<String,String> CanonicalNode = new HashMap<String,String>();
		
		//TODO Make some form of awesome array to do this in a loop
		try {
			CanonicalNode.put("serial_number",
					Node.getJSONObject("automatic").getJSONObject("dmi").getJSONObject("system").getString("serial_number"));
		} 
		catch (JSONException e) { CanonicalNode.put("serial_number","Serial Number Not Found"); }
		
		try {
			CanonicalNode.put("sku_number",
					Node.getJSONObject("automatic").getJSONObject("dmi").getJSONObject("system").getString("sku_number"));
		} 
		catch (JSONException e) { CanonicalNode.put("sku_number","SKU Not Found"); }
		
		try {
			CanonicalNode.put("family",
					Node.getJSONObject("automatic").getJSONObject("dmi").getJSONObject("system").getString("family"));
		} 
		catch (JSONException e) { CanonicalNode.put("family","Unknown OS"); }
		
		try {
			CanonicalNode.put("uptime",
					Node.getJSONObject("automatic").getString("uptime"));
			//Log.i("uptime",Node.getJSONObject("automatic").getString("uptime"));
		} 
		catch (JSONException e) { CanonicalNode.put("uptime","Uptime unknown"); }
		
		
		try {
			CanonicalNode.put("chef_environment","Environment: " + Node.getString("chef_environment"));
		} 
		catch (JSONException e) { CanonicalNode.put("chef_environment","Chef Environment Unknown"); }
		
		try {
			CanonicalNode.put("cpuCountString",Node.getJSONObject("automatic").getJSONObject("cpu").getString("real") + " / " + Node.getJSONObject("automatic").getJSONObject("cpu").getString("total"));
		} 
		catch (JSONException e) { CanonicalNode.put("cpuCountString","0 / 0"); }
		
		try {
			CanonicalNode.put("ramStatsString",Node.getJSONObject("automatic").getJSONObject("memory").getString("total") + " / " + Node.getJSONObject("automatic").getJSONObject("memory").getString("free"));
		} 
		catch (JSONException e) { CanonicalNode.put("ramStatsString","0kB Total / 0kB Free"); }
		
		try {
			CanonicalNode.put("platform",Node.getJSONObject("automatic").getString("platform"));
		} 
		catch (JSONException e) { CanonicalNode.put("platform",""); }
		
		try 
		{
			JSONArray runListArray = Node.getJSONArray("run_list");
			String runList = "";
			for(int i = 0; i < runListArray.length(); i++)
			{
				runList += runListArray.getString(i);			
				if(i != runListArray.length() - 1)
					runList += ", ";
			}

			CanonicalNode.put("run_list",runList);
			//Log.i("CanonicalizeNode",runList);
		} 
		catch (JSONException e) { CanonicalNode.put("run_list","Unknown"); }
		
		return CanonicalNode;
	}
	
	public JSONObject Search(String Query, String Index) throws Exception
	{
		String Path = this.PathSuffix + "/search/"+Index;
		
		Query = Query.replace(' ', '*');
		
		if(Index.equals("node"))
		{
			this.httpget = new HttpGet(this.ChefURL + Path + "?q=name:*"+Query+"*%20OR%20role:*"+Query+"*");
		}
		else
		{
			this.httpget = new HttpGet(this.ChefURL + Path + "?q=name:*"+Query+"*");
		}

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	//Log.i("JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
	
	public JSONObject CraftedSearch(String Query, String Index) throws Exception
	{
		String Path = this.PathSuffix + "/search/"+Index.toLowerCase();
		
		Query = Query.replace(" ", "%20");
		
		this.httpget = new HttpGet(this.ChefURL + Path.toLowerCase() + "?q="+Query);

    	List <NameValuePair> Headers = ChefAuth.GetHeaders(Path, "","GET");
    	for(int i = 0; i < Headers.size(); i++)
    	{
    		this.httpget.setHeader(Headers.get(i).getName(),Headers.get(i).getValue());
    	}
    	String jsonTempString = httpClient.execute(this.httpget, responseHandler);
    	Log.i("CraftedSearch JSONString:",jsonTempString);
    	JSONObject json = new JSONObject(jsonTempString);
    	return json;
	}
}
