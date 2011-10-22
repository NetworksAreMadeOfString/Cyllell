package net.networksaremadeofstring.cyllell;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity 
{
	private SharedPreferences settings = null;
	Typeface codeOpsFont = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        codeOpsFont = Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"); 
        
        TextView TitleBarText=(TextView)findViewById(R.id.TitleBarText);
        TitleBarText.setTypeface(codeOpsFont);
        
        //Populate the settings so we can get out some common info
        settings = getSharedPreferences("Cyllell", 0);
        
        Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
        
        //See if this is the first time the app has run so we can give the user an intro
        if(settings.getBoolean("FirstRun", true) == true)
        {
        	Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
        }
    }

}
