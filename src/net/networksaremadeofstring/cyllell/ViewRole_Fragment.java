package net.networksaremadeofstring.cyllell;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewRole_Fragment extends CyllellFragment
{
	private String URI = "";
	Cuts Cut = null;
	private SharedPreferences settings = null;
	private String FullJSON = "";
	
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
    	
		if(!isTabletDevice())
        {
    		((TextView) getActivity().findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/codeops_serif.ttf"));
        }
		
    	final Handler handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				//Populate the data
    				((TextView) getView().findViewById(R.id.RoleDescription)).setText(msg.getData().getString("description"));
    				((TextView) getView().findViewById(R.id.RunList)).setText(msg.getData().getString("run_list"));
    				((EditText) getView().findViewById(R.id.DefaultAttributes)).setText(msg.getData().getString("default_attributes"));
    				((EditText) getView().findViewById(R.id.OverrideAttributes)).setText(msg.getData().getString("override_attributes"));
    				
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
    				JSONObject Role = Cut.GetRole(URI);
    				//Parsing JSON
					handler.sendEmptyMessage(201);
					FullJSON = Role.toString(2);
					/*data.putString("maintainer_email", Role.getJSONObject("metadata").getString("maintainer_email"));
					data.putString("maintainer", Role.getJSONObject("metadata").getString("maintainer"));
					data.putString("version", Role.getJSONObject("metadata").getString("version"));*/
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
}
