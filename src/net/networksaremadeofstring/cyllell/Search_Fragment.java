package net.networksaremadeofstring.cyllell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Search_Fragment extends CyllellFragment
{
	ProgressDialog dialog;
	NodeListAdaptor NodeAdapter;
	JSONObject Nodes = null;
	HashMap<String, String> NodeMap;
	List<Node> listOfNodes = new ArrayList<Node>();
	ListView list;
	String query, index;
	Cuts threadCut;
	Handler handler;
	private SharedPreferences settings = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);

	    //Prep the handler to do all the UI updating etc
	    MakeHandler();
	    
	    return inflater.inflate(R.layout.search_main, container, false);
	}
	
	public void GetMoreDetails(final int Tag)
    {
		Toast.makeText(getActivity().getApplicationContext(), "Search results can't be queried for additional information at present.", Toast.LENGTH_SHORT).show();
    }
	
	public void PerformSearch(final Boolean Crafted)
	{
		try 
	      {
	    	  threadCut = new Cuts(getActivity().getApplicationContext());

			dialog = new ProgressDialog(getActivity());
	        dialog.setTitle("Chef Search");
	        dialog.setMessage("Searching for: "+query+"\n\nPlease wait: Prepping Authentication protocols");       
	        dialog.setIndeterminate(true);
	        dialog.show();
	        
			Thread dataPreload = new Thread() 
	    	{  
	    		public void run() 
	    		{
	    			try 
	    			{
	    				JSONObject Nodes;
	    				handler.sendEmptyMessage(200);
	    				if(Crafted)
	    				{
	    					Nodes = threadCut.CraftedSearch(query, index);
	    				}
	    				else
	    				{
	    					Nodes = threadCut.Search(query, index);
	    				}
	    				
						handler.sendEmptyMessage(201);
						JSONArray rows = Nodes.getJSONArray("rows");
						for(int i = 0; i < rows.length(); i++)
						{
							//Log.i("URI", ((JSONObject) rows.get(i)).getString("name"));
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
	
	public void MakeHandler()
	{
		handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				//TODO ListView population
    				NodeAdapter = new NodeListAdaptor(getActivity().getApplicationContext(), listOfNodes);
	    	        list.setAdapter(NodeAdapter);
	    	        
    				//Close the Progress dialog
        			dialog.dismiss();
    			}
    			else if(msg.what == 200)
    			{
    				dialog.setMessage("Searching for: "+query+"\n\nSending request to Chef...");
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
    				AlertDialog alertDialog = new AlertDialog.Builder(getActivity().getApplicationContext()).create();
    				alertDialog.setTitle("API Error");
    				alertDialog.setMessage("There was an error communicating with the API:\n" + msg.getData().getString("exception"));
    				alertDialog.setButton2("Back", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int which) 
    				   {
    					   //getActivity().getApplicationContext().finish();
    				   }
    				});
    				alertDialog.setIcon(R.drawable.error);
    				alertDialog.show();
    			}
    			
    			
    		}
    	};
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	
	    //List view to hold search results
	    list = (ListView) getView().findViewById(R.id.SearchResultsListView);
	    
	    settings = this.getActivity().getSharedPreferences("Cyllell", 0);
	    
	    if(settings.getBoolean("SearchFirstView", true) == true)
        {
        	Toast.makeText(getActivity(),"You can only search for items at the moment. Additional functionality will be added soon.", Toast.LENGTH_LONG).show();
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("SearchFirstView", false);
        	editor.commit();
        }
	    
	  //This is for the crafted search (not visible if the user came in via a search intent
	    ((Button) getView().findViewById(R.id.SearchButton)).setOnClickListener(new View.OnClickListener() {
	           public void onClick(View v) 
	           {
	        	   if(listOfNodes != null)
	        	   {
	        		   listOfNodes.clear();
	        	   }
	        	   index = ((Spinner) getView().findViewById(R.id.IndexChoice)).getSelectedItem().toString().toLowerCase();
	        	   query = ((TextView) getView().findViewById(R.id.SearchStringEditText)).getText().toString();
	        	   PerformSearch(false);
	           }
	    });
	    
	    // Get the intent, verify the action and get the query
	    /*Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) 
	    {
	    	getView().findViewById(R.id.SearchParamContainer).setVisibility(8);
	    	getView().findViewById(R.id.ShowSearchParams).setVisibility(0);
	    	getView().findViewById(R.id.SearchMainRelativeLayout).invalidate();
	    	
	    	query = intent.getStringExtra(SearchManager.QUERY);
	    	index = "node";
	    	
	    	PerformSearch(false);
	    }
	    else if (Intent.ACTION_SEARCH_LONG_PRESS.equals(intent.getAction()))
	    {
	    	//findViewById(R.id.SearchParamContainer).setVisibility(0);
	    	getView().findViewById(R.id.ShowSearchParams).setVisibility(4);
	    }
	    else
	    {
	    	//findViewById(R.id.SearchParamContainer).setVisibility(0);
	    	getView().findViewById(R.id.ShowSearchParams).setVisibility(4);
	    }*/  
    }
}