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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Base64;

public class Digester 
{
	public String hash_string(String str)  
	{
		MessageDigest md = null;
		byte[] sha1hash = new byte[40];
	    try 
	    {
			md = MessageDigest.getInstance("SHA-1");
		} 
	    catch (NoSuchAlgorithmException e) 
	    {
			// TODO I don't really know what do here - what device doesn't have SHA-1?
			e.printStackTrace();
		}
	    
	    try 
	    {
			md.update(str.getBytes("iso-8859-1"), 0, str.length());
		} catch (UnsupportedEncodingException e) 
		{
			// TODO Seriously? Sigh, OK I'll do something about this later
			e.printStackTrace();
		}
		
	    sha1hash = md.digest();
	    return Base64.encodeToString(sha1hash, 0);
	}
}
