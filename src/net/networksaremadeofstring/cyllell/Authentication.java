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
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.apache.http.client.methods.HttpGet;
import android.util.Base64;
import android.util.Log;

public class Authentication 
{
	public HttpGet httpget = null;
	private String URL = "https://api.opscode.com";//This won't work but hey ho
	private String ClientName = "Cyllell"; //The name of the API client
	private String PrivateKey = null;
	
	public Authentication(String URL,String _ClientName, String _PrivateKey)
	{
		this.httpget = new HttpGet(URL); 
		this.ClientName = _ClientName;
		this.PrivateKey = _PrivateKey;
	}
	
	private String Sign3(String dataToSign) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException
	{
		 PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.decode(this.PrivateKey.getBytes(),0));
	     KeyFactory kf = KeyFactory.getInstance("RSA");
	     PrivateKey pk = kf.generatePrivate(spec);
	     Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		 cipher.init(Cipher.ENCRYPT_MODE, pk);

		 byte[] EncryptedStream = new byte[cipher.getOutputSize(dataToSign.length())];
		 try 
		 {
			cipher.doFinal(dataToSign.getBytes(),0,dataToSign.length(), EncryptedStream,0);
		 } 
		 catch (ShortBufferException e) 
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 return Base64.encodeToString(EncryptedStream, Base64.NO_WRAP);
	}
	
	/*
	 * When doing GET requests the Body is an empty string, no need to make people send it
	 */
	public void SetHeaders(String Path) 
	{
		this.SetHeaders(Path, "");
	}
	
	/*
	 * Used for adding the other Headers required by a Chef server (excluding X-Ops-Authorization-$N);
	 * 	X-Ops-Sign
	 * 	X-Ops-Userid
	 * 	X-Ops-Timestamp
	 *  X-Ops-Content-Hash
	 */
	public void SetHeaders(String Path, String Body)
	{
		Log.i("setHeaders","Setting Headers");
		Digester Disgesteriser = new Digester();
		String signed_canonicalize_request = null;
		int charLocation = 0, AuthorizationIteration = 1;
		
		Calendar c = Calendar.getInstance(); 
		String TimeStamp = c.get(Calendar.YEAR) + "-" + Integer.toString((c.get(Calendar.MONTH) + 1)) + "-" + c.get(Calendar.DAY_OF_MONTH) +
				"T" + c.get(Calendar.HOUR_OF_DAY) + ":0" + c.get(Calendar.MINUTE) +":" + c.get(Calendar.SECOND) +"Z";

		this.httpget.setHeader("Accept","application/json");
		this.httpget.setHeader("'Content-Type","application/json");
		
		this.httpget.setHeader("X-Ops-Sign","version=1.0");
		this.httpget.setHeader("X-Ops-Userid",this.ClientName);
		this.httpget.setHeader("X-Ops-Timestamp",TimeStamp);
		this.httpget.setHeader("X-Ops-Content-Hash",Disgesteriser.hash_string(Body));
		
		Log.i("SetHeaders","String to sign:\r\n\tMethod:GET\n"+
			"\tHashed Path:" + Disgesteriser.hash_string(Path) + "\n"+
			"\tX-Ops-Content-Hash:"+Disgesteriser.hash_string(Body)+"\n"+
			"\tX-Ops-Timestamp:"+TimeStamp+"\n"+
			"\tX-Ops-UserId:"+this.ClientName);
		
		
		try {
			signed_canonicalize_request = Sign3("Method:GET"+
												"\nHashed Path:" + Disgesteriser.hash_string(Path) + 
												"\nX-Ops-Content-Hash:"+Disgesteriser.hash_string(Body)+
												"\nX-Ops-Timestamp:"+TimeStamp+
												"\nX-Ops-UserId:"+this.ClientName);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Header strings must be less than 60 chars so the signed request needs to be cut up into
		//chunks of 60 characters each one set in a Header X-Ops-Authorization-$N
		while(charLocation < signed_canonicalize_request.length())
		{
			String AuthString = "";
			int rubyLength = 1;
			
			while(rubyLength < 61 && charLocation < signed_canonicalize_request.length())
			{
				if(signed_canonicalize_request.charAt(charLocation) != '\n' && signed_canonicalize_request.charAt(charLocation) != '\r')
				{
					AuthString += signed_canonicalize_request.charAt(charLocation);
					
					rubyLength++;
				}
				charLocation++;
			}
			this.httpget.setHeader("X-Ops-Authorization-"+Integer.toString(AuthorizationIteration),AuthString);
			
			Log.i("TAG","X-Ops-Authorization-"+Integer.toString(AuthorizationIteration) + " : " + AuthString);
			AuthorizationIteration++;
		}		
		return;
	}
}