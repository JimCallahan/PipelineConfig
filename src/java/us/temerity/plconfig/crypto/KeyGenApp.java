// $Id: KeyGenApp.java,v 1.2 2004/03/18 18:00:53 jim Exp $

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import com.sun.crypto.provider.SunJCE;


/*------------------------------------------------------------------------------------------*/
/*   K E Y    G E N   A P P                                                                 */
/*------------------------------------------------------------------------------------------*/

/**
 * A utility application which when run generates a Diffie-Hellman key pair, encodes them
 * as Java source code field definitions and writes them into the files 
 * <CODE>PublicKey.field</CODE>, <CODE>PrivateKey.field</CODE>, <CODE>PublicKey.java</CODE> 
 * and <CODE>PrivateKey.java</CODE>. <P> 
 * 
 * These Java field definition files are then used to build the classes which provide 
 * class encryption and decryption of the Pipeline byte-code.
 */ 
public
class KeyGenApp
{  
  /*----------------------------------------------------------------------------------------*/
  /*   M A I N                                                                              */
  /*----------------------------------------------------------------------------------------*/

  public static void 
  main
  (
   String[] args  
  )
  {
    try {
      KeyGenApp app = new KeyGenApp();
      app.run();
    }
    catch(Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct the application.
   */ 
  public
  KeyGenApp() 
  {} 



  /*----------------------------------------------------------------------------------------*/
  /*   O P S                                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Generate the keys. 
   */
  public void
  run()
    throws NoSuchAlgorithmException, 
           InvalidAlgorithmParameterException, 
           InvalidParameterSpecException,
           IOException
  {
    /* create the Diffie-Hellman public/private key pair */ 
    KeyPair pair = null;
    {
      AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
      paramGen.init(512);
      
      AlgorithmParameters params = paramGen.generateParameters();
      DHParameterSpec paramSpec = 
	(DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

      KeyPairGenerator pairGen = KeyPairGenerator.getInstance("DH");
      pairGen.initialize(paramSpec);
      pair = pairGen.generateKeyPair();
    }
    
    writeKey("CompanyPublic",  pair.getPublic().getEncoded(),  true);
    writeKey("CompanyPrivate", pair.getPrivate().getEncoded(), false);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   H E L P E R S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  private void 
  writeKey
  (
   String title, 
   byte[] key, 
   boolean writeClass
  ) 
    throws IOException
  { 
    String hexString = null;
    {
      StringBuffer buf = new StringBuffer();
      
      int wk;
      for(wk=0; wk<key.length; wk++) {
	if((wk % 8) == 0) 
	  buf.append("\n    ");
	buf.append("0x");
	String hex = Integer.toHexString(Byte.valueOf(key[wk]).intValue() + 128);
	if(hex.length() == 1) 
	  buf.append("0");
	buf.append(hex);
	
	if(wk < (key.length-1)) 
	  buf.append(", ");
      }

      hexString = buf.toString();
    }

    {
      String field = 
	("  private static final int[] sBytes = {" + 
	 hexString + "\n" +
	 "  };\n");

      FileWriter out = new FileWriter(title + "Key.field");
      out.write(field, 0, field.length());
      out.flush();
      out.close();
    }


    if(writeClass) {
      String sclass = 
	("package us.temerity.plconfig;\n" +
	 "\n" +
	 "public\n" +
	 "class " + title + "Key\n" + 
	 "{\n" + 
	 "  public static final int[] sBytes = {" +    
	 hexString + "\n" +
	 "  };\n" +
	 "}\n");

      FileWriter out = new FileWriter("../" + title + "Key.java");
      out.write(sclass, 0, sclass.length());
      out.flush();
      out.close();
    }
  }
  
}


