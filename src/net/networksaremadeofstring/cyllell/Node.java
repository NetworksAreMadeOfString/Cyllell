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

import java.util.HashMap;

public class Node 
{
	//Basic Details
	private String Name;
	private String URI;
	private Boolean SpinnerVisible = false;
	private Boolean Error = false;
	
	//TELL USER ABOUT ALL THE THINGS
	private String RunList;
	private Boolean FullDetails = false;
	private String serial_number = "";
	
	//Being lazy
	public HashMap<String,String> Details;
	
	
	
	public Node(String _Name, String _URI) 
    {
            super();
            this.Name = _Name;
            this.URI = _URI;
    }
	
	public String GetName()
	{
		return this.Name;
	}
	
	public String GetURI()
	{
		return this.URI;
	}
	
	public Boolean GetFullDetails()
	{
		return this.FullDetails;
	}
	
	public void SetErrorState()
	{
		this.Error = true;
		this.FullDetails = false;
	}
	
	public Boolean GetErrorState()
	{
		return this.Error;
	}
	
	public void SetFullDetails(HashMap<String,String> CanonicalizedNode)
	{
		//Do lots of things!
    	//this.serial_number = CanonicalizedNode.get("serial_number");
    	this.Details = CanonicalizedNode;
    	
    	
    	
    	this.FullDetails = true;
	}
	
	public String GetRunList()
	{
		return this.URI;
	}
	
	public void SetRunList(String _RunList)
	{
		this.RunList = _RunList;
	}
	
	public void SetSpinnerVisible()
	{
		this.SpinnerVisible = true;
	}
	
	public Boolean isSpinnerVisible()
	{
		return this.SpinnerVisible;
	}

}
