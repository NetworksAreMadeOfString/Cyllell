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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Search extends Activity
{
	ProgressDialog dialog;
	NodeListAdaptor NodeAdapter;
	JSONObject Nodes = null;
	HashMap<String, String> NodeMap;
	List<Node> listOfNodes = new ArrayList<Node>();
	ListView list;
	String query;
	Cuts threadCut;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search_main);
	    ((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
	    
	    list = (ListView)findViewById(R.id.SearchResultsListView);
	    
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
	    {
	    	findViewById(R.id.SearchParamContainer).setVisibility(8);
	    	findViewById(R.id.ShowSearchParams).setVisibility(0);
	    	findViewById(R.id.SearchMainRelativeLayout).invalidate();
	    	
	    	query = intent.getStringExtra(SearchManager.QUERY);
		      try 
		      {
		    	  threadCut = new Cuts(Search.this);

				dialog = new ProgressDialog(this);
		        dialog.setTitle("Contacting Chef");
		        dialog.setMessage("Please wait: Prepping Authentication protocols");       
		        dialog.setIndeterminate(true);
		        dialog.show();
		        
		        
				final Handler handler = new Handler() 
		    	{
		    		public void handleMessage(Message msg) 
		    		{	
		    			//Once we've checked the data is good to use start processing it
		    			if(msg.what == 0)
		    			{
		    				//TODO ListView population
		    				NodeAdapter = new NodeListAdaptor(Search.this, listOfNodes);
			    	        list.setAdapter(NodeAdapter);
			    	        
		    				//Close the Progress dialog
		        			dialog.dismiss();
		    			}
		    			else if(msg.what == 200)
		    			{
		    				dialog.setMessage("Sending request to Chef...");
		    			}
		    			else if(msg.what == 201)
		    			{
		    				dialog.setMessage("Parsing JSON.....");
		    			}
		    			else if(msg.what == 202)
		    			{
		    				dialog.setMessage("Populating UI!");
		    			}
		    			else
		    			{
		    				//Close the Progress dialog
		    				dialog.dismiss();
		    				
		    				//Alert the user that something went terribly wrong
		    				AlertDialog alertDialog = new AlertDialog.Builder(Search.this).create();
		    				alertDialog.setTitle("API Error");
		    				alertDialog.setMessage("There was an error communicating with the API:\n" + msg.getData().getString("exception"));
		    				alertDialog.setButton2("Back", new DialogInterface.OnClickListener() {
		    				   public void onClick(DialogInterface dialog, int which) {
		    					   Search.this.finish();
		    				   }
		    				});
		    				alertDialog.setIcon(R.drawable.error);
		    				alertDialog.show();
		    			}
		    			
		    			
		    		}
		    	};
				
				
				Thread dataPreload = new Thread() 
		    	{  
		    		public void run() 
		    		{
		    			try 
		    			{
		    				handler.sendEmptyMessage(200);
		    				JSONObject Nodes = threadCut.Search(query, "node");
							handler.sendEmptyMessage(201);
							JSONArray rows = Nodes.getJSONArray("rows");
							for(int i = 0; i < rows.length(); i++)
							{
								//String URI = Nodes.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/nodes/", "");
								//Log.i("URI", URI);
								//listOfNodes.add(new Node(Keys.get(i).toString(), URI));
								Log.i("URI", ((JSONObject) rows.get(i)).getString("name"));
								listOfNodes.add(new Node(((JSONObject) rows.get(i)).getString("name"), ""));
							}
							handler.sendEmptyMessage(202);
							handler.sendEmptyMessage(0);
						} 
		    			catch (Exception e)
		    			{
		    				Message msg = new Message();
		    				Bundle data = new Bundle();
		    				data.putString("exception", e.getMessage());
		    				msg.setData(data);
		    				msg.what = 1;
		    				handler.sendMessage(msg);
		    				//handler.sendEmptyMessage(1);
						}
		    			
		    			return;
		    		}
		    	};
		    	
		    	dataPreload.start();
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
		      } 
		      catch (Exception e) 
		      {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      }
	    }
	    else if (Intent.ACTION_SEARCH_LONG_PRESS.equals(intent.getAction()))
	    {
	    	//findViewById(R.id.SearchParamContainer).setVisibility(0);
	    	findViewById(R.id.ShowSearchParams).setVisibility(4);
	    }
	    else
	    {
	    	//findViewById(R.id.SearchParamContainer).setVisibility(0);
	    	findViewById(R.id.ShowSearchParams).setVisibility(4);
	    }
	    
	    ((ImageView)findViewById(R.id.ShowSearchParams)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	findViewById(R.id.SearchParamContainer).setVisibility(0);
    	    	findViewById(R.id.ShowSearchParams).setVisibility(4);
    	    	findViewById(R.id.SearchMainRelativeLayout).invalidate();
            }
        });
	    
	    ((ImageView)findViewById(R.id.HideSearchParams)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
            	findViewById(R.id.SearchParamContainer).setVisibility(8);
    	    	findViewById(R.id.ShowSearchParams).setVisibility(0);
    	    	findViewById(R.id.SearchMainRelativeLayout).invalidate();
    	    	
            }
        });
	}
	
	public void GetMoreDetails(final int Tag)
    {
		Toast.makeText(Search.this, "Search results can't be queried for additional information at present.", Toast.LENGTH_SHORT).show();
    }
}
