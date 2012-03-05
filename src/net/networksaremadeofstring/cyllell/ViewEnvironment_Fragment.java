package net.networksaremadeofstring.cyllell;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class ViewEnvironment_Fragment extends Fragment
{
	private String URI = "";
	Cuts Cut = null;
	private SharedPreferences settings = null;
	private String FullJSON = "";
	
	public ViewEnvironment_Fragment(String _URI)
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
        
        return inflater.inflate(R.layout.environment_view_details, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	((TextView) getView().findViewById(R.id.EnvironmentTitle)).setText(this.URI);
    	((ProgressBar) getView().findViewById(R.id.progressBar1)).setVisibility(0);
		((TextView) getView().findViewById(R.id.ProgressStatus)).setVisibility(0);
    	
    	final Handler handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				//Populate the data
    				((TextView) getView().findViewById(R.id.EnvironmentDescription)).setText(msg.getData().getString("description"));
    				((TextView) getView().findViewById(R.id.CookbookList)).setText(msg.getData().getString("cookbook_list"));
    				((EditText) getView().findViewById(R.id.DefaultAttributes)).setText(msg.getData().getString("default_attributes"));
    				
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
    				JSONObject Environment = Cut.GetEnvironment(URI);
    				//Parsing JSON
					handler.sendEmptyMessage(201);
					FullJSON = Environment.toString(2);
					/*data.putString("maintainer_email", Role.getJSONObject("metadata").getString("maintainer_email"));
					data.putString("maintainer", Role.getJSONObject("metadata").getString("maintainer"));
					data.putString("version", Role.getJSONObject("metadata").getString("version"));*/
					data.putString("description", Environment.getString("description"));
					
					data.putString("default_attributes", Environment.getJSONObject("default_attributes").toString(2));
					
					String CookbookListString = "";
					/*JSONArray CookbookVersions = Environment.getJSONArray("cookbook_versions");
					int RunListCount = CookbookVersions.length();
					for(int i = 0; i < RunListCount; i++)
					{
						CookbookListString += CookbookVersions.getString(i) + ", ";
					}*/
					JSONArray Keys = Environment.getJSONObject("cookbook_versions").names();
					
					if(Keys != null)
					{
						for(int i = 0; i < Keys.length(); i++)
						{
							CookbookListString += Keys.getString(i) + ", ";
					    }
					}
					if(CookbookListString.length() > 0)
					{
						data.putString("cookbook_list", CookbookListString.substring(0, CookbookListString.length() -2));
					}
					else
					{
						data.putString("cookbook_list","There are no locked cookbooks in this environment.");
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
