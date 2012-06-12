/*
* Copyright (C) 2012 - Gareth Llewellyn
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewEnvironments_Fragment extends CyllellFragment
{
	Cuts Cut = null;
	ListView list;
	ProgressDialog dialog;
	Handler updateListNotify;
	Thread GetFullDetails;
	private SharedPreferences settings = null;
	Boolean CutInProgress = false;
	
	EnvironmentListAdaptor EnvironmentAdapter;
	List<Environment> listOfEnvironments = new ArrayList<Environment>();
	JSONObject Environments = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		list = (ListView) this.getActivity().findViewById(R.id.environmentsListView);
		settings = this.getActivity().getSharedPreferences("Cyllell", 0);
        try 
        {
			Cut = new Cuts(getActivity());
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        
        dialog = new ProgressDialog(getActivity());
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
    					listOfEnvironments.get(tag).SetSpinnerVisible();
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
    					Toast.makeText(ViewEnvironments_Fragment.this.getActivity(), "An error occured during that operation.", Toast.LENGTH_LONG).show();
    					listOfEnvironments.get(tag).SetErrorState();
    				}
    			}
    			EnvironmentAdapter.notifyDataSetChanged();
    		}
    	};
        
    	final Handler handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				OnClickListener listener = new OnClickListener()
    				{
						public void onClick(View v) {
							//Log.i("OnClick","Clicked");
							GetMoreDetails((Integer)v.getTag());
						}
					};
					
					EnvironmentAdapter = new EnvironmentListAdaptor(getActivity().getBaseContext(), listOfEnvironments,listener);
    				list = (ListView) getView().findViewById(R.id.environmentsListView);
    				if(list != null)
    				{
    					if(EnvironmentAdapter != null)
    					{
    						list.setAdapter(EnvironmentAdapter);
    					}
    					else
    					{
    						//Log.e("EnvironmentAdapter","EnvironmentAdapter is null");
    					}
    				}
    				else
    				{
    					//Log.e("List","List is null");
    				}
	    	        
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
    				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
    				alertDialog.setTitle("API Error");
    				alertDialog.setMessage("There was an error communicating with the API:\n" + msg.getData().getString("exception"));
    				alertDialog.setButton2("Back", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int which) 
    				   {
    					   getActivity().finish();
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
    			if(listOfEnvironments.size() > 0)
    			{
    				handler.sendEmptyMessage(0);
    			}
    			else
    			{
	    			try 
	    			{
	    				handler.sendEmptyMessage(200);
	    				Environments = Cut.GetEnvironments();
						handler.sendEmptyMessage(201);
	
						JSONArray Keys = Environments.names();
						
						for(int i = 0; i < Keys.length(); i++)
						{
					        listOfEnvironments.add(new Environment(Keys.getString(i), Environments.getString(Keys.getString(i)).replaceFirst("^(https://|http://).*/environments/", "")));
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
					}
    			}
    			return;
    		}
    	};
    	
    	dataPreload.start();
        return inflater.inflate(R.layout.environments_landing, container, false);
    }
	
	public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	
		if(!isTabletDevice())
        {
    		((TextView) getActivity().findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/codeops_serif.ttf"));
        }
    }
	
	public void GetMoreDetails(final int Tag)
    {
		if(isTabletDevice())
        {
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    	Fragment fragment = new ViewEnvironment_Fragment(listOfEnvironments.get(Tag).GetURI());
	        fragmentTransaction.replace(R.id.EnvironmentDetails, fragment,"EnvironmentTag");
	        fragmentTransaction.commit();
        }
		else
		{
			Intent GenericIntent = new Intent(getActivity().getApplicationContext(), Generic_Container.class);
        	GenericIntent.putExtra("fragment", "viewenvironment");
        	GenericIntent.putExtra("envURI", listOfEnvironments.get(Tag).GetURI());
        	getActivity().startActivity(GenericIntent);
		}
    }
}
