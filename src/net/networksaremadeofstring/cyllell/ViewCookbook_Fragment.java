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
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewCookbook_Fragment extends Fragment
{
	private String URI = "";
	Cuts Cut = null;
	SharedPreferences settings = null;
	String FullJSON = "";
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		//Log.e("onSaveInstanceState","Saving URI " + URI);
		outState.putString("URI", URI);
	    super.onSaveInstanceState(outState);
	}
	
	public ViewCookbook_Fragment(String _URI)
	{
		this.URI = _URI;
	}
	
	public ViewCookbook_Fragment()
	{
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
        
        if(savedInstanceState != null)
        {
        	URI = savedInstanceState.getString("URI");
        }
        
        return inflater.inflate(R.layout.cookbook_view_details, container, false);
    }
    
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	((TextView) getView().findViewById(R.id.CookbookTitle)).setText(this.URI);
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
    				((TextView) getView().findViewById(R.id.MaintainerEmail)).setText(msg.getData().getString("maintainer_email"));
    				((TextView) getView().findViewById(R.id.Maintainer)).setText(msg.getData().getString("maintainer"));
    				((TextView) getView().findViewById(R.id.cookbookVersion)).setText(msg.getData().getString("version"));
    				((TextView) getView().findViewById(R.id.CookbookDescription)).setText(msg.getData().getString("description"));
    				((TextView) getView().findViewById(R.id.Recipes)).setText(msg.getData().getString("recipes"));
    				((TextView) getView().findViewById(R.id.Templates)).setText(msg.getData().getString("templates"));
    				
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
    				JSONObject Cookbook = Cut.GetCookbook(URI);
    				//Parsing JSON
					handler.sendEmptyMessage(201);
					FullJSON = Cookbook.toString(2);
					data.putString("maintainer_email", Cookbook.getJSONObject("metadata").getString("maintainer_email"));
					data.putString("maintainer", Cookbook.getJSONObject("metadata").getString("maintainer"));
					data.putString("version", Cookbook.getJSONObject("metadata").getString("version"));
					data.putString("description", Cookbook.getJSONObject("metadata").getString("description"));
					
					String RecipesString = "";
					JSONArray Recipes = Cookbook.getJSONArray("recipes");
					int RecipeCount = Recipes.length();
					for(int i = 0; i < RecipeCount; i++)
					{
						RecipesString += Recipes.getJSONObject(i).getString("name") + ", ";
					}
					if(RecipesString.length() > 0)
					{
						data.putString("recipes", RecipesString.substring(0, RecipesString.length() -2));
					}
					else
					{
						data.putString("recipes","No Recipes specified");
					}
					
					String TemplateString = "";
					JSONArray Templates = Cookbook.getJSONArray("templates");
					int TemplateCount = Templates.length();
					for(int i = 0; i < TemplateCount; i++)
					{
						TemplateString += Templates.getJSONObject(i).getString("name") + ", ";
					}
					if(TemplateString.length() > 0)
					{
						data.putString("templates", TemplateString.substring(0, TemplateString.length() -2));
					}
					else
					{
						data.putString("templates", "There are no templates in this cookbook");
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
