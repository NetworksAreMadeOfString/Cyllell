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

public class NodeListAdaptor extends BaseAdapter
{
	private Context context;
    private List<Node> listNodes;
    private OnClickListener listener;
    private OnLongClickListener listenerLong;
    
    public NodeListAdaptor(Context context, List<Node> _listNodes) 
    {
        this.context = context;
        this.listNodes = _listNodes;
    }
    
    public NodeListAdaptor(Context context, List<Node> _listNodes, OnClickListener _listener) 
    {
        this.context = context;
        this.listNodes = _listNodes;
        this.listener = _listener;
    }
    
    public NodeListAdaptor(Context context, List<Node> _listNodes, OnClickListener _listener, OnLongClickListener _listenerLong) 
    {
        this.context = context;
        this.listNodes = _listNodes;
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
    	Node thisNode = listNodes.get(position);
    	if(thisNode.GetFullDetails() && thisNode.GetErrorState() == false)
    	{
    		return 1;
    	}
    	else
    	{
    		return 0;
    	}
    }
    
	public int getCount() {
		return listNodes.size();
	}

	public Object getItem(int position) 
	{
		return listNodes.get(position);
	}

	public long getItemId(int position) 
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Node thisNode = listNodes.get(position);
		if(this.getItemViewType(position) == 0)
		{
			if (convertView == null) 
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.node_listitem, null);
        	}
        	
        }
		else
    	{
			if (convertView == null) 
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.node_listitem_details, null);
        	}
    	}

        TextView DeviceNameTextView = (TextView) convertView.findViewById(R.id.NodeName);
        DeviceNameTextView.setText(thisNode.GetName());
        //DeviceNameTextView.setTypeface(Typeface.createFromAsset(((ViewNodes)context).getAssets(), "fonts/codeops_serif.ttf"));
        
        if(thisNode.isSelected())
        {
        	((View) convertView.findViewById(R.id.selectedIndicator)).setBackgroundColor(Color.rgb(247, 104, 26));
        	//((View) convertView.findViewById(R.id.selectedIndicator)).setVisibility(View.VISIBLE);
        	
        	/*Log.i("ID",Integer.toString(((View) convertView.findViewById(R.id.selectedIndicator)).getId()));
        	Log.i("height",Integer.toString(((View) convertView.findViewById(R.id.selectedIndicator)).getHeight()));
        	Log.i("width",Integer.toString(((View) convertView.findViewById(R.id.selectedIndicator)).getWidth()));
        	Log.i("parent",((View) convertView.findViewById(R.id.selectedIndicator)).getParent().toString());*/
        }
        else
        {
        	((View) convertView.findViewById(R.id.selectedIndicator)).setBackgroundColor(Color.rgb(244,242,230));
        	//((View) convertView.findViewById(R.id.selectedIndicator)).setVisibility(View.VISIBLE);
        }
        
        if(this.getItemViewType(position) == 1)
        {
        	((TextView) convertView.findViewById(R.id.Environment)).setText(thisNode.Details.get("chef_environment"));
        	((TextView) convertView.findViewById(R.id.Uptime)).setText(thisNode.Details.get("uptime"));
        	((TextView) convertView.findViewById(R.id.CPUCount)).setText(thisNode.Details.get("cpuCountString"));
        	((TextView) convertView.findViewById(R.id.RAMStats)).setText(thisNode.Details.get("ramStatsString"));
        	
        	if(thisNode.Details.get("run_list").length() > 1)
        	{
        		((TextView) convertView.findViewById(R.id.RunList)).setText(thisNode.Details.get("run_list"));
        	}
        	else
        	{
        		
        		((TextView) convertView.findViewById(R.id.RunList)).setText(R.string.RunListEmpty);
        	}
        	
        	String platform = thisNode.Details.get("platform");
        	if(platform.equals("centos"))
        	{
        		platform = "linux";
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setImageResource(R.drawable.linux);
        	}
        	else if(platform.equals("windows"))
        	{
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setImageResource(R.drawable.windows);
        	}
        	else if(platform.equals("debian"))
        	{
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setImageResource(R.drawable.debian);
        	}
        	else if(platform.equals("ubuntu"))
        	{
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setImageResource(R.drawable.ubuntu);
        	}
        	else if(platform.equals("fedora"))
        	{
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setImageResource(R.drawable.fedora);
        	}
        	else if(platform.equals("linux"))
        	{
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setImageResource(R.drawable.linux);
        	}
        	else
        	{
        		((TextView) convertView.findViewById(R.id.Platform)).setText(thisNode.Details.get("platform"));
        		((TextView) convertView.findViewById(R.id.Platform)).setVisibility(0);
        		((ImageView) convertView.findViewById(R.id.OSImage)).setVisibility(8);
        	}
        		
        	
        }
        else
        {
        	ProgressBar spinner = (ProgressBar) convertView.findViewById(R.id.MoreDetailsSpinner);
        	if(thisNode.isSpinnerVisible())
        	{
        		spinner.setVisibility(0);
        	}
        	else
        	{
        		spinner.setVisibility(4);
        	}
        	
        	if(thisNode.GetErrorState())
        	{
        		spinner.setVisibility(8);
        		((ImageView) convertView.findViewById(R.id.WarningIcon)).setVisibility(0);
        	}
        }
        
        convertView.setTag(position);
        //Log.i("NodeListAdaptor","Setting Tag to " + Integer.toString(position));
        //convertView.setOnClickListener(this);
        convertView.setLongClickable(true);
        convertView.setClickable(true);
        
        if(listener != null)
        	convertView.setOnClickListener((OnClickListener) listener);

        if(listenerLong != null)
        	convertView.setOnLongClickListener((OnLongClickListener) listenerLong);
        
        return convertView;
	}
}
