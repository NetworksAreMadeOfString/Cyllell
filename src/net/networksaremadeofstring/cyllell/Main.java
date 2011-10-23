package net.networksaremadeofstring.cyllell;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity 
{
	private SharedPreferences settings = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((TextView)findViewById(R.id.TitleBarText)).setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/codeops_serif.ttf"));
        
        //Populate the settings so we can get out some common info
        settings = getSharedPreferences("Cyllell", 0);
        
        ((ImageView)findViewById(R.id.NodeImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
	             Intent ViewNodesIntent = new Intent(Main.this, ViewNodes.class);
	             Main.this.startActivity(ViewNodesIntent);
            }
        });
        
        
        ((ImageView)findViewById(R.id.SettingsImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
	             Intent ViewSettingsIntent = new Intent(Main.this, ViewSettings.class);
	             Main.this.startActivity(ViewSettingsIntent);
            }
        });
        
        ((ImageView)findViewById(R.id.SearchImageView)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
	             Intent SearchIntent = new Intent(Main.this, Search.class);
	             Main.this.startActivity(SearchIntent);
            }
        });
        

        /*NodesButton.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(event.getPressure() > 0.5)
				{
					Toast toast = Toast.makeText(Main.this, "View all the nodes in the chef infrastructure", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.TOP|Gravity.LEFT, v.getLeft(), v.getBottom()+64);
					toast.show();
				}
				return false;
			}
		});*/
        
        
        
        //See if this is the first time the app has run so we can give the user an intro
        if(settings.getBoolean("FirstRun", true) == true)
        {
        	Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
        }
    }
    
   /* @Override
    public boolean onSearchRequested() 
    {
    	Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
    	
         Bundle appData = new Bundle();
         appData.putBoolean("key", true);
         startSearch(null, false, appData, false);
         return true;
     }*/

}
