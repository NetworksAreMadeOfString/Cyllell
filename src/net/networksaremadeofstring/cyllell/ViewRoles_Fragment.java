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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class ViewRoles_Fragment extends CyllellFragment
{
	Cuts Cut = null;
	ListView list;
	ProgressDialog dialog;
	Handler updateListNotify;
	Thread GetFullDetails;
	SharedPreferences settings = null;
	Boolean CutInProgress = false;
	int selectedRole = 0;
	ActionMode mActionMode;
	RoleListAdaptor RoleAdapter;
	List<Role> listOfRoles = new ArrayList<Role>();
	JSONObject Roles = null;
	AlertDialog rolesContextualDialog;
	Dialog editRole;
	static ViewRoles_Handler editRoleHandler;
	
	public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		list = (ListView) this.getActivity().findViewById(R.id.rolesListView);
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
        if(listOfRoles.size() < 1)
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
    					listOfRoles.get(tag).SetSpinnerVisible();
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
    					Toast.makeText(ViewRoles_Fragment.this.getActivity(), "An error occured during that operation.", Toast.LENGTH_LONG).show();
    					listOfRoles.get(tag).SetErrorState();
    				}
    			}
    			RoleAdapter.notifyDataSetChanged();
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
					
					RoleAdapter = new RoleListAdaptor(getActivity().getBaseContext(), listOfRoles,listener,listenerLong);
    				list = (ListView) getView().findViewById(R.id.rolesListView);
    				if(list != null)
    				{
    					if(RoleAdapter != null)
    					{
    						list.setAdapter(RoleAdapter);
    					}
    					else
    					{
    						//Log.e("CookbookAdapter","CookbookAdapter is null");
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
    					   //getActivity().finish();
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
    			if(listOfRoles.size() > 0)
    			{
    				handler.sendEmptyMessage(0);
    			}
    			else
    			{
	    			try 
	    			{
	    				handler.sendEmptyMessage(200);
	    				Roles = Cut.GetRoles();
						handler.sendEmptyMessage(201);
	
						JSONArray Keys = Roles.names();
						
						for(int i = 0; i < Keys.length(); i++)
						{
					        listOfRoles.add(new Role(Keys.getString(i), Roles.getString(Keys.getString(i)).replaceFirst("^(https://|http://).*/roles/", "")));
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
        return inflater.inflate(R.layout.roles_landing, container, false);
    }
	
	public void GetMoreDetails(final int Tag)
    {
		//if(isTabletDevice())
        //{
			FragmentManager fm = getActivity().getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
	    	Fragment fragment = new ViewRole_Fragment(listOfRoles.get(Tag).GetURI());

	        fragmentTransaction.replace(R.id.RoleDetails, fragment,"RoleTag");
	        fragmentTransaction.commit();
       // }
		/*else
		{
			Intent GenericIntent = new Intent(getActivity().getApplicationContext(), Generic_Container.class);
        	GenericIntent.putExtra("fragment", "viewrole");
        	GenericIntent.putExtra("roleURI", listOfRoles.get(Tag).GetURI());
        	getActivity().startActivity(GenericIntent);
		}*/
    }
	
	public void selectForCAB(int id)
	{
		selectedRole = id;
    	mActionMode = getSherlockActivity().startActionMode(mActionModeCallback);
    	listOfRoles.get(selectedRole).SetSelected(true);
    	RoleAdapter.notifyDataSetChanged();
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() 
	{

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_roles, menu);
            mode.setTitle("knife role edit " + listOfRoles.get(selectedRole).GetName());
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
            	case R.id.edit_role:
            	{
            		/*Context mContext = getActivity();
	        		editRole = new Dialog(mContext);
	
	        		editRole.setContentView(R.layout.role_edit_details);
	        		editRole.setTitle("Edit " + listOfRoles.get(selectedRole).GetName());
		        	
	        		editRole.show();
	        		
	        		editRoleHandler = new ViewRoles_Handler(editRole,getActivity());
	        		final String URI = listOfRoles.get(selectedRole).GetURI();
	        		
	        		Thread GetRawJSON = new Thread() 
    	        	{  
    	        		private Message msg = new Message();
    	        		private Bundle data = new Bundle();
    	    			
    	        		public void run() 
    	        		{
    	        			try 
    	        			{
								JSONObject Role = Cut.GetRole(URI);
								data.putString("RawJSON", Role.toString());
								msg.setData(data);
								msg.what = R.integer.update_edit_dialog;
								editRoleHandler.sendMessage(msg);
							} 
    	        			catch (org.apache.http.client.HttpResponseException e)
    	        			{
    	        				Log.e("StatusCode",Integer.toString(e.getStatusCode()));
    	        				if(e.getStatusCode() == 401 || e.getStatusCode() == 403)
    	        				{
    	        					editRoleHandler.sendEmptyMessage(R.integer.http_forbidden);
    	        				}
    	        				else
    	        				{
    	        					e.printStackTrace();
    	        					editRoleHandler.sendEmptyMessage(R.integer.http_bad_request);
    	        				}
    	        			}
	        				catch (Exception e) 
		        	    	{
								e.printStackTrace();
								editRoleHandler.sendEmptyMessage(R.integer.http_bad_request);
		        	    	}
    	        		}
    	        	};
    	        	GetRawJSON.start();
	        		*/
            		GetMoreDetails(selectedRole);
	        		
		            return true;
            	}
            	
            	case R.id.delete_role:
            	{
            		Toast.makeText(getActivity(), "Deleting Roles is not available.\r\nIf you think it is a neccessary feature please email the author.", Toast.LENGTH_LONG).show();
		            return true;
            	}
            	
		        default:
		        	listOfRoles.get(selectedRole).SetSelected(false);
		        	selectedRole = 0;
		        	RoleAdapter.notifyDataSetChanged();
		            return false;
			}
        }
			
			// Called when the user exits the action mode
			@Override
			public void onDestroyActionMode(ActionMode mode) 
			{
				listOfRoles.get(selectedRole).SetSelected(false);
				selectedRole = 0;
	        	RoleAdapter.notifyDataSetChanged();
			    mActionMode = null;
			}
		};
}
