// $Id: CustomerProfile.java,v 1.1 2004/03/17 21:50:31 jim Exp $

package us.temerity.plconfig;

import java.io.*;
import java.util.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import com.sun.crypto.provider.SunJCE;

/*------------------------------------------------------------------------------------------*/
/*   C U S T O M E R   P R O F I L E                                                        */
/*------------------------------------------------------------------------------------------*/

/**
 * 
 */ 
public
class CustomerProfile 
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct a new customer profile from the given file.
   * 
   * @param file [<B>in</B>]
   *   The file containing the customer profile.
   * 
   * @throws IOException 
   *   If unable to read or decipher the customer profile.
   */ 
  public
  CustomerProfile
  (
   File file
  )
    throws IOException
  {
    pProfile = new HashMap<String,Object>();

    try {
      read(file);
    }
    catch(Exception ex) {
      throw new IOException("Unable to read the customer profile from (" + file + ")!");
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                         */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Get the titles of the customer profile entries.
   */
  public Set<String>
  getTitles() 
  {
    return Collections.unmodifiableSet(pProfile.keySet());
  }

  /**
   * Get the customer profile entry with the given title.
   * 
   * @return
   *   The entry value or <CODE>null</CODE> if no entry with the given title exists.
   */
  public Object
  getEntry
  (
   String name
  )
  {
    return pProfile.get(name);
  }
  


  /*----------------------------------------------------------------------------------------*/
  /*   I / O                                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Read a customer profile from the given file.
   * 
   * @param file [<B>in</B>]
   *   The file containing the customer profile.
   * 
   */
  private void 
  read
  (
   File file 
  )
    throws NoSuchAlgorithmException, 
           InvalidAlgorithmParameterException, 
           InvalidParameterSpecException,
           InvalidKeySpecException, 
           InvalidKeyException, 
           NoSuchPaddingException, 
           BadPaddingException, 
           IllegalBlockSizeException, 
           ClassNotFoundException,
           IOException
  {  
    /* retrieve the customers public key and encrypted profile */ 
    PublicKey publicKey = null;
    String encrypted = null;
    {
      FileReader in = new FileReader(file);
      
      int keySize = 0;
      {
	char cs[] = new char[4];
	in.read(cs, 0, cs.length);
	keySize = Integer.valueOf(new String(cs));
      }

      {
	char cs[] = new char[keySize];
	in.read(cs, 0, cs.length);
	
	byte bytes[] = decodeBytes(new String(cs));
	
	KeyFactory factory = KeyFactory.getInstance("DH");
	X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
	publicKey = factory.generatePublic(spec);
      }

      {
	StringBuffer buf = new StringBuffer();
	char cs[] = new char[1024];
	while(true) {
	  int size = in.read(cs);
	  if(size == -1)
	    break;
	  
	  buf.append(cs, 0, size);
	}

	encrypted = buf.toString();
      }

      in.close();
    }

    /* decrypt the profile */ 
    HashMap<String,Object> profile = null;
    {
      /* retrieve the company's private key */ 
      PrivateKey privateKey = null;
      {
	byte bytes[] = new byte[TemerityPrivateKey.sKey.length];
	int wk;
	for(wk=0; wk<bytes.length; wk++) 
	  bytes[wk] = Integer.valueOf(TemerityPrivateKey.sKey[wk] - 128).byteValue();
	
	KeyFactory factory = KeyFactory.getInstance("DH");
	PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
	privateKey = factory.generatePrivate(spec);
      }
      
      /* use the customers public key and the companies private key to create a DES key */ 
      SecretKey key = null;
      {
	KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
	keyAgree.init(privateKey);
	keyAgree.doPhase(publicKey, true);
	key = keyAgree.generateSecret("DES");
      }
	
      /* decrpyt the profile text */ 
      Cipher decipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      decipher.init(Cipher.DECRYPT_MODE, key);
      byte raw[] = decipher.doFinal(decodeBytes(encrypted));

      /* convert the raw bytes into the profile table */ 
      {
	ByteArrayInputStream bin = new ByteArrayInputStream(raw);

	ObjectInputStream in = new ObjectInputStream(bin);
	pProfile = (HashMap<String,Object>) in.readObject();
	in.close();
      }
    }
  }

  /**
   * Convert a packed hexidecimal string into an array of bytes.
   */
  private byte[]
  decodeBytes
  (
   String text
  ) 
  {
    if((text.length() % 2) != 0) 
      throw new IllegalArgumentException("Encoded text length was not a mulitple of two!");

    char cs[]    = text.toCharArray();
    byte bytes[] = new byte[cs.length / 2];
   
    int wk;
    for(wk=0; wk<bytes.length; wk++) {
      int v = Integer.valueOf(new String(cs, wk*2, 2), 16) - 128;
      bytes[wk] = Integer.valueOf(v).byteValue();
    }

    return bytes;
  }



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The customer profile table.
   */ 
  private HashMap<String,Object>  pProfile;
}


