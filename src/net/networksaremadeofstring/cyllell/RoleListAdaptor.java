/*
* Copyright (C) 2011 - Gareth Llewellyn
*
* This file is part of Cyllell - http://blog.NetworksAreMadeOfString.co.uk/cyllell/
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>
*/
package net.networksaremadeofstring.cyllell;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RoleListAdaptor extends BaseAdapter
{
	private Context context;
    private List<Role> listRoles;
    private OnClickListener listener;
    private OnLongClickListener listenerLong;
    
    public RoleListAdaptor(Context context, List<Role> _listRoles) 
    {
        this.context = context;
        this.listRoles = _listRoles;
    }
    
    public RoleListAdaptor(Context context, List<Role> _listRoles, OnClickListener _listener) 
    {
        this.context = context;
        this.listRoles = _listRoles;
        this.listener = _listener;
    }
    
    public RoleListAdaptor(Context context, List<Role> _listRoles, OnClickListener _listener, OnLongClickListener _listenerLong) 
    {
        this.context = context;
        this.listRoles = _listRoles;
        this.listener = _listener;
        this.listenerLong = _listenerLong;
    }
    
    @Override
    public int getViewTypeCount() 
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position) 
    {
    	Role thisRole = listRoles.get(position);
    	if(thisRole.GetErrorState() == false)
    	{
    		return 1;
    	}
    	else
    	{
    		return 0;
    	}
    }
    
	public int getCount() {
		return listRoles.size();
	}

	public Object getItem(int position) 
	{
		return listRoles.get(position);
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Role thisRole = listRoles.get(position);
		if (convertView == null) 
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.cookbook_listitem, null);
    	}
    
        ((TextView) convertView.findViewById(R.id.CookbookName)).setText(thisRole.GetName());
        ((TextView) convertView.findViewById(R.id.cookbookVersion)).setText(thisRole.GetVersion());
        
        //DeviceNameTextView.setTypeface(Typeface.createFromAsset(((ViewNodes)context).getAssets(), "fonts/codeops_serif.ttf"));
        
        if(this.getItemViewType(position) == 1)
        {
        	//((TextView) convertView.findViewById(R.id.Environment)).setText(thisCookbook.Details.get("chef_environment"));
        }
        else
        {
        	ProgressBar spinner = (ProgressBar) convertView.findViewById(R.id.MoreDetailsSpinner);
        	if(thisRole.isSpinnerVisible())
        	{
        		spinner.setVisibility(0);
        	}
        	else
        	{
        		spinner.setVisibility(4);
        	}
        	
        	if(thisRole.GetErrorState())
        	{
        		spinner.setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.WarningIcon)).setVisibility(0);
        	}
        }
        //Log.i(thisRole.GetName(),thisRole.isSelected().toString());
        if(thisRole.isSelected())
        {
        	((View) convertView.findViewById(R.id.selectedIndicator)).setBackgroundColor(Color.rgb(247, 104, 26));
        }
        else
        {
        	//((View) convertView.findViewById(R.id.selectedIndicator)).setBackgroundColor(Color.rgb(244,242,230));
        }
        
        convertView.setTag(position);
        
        if(listener != null)
        	convertView.setOnClickListener((OnClickListener) listener);
        
        if(listenerLong != null)
        	convertView.setOnLongClickListener((OnLongClickListener) listenerLong);
        
        return convertView;
	}
}
