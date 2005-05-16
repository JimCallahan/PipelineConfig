// $Id: ConfigApp.java,v 1.25 2005/05/16 22:24:00 jim Exp $

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

      pProfile.put("TemporaryDirectory", new File("/usr/tmp"));
      
      pProfile.put("MasterHostname", "localhost");
      pProfile.put("MasterPort",     53135);
      pProfile.put("MasterHeapSize", 536870912L);      
      pProfile.put("NodeDirectory",  new File("/usr/share/pipeline"));
      
      pProfile.put("FileHostname",        "localhost");
      pProfile.put("FilePort",            53136);
      pProfile.put("FileHeapSize",        536870912L);  
      pProfile.put("ProductionDirectory", new File("/base/prod"));

      pProfile.put("QueueHostname",  "localhost");
      pProfile.put("QueuePort",      53139);
      pProfile.put("QueueHeapSize",  536870912L);  
      pProfile.put("JobPort",        53140);
      pProfile.put("QueueDirectory", new File("/usr/share/pipeline"));

      pProfile.put("PluginHostname",  "localhost");
      pProfile.put("PluginPort",     53141);
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


  /*----------------------------------------------------------------------------------------*/

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

    pProfile.put("RootInstallDirectory", canon);
  }


  /*----------------------------------------------------------------------------------------*/

  /** 
   * Set the duration of an evaluation license.
   */
  public void 
  setEvaluationLicense()
    throws IllegalConfigException
  {
    try {
      long start = TimeService.getTime();
      pProfile.put("LicenseStart", new Date(start));
      pProfile.put("LicenseStartStamp", new Long(start));

      long end = start + 2592000000L;
      pProfile.put("LicenseEnd" , new Date(end));
      pProfile.put("LicenseEndStamp" , new Long(end));

      pProfile.put("LicenseType", "30-Day Evaluation");
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
      long start = TimeService.getTime();
      pProfile.put("LicenseStart", new Date(start));
      pProfile.put("LicenseStartStamp", new Long(start));

      long end = start + 30758400000L;
      pProfile.put("LicenseEnd" , new Date(end));
      pProfile.put("LicenseEndStamp" , new Long(end));

      pProfile.put("LicenseType", "Annual");
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
      pProfile.put("LicenseStart", new Date(0L));
      pProfile.put("LicenseStartStamp", new Long(0L));

      pProfile.put("LicenseEnd" , new Date(Long.MAX_VALUE));
      pProfile.put("LicenseEndStamp" , Long.MAX_VALUE);

      pProfile.put("LicenseType", "Perpetual");
    }
    catch(IOException ex) {
      throw new IllegalConfigException(ex.getMessage());
    }
  }

  
  /*----------------------------------------------------------------------------------------*/

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


  /*----------------------------------------------------------------------------------------*/

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


  /*----------------------------------------------------------------------------------------*/

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
   * Set the network port listened to by plmaster(1).
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
   * Set the maximum heap size of the Java VM running plmaster(1).
   */
  public void 
  setMasterHeapSize
  (
   long size
  ) 
    throws IllegalConfigException
  {
    validateHeapSize(size, "master");
    pProfile.put("MasterHeapSize", size); 
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


  /*----------------------------------------------------------------------------------------*/

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
   * Disable the used of plnotify(1) and caching by plmaster(1).
   */ 
  public void 
  disableCache() 
  {
    pProfile.put("EnableCaching", false);
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
   * Set the maximum heap size of the Java VM running plfilemgr(1).
   */
  public void 
  setFileHeapSize
  (
   long size
  ) 
    throws IllegalConfigException
  {
    validateHeapSize(size, "file");
    pProfile.put("FileHeapSize", size);
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


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the hostname which runs plqueuemgr(1).
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
   * Set the the network port listened to by plqueuemgr(1).
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
   * Set the maximum heap size of the Java VM running plqueuemgr(1).
   */
  public void 
  setQueueHeapSize
  (
   long size
  ) 
    throws IllegalConfigException
  {
    validateHeapSize(size, "queue");
    pProfile.put("QueueHeapSize", size);
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


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the hostname which runs plpluginmgr(1).
   */
  public void 
  setPluginHostname
  (
   String host
  ) 
  {
    pProfile.put("PluginHostname", host);
  }

  /**
   * Set the the network port listened to by plpluginmgr(1).
   */
  public void 
  setPluginPort
  (
   int num
  ) 
    throws IllegalConfigException
  {
    if(num < 0) 
      throw new IllegalConfigException
	("The plugin port number (" + num + ") cannot be negative!");
       
    pProfile.put("PluginPort", num);
  }


  /*----------------------------------------------------------------------------------------*/

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

      if(root.exists()) {
	if(!root.isDirectory()) {
	  throw new IllegalConfigException
	    ("The path (" + root + ") is not a directory!");
	}
      }
    }

    /* license period */ 
    if(pProfile.get("LicenseType") == null)
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
	jars.add("jogl.jar");

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
	    buf.append("Could not find the JOGL JAR file (" + jar + ") in either the " + 
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
	jars.add("libjogl.so");
	jars.add("libjogl_cg.so");

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
	    buf.append("Could not find the JOGL native library (" + jar + ") in either " + 
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
      titles.add("QueuePort");
      titles.add("JobPort");
      titles.add("PluginPort");
      
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
    File cdir = new File("plconfig");
    if(cdir.exists()) {
      if(!cdir.isDirectory()) 
	throw new IOException
	  ("Unable to write the configuration files to: " + cdir);
    }
    else {
      if(!cdir.mkdirs()) 
	throw new IOException
	  ("Unable to create the configuration file directory: " + cdir);
    }

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
    writeEncodedData(new File(cdir, "temerity-software.key"), 
		     pair.getPrivate().getEncoded(), null);

    /* write the customer profile as: site-profile */ 
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

      /* write the customer public key and encrypted profile to: site-profile */ 
      writeEncodedData(new File(cdir, "site-profile"), 
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

      File file = new File(cdir, "plconfig-settings.txt");
      if(file.exists() && !file.canWrite()) 
	throw new IOException
	  ("Unable to write (" + file + ") because it has been locked!");

      FileWriter out = new FileWriter(file);
      out.write(config);
      out.write("Command Line:\n" +
		"  plconfig " + pPackedArgs + "\n\n");
      out.flush();
      out.close();
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

    String profileText = null;
    if(profile != null)
      profileText = encodeBytes(profile);

    FileWriter out = new FileWriter(file);
    out.write(keyText);
    if(profileText != null) 
      out.write(profileText);
    out.flush();
    out.close();
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
       "  plconfig --help\n" +
       "  plconfig --html-help\n" +
       "  plconfig --version\n" + 
       "  plconfig --release-date\n" + 
       "  plconfig --copyright\n" + 
       "\n" + 
       "OPTIONS:\n" +
       "  [--pipeline-user=...] [--host-ids]\n" + 
       "  [--home-dir=...] [--temp-dir=...]\n" + 
       "  [--master-host=...] [--master-port=...] [--master-heap-size=...]\n" +
       "  [--file-host=...] [--file-port=...] [--file-heap-size=...]\n" +  
       "  [--queue-host=...] [--queue-port=...] [--queue-heap-size=...] [--job-port=...]\n" + 
       "  [--node-dir=...] [--prod-dir=...] [--queue-dir=...]\n" + 
       "  [--plugin-host=...] [--plugin-port=...]\n" + 
       "  [--class-path] [--library-path]\n" +
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
  protected void
  handleParseException
  (
   ParseException ex
  ) 
  {
    StringBuffer buf = new StringBuffer();
    buf.append("ERROR: ");

    try {
      /* build a non-duplicate set of expected token strings */ 
      TreeSet expected = new TreeSet();
      {
	int wk;
	for(wk=0; wk<ex.expectedTokenSequences.length; wk++) {
	  int kind = ex.expectedTokenSequences[wk][0];
	  String explain = tokenExplain(kind, true);
	  if(explain != null) 
	    expected.add(explain);
	}
      }
      
      /* message header */ 
      Token tok = ex.currentToken.next;
      String next = ex.tokenImage[tok.kind];
      if(next.length() > 0) {
	String value = toASCII(tok.image);
	boolean hasValue = (value.length() > 0);	
	String explain = tokenExplain(tok.kind, false);

	if(hasValue || (explain != null)) {
	  buf.append("Found ");
	  
	  if(explain != null)
	    buf.append(explain + ", ");
	
	  if(hasValue)
	    buf.append("\"" + value + "\" ");

	  buf.append("s");
	}
	else {
	  buf.append("S");
	}

	buf.append("tarting at character (" + ex.currentToken.next.beginColumn + ").\n");
      }

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
   * Generate an explanitory message for the non-literal token.
   */ 
  protected String
  tokenExplain
  (
   int kind,
   boolean printLiteral
  ) 
  {
    switch(kind) {
    case ConfigOptsParserConstants.EOF:
      return "EOF";

    case ConfigOptsParserConstants.UNKNOWN_OPTION:
      return "an unknown option";

    case ConfigOptsParserConstants.UNKNOWN_COMMAND:
      return "an unknown command";

    case ConfigOptsParserConstants.INTEGER:
      return "an integer";

    case ConfigOptsParserConstants.PATH_ARG:
      return "an file system path";

    case ConfigOptsParserConstants.SEARCH_PATH_ARG:
      return "an directory search path";

    case ConfigOptsParserConstants.HOSTNAME:
      return "a hostname";
    
    case ConfigOptsParserConstants.USERNAME:
      return "a user name";

    case ConfigOptsParserConstants.BYTE_SIZE:
      return "a byte size";

    case ConfigOptsParserConstants.KILO:
      return "\"K\" kilobytes";

    case ConfigOptsParserConstants.MEGA:
      return "\"M\" megabytes";

    case ConfigOptsParserConstants.GIGA:
      return "\"G\" gigabytes";

    default: 
      if(printLiteral) 
	return ConfigOptsParserConstants.tokenImage[kind];
      else 
	return null;
    }      
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
  /*   H E L P E R S                                                                        */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Validate a heap size value. 
   * 
   * @param size
   *   The heap size. 
   * 
   * @param title
   *   The title of the heap.
   */ 
  public void
  validateHeapSize
  (
   long size, 
   String title
  )
    throws IllegalConfigException
  {
    if(size < 0) 
      throw new IllegalConfigException
	("The " + title + " heap size (" + size + ") cannot be negative!");

    if(size < 2097152) 
      throw new IllegalConfigException
	("The " + title + " heap size (" + size + ") must be at least (2M)!");
      
    if((size % 1024L) != 0) 
      throw new IllegalConfigException
	("The " + title + " heap size (" + size + ") must be a multiple of (1024)!");
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

}


