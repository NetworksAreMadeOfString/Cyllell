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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import android.util.Base64;
import android.util.Log;

public class Authentication 
{
	public URLConnection conn = null;
	public URL chefURL = null;

	public Authentication(String URL)
	{
		try 
		{
			chefURL = new URL(URL);
		} 
		catch (MalformedURLException e) 
		{
			return;
		}  
		
		try 
		{
			conn = chefURL.openConnection();
		} 
		catch (IOException e) 
		{
			return;
		}  
	}
	

	/* This is (currently) a very poor attempt at 'signing' the signatue base string 
	 * for talking with Chef and creating the X-Ops-Authorization-$N headers
	 * 
	 * For now I'm going to pretend that this isn't really *really* crap
	 */
	public String Sign(String dataToSign)
	{
		KeyPairGenerator kpg = null;
		byte[] data = null;
		byte[] signatureBytes = null;
		Signature sig = null;
		
		try 
		{
			kpg = KeyPairGenerator.getInstance("RSA");
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    kpg.initialize(1024);
	    KeyPair keyPair = kpg.genKeyPair();

	   
		try 
		{
			data = dataToSign.getBytes("UTF8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try 
		{
			sig = Signature.getInstance("SHA1WithRSA");
		} 
		catch (NoSuchAlgorithmException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try 
	    {
			sig.initSign(keyPair.getPrivate());
		} 
	    catch (InvalidKeyException e) 
	    {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    try 
	    {
			sig.update(data);
		} 
	    catch (SignatureException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
		try 
		{
			signatureBytes = sig.sign();
		} 
		catch (SignatureException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*System.out.println("Signature Base64:" + Base64.encodeToString(signatureBytes, 0));
	    System.out.println("Signature       :" + signatureBytes.toString());
	    System.out.println("Signature       :" + Integer.toString(signatureBytes.length));
		*/
	    try {
			sig.initVerify(keyPair.getPublic());
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			sig.update(data);
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    /*try {
			System.out.println(sig.verify(signatureBytes));
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		return Base64.encodeToString(signatureBytes, 0);
	}

	/*
	 * Used for adding the other Headers required by a Chef server (excluding X-Ops-Authorization-$N);
	 * 	X-Ops-Sign
	 * 	X-Ops-Userid
	 * 	X-Ops-Timestamp
	 *  X-Ops-Content-Hash
	 */
	public URLConnection AddHeaders(String UserID)
	{
		java.util.Date CurrentTime = new java.util.Date();
		Digester Disgesteriser = new Digester();
		String signed_canonicalize_request = null;
		//TODO This is wrong - oh so very wrong!
		String TimeStamp = Integer.toString(CurrentTime.getYear()) +"-"+ Integer.toString(CurrentTime.getMonth()) +"-"+ Integer.toString(CurrentTime.getDay()) +"T"+ CurrentTime.getHours() +":"+CurrentTime.getMinutes()+":"+CurrentTime.getSeconds()+"Z";
		int charLocation = 1, AuthorizationIteration = 1;
		
		//Add the various Custom headers we need to send
		/*conn.addRequestProperty("X-Ops-Sign","version=1.0");
		conn.addRequestProperty("X-Ops-Userid",UserID);
		conn.addRequestProperty("X-Ops-Timestamp",TimeStamp);*/
		
		
		conn.setRequestProperty("X-Ops-Sign","version=1.0");
		conn.setRequestProperty("X-Ops-Userid",UserID);
		conn.setRequestProperty("X-Ops-Timestamp",TimeStamp);
		
		
		//I have no idea what the 'body' is in this context
		//conn.addRequestProperty("X-Ops-Content-Hash",Disgesteriser.hash_string(""));
		conn.setRequestProperty("X-Ops-Content-Hash",Disgesteriser.hash_string(""));
		
		/*Log.i("TAG","X-Ops-Sign : version=1.0");
		Log.i("TAG","X-Ops-Userid : " + UserID);
		Log.i("TAG","X-Ops-Timestamp : " + TimeStamp);
		Log.i("TAG","X-Ops-Content-Hash: NONE");*/
		
		signed_canonicalize_request = Sign("Method:GET\n"+
									"Hashed Path:" + Disgesteriser.hash_string("/organizations/namos/clients") + "\n"+
									"X-Ops-Content-Hash:"+Disgesteriser.hash_string("")+"\n"+
									"X-Ops-Timestamp:"+TimeStamp+"\n"+
									"X-Ops-UserId:"+UserID);
		
		//Header strings must be less than 60 chars so the signed request needs to be cut up into
		//chunks of 60 characters each one set in a Header X-Ops-Authorization-$N
		while(charLocation < signed_canonicalize_request.length())
		{
			String AuthString = "";
			int rubyLength = 1;
			
			while(rubyLength < 60 && charLocation < signed_canonicalize_request.length())
			{
				AuthString += signed_canonicalize_request.charAt(charLocation);
				
				rubyLength++;
				charLocation++;
			}
			//conn.addRequestProperty("X-Ops-Authorization-"+Integer.toString(AuthorizationIteration),AuthString);
			conn.setRequestProperty("X-Ops-Authorization-"+Integer.toString(AuthorizationIteration),AuthString);
			Log.i("TAG","X-Ops-Authorization-"+Integer.toString(AuthorizationIteration) + " : " + AuthString);
			AuthorizationIteration++;
		}
		
		return conn;
	}
}