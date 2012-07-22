package net.networksaremadeofstring.cyllell;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Message;

public class CyllellCache extends SQLiteOpenHelper 
{
	 
    final static int DB_VERSION = 1;
    final static String DB_NAME = "cyllellCache";
    Context context;
    SQLiteDatabase qdb;
    
    public CyllellCache(Context context) 
    {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        qdb = this.getReadableDatabase();
    }
    
    public void RefreshCache(final Cuts Cut)
    {
    	Thread ProcessDatabase = new Thread() 
    	{  
    		public void run() 
    		{
    			//Nodes -----------------------------------------------------------------
    			qdb.delete("nodes", null, null);
    			qdb.beginTransaction();
    			JSONObject Nodes = null;
				try 
				{
					Nodes = Cut.GetNodes();
					JSONArray Keys = Nodes.names();
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
				qdb.delete("roles", null, null);
				qdb.beginTransaction();
    			JSONObject Roles = null;
				try 
				{
					Roles = Cut.GetRoles();
					JSONArray Keys = Roles.names();
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
				qdb.delete("environments", null, null);
				qdb.beginTransaction();
    			JSONObject Environments = null;
				try 
				{
					Environments = Cut.GetEnvironments();
					JSONArray Keys = Environments.names();
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