// $Id: KeyGenApp.java,v 1.4 2004/03/22 23:38:52 jim Exp $

import java.io.*;
import java.math.*;
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
 * as Java source code field definitions in the file <CODE>CompanyKeyFields</CODE>.  These 
 * field definitions are incorporated into the <CODE>CryptoApp</CODE> class. <P>  
 * 
 * The public key is also encoded as a Java source code for the class 
 * <CODE>../Enigma.java</CODE> which becomes part of the <B>plconfig</B>(1) program.
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
      paramGen.init(1024);
      
      AlgorithmParameters params = paramGen.generateParameters();
      DHParameterSpec paramSpec = 
	(DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);

      KeyPairGenerator pairGen = KeyPairGenerator.getInstance("DH");
      pairGen.initialize(paramSpec);
      pair = pairGen.generateKeyPair();
    }

    {
      BigInteger privateKey = new BigInteger(pair.getPrivate().getEncoded());
      BigInteger publicKey  = new BigInteger(pair.getPublic().getEncoded());
      
      {
	String field = 
	  ("  private static final String sCompanyPrivateKey = \n" + 
	   "    \"" + privateKey + "\";\n" + 
	   "\n" +
	   "  private static final String sCompanyPublicKey = \n" + 
	   "    \"" + publicKey + "\";\n");
	
	FileWriter out = new FileWriter("CompanyKeyFields");
	out.write(field, 0, field.length());
	out.flush();
	out.close();
      }

      { 
	String sclass = 
	  ("package us.temerity.plconfig;\n" +
	   "\n" +
	   "public\n" +
	   "class Enigma\n" + 
	   "{\n" + 
	   "  public static final String sData = \n" + 
	   "    \"" + publicKey + "\";\n" +
	   "}\n");
	
	FileWriter out = new FileWriter("../Enigma.java");
	out.write(sclass, 0, sclass.length());
	out.flush();
	out.close();
      }
    }
  }
}


