// $Id: ConfigApp.java,v 1.15 2004/08/27 22:18:20 jim Exp $

package us.temerity.plconfig;

import java.io.*;
import java.math.*;
import java.net.*;
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
 * The top-level class for the <B>plconfig</B>(1) program.
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
   * Construct the application with the given command-line arguments.
   * 
   * @param args 
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

    pProfile = new TreeMap<String,Object>();
    {
      pProfile.put("PlConfigVersion", PackageInfo.sVersion);

      pProfile.put("PipelineUser",  "pipeline");
      pProfile.put("PipelineGroup", "pipeline");

      pProfile.put("ToolsetDirectory", new File("/base/toolset"));
      pProfile.put("TemporaryDirectory", new File("/usr/tmp"));
      
      pProfile.put("MasterHostname", "localhost");
      pProfile.put("MasterPort",     53135);
      pProfile.put("NodeDirectory",  new File("/usr/share/pipeline"));
      
      pProfile.put("FileHostname",        "localhost");
      pProfile.put("FilePort",            53136);
      pProfile.put("NotifyControlPort",   53137);
      pProfile.put("NotifyMonitorPort",   53138);
      pProfile.put("ProductionDirectory", new File("/base/prod"));

      pProfile.put("QueueHostname",  "localhost");
      pProfile.put("QueuePort",      53139);
      pProfile.put("JobPort",        53140);
      pProfile.put("QueueDirectory", new File("/usr/share/pipeline"));
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                         */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the pipeline user name. 
   */ 
  public void 
  setPipelineUser
  (
   String user
  ) 
    throws IllegalConfigException
  {
    pProfile.put("PipelineUser", user);
  }


  /**
   * Get the root installation directory.
   */
  public File
  getRootDirectory()
  {
    return (File) pProfile.get("RootInstallDirectory");
  }

  /**
   * Set the root installation directory.
   */
  public void 
  setRootDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    File canon = null;
    try {
      canon = dir.getCanonicalFile();
    }
    catch(IOException ex) {
      throw new IllegalConfigException
	  ("Unable to determine the absolute canonical path to the root installation " +
	   "directory (" + dir + ")!");
    }

    if(!canon.exists()) 
      throw new IllegalConfigException
	  ("The root installation directory (" + dir + ") does not exist!");
    
    if(!canon.isDirectory())  
      throw new IllegalConfigException
	("The root installation directory (" + dir + ") is not a directory!");

    if(!canon.canWrite()) 
      throw new IllegalConfigException
	("No write permission for the root installation directory (" + dir + ")!");

    pProfile.put("RootInstallDirectory", canon);
  }


  /** 
   * Set the duration of an evaluation license.
   */
  public void 
  setEvaluationLicense()
    throws IllegalConfigException
  {
    try {
      long now = TimeService.getTime();
      pProfile.put("LicenseStart", new Date(now));
      pProfile.put("LicenseEnd" ,  new Date(now + 2592000000L));
    }
    catch(IOException ex) {
      throw new IllegalConfigException(ex.getMessage());
    }
  }

  /** 
   * Set the duration of an annual license.
   */
  public void 
  setAnnualLicense()
    throws IllegalConfigException
  {
    try {
      long now = TimeService.getTime();
      pProfile.put("LicenseStart", new Date(now));
      pProfile.put("LicenseEnd" ,  new Date(now + 30758400000L)); 
    }
    catch(IOException ex) {
      throw new IllegalConfigException(ex.getMessage());
    }
  }

  /** 
   * Set the duration of an perpetual license.
   */
  public void 
  setPerpetualLicense()
    throws IllegalConfigException
  {
    try {
      long now = TimeService.getTime();
      pProfile.put("LicenseStart", new Date(now));
      pProfile.put("LicenseEnd" ,  new Date(Long.MAX_VALUE)); 
    }
    catch(IOException ex) {
      throw new IllegalConfigException(ex.getMessage());
    }
  }

  
  /**
   * Read the contents of the host IDs file. 
   */
  public void 
  readHostIDs
  (
   File file
  ) 
    throws IllegalConfigException
  {
    try {
      TreeMap<String,BigInteger> hostIDs = new TreeMap<String,BigInteger>();

      FileReader reader = new FileReader(file);
      boolean done = false;
      int line = 1;
      while(true) {
	/* read a line */ 
	StringBuffer buf = new StringBuffer();
	while(true) {
	  int next = reader.read();
	  if(next == -1) {
	    done = true;
	    break;
	  }
	  
	  char c = (char) next;
	  if(c == '\n') 
	    break;
	  
	  buf.append(c);
	}
	
	if(done) 
	  break;

	String host      = null;
	BigInteger cksum = null;
	
	String[] fields = buf.toString().split(" ");
	int wk, cnt;
	for(wk=0, cnt=0; wk<fields.length; wk++) {
	  if(fields[wk].length() > 0) {
	    if(cnt == 0) {
	      host = fields[wk];
	    }
	    else if(cnt == 1) {
	      cksum = new BigInteger(fields[wk]);
	    }
	    
	    cnt++;
	  }
	}
	
	if((host != null) && (cksum != null)) {
	  hostIDs.put(host, cksum);
	}
	else {
	  throw new IllegalConfigException
	    ("Syntax Error: on line [" + line + "] of Host IDs file (" + file + ")!");
	}
	
	line++;
      }
      
      if(hostIDs.isEmpty()) 
	throw new IllegalConfigException
	  ("The Host IDs file (" + file + ") did not specify any hosts!");

      pProfile.put("HostIDs", hostIDs);       
    }
    catch(NumberFormatException ex) {
      throw new IllegalConfigException("Illegal Host IDs file (" + file + ")!" + 
				       "\n" + getFullMessage(ex));
    }
    catch(IOException ex) {
      throw new IllegalConfigException("Illegal Host IDs file (" + file + ")!" + 
				       "\n" + getFullMessage(ex));
    }
  }

  /**
   * Set the root user home directory.
   */
  public void 
  setHomeDirectory
  (
   File dir
  ) 
  {
    pProfile.put("HomeDirectory", dir);
  }

  /**
   * Set the root toolset directory.
   */
  public void 
  setToolsetDirectory
  (
   File dir
  ) 
  {
    pProfile.put("ToolsetDirectory", dir);
  }

  /**
   * Set the root temporary directory.
   */
  public void 
  setTemporaryDirectory
  (
   File dir
  ) 
  {
    pProfile.put("TemporaryDirectory", dir);
  }



  /**
   * Set the hostname which runs plmaster(1).
   */
  public void 
  setMasterHostname
  (
   String host
  ) 
  {
    pProfile.put("MasterHostname", host);
  }

  /**
   * Set the the network port listened to by plmaster(1).
   */
  public void 
  setMasterPort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The master port number (" + num + ") cannot be negative!");
       
    pProfile.put("MasterPort", num);
  }

  /**
   * Set the root node directory.
   */
  public void 
  setNodeDirectory
  (
   File dir
  ) 
  {
    pProfile.put("NodeDirectory", dir);
  }



  /**
   * Set the portname which runs plfilemgr(1).
   */
  public void 
  setFileHostname
  (
   String host
  ) 
  {
    pProfile.put("FileHostname", host);
  }

  /**
   * Set the network port listened to by plfilemgr(1).
   */
  public void 
  setFilePort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The file port number (" + num + ") cannot be negative!");
       
    pProfile.put("FilePort", num);
  }

  /**
   * Set the network port listened to by plnotify(1) for control requests.
   */
  public void 
  setNotifyControlPort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The notify control port number (" + num + ") cannot be negative!");
       
    pProfile.put("NotifyControlPort", num);
  }

  /**
   * Set the network port listened to by plnotify(1) for monitor requests.
   */
  public void 
  setNotifyMonitorPort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The notify monitor port number (" + num + ") cannot be negative!");
       
    pProfile.put("NotifyMonitorPort", num);
  }

  /**
   * Set the root production directory.
   */
  public void 
  setProdDirectory
  (
   File dir
  ) 
  {
    pProfile.put("ProductionDirectory", dir);
  }


  /**
   * Set the hostname which runs plqueue(1).
   */
  public void 
  setQueueHostname
  (
   String host
  ) 
  {
    pProfile.put("QueueHostname", host);
  }

  /**
   * Set the the network port listened to by plqueue(1).
   */
  public void 
  setQueuePort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The queue port number (" + num + ") cannot be negative!");
       
    pProfile.put("QueuePort", num);
  }

  /**
   * Set the the network port listened to by pljobmgr(1).
   */
  public void 
  setJobPort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The job port number (" + num + ") cannot be negative!");
       
    pProfile.put("JobPort", num);
  }

  /**
   * Set the root queue directory.
   */
  public void 
  setQueueDirectory
  (
   File dir
  ) 
  {
    pProfile.put("QueueDirectory", dir);
  }



  /**
   * Set additional Java class search path.
   */
  public void 
  setClassPath
  (
   String path
  ) 
  {
    pProfile.put("ClassPath", path);
  }
  
  /**
   * Set additional native library search path.
   */
  public void 
  setLibraryPath
  (
   String path
  ) 
  {
    pProfile.put("LibraryPath", path);
  }
  

  /**
   * Make the generated output files read-only.
   */
  public void 
  lockOutputFiles()
  {
    pLock = true;
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
      /* parse the command-line options */ 
      ConfigOptsParser parser = 
	new ConfigOptsParser(new StringReader(pPackedArgs));
      parser.setApp(this);
      parser.CommandLine();
      
      /* validate and complete the Pipeline configuration */ 
      validate();

      /* generate the output files */ 
      generate();

      success = true;
    }
    catch(ParseException ex) {
      handleParseException(ex);
    }
    catch(Exception ex) {
      System.out.print("ERROR: " + ex.getMessage() + "\n");
    }

    System.exit(success ? 0 : 1);
  }


  /*-- RUN HELPERS -------------------------------------------------------------------------*/

  /**
   * Validate and complete the Pipeline configuration. 
   */
  private void
  validate()
    throws ParseException, IllegalConfigException, SocketException
  {
    /* make sure we are the pipeline user/group */ 
    String pluser  = null;
    String plgroup = null;
    {
      pluser = (String) pProfile.get("PipelineUser");
      if((pluser == null) || (pluser.length() == 0)) 
	throw new IllegalConfigException
	  ("The pipeline user was illegal!");

      if(!System.getProperty("user.name").equals(pluser)) 
	throw new IllegalConfigException
	  ("The plconfig(1) tool must be run by the (" + pluser + ") user!");

      try {
	String args[] = new String[1];
	args[0] = "groups";

	Process proc = Runtime.getRuntime().exec(args);
	
	InputStream in = proc.getInputStream();
	byte buf[] = new byte[1024];
	int num = in.read(buf, 0, buf.length);
	in.close();
	  
	if(num == -1) 
	  throw new IOException();
	
	String line = new String(buf);
	String fields[] = line.split("[ \t\n]");
	int wk;
	for(wk=0; wk<fields.length; wk++) {
	  if(fields[wk].length() > 0) {
	    plgroup = fields[wk];
	    pProfile.put("PipelineGroup", plgroup);
	    break;
	  }
	}
	
	if(plgroup == null) 
	  throw new IOException();

	int exitCode = proc.waitFor();
	if(exitCode != 0)
	  throw new IOException();
      }
      catch(Exception ex) {
	throw new IllegalConfigException
	  ("Unable to determine the current group!");
      }
      assert(plgroup != null);

      String kind[] = { "User", "Group" };
      int wk;
      for(wk=0; wk<kind.length; wk++) {
	try {
	  String args[] = new String[2];
	  args[0] = "id";
	  args[1] = ("--" + kind[wk].toLowerCase());
	  
	  Process proc = Runtime.getRuntime().exec(args);
	  
	  InputStream in = proc.getInputStream();
	  byte buf[] = new byte[1024];
	  int num = in.read(buf, 0, buf.length);
	  in.close();
	  
	  if(num == -1) 
	    throw new IOException();

	  pProfile.put("Pipeline" + kind[wk] + "ID", new Integer(new String(buf, 0, num-1)));
	  
	  int exitCode = proc.waitFor();
	  if(exitCode != 0)
	    throw new IOException();
	}
	catch(Exception ex) {
	  throw new IllegalConfigException
	    ("Unable to determine the current " + kind[wk].toLowerCase() + " ID!");
	}
      }
    }

    /* root installation directory and "config" subdir */ 
    {
      File root = getRootDirectory();
      if(root == null) 
	throw new ParseException("The --root-dir option is required!");
    
      File config = new File(root, "config");
      if(config.exists()) {
	if(config.isDirectory()) {
	  if(!config.canWrite()) 
	    throw new ParseException
	      ("The (" + config + ") directory is not writable by the " + 
	       "(" + pluser + ") user and (" + plgroup  + ") group!");
	}
	else {
	  throw new IllegalConfigException
	    ("The path (" + config + ") is not a directory!");
	}
      }
      else {
	if(!config.mkdir())
	  throw new IllegalConfigException
	    ("Unable to create the (" + config + ") directory!");
      }
    }

    /* license period */ 
    if((pProfile.get("LicenseStart") == null) || (pProfile.get("LicenseEnd") == null)) 
      throw new ParseException("One of --evaluation, --annual or --perpetual is required!");

    /* host IDs */ 
    if(pProfile.get("HostIDs") == null) 
      throw new IllegalConfigException
	("No hosts where specified using the --host-ids option!"); 
      
    /* system information */ 
    {
      pProfile.put("JavaVendor",       System.getProperty("java.vm.vendor"));
      pProfile.put("JavaName",         System.getProperty("java.vm.name"));
      pProfile.put("JavaVersion",      System.getProperty("java.vm.version"));
      pProfile.put("JavaClassVersion", System.getProperty("java.class.version"));
      pProfile.put("JavaHome",         System.getProperty("java.home"));
      pProfile.put("BootClassPath",    System.getProperty("sun.boot.class.path"));

      pProfile.put("OS-Name",    System.getProperty("os.name"));
      pProfile.put("OS-Version", System.getProperty("os.version"));
      pProfile.put("OS-Arch",    System.getProperty("os.arch"));
    }

    /* home directory */ 
    if(pProfile.get("HomeDirectory") == null) {
      String home = System.getProperty("user.home"); 
      if(home != null) {
	if(home.endsWith(pluser))
	  pProfile.put("HomeDirectory", home.substring(0, home.length() - 9));
      }
      else {
	pProfile.put("HomeDirectory", "/home");
      }
    }

    /* toolset directory */ 
    {
      File dir = (File) pProfile.get("ToolsetDirectory");
      if(!dir.isAbsolute()) 
	throw new IllegalConfigException
	  ("The root toolset directory (" + dir + ") was not absolute!");
    }
      
    /* temporary directory */ 
    {
      File dir = (File) pProfile.get("TemporaryDirectory");
      if(!dir.isAbsolute()) 
	throw new IllegalConfigException
	  ("The temporary directory (" + dir + ") was not absolute!");
    }
      
    /* node directory */ 
    {
      File dir = (File) pProfile.get("NodeDirectory");
      if(!dir.isAbsolute()) 
	throw new IllegalConfigException
	  ("The root node directory (" + dir + ") was not absolute!");
    }
      
    /* production directory */ 
    {
      File dir = (File) pProfile.get("ProductionDirectory");
      if(!dir.isAbsolute()) 
	throw new IllegalConfigException
	  ("The root production directory (" + dir + ") was not absolute!");
    }

    /* support JAR files */ 
    {
      TreeSet<File> paths = new TreeSet<File>();
      {
	String jre = (String) pProfile.get("JavaHome");
	if(jre != null) 
	  paths.add(new File(jre, "lib/ext"));

	String cpath = (String) pProfile.get("ClassPath");
	if(cpath != null) {
	  String parts[] = cpath.split(":");
	  int wk;
	  for(wk=0; wk<parts.length; wk++) {
	    File dir = new File(parts[wk]);
	    if(dir.isDirectory())
	      paths.add(dir);
	  }
	}
      }
	
      {
	ArrayList<String> jars = new ArrayList<String>();
	jars.add("vecmath.jar");
	jars.add("j3daudio.jar");
	jars.add("j3dcore.jar");
	jars.add("j3dutils.jar");

	for(String jar : jars) {
	  boolean found = false;
	  for(File dir : paths) {
	    File path = new File(dir, jar);
	    if(path.isFile()) {
	      found = true;
	      break;
	    }
	  }

	  if(!found) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("Could not find the Java 3D JAR file (" + jar + ") in either the " + 
		       "JRE or any of the directories supplied with the --class-path " + 
		       "option.n\n" + 
		       "Directories Searched:\n");

	    for(File dir : paths) 
	      buf.append("  " + dir + "\n");

	    throw new IllegalConfigException(buf.toString());
	  }
	}
      }	

      {
	ArrayList<String> jars = new ArrayList<String>();
	jars.add("jai_core.jar");
	jars.add("jai_codec.jar");
	jars.add("mlibwrapper_jai.jar");
	jars.add("jai_imageio.jar");
	jars.add("clibwrapper_jiio.jar");

	for(String jar : jars) {
	  boolean found = false;
	  for(File dir : paths) {
	    File path = new File(dir, jar);
	    if(path.isFile()) {
	      found = true;
	      break;
	    }
	  }

	  if(!found) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("Could not find the Java Advanced Imaging JAR file (" + jar + ") in " +
		       "either the JRE or any of the directories supplied with the " + 
		       "--class-path option.\n\n" + 
		       "Directories Searched:\n");

	    for(File dir : paths) 
	      buf.append("  " + dir + "\n");

	    throw new IllegalConfigException(buf.toString());
	  }
	}
      }
    }

    /* support native libraries */ 
    {
      TreeSet<File> paths = new TreeSet<File>();
      {
	String jre = (String) pProfile.get("JavaHome");
	if(jre != null) 
	  paths.add(new File(jre, "lib/i386"));

	String lpath = (String) pProfile.get("LibraryPath");
	if(lpath == null) 
	  lpath = System.getenv("LD_LIBRARY_PATH");
	if(lpath != null) {
	  String parts[] = lpath.split(":");
	  int wk;
	  for(wk=0; wk<parts.length; wk++) {
	    File dir = new File(parts[wk]);
	    if(dir.isDirectory())
	      paths.add(dir);
	  }
	}
      }
	
      {
	ArrayList<String> jars = new ArrayList<String>();
	jars.add("libj3daudio.so");
	jars.add("libJ3DUtils.so");

	for(String jar : jars) {
	  boolean found = false;
	  for(File dir : paths) {
	    File path = new File(dir, jar);
	    if(path.isFile()) {
	      found = true;
	      break;
	    }
	  }

	  if(!found) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("Could not find the Java 3D native library (" + jar + ") in either " + 
		       "the JRE or any of the directories supplied with the --library-path " +
		       "option.\n\n" + 
		       "Directories Searched:\n");

	    for(File dir : paths) 
	      buf.append("  " + dir + "\n");

	    throw new IllegalConfigException(buf.toString());
	  }
	}
      }	

      {
	ArrayList<String> jars = new ArrayList<String>();
	jars.add("libmlib_jai.so");
	jars.add("libclib_jiio.so");

	for(String jar : jars) {
	  boolean found = false;
	  for(File dir : paths) {
	    File path = new File(dir, jar);
	    if(path.isFile()) {
	      found = true;
	      break;
	    }
	  }

	  if(!found) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("Could not find the Java Advanced Imaging native library (" + jar + 
		       ") in either the JRE or any of the directories supplied with the " +
		       "--library-path option.\n\n" + 
		       "Directories Searched:\n");
	    
	    for(File dir : paths) 
	      buf.append("  " + dir + "\n");

	    throw new IllegalConfigException(buf.toString());
	  }
	}
      }
    }

    /* make sure the network ports don't conflict */ 
    {
      ArrayList<String> titles = new ArrayList<String>();
      titles.add("MasterPort");
      titles.add("FilePort");
      titles.add("NotifyControlPort");
      titles.add("NotifyMonitorPort");
      titles.add("QueuePort");
      titles.add("JobPort");
      
      HashMap<Integer,String> names = new HashMap<Integer,String>();
      for(String title : titles) {
	if(names.containsKey(pProfile.get(title))) {
	  throw new IllegalConfigException
	    ("The network port (" + pProfile.get(title)  + ") cannot be used by both " +
	     "the " + title + " and the " + names.get(pProfile.get(title)) + "!");
	}
	
	names.put((Integer) pProfile.get(title), title);
      }    
    }
  }
  
  /**
   * Generate and write the output files.
   */
  private void 
  generate()
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
      BigInteger big = new BigInteger(Enigma.sData);
      KeyFactory factory = KeyFactory.getInstance("DH");
      X509EncodedKeySpec spec = new X509EncodedKeySpec(big.toByteArray());
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

    /* use the company public key and the customers private key to create a DES key */ 
    SecretKey key = null;
    {
      KeyAgreement keyAgree = KeyAgreement.getInstance("DH");
      keyAgree.init(pair.getPrivate());
      keyAgree.doPhase(publicKey, true);
      key = keyAgree.generateSecret("DES");
    }
    
    /* write the customers private key as: temerity-software.key */ 
    {
      /* convert the critical profile information into raw bytes */ 
      byte raw[] = null;
      {
	TreeMap<String,Object> table = new TreeMap<String,Object>();
	table.put("LicenseStart", pProfile.get("LicenseStart"));
	table.put("LicenseEnd",   pProfile.get("LicenseEnd"));
	table.put("HostIDs",      pProfile.get("HostIDs"));

	ByteArrayOutputStream bout = new ByteArrayOutputStream();

	ObjectOutputStream out = new ObjectOutputStream(bout);
	out.writeObject(table);
	out.flush();
	out.close();

	raw = bout.toByteArray();
      }

      /* encrypt the profile string */ 
      Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key);

      writeEncodedData(new File(getRootDirectory(), "config/temerity-software.key"), 
		       pair.getPrivate().getEncoded(), cipher.doFinal(raw));
    }

    /* write the customer profile as: customer-profile */ 
    {
      /* convert the profile table into raw bytes */ 
      byte raw[] = null;
      {
	ByteArrayOutputStream bout = new ByteArrayOutputStream();

	ObjectOutputStream out = new ObjectOutputStream(bout);
	out.writeObject(pProfile);
	out.flush();
	out.close();

	raw = bout.toByteArray();
      }

      /* encrypt the profile string */ 
      Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, key);

      /* write the customer public key and encrypted profile to: customer-profile */ 
      writeEncodedData(new File(getRootDirectory(), "config/customer-profile"), 
		       pair.getPublic().getEncoded(), cipher.doFinal(raw));
    }

    /* print the configuration parameters and write them to: plconfig-settings.txt */ 
    {
      String config = null;
      {
	StringBuffer buf = new StringBuffer();
	buf.append("Pipeline Configuration:\n");
	for(String title : pProfile.keySet()) {
	  buf.append("  " + title + " = ");
	  if(title.equals("HostIDs")) {
	    buf.append("\n");

	    TreeMap<String,BigInteger> hostIDs = 
	      (TreeMap<String,BigInteger>) pProfile.get("HostIDs");
	    for(String host : hostIDs.keySet()) {
	      BigInteger cksum = hostIDs.get(host);

	      buf.append("    " + host + " ");
	      
	      int wk;
	      for(wk=0; wk<(29 - host.length()); wk++) 
		buf.append(" ");
	      
	      buf.append(cksum);
	      buf.append("\n");    
	    }
	  }
	  else {
	    buf.append(pProfile.get(title) + "\n");
	  }
	}
	buf.append("\n");
	config = buf.toString();
      }

      System.out.print(config);

      File file = new File(getRootDirectory(), "config/plconfig-settings.txt");
      if(file.exists() && !file.canWrite()) 
	throw new IOException
	  ("Unable to write (" + file + ") because it has been locked!");

      FileWriter out = new FileWriter(file);
      out.write(config);
      out.write("Command Line:\n" +
		"  plconfig " + pPackedArgs + "\n\n");
      out.flush();
      out.close();

      if(pLock)
	file.setReadOnly();
    }
  }

  /**
   * Write the given key and encrypted profile information as encoded text file.
   */
  private void 
  writeEncodedData
  ( 
   File file, 
   byte[] key, 
   byte[] profile
  ) 
    throws IOException
  {
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
    out.write(keyText);
    out.write(profileText);
    out.flush();
    out.close();

    if(pLock)
      file.setReadOnly();
  }

  /**
   * Convert a an array of bytes into a String containing numeric digits.
   */
  private String
  encodeBytes
  (
   byte[] bytes
  ) 
  {
    BigInteger big = new BigInteger(bytes);
    return big.toString();
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
      ("USAGE:\n" +
       "  plconfig --root-dir=... --evaluation|--annual|--perpetual [options]\n" + 
       "\n" +
       "  plconfig --graphical\n" +
       "\n" + 
       "  plconfig --help\n" +
       "  plconfig --html-help\n" +
       "  plconfig --version\n" + 
       "  plconfig --release-date\n" + 
       "  plconfig --copyright\n" + 
       "\n" + 
       "OPTIONS:\n" +
       "  [--host-ids]\n" +
       "  [--home-dir=...][--toolset-dir=...][--temp-dir=...]\n" + 
       "  [--master-host=...][--master-port=...][node-dir=...]\n" + 
       "  [--file-host=...][--file-port=...][--prod-dir=...]\n" +
       "  [--notify-control-port][--notify-monitor-port]\n" + 
       "  [--class-path][--library-path]\n" +
       "  [--lock]\n" +
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
   * @param args 
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



  /*-- PARSING HELPERS ---------------------------------------------------------------------*/
  
  /** 
   * Generate a string containing both the exception message and stack trace. 
   * 
   * @param ex 
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
   * The customer profile table.
   */ 
  private TreeMap<String,Object>  pProfile;

  /**
   * Make the generated output files read-only?
   */
  private boolean pLock;
}


