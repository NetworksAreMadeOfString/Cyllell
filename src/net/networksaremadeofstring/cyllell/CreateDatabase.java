package net.networksaremadeofstring.cyllell;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CreateDatabase extends Activity
{
	private SharedPreferences settings = null;
    int requestCode; //Used for evaluating what the settings Activity returned (Should always be 1)
    SQLiteDatabase cacheDB = null;
    Cuts Cut;
    
    Handler updateUI;
	Thread ProcessDatabase;
	Button CreateCacheButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.createdatabase); 
        
        ((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        
        //Populate the settings so we can get out some common info
        settings = getSharedPreferences("Cyllell", 0);

        try 
        {
			Cut = new Cuts(this);
		} 
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        createHandlers();
        createThread();
        ProcessDatabase.start();
    }
    
    private void createHandlers()
    {
    	updateUI = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			Boolean threadSuccess = msg.getData().getBoolean("success",false);
    			((TextView) findViewById(R.id.lastUpdateLabel)).setText(msg.getData().getString("lastUpdate"));
    			
    			switch(msg.what)
    			{
	    			case 0://DB Connected
	    			{
	    				((ImageView) findViewById(R.id.step1image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(10);
	    			}
	    			break;
	    			
	    			case 1://Environments table
	    			{
	    				((ImageView) findViewById(R.id.step2image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(14);
	    			}
	    			break;
	    			
	    			case 2://Nodes table
	    			{
	    				((ImageView) findViewById(R.id.step2image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(18);
	    			}
	    			break;
	    			
	    			case 3://Node details table
	    			{
	    				((ImageView) findViewById(R.id.step3image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(22);
	    			}
	    			break;
	    			
	    			case 4://Roles tables
	    			{
	    				((ImageView) findViewById(R.id.step4image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(25);
	    			}
	    			break;
	    			
	    			
	    			case 9://Increment the counter
	    			{
	    				int progress = ((ProgressBar) findViewById(R.id.overallProgress)).getProgress() + msg.getData().getInt("progress",0);
	    				//Log.i("Progress","Increasing Progress Bar by: " + Integer.toString(progress) + " from " + Integer.toString(((ProgressBar) findViewById(R.id.overallProgress)).getProgress()));
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(progress);
	    			}
	    			break;
	    			
	    			case 10://Nodes table
	    			{
	    				((ImageView) findViewById(R.id.step9image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(50);
	    			}
	    			break;
	    			
	    			case 11://Roles table
	    			{
	    				((ImageView) findViewById(R.id.step11image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(75);
	    			}
	    			break;
	    			
	    			case 12://Roles table
	    			{
	    				((ImageView) findViewById(R.id.step12image)).setImageResource(R.drawable.tick);
	    				((ProgressBar) findViewById(R.id.overallProgress)).setProgress(100);
	    			}
	    			break;
	    			
	    			
	    			case 25://checksumming
	    			{
	    				((LinearLayout) findViewById(R.id.finalizeContainer)).setVisibility(0);
	    			}
	    			break;
	    			
	    			case 26://Done
	    			{
	    				Toast.makeText(CreateDatabase.this, "Local caching complete! \n\nStarting Cyllell", Toast.LENGTH_SHORT).show();
	    	    		
	    				SharedPreferences.Editor editor = settings.edit();
	    	        	editor.putBoolean("DatabaseCreated", true);
	    	        	editor.commit();
	    				
	    	    		//Intent MainIntent = new Intent(CreateDatabase.this, MainLanding.class);
	    	    		//CreateDatabase.this.startActivity(MainIntent);
	    	    		finish();
	    			}
	    			break;
    			}
    			((RelativeLayout) findViewById(R.id.createDatabaseRL)).invalidate();
    		}
    	};
    		
    }
    
    private void createThread()
    {
    	ProcessDatabase = new Thread() 
    	{  
    		private Boolean continueThread = true;
    		private Message msg = new Message();
    		private Bundle data = new Bundle();
			
    		public void run() 
    		{
    			//Connect to the DB
				try
				{
					cacheDB = CreateDatabase.this.openOrCreateDatabase("cyllellCache", MODE_PRIVATE, null);
					data.putString("lastUpdate", "Created database!");
					data.putBoolean("success", true);
				}
				catch(Exception e)
				{
					continueThread = false;
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
				msg.what = 0;
				msg.setData(data);
				updateUI.sendMessage(msg);
				
				if(!continueThread)
    			{
    				updateUI.sendEmptyMessage(88);
    				return;
    			}
				
				//Create environments table
				msg = new Message();
				data = new Bundle();
				try
				{
    				cacheDB.execSQL("CREATE TABLE \"environments\" (\"cyllell_env_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"env_name\" TEXT NOT NULL  UNIQUE , \"env_uri\" TEXT NOT NULL  UNIQUE , \"env_desc\" TEXT, \"env_cookbook_versions\" TEXT)");
    				data.putString("lastUpdate", "Created environments table!");
    				data.putBoolean("success", true);
	    		}
				catch(Exception e)
				{
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
				msg.what = 1;
				msg.setData(data);
				updateUI.sendMessage(msg);
    			
				//Create nodes table
				msg = new Message();
				data = new Bundle();
				try
				{
					cacheDB.execSQL("CREATE TABLE \"nodes\" (\"cyllel_node_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"node_name\" TEXT NOT NULL , \"node_uri\" TEXT NOT NULL , \"cyllell_last_update\" DATETIME NOT NULL  DEFAULT CURRENT_DATE)");
					data.putString("lastUpdate", "Created nodes table!");
					data.putBoolean("success", true);
				}
				catch(Exception e)
				{
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
				msg.what = 2;
				msg.setData(data);
				updateUI.sendMessage(msg);
    			
				//Node details
				msg = new Message();
				data = new Bundle();
				try
				{
					cacheDB.execSQL("CREATE TABLE \"node_details\" (\"cyllell_node_details_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"node_id\" INTEGER NOT NULL , \"node_details\" TEXT NOT NULL )");
					data.putString("lastUpdate", "Created node details table!");
					data.putBoolean("success", true);
				}
				catch(Exception e)
				{
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
    			
    			msg.what = 3;
				msg.setData(data);
				updateUI.sendMessage(msg);
    			
				//Roles table
				msg = new Message();
				data = new Bundle();
				try
				{
	    			cacheDB.execSQL("CREATE TABLE \"roles\" (\"cyllell_role_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"role_name\" TEXT NOT NULL  UNIQUE , \"role_uri\" TEXT NOT NULL  UNIQUE , \"role_description\" TEXT, \"role_attributes\" TEXT, \"role_run_list\" TEXT, \"role_attributes_override\" TEXT, \"role_attributes_default\" TEXT)");
	    			data.putString("lastUpdate", "Created roles table!");
	    			data.putBoolean("success", true);
				}
				catch(Exception e)
				{
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
    			
    			msg.what = 4;
				msg.setData(data);
				updateUI.sendMessage(msg);
    			
    			/*cacheDB.execSQL("");
    			updateUI.sendEmptyMessage(5);
    			
    			cacheDB.execSQL("");
    			updateUI.sendEmptyMessage(6);
    			
    			cacheDB.execSQL("");
    			updateUI.sendEmptyMessage(7);
    			
    			cacheDB.execSQL("");
    			updateUI.sendEmptyMessage(8);
    			*/
    			
    			//Get nodes info
    			if(!continueThread)
    			{
    				updateUI.sendEmptyMessage(88);
    				return;
    			}
    			
    			//Nodes -----------------------------------------------------------------
    			cacheDB.delete("nodes", null, null);
    			cacheDB.beginTransaction();
    			JSONObject Nodes = null;
				try 
				{
					Nodes = Cut.GetNodes();
					JSONArray Keys = Nodes.names();
					double increase = (25 / (float)Nodes.length());
					
					//Log.i("NodesIncrease", Double.toString(increase));
					double progress = increase;
					for(int i = 0; i < Nodes.length(); i++)
					{
						String URI = Nodes.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/nodes/", "");
						ContentValues values = new ContentValues(2);
						values.put("node_name", Keys.get(i).toString());
						values.put("node_uri", URI);
						
						progress += increase;
						
						if(progress > 1.0)
						{
							msg = new Message();
							data = new Bundle();
							data.putInt("progress", (int)progress);
							msg.what = 9;
							data.putString("lastUpdate", "Added Node " + Keys.get(i).toString());
							msg.setData(data);
							updateUI.sendMessage(msg);
							progress = 0;
						}
						
						cacheDB.insert("nodes", null, values);
					}
					cacheDB.setTransactionSuccessful();
					cacheDB.endTransaction();
					data = new Bundle();
					data.putBoolean("success", true);
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					cacheDB.endTransaction();
					data = new Bundle();
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
				
				msg = new Message();
				msg.what = 10;
				data.putString("lastUpdate", "All nodes added!");
				msg.setData(data);
				updateUI.sendMessage(msg);
				
				
				//Roles -----------------------------------------------------------------
				cacheDB.delete("roles", null, null);
				cacheDB.beginTransaction();
    			JSONObject Roles = null;
				try 
				{
					Roles = Cut.GetRoles();
					JSONArray Keys = Roles.names();
					double increase = (25 / (float)Roles.length());
					double progress = increase;
					for(int i = 0; i < Roles.length(); i++)
					{
						//In future versions I might get all the role details here too
						String URI = Roles.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/roles/", "");
						ContentValues values = new ContentValues(2);
						values.put("role_name", Keys.get(i).toString());
						values.put("role_uri", URI);

						
						progress += increase;
						
						if(progress > 1.0)
						{
							msg = new Message();
							data = new Bundle();
							data.putInt("progress", (int)progress);
							msg.what = 9; //This is a cheat
							data.putString("lastUpdate", "Added Role " + Keys.get(i).toString());
							msg.setData(data);
							updateUI.sendMessage(msg);
							progress = 0;
						}
						
						cacheDB.insert("roles", null, values);
					}
					cacheDB.setTransactionSuccessful();
					cacheDB.endTransaction();
					data = new Bundle();
					data.putBoolean("success", true);
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					cacheDB.endTransaction();
					data = new Bundle();
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
				
				msg = new Message();
				msg.what = 11;
				data.putString("lastUpdate", "All roles added!");
				msg.setData(data);
				updateUI.sendMessage(msg);
				
				
				//Environments -----------------------------------------------------------------
				cacheDB.delete("environments", null, null);
				cacheDB.beginTransaction();
    			JSONObject Environments = null;
				try 
				{
					Environments = Cut.GetEnvironments();
					JSONArray Keys = Environments.names();
					double increase = (25 / (float)Environments.length());
					double progress = increase;
					for(int i = 0; i < Environments.length(); i++)
					{
						String URI = Environments.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/environments/", "");
						ContentValues values = new ContentValues(2);
						values.put("env_name", Keys.get(i).toString());
						values.put("env_uri", URI);
						
						progress += increase;
						
						if(progress > 1.0)
						{
							msg = new Message();
							data = new Bundle();
							data.putInt("progress", (int)progress);
							msg.what = 9; //This is a cheat
							data.putString("lastUpdate", "Added Environment " + Keys.get(i).toString());
							msg.setData(data);
							updateUI.sendMessage(msg);
							progress = 0;
						}
						
						cacheDB.insert("environments", null, values);
					}
					cacheDB.setTransactionSuccessful();
					cacheDB.endTransaction();
					data = new Bundle();
					data.putBoolean("success", true);
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					cacheDB.endTransaction();
					data = new Bundle();
					data.putBoolean("success", false);
					data.putString("exception", e.getMessage());
				}
				
				msg = new Message();
				msg.what = 12;
				data.putString("lastUpdate", "All Environments added!");
				msg.setData(data);
				updateUI.sendMessage(msg);
				
				//Finish up
				msg = new Message();
				msg.what = 25;
				data.putString("lastUpdate", "Caching Complete! Checking database contents....");
				msg.setData(data);
				updateUI.sendMessage(msg);
				//updateUI.sendEmptyMessage(25);
				
				cacheDB.close();
				//Right now there's nothing to check
				updateUI.sendEmptyMessageDelayed(26, 2000);
    		}
    	};
    }
}
