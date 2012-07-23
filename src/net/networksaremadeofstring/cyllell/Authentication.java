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
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.util.Base64;

public class Authentication 
{
	private String ClientName = "Cyllell"; //The name of the API client
	private String PrivateKey = null;
	
	public Authentication(String _ClientName, String _PrivateKey)
	{
		this.ClientName = _ClientName;
		this.PrivateKey = _PrivateKey;		
	}
	
	private String SignHeaders(String dataToSign) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException
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

	private String GetTimeStamp()
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(new Date());
	}
	
	public List <NameValuePair> GetHeaders(String Path, String Body, String Method) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, NoSuchProviderException, URISyntaxException
	{
		List <NameValuePair> Headers = new ArrayList <NameValuePair>();
		
		//Log.i("setHeaders","Getting Headers");
		Digester Disgesteriser = new Digester();
		String signed_canonicalize_request = null;
		int charLocation = 0, AuthorizationIteration = 1;
		
		String TimeStamp = this.GetTimeStamp();
		Headers.add(new BasicNameValuePair("Accept","application/json"));
		Headers.add(new BasicNameValuePair("Content-Type","application/json"));
		
		Headers.add(new BasicNameValuePair("X-Ops-Sign","version=1.0"));
		Headers.add(new BasicNameValuePair("X-Chef-Version","0.10.4"));
		Headers.add(new BasicNameValuePair("X-Ops-UserId",this.ClientName));
		Headers.add(new BasicNameValuePair("X-Ops-Timestamp",TimeStamp));
		String HashedBody = Disgesteriser.hash_string(Body);
		/*if(HashedBody.length() > 60)
		{
			String tempBody = "";
			int rubyLength = 1;
			int bodycharLocation = 0;
			int HashedBodyLength = HashedBody.length();
			while(bodycharLocation < HashedBodyLength)
			{
				while(rubyLength < 61 && bodycharLocation < HashedBodyLength)
				{
					if(HashedBody.charAt(charLocation) != '\n' && HashedBody.charAt(charLocation) != '\r')
					{
						tempBody += HashedBody.charAt(bodycharLocation);
						rubyLength++;
					}
					bodycharLocation++;
				}
				tempBody += "\n";
			}
			
			HashedBody = tempBody;
		}
		Log.e("HashedBody",HashedBody);*/
		
		Headers.add(new BasicNameValuePair("X-Ops-Content-Hash",HashedBody));
		
		signed_canonicalize_request = SignHeaders("Method:"+Method+
				"\nHashed Path:" + Disgesteriser.hash_string(Path) +
				"\nX-Ops-Content-Hash:"+HashedBody+
				"\nX-Ops-Timestamp:"+TimeStamp+
				"\nX-Ops-UserId:"+this.ClientName);


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
			Headers.add(new BasicNameValuePair("X-Ops-Authorization-"+Integer.toString(AuthorizationIteration),AuthString));
			
			//Log.i("TAG","X-Ops-Authorization-"+Integer.toString(AuthorizationIteration) + " : " + AuthString);
			AuthorizationIteration++;
		}	

		return Headers;
	}
}