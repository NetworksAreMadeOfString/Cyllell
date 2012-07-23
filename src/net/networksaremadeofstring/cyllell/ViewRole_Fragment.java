package net.networksaremadeofstring.cyllell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewRole_Fragment extends CyllellFragment
{
	private String URI = "";
	Cuts Cut = null;
	private SharedPreferences settings = null;
	//private String FullJSON = "";
	ProgressDialog dialog;
	Handler updateHandler;
	JSONObject Role;
	
	public ViewRole_Fragment(String _URI)
	{
		this.URI = _URI;
	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
    	settings = this.getActivity().getSharedPreferences("Cyllell", 0);
        try 
        {
			Cut = new Cuts(getActivity());
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        
        return inflater.inflate(R.layout.role_view_details, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	((TextView) getView().findViewById(R.id.RoleTitle)).setText(this.URI);
    	((ProgressBar) getView().findViewById(R.id.progressBar1)).setVisibility(0);
		((TextView) getView().findViewById(R.id.ProgressStatus)).setVisibility(0);
    	
		if(settings.getBoolean("RolesFirstView", true) == true)
        {
        	Toast rolehelp = Toast.makeText(getActivity(),"Use the Menu to access additional options such as Role Editing", Toast.LENGTH_LONG);
        	rolehelp.setGravity(Gravity.TOP|Gravity.RIGHT, 5, 40);
        	rolehelp.show();
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("RolesFirstView", false);
        	editor.commit();
        }
		
		((Button) getView().findViewById(R.id.EditRoleButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				dialog = new ProgressDialog(getActivity());
		        dialog.setTitle("Updating Role..");
		        dialog.setMessage("Please wait: Prepping Authentication protocols");       
		        dialog.setIndeterminate(true);
		        dialog.show();
		        UpdateRole();
			}
		});
		
		/*((EditText) getView().findViewById(R.id.RunList)).setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) 
			{
				// TODO Auto-generated method stub
				//return false;
				Toast.makeText(getActivity(), "Choose a Recipe", Toast.LENGTH_SHORT).show();
				return true;
			}
		});*/

    	final Handler handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				//Populate the data
    				/*((TextView) getView().findViewById(R.id.RoleDescription)).setText(msg.getData().getString("description"));
    				((TextView) getView().findViewById(R.id.RunList)).setText(msg.getData().getString("run_list"));
    				((EditText) getView().findViewById(R.id.DefaultAttributes)).setText(msg.getData().getString("default_attributes"));
    				((EditText) getView().findViewById(R.id.OverrideAttributes)).setText(msg.getData().getString("override_attributes"));*/
    				try
    				{
    					((TextView) getView().findViewById(R.id.RoleDescription)).setText(Role.getString("description"));
    					((TextView) getView().findViewById(R.id.RunList)).setText(Role.getJSONArray("run_list").toString(3));
    					((EditText) getView().findViewById(R.id.DefaultAttributes)).setText(Role.getJSONObject("default_attributes").toString(3));
    					((EditText) getView().findViewById(R.id.OverrideAttributes)).setText(Role.getJSONObject("override_attributes").toString(3));
    				}
    				catch(Exception e)
    				{
    					Toast.makeText(getActivity(), "There was an error processing the JSON.\r\nIt would be advisable to try that again.", Toast.LENGTH_SHORT).show();
    				}
    				
					//Hide the progress dialog
    				((ProgressBar) getView().findViewById(R.id.progressBar1)).setVisibility(8);
    				((TextView) getView().findViewById(R.id.ProgressStatus)).setVisibility(8);

    				//((RelativeLayout) getView().findViewById(R.id.relativeLayout1)).setLayoutParams(new LayoutParams(-1,-1));
    				getView().invalidate();
    			}
    			else if(msg.what == 200)
    			{
    				((TextView) getView().findViewById(R.id.ProgressStatus)).setText("Sending request to Chef...");
    			}
    			else if(msg.what == 201)
    			{
    				((TextView) getView().findViewById(R.id.ProgressStatus)).setText("Parsing JSON.....");
    			}
    			else if(msg.what == 202)
    			{
    				((TextView) getView().findViewById(R.id.ProgressStatus)).setText("Populating UI!");
    			}
    			else
    			{
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
    			try 
    			{
    				Message msg = new Message();
    				Bundle data = new Bundle();
    				
    				//Sending request
    				handler.sendEmptyMessage(200);
    				Role = Cut.GetRole(URI);
    				Log.e("Role",Role.toString(3));
    				//Parsing JSON
					handler.sendEmptyMessage(201);
					//FullJSON = Role.toString(2);
					data.putString("description", Role.getString("description"));
					data.putString("default_attributes", Role.getJSONObject("default_attributes").toString(2));
					data.putString("override_attributes", Role.getJSONObject("override_attributes").toString(2));
					
					String RunListString = "";
					JSONArray RunListItems = Role.getJSONArray("run_list");
					int RunListCount = RunListItems.length();
					for(int i = 0; i < RunListCount; i++)
					{
						RunListString += RunListItems.getString(i) + ", ";
					}
					if(RunListString.length() > 0)
					{
						data.putString("run_list", RunListString.substring(0, RunListString.length() -2));
					}
					else
					{
						data.putString("run_list","No run list specified");
					}
					
					
					
					//Populating UI
					handler.sendEmptyMessage(202);
					
    				msg.setData(data);
    				msg.what = 0;
    				handler.sendMessage(msg);
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
    			return;
    		}
    	};
    	
    	dataPreload.start();
    }
    
    private void UpdateRole()
    {
    	updateHandler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			switch(msg.what)
    			{
    				case 0:
    				{
    					dialog.setMessage("Success!");
    					dialog.dismiss();
    					Toast.makeText(getActivity(), "Role successfully updated!", Toast.LENGTH_SHORT).show();
    				}
    				break;
    				
    				case 1:
    				{
    					dialog.setMessage("Packaging new JSON...");
    				}
    				break;
    				
    				case 2:
    				{
    					dialog.setMessage("Sending to Chef server...");
    				}
    				break;
    				
    				default:
    				{
    					dialog.dismiss();
    					Toast.makeText(getActivity(), "An Exception Occured;\r\n" + msg.getData().getString("exception"), Toast.LENGTH_LONG).show();
    				}
    			}
    		}
    	};
    	
    	Thread ProcessRequest = new Thread() 
		{  
			public void run() 
			{
				Message msg = new Message();
				Bundle data = new Bundle();
				
				try 
				{
					JSONObject newRole = Role;
					updateHandler.sendEmptyMessage(1);
					try
					{
						newRole.put("run_list", new JSONArray(((EditText) getView().findViewById(R.id.RunList)).getText().toString()));
					}
					catch(JSONException j)
					{
						//newRole.put("run_list", new JSONArray("[]"));
						data.putString("exception", "The Runlist is not valid JSON:\r\n" + j.getMessage());
						Message.obtain();
	    				msg.setData(data);
	    				msg.what = 99;
	    				updateHandler.sendMessage(msg);
	    				return;
					}
					
					
					try
					{
						newRole.put("default_attributes", new JSONObject(((EditText) getView().findViewById(R.id.DefaultAttributes)).getText().toString()));
					}
					catch(JSONException j)
					{
						//newRole.put("default_attributes", new JSONObject("{}"));
						data.putString("exception", "The default attributes are not valid JSON:\r\n" + j.getMessage());
						Message.obtain();
	    				msg.setData(data);
	    				msg.what = 100;
	    				updateHandler.sendMessage(msg);
	    				return;
					}
					
					try
					{
						newRole.put("override_attributes", 	new JSONObject(((EditText) getView().findViewById(R.id.OverrideAttributes)).getText().toString()));
					}
					catch(JSONException j)
					{
						//newRole.put("override_attributes", new JSONObject("{}"));
						data.putString("exception", "The override attributes are not valid JSON:\r\n" + j.getMessage());
						Message.obtain();
	    				msg.setData(data);
	    				msg.what = 101;
	    				updateHandler.sendMessage(msg);
	    				return;
					}
					
					updateHandler.sendEmptyMessage(2);
					Cuts Cut = new Cuts(getActivity());
					if(Cut.UpdateRole(newRole))
					{
						updateHandler.sendEmptyMessage(0);
					}
					else
					{
						data.putString("exception", "The update was unsuccessful. There was no reason given.");
						Message.obtain();
	    				msg.setData(data);
	    				msg.what = 102;
	    				updateHandler.sendMessage(msg);
					}
				}
				catch (org.json.JSONException j)
				{
    				data.putString("exception", j.getMessage());
    				Message.obtain();
    				msg.setData(data);
    				msg.what = 103;
    				updateHandler.sendMessage(msg);
				}
				catch (org.apache.http.client.HttpResponseException e)
				{
					Log.e("StatusCode",Integer.toString(e.getStatusCode()));
					if(e.getStatusCode() == 401 || e.getStatusCode() == 403)
					{
						//updateHandler.sendEmptyMessage(2);
						data.putString("exception", "That request was denied (HTTP 401 or 403) probably due to Hosted Chef Role based Access control;\r\n" + e.getMessage());
						Message.obtain();
	    				msg.setData(data);
	    				msg.what = 104;
	    				updateHandler.sendMessage(msg);
					}
					else
					{
						e.printStackTrace();
						data.putString("exception", e.getMessage());
						Message.obtain();
	    				msg.setData(data);
	    				msg.what = 105;
	    				updateHandler.sendMessage(msg);
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					data.putString("exception", e.getMessage());
					Message.obtain();
    				msg.setData(data);
    				msg.what = 106;
    				updateHandler.sendMessage(msg);
				}
			}

		};
		ProcessRequest.start();
    }
}
