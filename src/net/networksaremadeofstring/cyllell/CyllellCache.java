package net.networksaremadeofstring.cyllell;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CyllellCache extends SQLiteOpenHelper 
{
	 
    final static int DB_VERSION = 1;
    final static String DB_NAME = "cyllellCache";
    Context context;
    SQLiteDatabase qdb;
    Handler cacheHandler;
    
    public CyllellCache(Context context) 
    {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        qdb = this.getReadableDatabase();
    }
    
    public void RefreshCache(final Cuts Cut)
    {
    	cacheHandler = new Handler() 
    	{
    		public void handleMessage(Message msg) 
    		{	
    			/*String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
				int icon = R.drawable.ic_stat_chef_changes;
				CharSequence tickerText = "Cyllell cache has finished refreshing";
				long when = System.currentTimeMillis();
				Notification notification = new Notification(icon, tickerText, when);
				
				CharSequence contentTitle = "Cyllell Notification";
				CharSequence contentText = "The local cache has finished updating. Select to view more details.";
				Intent notificationIntent = new Intent(context, MainLanding.class);
				PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				mNotificationManager.notify(1, notification);*/
				
				/*Toast toast = Toast.makeText(context, "Cyllell's local cache has finished updating.", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.BOTTOM|Gravity.RIGHT, 0, 0);*/
    			
	    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View layout = inflater.inflate(R.layout.cache_toast,  null);
	
				TextView text = (TextView) layout.findViewById(R.id.text);
				text.setText("Cyllell's local cache has finished updating.");
	
				Toast toast = new Toast(context);
				toast.setGravity(Gravity.BOTTOM|Gravity.RIGHT, 0, 0);
				toast.setDuration(Toast.LENGTH_LONG);
				toast.setView(layout);
				toast.show();
    		}
    	};
    	
    	Thread ProcessDatabase = new Thread() 
    	{  
    		public void run() 
    		{
    			Log.e("RefreshCache","Nodes -----------------------------------------------------------------");
    			//Nodes -----------------------------------------------------------------
    			JSONObject Nodes = null;
				try 
				{
					Nodes = Cut.GetNodes();
					JSONArray Keys = Nodes.names();
					qdb.delete("nodes", null, null);
	    			qdb.beginTransaction();
	    			
					for(int i = 0; i < Nodes.length(); i++)
					{
						String URI = Nodes.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/nodes/", "");
						ContentValues values = new ContentValues(2);
						values.put("node_name", Keys.get(i).toString());
						values.put("node_uri", URI);
						
						qdb.insert("nodes", null, values);
					}
					qdb.setTransactionSuccessful();
					qdb.endTransaction();
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					qdb.endTransaction();
				}
				
				//Roles -----------------------------------------------------------------
				Log.e("RefreshCache","Roles -----------------------------------------------------------------");
				
    			JSONObject Roles = null;
				try 
				{
					Roles = Cut.GetRoles();
					JSONArray Keys = Roles.names();
					qdb.delete("roles", null, null);
					qdb.beginTransaction();
					for(int i = 0; i < Roles.length(); i++)
					{
						//In future versions I might get all the role details here too
						String URI = Roles.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/roles/", "");
						ContentValues values = new ContentValues(2);
						values.put("role_name", Keys.get(i).toString());
						values.put("role_uri", URI);
						qdb.insert("roles", null, values);
					}
					qdb.setTransactionSuccessful();
					qdb.endTransaction();
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					qdb.endTransaction();
				}
				
				//Environments -----------------------------------------------------------------
				Log.e("RefreshCache","Environments -----------------------------------------------------------------");
				
    			JSONObject Environments = null;
				try 
				{
					Environments = Cut.GetEnvironments();
					JSONArray Keys = Environments.names();
					qdb.delete("environments", null, null);
					qdb.beginTransaction();
					for(int i = 0; i < Environments.length(); i++)
					{
						String URI = Environments.getString(Keys.get(i).toString()).replaceFirst("^(https://|http://).*/environments/", "");
						ContentValues values = new ContentValues(2);
						values.put("env_name", Keys.get(i).toString());
						values.put("env_uri", URI);
						
						qdb.insert("environments", null, values);
					}
					qdb.setTransactionSuccessful();
					qdb.endTransaction();
				} 
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					qdb.endTransaction();
				}
				
				//Finish up
				//qdb.close();
				cacheHandler.sendEmptyMessage(1);
    		}
    	};
    	
    	ProcessDatabase.start();
    }
    
    public Cursor getEnvironments()
    {
    	
		return qdb.rawQuery("SELECT env_name FROM environments", null);
    }
    
    public Cursor getRoles()
    {
    	//SQLiteDatabase qdb = this.getReadableDatabase();
		return qdb.rawQuery("SELECT role_name FROM roles", null);
    }

	@Override
	public void onCreate(SQLiteDatabase arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
	{
		// TODO Auto-generated method stub
		
	}
}