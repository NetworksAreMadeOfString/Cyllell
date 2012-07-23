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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ViewNodes extends Activity
{
	Cuts Cut = null;
	JSONObject Nodes = null;
	HashMap<String, String> NodeMap;
	List<Node> listOfNodes = new ArrayList<Node>();
	ListView list;
	ProgressDialog dialog;
	NodeListAdaptor NodeAdapter;
	Handler updateListNotify;
	Thread GetFullDetails;
	SharedPreferences settings = null;
	Boolean CutInProgress = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nodeslanding);
        
        settings = this.getSharedPreferences("Cyllell", 0);
        
        ((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        
        list = (ListView)findViewById(R.id.nodesListView);
        
        try 
        {
			Cut = new Cuts(this);
		} 
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        dialog = new ProgressDialog(this);
        dialog.setTitle("Contacting Chef");
        dialog.setMessage("Please wait: Prepping Authentication protocols");       
        dialog.setIndeterminate(true);
        dialog.show();
        
        updateListNotify = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			int tag = msg.getData().getInt("tag", 999999);
    			
    			if(msg.what == 0)
    			{
    				if(tag != 999999)
    				{
	    				//Log.i("TAG - Handler", Integer.toString(tag));
	    				listOfNodes.get(tag).SetSpinnerVisible();
						//list.invalidate();
    				}
    			}
    			else if(msg.what == 1)
    			{
    				//Get rid of the lock
    				CutInProgress = false;
    				
    				//the notifyDataSetChanged() will handle the rest
    			}
    			else if (msg.what == 99)
    			{
    				if(tag != 999999)
    				{
    					Toast.makeText(ViewNodes.this, "An error occured during that operation.", Toast.LENGTH_LONG).show();
    					//Log.i("TAG - Handler", Integer.toString(tag));
    					listOfNodes.get(tag).SetErrorState();
    				}
    			}
    			NodeAdapter.notifyDataSetChanged();
    		}
    	};
        
    	final Handler handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				//TODO ListView population
    				NodeAdapter = new NodeListAdaptor(ViewNodes.this, listOfNodes);
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
    				AlertDialog alertDialog = new AlertDialog.Builder(ViewNodes.this).create();
    				alertDialog.setTitle("API Error");
    				alertDialog.setMessage("There was an error communicating with the API:\n" + msg.getData().getString("exception"));
    				alertDialog.setButton2("Back", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int which) {
    					   ViewNodes.this.finish();
    				   }
    				});
    				alertDialog.setIcon(R.drawable.icon);
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
					Nodes = Cut.GetNodes();
					handler.sendEmptyMessage(201);
					JSONArray Keys = Nodes.names();
					for(int i = 0; i < Nodes.length(); i++)
					{
						String URI = Nodes.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/nodes/", "");
						//Log.i("URI", URI);
						listOfNodes.add(new Node(Keys.get(i).toString(), URI));
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
    
    public void GetMoreDetails(final int Tag)
    {
    	//listOfNodes.get(Tag).SetFullDetails(true);
    	if(CutInProgress)
    	{
    		Toast.makeText(ViewNodes.this, "For security reasons only one request can be made at a time.", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		//CutInProgress = true;
	    	GetFullDetails = new Thread() 
	    	{  
	    		public void run() 
	    		{
	    			try 
	    			{
	    				//Log.i("TAG", Integer.toString(Tag));
	    				Message msg = new Message();
	    				Bundle data = new Bundle();
	    				data.putInt("tag", Tag);
	    				msg.setData(data);
	    				msg.what = 0;
	    				//Set the spinner going
	    				updateListNotify.sendMessage(msg);
	    				
	    				//<Xzibit>I heard you liked passing function returns to functions so I've returned a function to your function whilst you call a function
	    				//listOfNodes.get(Tag).SetFullDetails(Cut.CanonicalizeNode(Cut.GetNode(listOfNodes.get(Tag).GetURI())));
	    				
	    				//Bypass locking issues
	    				Cuts threadCut = new Cuts(ViewNodes.this);
	    				listOfNodes.get(Tag).SetFullDetails(threadCut.CanonicalizeNode(threadCut.GetNode(listOfNodes.get(Tag).GetURI())));
	    				
	    				//Tell the UI thread we are done and it can notify the adaptor
	    				updateListNotify.sendEmptyMessage(1);
					} 
	    			catch (Exception e)
	    			{
	    				//Log.e("GetMoreDetails","An actual exception occured!");
	    				e.printStackTrace();
	    				
	    				Message msg = new Message();
	    				Bundle data = new Bundle();
	    				data.putInt("tag", Tag);
	    				msg.setData(data);
	    				msg.what = 99;
	    				updateListNotify.sendMessage(msg);
					}
	    			
	    			return;
	    		}
	    	};
	    	
	    	GetFullDetails.start();
    	}
    }
}
