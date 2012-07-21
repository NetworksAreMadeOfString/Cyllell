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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;



public class ViewNodes_Fragment extends SherlockFragment
{
	Cuts Cut = null;
	JSONObject Nodes = null;
	HashMap<String, String> NodeMap;
	List<Node> listOfNodes = new ArrayList<Node>();
	ListView list;
	ProgressDialog dialog;
	NodeListAdaptor NodeAdapter;
	Handler updateListNotify;
	Handler handler;
	Thread GetFullDetails;
	private SharedPreferences settings = null;
	Boolean CutInProgress = false;
	Boolean Paused = false;
	Thread dataPreload;
	String instanceTime = "";
	int selectedNode = 0;
	ActionMode mActionMode;
	AlertDialog nodeContextualDialog;
	
	public void onActivityCreated(Bundle savedInstanceState)
    {
		Log.e("onActivityCreated","Called");
    	super.onCreate(savedInstanceState);
    	setRetainInstance(true);

    	settings = this.getActivity().getSharedPreferences("Cyllell", 0);
    	
    	if(settings.getBoolean("NodesFirstView", true) == true)
        {
        	Toast.makeText(getActivity(),"Perform a short press to see OHAI info or perform a long press to edit nodes.", Toast.LENGTH_LONG).show();
        	SharedPreferences.Editor editor = settings.edit();
        	editor.putBoolean("NodesFirstView", false);
        	editor.commit();
        }
    	
		list = (ListView) this.getActivity().findViewById(R.id.nodesListView);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
        try 
        {
			Cut = new Cuts(this.getActivity());
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        
        dialog = new ProgressDialog(this.getActivity());
        dialog.setTitle("Contacting Chef");
        dialog.setMessage("Please wait: Prepping Authentication protocols");       
        dialog.setIndeterminate(true);
        if(listOfNodes.size() < 1)
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
    					Toast.makeText(ViewNodes_Fragment.this.getActivity(), "An error occured during that operation.", Toast.LENGTH_LONG).show();
    					//Log.i("TAG - Handler", Integer.toString(tag));
    					listOfNodes.get(tag).SetErrorState();
    				}
    			}
    			NodeAdapter.notifyDataSetChanged();
    		}
    	};
        
    	handler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			//Once we've checked the data is good to use start processing it
    			if(msg.what == 0)
    			{
    				OnClickListener listener = new OnClickListener()
    				{
						public void onClick(View v) 
						{
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
					
    				NodeAdapter = new NodeListAdaptor(getActivity(), listOfNodes,listener,listenerLong);
    				list = (ListView) getActivity().findViewById(R.id.nodesListView);
    				if(list != null)
    				{
    					if(NodeAdapter != null)
    					{
    						list.setAdapter(NodeAdapter);
    					}
    					else
    					{
    						//Log.e("NodeAdapter","NodeAdapter is null");
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
    			else if(msg.what == 300)
    			{
    				//Toast.makeText(ViewNodes_Fragment.this.getActivity(), "Making Request", Toast.LENGTH_SHORT).show();
    				dialog = new ProgressDialog(ViewNodes_Fragment.this.getActivity());
    		        dialog.setTitle("Contacting Chef");
    		        dialog.setMessage("Changing Environment");       
    		        dialog.setIndeterminate(true);
    		        dialog.show();
    			}
    			else if(msg.what == 301)
    			{
    				dialog.dismiss();
    				Toast.makeText(ViewNodes_Fragment.this.getActivity(), "Node Updated!", Toast.LENGTH_SHORT).show();
    				nodeContextualDialog.dismiss();
    				listOfNodes.get(selectedNode).SetSelected(false);
		        	selectedNode = 0;
		        	NodeAdapter.notifyDataSetChanged();
    			}
    			else if(msg.what == 302)
    			{
    				dialog.dismiss();
    				Toast.makeText(ViewNodes_Fragment.this.getActivity(), "There was a problem updating that node.", Toast.LENGTH_SHORT).show();
    			}	
    			else if(msg.what == 303)
    			{
    				dialog.dismiss();
    				Toast.makeText(ViewNodes_Fragment.this.getActivity(), "You are not authorized to update that node.\r\n[Hosted Chef RBAC]", Toast.LENGTH_SHORT).show();
    			}
    			
    			
    			//-------------- Edit Run List
    			else if(msg.what == 400)
    			{
    				//Toast.makeText(ViewNodes_Fragment.this.getActivity(), "Making Request", Toast.LENGTH_SHORT).show();
    				dialog = new ProgressDialog(ViewNodes_Fragment.this.getActivity());
    		        dialog.setTitle("Contacting Chef");
    		        dialog.setMessage("Adding to run list....");       
    		        dialog.setIndeterminate(true);
    		        dialog.show();
    			}
    			else if(msg.what == 401)
    			{
    				dialog.dismiss();
    				Toast.makeText(ViewNodes_Fragment.this.getActivity(), "Node Updated!", Toast.LENGTH_SHORT).show();
    				nodeContextualDialog.dismiss();
    				listOfNodes.get(selectedNode).SetSelected(false);
		        	selectedNode = 0;
		        	NodeAdapter.notifyDataSetChanged();
    			}
    			else if(msg.what == 402)
    			{
    				dialog.dismiss();
    				Toast.makeText(ViewNodes_Fragment.this.getActivity(), "There was a problem updating that node.", Toast.LENGTH_SHORT).show();
    			}	
    			else if(msg.what == 403)
    			{
    				dialog.dismiss();
    				Toast.makeText(ViewNodes_Fragment.this.getActivity(), "You are not authorized to update that node.\r\n[Hosted Chef RBAC]", Toast.LENGTH_SHORT).show();
    			}
    			//-------------- End Edit Run List
    			else
    			{
    				//Close the Progress dialog
    				dialog.dismiss();
    				
    				//Alert the user that something went terribly wrong
    				AlertDialog alertDialog = new AlertDialog.Builder(ViewNodes_Fragment.this.getActivity()).create();
    				alertDialog.setTitle("API Error");
    				alertDialog.setMessage("There was an error communicating with the API:\n" + msg.getData().getString("exception"));
    				alertDialog.setButton2("Back", new DialogInterface.OnClickListener() {
    				   public void onClick(DialogInterface dialog, int which) {
    					   ViewNodes_Fragment.this.getActivity().finish();
    				   }
    				});
    				alertDialog.setIcon(R.drawable.icon);
    				alertDialog.show();
    			}
    			
    			
    		}
    	};
    	
    	dataPreload = new Thread() 
    	{  
    		public void run() 
    		{
    			if(listOfNodes.size() > 0)
    			{
    				handler.sendEmptyMessage(0);
    			}
    			else
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
					}
    			}
    			
    			return;
    		}
    	};
    	
    	dataPreload.start();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		//Log.e("onCreateView","Called");
        return inflater.inflate(R.layout.nodeslanding, container, false);
    }
	
	public void GetMoreDetails(final int Tag)
    {
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

    				Cuts threadCut = new Cuts(ViewNodes_Fragment.this.getActivity());
    				listOfNodes.get(Tag).SetFullDetails(threadCut.CanonicalizeNode(threadCut.GetNode(listOfNodes.get(Tag).GetURI())));
    				
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
	
	public void selectForCAB(int id)
	{
		selectedNode = id;
    	mActionMode = getSherlockActivity().startActionMode(mActionModeCallback);
    	listOfNodes.get(selectedNode).SetSelected(true);
    	NodeAdapter.notifyDataSetChanged();
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() 
	{

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_nodes, menu);
            mode.setTitle("knife node edit " + listOfNodes.get(selectedNode).GetName());
            //mode.setSubtitle("'knife node edit'");
            /*menu.getItem(0).setShowAsActionFlags(4);
            menu.getItem(1).setShowAsActionFlags(4);*/
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
            	case R.id.EditRunList:
            	{
            		Cursor dbResults = ((MainLanding) getActivity()).cacheDB.getRoles();
    	    		final CharSequence[] items = new CharSequence[dbResults.getCount()];
    	    		int i = 0;
    	    		while(dbResults.moveToNext())
        			{
    	    			items[i] = dbResults.getString(0);
    	    			i++;
        			}
    	    		
    	    		AlertDialog.Builder builder = new AlertDialog.Builder(ViewNodes_Fragment.this.getActivity());
		        	builder.setTitle("Add Roles to Runlist");
		        	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		        	    public void onClick(DialogInterface dialog, int item) 
		        	    {
		        	    	final String URI = listOfNodes.get(selectedNode).GetURI();
		        	    	final String Role = items[item].toString();
		        	    	
		        	    		handler.sendEmptyMessage(400);
		        	    		Thread ProcessRequest = new Thread() 
		        	        	{  
		        	        		private Boolean continueThread = true;
		        	        		private Message msg = new Message();
		        	        		private Bundle data = new Bundle();
		        	    			
		        	        		public void run() 
		        	        		{
		        	        			try 
		    		        	    	{
		        	        				if(Cut.AddRunList(URI, Role))
		        	        				{
		        	        					handler.sendEmptyMessage(301);
		        	        				}
		        	        				else
		        	        				{
		        	        					handler.sendEmptyMessage(302);
		        	        				}
		    		        	    	}
		        	        			catch (org.apache.http.client.HttpResponseException e)
		        	        			{
		        	        				Log.e("StatusCode",Integer.toString(e.getStatusCode()));
		        	        				if(e.getStatusCode() == 401 || e.getStatusCode() == 403)
		        	        				{
		        	        					handler.sendEmptyMessage(403);
		        	        				}
		        	        				else
		        	        				{
		        	        					e.printStackTrace();
		        								handler.sendEmptyMessage(402);
		        	        				}
		        	        			}
	        	        				catch (Exception e) 
	        		        	    	{
	        								e.printStackTrace();
	        								handler.sendEmptyMessage(402);
	        		        	    	}
		        	        		}

		        	        	};
		        	        	ProcessRequest.start();
		        	    }
		        	});
		        	
		        	nodeContextualDialog = builder.create();
		        	nodeContextualDialog.show();
		        	
		            return true;
            	}
            	
		        case R.id.EditEnv:
		        {
    	    		// CyllellCache cacheDB = new CyllellCache(ViewNodes_Fragment.this.getActivity());
		        	
    	    		Cursor dbResults = ((MainLanding) getActivity()).cacheDB.getEnvironments();
    	    		final CharSequence[] items = new CharSequence[dbResults.getCount()];
    	    		int i = 0;
    	    		while(dbResults.moveToNext())
        			{
    	    			items[i] = dbResults.getString(0);
    	    			i++;
        			}
    	    		
    	    		
		        	AlertDialog.Builder builder = new AlertDialog.Builder(ViewNodes_Fragment.this.getActivity());
		        	builder.setTitle("Set Environment");
		        	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		        	    public void onClick(DialogInterface dialog, int item) 
		        	    {
		        	    	final String URI = listOfNodes.get(selectedNode).GetURI();
		        	    	final String Env = items[item].toString();
		        	    	
		        	    		handler.sendEmptyMessage(300);
		        	    		Thread ProcessRequest = new Thread() 
		        	        	{  
		        	        		private Boolean continueThread = true;
		        	        		private Message msg = new Message();
		        	        		private Bundle data = new Bundle();
		        	    			
		        	        		public void run() 
		        	        		{
		        	        			try 
		    		        	    	{
		        	        				if(Cut.SetEnvironment(URI, Env))
		        	        				{
		        	        					handler.sendEmptyMessage(301);
		        	        				}
		        	        				else
		        	        				{
		        	        					handler.sendEmptyMessage(302);
		        	        				}
		    		        	    	}
		        	        			catch (org.apache.http.client.HttpResponseException e)
		        	        			{
		        	        				Log.e("StatusCode",Integer.toString(e.getStatusCode()));
		        	        				if(e.getStatusCode() == 401 || e.getStatusCode() == 403)
		        	        				{
		        	        					handler.sendEmptyMessage(303);
		        	        				}
		        	        				else
		        	        				{
		        	        					e.printStackTrace();
		        								handler.sendEmptyMessage(302);
		        	        				}
		        	        			}
	        	        				catch (Exception e) 
	        		        	    	{
	        								e.printStackTrace();
	        								handler.sendEmptyMessage(302);
	        		        	    	}
		        	        		}

		        	        	};
		        	        	ProcessRequest.start();
		        	    }
		        	});
		        	nodeContextualDialog = builder.create();
		        	nodeContextualDialog.show();
		        	
		            return true;
		        }
            
		        default:
		        	listOfNodes.get(selectedNode).SetSelected(false);
		        	selectedNode = 0;
		        	NodeAdapter.notifyDataSetChanged();
		            return false;
			}
        }
			
			// Called when the user exits the action mode
			@Override
			public void onDestroyActionMode(ActionMode mode) 
			{
				listOfNodes.get(selectedNode).SetSelected(false);
				selectedNode = 0;
			    mActionMode = null;
			    NodeAdapter.notifyDataSetChanged();
			}
		};
}
