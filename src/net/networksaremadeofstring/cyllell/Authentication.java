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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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
-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEArm9bmHPkTlUYeUahvnNGK6bpPg3zmxdELzK77s3BuzZf82Al
dkkDc/hBzyCnUxzDmnkVfGNtFCLkdR/QVGN9L2JZxfa47xLHHDuNZVk4ezKJ2sMx
0BYydo5oIMoQnnDKke64VR1pkSDpbrUy1rYS91MDRycWU1126L2w85gt3ApF9oGc
i2+QVVhuP4pwXBHC0xqIRCvDwg6Py8bS6CX8h8rsM+7kMCGeD1C7/WweWj82cgVQ
OOsJ7PavcbpQnAbM4aj4lzmNvi4UXGTRiyBwtj7pRVA8FH+TYSc8xkKUD2m/CVmd
wW0U9H14D7OkShvqPKQ1vEkjDHXaDrR7sFdFkwIDAQABAoIBAGmEe0fxcv3fg/p4
u4bVfHETeqxLDD2ZCWnzqrN+S97PuaMjWgX/jNMuLcD5473m/HwJGvIvwuAXa1Ne
d0tVE3kCaYPTB3O3TDLL43CXVA9SNwnaYduaUdRduKd9FCiD2kYEvgTe0ek0m4CY
o7Q2V6q80JLOFjc/ppqLhOsV90fO/0z0ZtAmpdR8mzFoRIVaI3uog3Ps9/yTCfhs
9YL+QtPA8HGR/QakR6hO2fhRhAz/L3Hnd8CW8zzrHBt3WZYQDCQBdhPtyRQdX/vW
b0CQRQCv6gleIkE6x37lxCQX4wdQv1saD2LFlZFjFWq6h1IMQzs1YYeLXWjqAzFI
UYTX1sECgYEA2pIrjB6KrAeum21d0EE3Wd3Z1KiWoTBUcAHbWUNa3lu10GQcDSlp
tU0VWthLNaorjcnZTXaqhirtCHEilfYA7ZclA/XU4w7OHxIX2SWssJ1JFWbP+tSO
vL82OokF3wZHnD8SBROpmoBdsOTjvG6zGeMvTq8+B/wc9qKHYgam9QkCgYEAzE5U
v5MCdEjlGuzO9Cuz8aNfPP57gym+Ag/fwwA7kcYU7TLQV/BrNJdqZqbz78z1pEJB
E8fDHJggYENxpigDovpa32OpIqB0hKNl7YdEPTYXKApb6ifPGZKkWhMbwQAET5sh
iG0oIpzhJfOCvRQ85RGENwOpTr9mbCuoAa9qCLsCgYEA2PoQNuo+2WZK5MrePZXw
P8snqp+t4NgcipCdUvC1bVX3mKc43awF31BlaaiciOqkj/4YNXke8U/9vMqq/dmX
tES+Hz9Ulg8ledy5RfzLgQyy94b34lZOWHstd1B9Pph6UFagKeAKF3FlEO04UuBF
9eX9GYyH9N9HVWOlKB/YcBkCgYEAkLnYbREdxvQwX39rJyqiQMVDXPjE0+hI8jr+
fqt2h1AzWMgLMJBd0RaFyAvyd2fQhrhsUy/KNkJJhQehxVtJIDWLE/4MPTw+7gq1
1Kpim53qj3GvJNNocKwhgrFQksqJQz8YZQU2Tjalg3XZoklozbg32aTdVaxeSLgw
PmldiqsCgYBXSLaOktXQf8XsED0Ffk5QV8Gmdo2byeS16WLDf64mnY0FN9YqJ5go
6ppwEJ8TOP5N35dG3vigayoFU0del5Vn2M/TxJbU3RYq0z5bEIBZ4D7eSpSusG2r
mga6ogJKRo/GZ7vqNqX/BcKxM0D3wrHeew+OlWLyiefuF5nSVHPGkQ==
-----END RSA PRIVATE KEY-----
*/
	public String Sign2(String dataToSign) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		String modulus = "00:ae:6f:5b:98:73:e4:4e:55:18:79:46:a1:be:73:46:2b:a6:e9:3e:0d:f3:9b:17:44:2f:32:bb:ee:cd:c1:bb:36:5f:f3:60:25:76:49:03:73:f8:41:cf:20:a7:53:1c:c3:9a:79:15:7c:63:6d:14:22:e4:75:1f:d0:54:63:7d:2f:62:59:c5:f6:b8:ef:12:c7:1c:3b:8d:65:59:38:7b:32:89:da:c3:31:d0:16:32:76:8e:68:20:ca:10:9e:70:ca:91:ee:b8:55:1d:69:91:20:e9:6e:b5:32:d6:b6:12:f7:53:03:47:27:16:53:5d:76:e8:bd:b0:f3:98:2d:dc:0a:45:f6:81:9c:8b:6f:90:55:58:6e:3f:8a:70:5c:11:c2:d3:1a:88:44:2b:c3:c2:0e:8f:cb:c6:d2:e8:25:fc:87:ca:ec:33:ee:e4:30:21:9e:0f:50:bb:fd:6c:1e:5a:3f:36:72:05:50:38:eb:09:ec:f6:af:71:ba:50:9c:06:cc:e1:a8:f8:97:39:8d:be:2e:14:5c:64:d1:8b:20:70:b6:3e:e9:45:50:3c:14:7f:93:61:27:3c:c6:42:94:0f:69:bf:09:59:9d:c1:6d:14:f4:7d:78:0f:b3:a4:4a:1b:ea:3c:a4:35:bc:49:23:0c:75:da:0e:b4:7b:b0:57:45:93";
		String privateexponent = "69:84:7b:47:f1:72:fd:df:83:fa:78:bb:86:d5:7c:71:13:7a:ac:4b:0c:3d:99:09:69:f3:aa:b3:7e:4b:de:cf:b9:a3:23:5a:05:ff:8c:d3:2e:2d:c0:f9:e3:bd:e6:fc:7c:09:1a:f2:2f:c2:e0:17:6b:53:5e:77:4b:55:13:79:02:69:83:d3:07:73:b7:4c:32:cb:e3:70:97:54:0f:52:37:09:da:61:db:9a:51:d4:5d:b8:a7:7d:14:28:83:da:46:04:be:04:de:d1:e9:34:9b:80:98:a3:b4:36:57:aa:bc:d0:92:ce:16:37:3f:a6:9a:8b:84:eb:15:f7:47:ce:ff:4c:f4:66:d0:26:a5:d4:7c:9b:31:68:44:85:5a:23:7b:a8:83:73:ec:f7:fc:93:09:f8:6c:f5:82:fe:42:d3:c0:f0:71:91:fd:06:a4:47:a8:4e:d9:f8:51:84:0c:ff:2f:71:e7:77:c0:96:f3:3c:eb:1c:1b:77:59:96:10:0c:24:01:76:13:ed:c9:14:1d:5f:fb:d6:6f:40:90:45:00:af:ea:09:5e:22:41:3a:c7:7e:e5:c4:24:17:e3:07:50:bf:5b:1a:0f:62:c5:95:91:63:15:6a:ba:87:52:0c:43:3b:35:61:87:8b:5d:68:ea:03:31:48:51:84:d7:d6:c1";
		
		BigInteger BIModulus = new BigInteger(1, Base64.decode(modulus, 0));
		BigInteger BIExponent = new BigInteger(1, Base64.decode(privateexponent, 0));
		
		
		KeyFactory rSAKeyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = rSAKeyFactory.generatePrivate(new RSAPrivateKeySpec(BIModulus,BIExponent));
		
		/*Signature sig = Signature.getInstance("SHA1WithRSA");
		byte[] data = dataToSign.getBytes("UTF8");
		sig.initSign(privateKey);
		sig.update(data);
		byte[] signatureBytes = sig.sign();
		Log.i("test","Signature: " + signatureBytes.toString());*/
		
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] EncryptedStream = cipher.doFinal(dataToSign.getBytes("UTF8"));
		
		Log.i("test","Signature: " + EncryptedStream.toString());

		Log.i("test","Test: " + Base64.encodeToString(cipher.doFinal("test".getBytes("UTF8")),0));
		return Base64.encodeToString(EncryptedStream, 0);
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
		int charLocation = 0, AuthorizationIteration = 1;
		TimeStamp = "2011-07-11T01:21:38Z";
		//Temp override
		UserID = "Android4";
		conn.setRequestProperty("X-Ops-Sign","version=1.0");
		conn.setRequestProperty("X-Ops-Userid",UserID);
		conn.setRequestProperty("X-Ops-Timestamp",TimeStamp);
		
		
		//I have no idea what the 'body' is in this context
		//conn.addRequestProperty("X-Ops-Content-Hash",Disgesteriser.hash_string(""));
		conn.setRequestProperty("X-Ops-Content-Hash",Disgesteriser.hash_string(""));
		
		
		Log.i("test","Method:GET\n"+
										"Hashed Path:" + Disgesteriser.hash_string("/nodes") + "\n"+
										"X-Ops-Content-Hash:"+Disgesteriser.hash_string("")+"\n"+
										"X-Ops-Timestamp:"+TimeStamp+"\n"+
										"X-Ops-UserId:"+UserID);
		
		
		try {
			signed_canonicalize_request = Sign2("Method:GET\n"+
										"Hashed Path:" + Disgesteriser.hash_string("/nodes") + "\n"+
										"X-Ops-Content-Hash:"+Disgesteriser.hash_string("")+"\n"+
										"X-Ops-Timestamp:"+TimeStamp+"\n"+
										"X-Ops-UserId:"+UserID);
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
		}
		
		Log.i("test",signed_canonicalize_request.toString());
		//Header strings must be less than 60 chars so the signed request needs to be cut up into
		//chunks of 60 characters each one set in a Header X-Ops-Authorization-$N
		while(charLocation < signed_canonicalize_request.length())
		{
			String AuthString = "";
			int rubyLength = 1;
			
			while(rubyLength < 60 && charLocation < signed_canonicalize_request.length())
			{
				if(signed_canonicalize_request.charAt(charLocation) != '\n' && signed_canonicalize_request.charAt(charLocation) != '\r')
				{
					AuthString += signed_canonicalize_request.charAt(charLocation);
					
					rubyLength++;
				}
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