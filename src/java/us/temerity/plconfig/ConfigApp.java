// $Id: ConfigApp.java,v 1.1 2004/03/17 21:50:31 jim Exp $

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
/*   C O N F I G   A P P                                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * 
 */ 
public
class ConfigApp
{  
  /*----------------------------------------------------------------------------------------*/
  /*   M A I N                                                                              */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The top-level application method.
   */ 
  public static void 
  main
  (
   String[] args  
  )
  {
    ConfigApp app = new ConfigApp(args);
    app.run();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct an application with the given name and command-line arguments.
   * 
   * @param args [<B>in</B>]
   *   The command-line arguments.
   */ 
  public
  ConfigApp
  ( 
   String[] args
  )
  {
    pName = "plconfig";
    setPackedArgs(args);

    pHomeDir    = new File("/home");
    pMasterPort = 53135;
    pFilePort   = 53136;
  }


  
  /*----------------------------------------------------------------------------------------*/
  /*   R U N                                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The top-level method of the application. <P> 
   */
  public void
  run()
  {
    boolean success = false;
    try {
      ConfigOptsParser parser = 
	new ConfigOptsParser(new StringReader(pPackedArgs));
      
      parser.setApp(this);
      parser.CommandLine();
      
      /* validate and complete the Pipeline configuration */ 
      HashMap<String,Object> profile = config();

      /* generate the output files */ 
      generate(profile);

      success = true;
    }
    catch(ParseException ex) {
      handleParseException(ex);
    }
    catch(Exception ex) {
      ex.printStackTrace(System.out);
      System.out.print("ERROR: " + ex.getMessage() + "\n");
    }

    System.exit(success ? 0 : 1);
  }


  /*-- RUN HELPERS -------------------------------------------------------------------------*/

  /**
   * Validate and complete the Pipeline configuration. 
   */
  private HashMap<String,Object> 
  config()
    throws ParseException, IllegalConfigException
  {
    HashMap<String,Object> profile = new HashMap<String,Object>();

    if(pRootDir == null) 
      throw new ParseException("The --root-dir option is required!");
    profile.put("RootDirectory", pRootDir);

    if(pNodeDir == null) 
      throw new ParseException("The --node-dir option is required!");

    if(pProdDir == null) 
      throw new ParseException("The --prod-dir option is required!");

    if(pToolsetDir == null) 
      throw new ParseException("The --toolset-dir option is required!");

    if(pMasterHost == null) 
      throw new ParseException("The --master-host option is required!");
    
    if(pFileHost == null) 
      throw new ParseException("The --file-host option is required!");
    

    if(!System.getProperty("user.name").equals("pipeline")) 
      throw new IllegalConfigException
	("The plconfig(1) tool must be run by the \"pipeline\" user!");
      
    //System.getProperties().list(System.out);

    // ...

    return profile;
  }

  
  /**
   * Generate and write the output files.
   */
  private void 
  generate
  (
   HashMap<String,Object> profile
  ) 
    throws 
           NoSuchAlgorithmException, 
           InvalidAlgorithmParameterException, 
           InvalidParameterSpecException,
           InvalidKeySpecException, 
           InvalidKeyException, 
           NoSuchPaddingException, 
           BadPaddingException, 
           IllegalBlockSizeException, 
           IOException
  {  
    /* retrieve the company's public key */ 
    PublicKey publicKey = null;
    {
      byte bytes[] = new byte[TemerityPublicKey.sKey.length];
      int wk;
      for(wk=0; wk<bytes.length; wk++) 
	bytes[wk] = Integer.valueOf(TemerityPublicKey.sKey[wk] - 128).byteValue();

      KeyFactory factory = KeyFactory.getInstance("DH");
      X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
      publicKey = factory.generatePublic(spec);
    }

    /* generate a customer key pair */ 
    KeyPair pair = null;
    {
      DHParameterSpec paramSpec = ((DHPublicKey) publicKey).getParams();
      KeyPairGenerator pairGen = KeyPairGenerator.getInstance("DH");
      pairGen.initialize(paramSpec);
      pair = pairGen.generateKeyPair();
    }

    /* write the customers private key as: pipeline-license.key */ 
    writeLicense(pair.getPrivate().getEncoded());

    /* write the customer profile as: pipeline.profile */ 
    {
      /* use the company public key and the customers private key to create a DES key */ 
      SecretKey key = null;
      {
	KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
	keyAgree.init(pair.getPrivate());
	keyAgree.doPhase(publicKey, true);
	key = keyAgree.generateSecret("DES");
      }

      /* convert the profile table into raw bytes */ 
      byte raw[] = null;
      {
	ByteArrayOutputStream bout = new ByteArrayOutputStream();

	ObjectOutputStream out = new ObjectOutputStream(bout);
	out.writeObject(profile);
	out.flush();
	out.close();

	raw = bout.toByteArray();
      }

      /* encrypt the profile string */ 
      Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key);

      /* write the customer public key and encrypted profile to: pipeline.profile */ 
      writeProfile(pair.getPublic().getEncoded(), cipher.doFinal(raw));
    }

    // DEBUG -------------
    {
      CustomerProfile cp = new CustomerProfile(new File(pRootDir, "pipeline.profile"));
      System.out.print("CUSTOMER PROFILE:\n");
      for(String title : cp.getTitles()) 
	System.out.print("  " + title + " = " + cp.getEntry(title) + "\n");
    }
  }

  /**
   * Write the customers private key as: pipeline-license.key 
   */
  private void 
  writeLicense
  ( 
   byte[] key
  ) 
    throws IOException
  {
    File file = new File(pRootDir, "pipeline-license.key");
    if(file.exists() && !file.canWrite()) 
      throw new IOException
	("Unable to write (" + file + ") because it has been locked!");

    String text = null;
    {
      StringBuffer buf = new StringBuffer();

      String encoded = encodeBytes(key);

      String str = Integer.valueOf(encoded.length()).toString();
      int wk;
      for(wk=str.length(); wk<4; wk++) 
	buf.append("0");
      buf.append(str);
      
      buf.append(encoded);

      text = buf.toString();
    }
    
    FileWriter out = new FileWriter(file);
    out.write(text, 0, text.length());
    out.flush();
    out.close();
  }

  /**
   * Write the customer profile as: pipeline.profile 
   */
  private void 
  writeProfile
  ( 
   byte[] key, 
   byte[] profile
  ) 
    throws IOException
  {
    File file = new File(pRootDir, "pipeline.profile");
    if(file.exists() && !file.canWrite()) 
      throw new IOException
	("Unable to write (" + file + ") because it has been locked!");

    String keyText = null;
    {
      StringBuffer buf = new StringBuffer();

      String encoded = encodeBytes(key);

      String str = Integer.valueOf(encoded.length()).toString();
      int wk;
      for(wk=str.length(); wk<4; wk++) 
	buf.append("0");
      buf.append(str);
      
      buf.append(encoded);

      keyText = buf.toString();
    }

    String profileText = encodeBytes(profile);

    FileWriter out = new FileWriter(file);
    out.write(keyText, 0, keyText.length());
    out.write(profileText, 0, profileText.length());
    out.flush();
    out.close();
  }

  /**
   * Convert a an array of bytes into packed hexidecimal string.
   */
  private String
  encodeBytes
  (
   byte[] bytes
  ) 
  {
    StringBuffer buf = new StringBuffer();

    int wk;
    for(wk=0; wk<bytes.length; wk++) {
      String hex = Integer.toHexString(Byte.valueOf(bytes[wk]).intValue() + 128);
      if(hex.length() == 1) 
	buf.append("0");
      buf.append(hex.toUpperCase());
    }

    return buf.toString();
  }




  /*----------------------------------------------------------------------------------------*/
  /*   O P T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The implementation of the <CODE>--help</CODE> command-line option.
   */ 
  public void
  help()
  {
    System.out.print
      ("USAGE:  \n" +
       "  plconfig [options] \n" + 
       "\n" +
       "  plconfig --graphical \n" +
       "\n" + 
       "  plconfig --help \n" +
       "  plconfig --html-help \n" +
       "  plconfig --version \n" + 
       "  plconfig --release-date \n" + 
       "  plconfig --copyright \n" + 
       "\n" + 
       "GLOBAL OPTIONS: \n" +
       "  [--root-dir=...][--node-dir=...][--prod-dir=...][--toolset-dir=...]\n" + 
       "  [--master-host=...][--file-host=...] \n" + 
       "  [--master-port=...][--file-port=...] \n" +
       "  [--home-dir=...] \n" + 
       "\n" + 
       "\n" +  
       "Use \"plconfig --html-help\" to browse the full documentation.\n");
  }


  /**
   * The implementation of the <CODE>--html-help</CODE> command-line option.
   */ 
  public void
  htmlHelp()
  {
    try {
      boolean isRunning = false;
      {
	String args[] = {
	  "mozilla", 
	  "-remote", 
	  "ping()"
	};      
	
	Process proc = Runtime.getRuntime().exec(args);
	
	int exitCode = -1;
	try {
	  exitCode = proc.waitFor();
	}
	catch(InterruptedException ex) {
	  System.out.print(ex.getMessage() + "\n");
	}
	
	isRunning = (exitCode == 0);
      }

      if(isRunning) {
	String args[] = {
	  "mozilla", 
	  "-remote", 
	  ("openURL(" + 
	   "file://" + PackageInfo.sDocsDir + "/" + pName + ".html" + 
	   ", new-tab)")
	}; 
	
	Process proc = Runtime.getRuntime().exec(args);
	
	int exitCode = -1;
	try {
	  exitCode = proc.waitFor();
	}
	catch(InterruptedException ex) {
	  System.out.print(ex.getMessage() + "\n");
	}

	System.exit(exitCode);
      }
      else {
	String args[] = {
	  "mozilla", 
	  ("file://" + PackageInfo.sDocsDir + "/" + pName + ".html")
	};
	
	Process proc = Runtime.getRuntime().exec(args);

	int exitCode = -1;
	try {
	  exitCode = proc.waitFor();
	}
	catch(InterruptedException ex) {
	  System.out.print(ex.getMessage() + "\n");
	}

	System.exit(exitCode);
      }
    }
    catch(IOException ex) {
      System.out.print("ERROR: " + ex.getMessage() + "\n");
      System.exit(1);
    }
  }

  /**
   * The implementation of the <CODE>--version</CODE> command-line option.
   */ 
  public void
  version()
  {
    System.out.print(PackageInfo.sVersion + "\n");
  }

  /**
   * The implementation of the <CODE>--release-date</CODE> command-line option.
   */  
  public void
  releaseDate()
  {
    System.out.print(PackageInfo.sRelease + "\n");
  }
    
  /**
   * The implementation of the <CODE>--copyright</CODE> command-line option.
   */ 
  public void
  copyright()
  {
    System.out.print(PackageInfo.sCopyright + "\n");
  }
    


  /*----------------------------------------------------------------------------------------*/
  /*   P A R S I N G                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Concatentate all of the command-line arguments into a single <CODE>String</CODE>
   * suitable for parsing by the command-line parser of the application.
   * 
   * @param args [<B>in</B>]
   *   The command-line arguments.
   */ 
  public void
  setPackedArgs
  (
   String[] args 
  )
  {
    StringBuffer buf = new StringBuffer();
    
    int wk;
    for(wk=0; wk<args.length; wk++) {
      char chars[] = args[wk].toCharArray();

      int eqIdx = -1;
      boolean hasWs = false;

      int ck;
      for(ck=0; ck<chars.length; ck++) {
	if(chars[ck] == '=')
	  eqIdx = ck+1;
	else if((chars[ck] == ' ') || (chars[ck] == '\t')) {
	  hasWs = true;
	  break;
	}
      }

      if(hasWs && (eqIdx != -1) && (eqIdx < args[wk].length()))
	buf.append(args[wk].substring(0, eqIdx) + 
		   "\"" + args[wk].substring(eqIdx) + "\" ");
      else 
	buf.append(args[wk] + " ");
    }
    
    pPackedArgs = buf.toString();
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the root installation directory.
   */
  public void 
  setRootDir
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    if(!dir.exists()) 
      throw new IllegalConfigException
	  ("The root install directory (" + dir + ") does not exist!");
    
    if(!dir.isDirectory())  
      throw new IllegalConfigException
	("The root install directory (" + dir + ") is not a directory!");

    pRootDir = dir;
  }
  
  /**
   * Set the root node directory.
   */
  public void 
  setNodeDir
  (
   File dir
  ) 
  {
    pNodeDir = dir;
  }

  /**
   * Set the root production directory.
   */
  public void 
  setProdDir
  (
   File dir
  ) 
  {
    pProdDir = dir;
  }

  /**
   * Set the root toolset directory.
   */
  public void 
  setToolsetDir
  (
   File dir
  ) 
  {
    pToolsetDir = dir;
  }

  /**
   * Set the root user home directory.
   */
  public void 
  setHomeDir
  (
   File dir
  ) 
  {
    pHomeDir = dir;
  }

  /**
   * Set the  hostname which runs plmaster(1).
   */
  public void 
  setMasterHost
  (
   String hostname
  ) 
  {
    pMasterHost = hostname;
  }

  /**
   * Set the the network port listened to by plmaster(1).
   */
  public void 
  setMasterPort
  (
   int num
  ) 
  {
    pMasterPort = num;
  }

  /**
   * Set the portname which runs plfilemgr(1).
   */
  public void 
  setFileHost
  (
   String hostname
  ) 
  {
    pFileHost = hostname;
  }

  /**
   * Set the network port listened to by plfilemgr(1).
   */
  public void 
  setFilePort
  (
   int num
  ) 
  {
    pFilePort = num;
  }


  /*-- PARSING HELPERS ---------------------------------------------------------------------*/
  
  /** 
   * Generate a string containing both the exception message and stack trace. 
   * 
   * @param ex [<B>in</B>]
   *   The thrown exception.   
   */ 
  private String 
  getFullMessage
  (
   Throwable ex
  ) 
  {
    StringBuffer buf = new StringBuffer();
     
    if(ex.getMessage() != null) 
      buf.append(ex.getMessage() + "\n\n"); 	
    else if(ex.toString() != null) 
      buf.append(ex.toString() + "\n\n"); 	
      
    buf.append("Stack Trace:\n");
    StackTraceElement stack[] = ex.getStackTrace();
    int wk;
    for(wk=0; wk<stack.length; wk++) 
      buf.append("  " + stack[wk].toString() + "\n");
   
    return (buf.toString());
  }
  

  /**
   * Log a command-line argument parsing exception.
   */
  private void
  handleParseException
  (
   ParseException ex
  ) 
  {
    StringBuffer buf = new StringBuffer();
    buf.append("Illegal Args: ");

    try {
      /* build a non-duplicate set of expected token strings */ 
      TreeSet expected = new TreeSet();
      {
	int wk;
	for(wk=0; wk<ex.expectedTokenSequences.length; wk++) {
	  String str = ex.tokenImage[ex.expectedTokenSequences[wk][0]];
	  if(!str.equals("\"\\n\"") && !str.equals("<NL1>"))
	    expected.add(ex.tokenImage[ex.expectedTokenSequences[wk][0]]);
	}
      }

      
      /* message header */ 
      Token tok = ex.currentToken.next;
      String next = ex.tokenImage[tok.kind];
      if(next.length() > 0) {
	
	char[] ary = next.toCharArray();
	boolean hasKind  = (ary.length>0 && ary[0] == '<' && ary[ary.length-1] == '>');
	
	String value = toASCII(tok.image);
	boolean hasValue = (value.length() > 0);
	
	if(hasKind || hasValue) 
	  buf.append("found ");
	
	if(hasKind) 
	  buf.append(next + ", ");
	
	if(hasValue)
	  buf.append("\"" + value + "\", ");
      }
      
      buf.append("starting at arg " + ex.currentToken.next.beginLine + 
		 ", character " + ex.currentToken.next.beginColumn + ".\n");
      

      /* expected token list */ 
      Iterator iter = expected.iterator();
      if(expected.size()==1 && iter.hasNext()) {
	String str = (String) iter.next();
	if(str.equals("<EOF>")) 
	  buf.append("  Was NOT expecting any more arguments!");
	else 
	  buf.append("  Was expecting: " + str);
      }
      else {
	buf.append("  Was expecting one of:\n");
	while(iter.hasNext()) {
	  String str = (String) iter.next();
	  buf.append("    " + str);
	  if(iter.hasNext())
	    buf.append("\n");
	}
      }
    }
    catch (NullPointerException e) {
      buf.append(ex.getMessage());
    }

    /* log the message */ 
    System.out.print(buf.toString() + "\n");
  }
 
  /**
   * Convert non-printable characters in the given <CODE>String</CODE> into ASCII literals.
   */ 
  private String 
  toASCII
  (
   String str
  ) 
  {
    StringBuffer buf = new StringBuffer();

    char ch;
    for (int i = 0; i < str.length(); i++) {
      switch (str.charAt(i)) {
      case 0 :
	continue;
      case '\b':
	buf.append("\\b");
	continue;
      case '\t':
	buf.append("\\t");
	continue;
      case '\n':
	buf.append("");  /* newlines are used to seperate args... */ 
	continue;
      case '\f':
	buf.append("\\f");
	continue;
      case '\r':
	buf.append("\\r");
	continue;
      case '\"':
	buf.append("\\\"");
	continue;
      case '\'':
	buf.append("\\\'");
	continue;
      case '\\':
	buf.append("\\\\");
	continue;
      default:
	if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e) {
	  String s = "0000" + Integer.toString(ch, 16);
	  buf.append("\\u" + s.substring(s.length() - 4, s.length()));
	} else {
	  buf.append(ch);
	}
	continue;
      }
    }

    return (buf.toString());
  }

  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The name of the application.
   */ 
  private String pName;        

  /**
   * The single concatenated command-line argument string.
   */
  private String pPackedArgs;  



  /**
   * The root installation directory.
   */ 
  private File pRootDir;

  /**
   * The root node directory.
   */ 
  private File pNodeDir;

  /**
   * The root production directory.
   */ 
  private File pProdDir;

  /**
   * The root toolset directory.
   */ 
  private File pToolsetDir;

  /**
   * The root user home directory.
   */ 
  private File pHomeDir;

  /**
   * The hostname which runs plmaster(1).
   */ 
  private String pMasterHost;

  /**
   * The hostname which runs plfilemgr(1).
   */ 
  private String pFileHost;

  /**
   * The network port listened to by plmaster(1).
   */ 
  private int pMasterPort;

  /**
   * The network port listened to by plfilemgr(1).
   */ 
  private int pFilePort;
}


