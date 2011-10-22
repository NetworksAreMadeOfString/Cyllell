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
        
        ImageView NodesButton = (ImageView)findViewById(R.id.imageView1);
        /*NodesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
             Intent ViewNodesIntent = new Intent(Main.this, ViewNodes.class);
             Main.this.startActivity(ViewNodesIntent);
            }
        });*/
        
        NodesButton.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				Toast toast = Toast.makeText(Main.this, "View all the nodes in the chef infrastructure", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.LEFT, v.getLeft(), v.getBottom());
				toast.show();
				return false;
			}
		});
        
        Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
        
        //See if this is the first time the app has run so we can give the user an intro
        if(settings.getBoolean("FirstRun", true) == true)
        {
        	Toast.makeText(Main.this, "Blah blah", Toast.LENGTH_LONG).show();
        }
    }

}
