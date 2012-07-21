package net.networksaremadeofstring.cyllell;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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