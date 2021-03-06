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
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class ViewEnvironments_Fragment extends CyllellFragment
{
	Cuts Cut = null;
	ListView list;
	ProgressDialog dialog;
	Handler updateListNotify;
	Thread GetFullDetails;
	SharedPreferences settings = null;
	Boolean CutInProgress = false;
	int selectedEnv = 0;
	ActionMode mActionMode;
	
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
        if(listOfEnvironments.size() < 1)
        {
        	dialog.show();
        }
        
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
					
					OnLongClickListener listenerLong = new OnLongClickListener()
    				{
						public boolean onLongClick(View v) 
						{
							selectForCAB((Integer)v.getTag());
							return true;
						}
					};
					
					EnvironmentAdapter = new EnvironmentListAdaptor(getActivity().getBaseContext(), listOfEnvironments,listener, listenerLong);
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
    	
		/*if(!isTabletDevice())
        {
    		((TextView) getActivity().findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/codeops_serif.ttf"));
        }*/
    }
	
	public void GetMoreDetails(final int Tag)
    {
		//if(isTabletDevice())
		if(true)
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
	
	public void selectForCAB(int id)
	{
		selectedEnv = id;
    	mActionMode = getSherlockActivity().startActionMode(mActionModeCallback);
    	listOfEnvironments.get(selectedEnv).SetSelected(true);
    	EnvironmentAdapter.notifyDataSetChanged();
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() 
	{

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_environments, menu);
            mode.setTitle("Manage Environment");
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
        {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
        {
            switch (item.getItemId()) 
            {
            	case R.id.delete_env:
            	{
            		Toast.makeText(getActivity(), "Deleting Environments is not available.\r\nIf you think it is a neccessary feature please email the author.", Toast.LENGTH_LONG).show();
		            return true;
            	}
            	
            	case R.id.edit_env:
            	{
            		GetMoreDetails(selectedEnv);
            		//Toast.makeText(getActivity(), "To edit an environment single press the environment you wish to edit from the list.\r\nOnce it has loaded make your changes then click the 'Update Environment' button at the bottom.", Toast.LENGTH_LONG).show();
		            return true;
            	}
            	
		        default:
		        	listOfEnvironments.get(selectedEnv).SetSelected(false);
		        	selectedEnv = 0;
		        	EnvironmentAdapter.notifyDataSetChanged();
		            return false;
			}
        }
			
			// Called when the user exits the action mode
			@Override
			public void onDestroyActionMode(ActionMode mode) 
			{
				listOfEnvironments.get(selectedEnv).SetSelected(false);
				selectedEnv = 0;
				EnvironmentAdapter.notifyDataSetChanged();
			    mActionMode = null;
			}
		};
}
