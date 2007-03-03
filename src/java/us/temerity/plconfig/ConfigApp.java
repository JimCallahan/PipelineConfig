// $Id: ConfigApp.java,v 1.40 2007/03/03 22:19:21 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;
import us.temerity.plconfig.glue.*;
import us.temerity.plconfig.laf.LookAndFeelLoader;

import java.awt.*;
import java.awt.event.*;
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
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.synth.*;


/*------------------------------------------------------------------------------------------*/
/*   C O N F I G   A P P                                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * The top-level class for the <B>plconfig</B>(1) program.
 */ 
public
class ConfigApp
  extends BaseApp
  implements ActionListener
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
    super("plconfig");
    packageArguments(args);

    pProfile = new TreeMap<String,Object>();
    {
      pProfile.put("PlConfigVersion", PackageInfo.sVersion);

      pProfile.put("PipelineUser",  "pipeline");
      pProfile.put("PipelineGroup", "pipeline");

      pProfile.put("HomeDirectory",      "/home");
      pProfile.put("TemporaryDirectory", "/usr/tmp");
      
      pProfile.put("MasterPort",     53135);
      pProfile.put("MasterHeapSize", 536870912L);      
      pProfile.put("NodeDirectory",  "/usr/share/pipeline");
      
      pProfile.put("FilePort",            53136);
      pProfile.put("FileHeapSize",        134217728L);  
      pProfile.put("ProductionDirectory", "/base/prod");

      pProfile.put("QueuePort",      53139);
      pProfile.put("QueueHeapSize",  268435456L);  
      pProfile.put("JobPort",        53140);
      pProfile.put("QueueDirectory", "/usr/share/pipeline");

      pProfile.put("PluginPort",     53141);

      pProfile.put("MacClients",            false);
      pProfile.put("MacHomeDirectory",      "/Users");
      pProfile.put("MacTemporaryDirectory", "/var/tmp");

      pProfile.put("WinClients",            false);
      pProfile.put("WinHomeDirectory",      "C:/Documents and Settings");
      pProfile.put("WinTemporaryDirectory", "C:/WINDOWS/Temp");
      pProfile.put("WinJavaHome",           "C:/Program Files/Java/jdk1.5.0_11/jre");

      pProfile.put("LegacyPlugins", false);

      pProfile.put("JavaHome",         System.getProperty("java.home"));
      pProfile.put("JavaVendor",       System.getProperty("java.vm.vendor"));
      pProfile.put("JavaName",         System.getProperty("java.vm.name"));
      pProfile.put("JavaVersion",      System.getProperty("java.version"));
      pProfile.put("JavaClassVersion", System.getProperty("java.class.version"));

      pProfile.put("OS-Name",    System.getProperty("os.name"));
      pProfile.put("OS-Version", System.getProperty("os.version"));
      pProfile.put("OS-Arch",    System.getProperty("os.arch"));
    }

    pPanelLabels = new ArrayList<JLabel>();
    pPanels      = new ArrayList<JBaseConfigPanel>();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   G R A P H I C A L   M O D E                                                          */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Run in batch mode.
   */ 
  public void 
  setBatchMode()
  {
    pIsBatchMode = true;  
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Get the NTP based time only once.
   */ 
  public long
  getNetTime() 
    throws IllegalConfigException    
  {
    if(pNetTime == null) {
      try {
	pNetTime = TimeService.getTime();
      }
      catch(IOException ex) {
	throw new IllegalConfigException
	  ("Unable to contact a known NTP server to determine the current time!\n" + 
	   "\n" + 
	   "Your network may be misconfigured or a firewall may be preventing NTP " + 
	   "connections to Internet based time servers.  You will need to contect your " + 
	   "local System Administrator to resolve these issues before proceeding with " + 
	   "configuration of Pipeline.\n" +
	   "\n" +
	   ex.getMessage());
      }
    }
    
    return pNetTime;
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S I T E   P R O F I L E                                                              */
  /*----------------------------------------------------------------------------------------*/

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
    pProfile.put("RootInstallDirectory", 
		 validateCanonicalDir(dir, "Root Install Directory").getPath()); 
  }

  public void 
  setRootDirectory
  (
   String path 
  ) 
    throws IllegalConfigException
  {
    pProfile.put("RootInstallDirectory", 
		 validateCanonicalDir(path, "Root Install Directory").getPath()); 
  }

  /** 
   * Get the root installation directory.
   */ 
  public File
  getRootDirectory()
  {
    String dir = (String) pProfile.get("RootInstallDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }
  


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the default daemon server hostname.
   */
  public void 
  setServerHostname
  (
   String host
  ) 
  {
    pServerHostname = host; 
  }

  /** 
   * Get the default daemon server hostname.
   */ 
  public String
  getServerHostname()
  {
    return pServerHostname;
  }


  /*----------------------------------------------------------------------------------------*/

  /** 
   * Clear all license related settings. 
   */
  public void 
  clearLicense()
    throws IllegalConfigException
  {
    pProfile.remove("LicenseStart");
    pProfile.remove("LicenseStartStamp"); 
    pProfile.remove("LicenseEnd"); 
    pProfile.remove("LicenseEndStamp"); 
    pProfile.remove("LicenseType"); 
  }

  /** 
   * Set the duration of an evaluation license.
   */
  public void 
  setEvaluationLicense()
    throws IllegalConfigException
  {
    long start = getNetTime();
    pProfile.put("LicenseStart", new Date(start));
    pProfile.put("LicenseStartStamp", new Long(start));
    
    long end = start + 5184000000L;
    pProfile.put("LicenseEnd" , new Date(end));
    pProfile.put("LicenseEndStamp" , new Long(end));
    
    pProfile.put("LicenseType", "60-Day Evaluation");
  }

  /** 
   * Set the duration of a limited license which expires on a given date.
   */
  public void 
  setLimitedLicense
  (
   Date expiration
  )
    throws IllegalConfigException
  {
    long start = getNetTime();
    long end = expiration.getTime();
    if(start >= end) 
      end = start + 2592000000L;

    pProfile.put("LicenseStart", new Date(start));
    pProfile.put("LicenseStartStamp", new Long(start));
    
    pProfile.put("LicenseEnd" , new Date(end));
    pProfile.put("LicenseEndStamp" , new Long(end));
    
    pProfile.put("LicenseType", "Limited");
  }

  /** 
   * Set the duration of an perpetual license.
   */
  public void 
  setPerpetualLicense()
    throws IllegalConfigException
  {
    pProfile.put("LicenseStart", new Date(0L));
    pProfile.put("LicenseStartStamp", new Long(0L));
    
    pProfile.put("LicenseEnd" , new Date(Long.MAX_VALUE));
    pProfile.put("LicenseEndStamp" , Long.MAX_VALUE);
    
    pProfile.put("LicenseType", "Perpetual");
  }


  /**
   * Get the license type.
   */ 
  public String
  getLicenseType()
  {
    return (String) pProfile.get("LicenseType");
  }

  /**
   * Get the license start date.
   */ 
  public Date
  getLicenseStart()
  {
    return (Date) pProfile.get("LicenseStart");
  }

  /**
   * Get the license end date.
   */ 
  public Date
  getLicenseEnd()
  {
    return (Date) pProfile.get("LicenseEnd");
  }

  
  /*----------------------------------------------------------------------------------------*/

  /**
   * Remove all host IDs.
   */ 
  public void 
  clearHostIDs() 
  {
    pProfile.remove("HostIDs");
  }

  /**
   * Set the table of host IDs.
   */ 
  public void 
  setHostIDs
  (
   TreeMap<String,BigInteger> ids
  ) 
    throws IllegalConfigException
  {
    if((ids == null) || ids.isEmpty()) 
      throw new IllegalConfigException("No Host IDs were specified!");

    pProfile.put("HostIDs", ids);
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

      String output = null;
      {
	FileReader reader = new FileReader(file);
	
	StringBuffer buf = new StringBuffer();
	char cbuf[] = new char[8192];
	while(true) {
	  int num = reader.read(cbuf);
	  
	  if(num == -1)
	    break;

	  buf.append(cbuf, 0, num);
	}
	
	reader.close();

	output = buf.toString();
      }
      
      String lines[] = output.split("\n");
      int lk;
      for(lk=0; lk<lines.length; lk++) {
	String id = null;
	String parts[] = lines[lk].split(" ");
	int wk;
	for(wk=0; wk<parts.length; wk++) {
	  if(parts[wk].length() > 0) {
	    if(id == null) {
	      id = parts[wk];
	    }
	    else {
	      hostIDs.put(id, new BigInteger(parts[wk]));
	      id = null;
	    }
	  }
	}
      }
      
      if(hostIDs.isEmpty()) 
	throw new IllegalConfigException
	  ("The Host IDs file (" + file + ") did not specify any hosts!");

      pProfile.put("HostIDs", hostIDs);       
    }
    catch(Exception ex) {
      throw new IllegalConfigException
	("Illegal Host IDs file (" + file + ")!\n" + 
	 "  " + ex.getMessage());
    }
  }

  /**
   * Get the table of host IDs.
   */ 
  public TreeMap<String,BigInteger> 
  getHostIDs() 
  {
    return (TreeMap<String,BigInteger>) pProfile.get("HostIDs");
  }

  /**
   * Validate that all server hosts are included in the host IDs table.
   */ 
  public void 
  validateHostIDs() 
    throws IllegalConfigException
  {
    TreeMap<String,BigInteger> hostIDs = getHostIDs();
    if(hostIDs == null) 
      throw new IllegalConfigException
	("No Host IDs where specified!"); 

    {
      String host = getMasterHostname();
      if(!hostIDs.containsKey(host))
	throw new IllegalConfigException
	  ("No Host ID was provided for the Master Manager host (" + host + ")!");
    } 

    {
      String host = getFileHostname();
      if(!hostIDs.containsKey(host))
	throw new IllegalConfigException
	  ("No Host ID was provided for the File Manager host (" + host + ")!");
    } 

    {
      String host = getQueueHostname();
      if(!hostIDs.containsKey(host))
	throw new IllegalConfigException
	  ("No Host ID was provided for the Queue Manager host (" + host + ")!");
    } 
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the pipeline user ID. 
   */ 
  public void 
  setPipelineUserID
  (
   int uid
  ) 
    throws IllegalConfigException
  {
    pProfile.put("PipelineUserID", uid);
  }

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
   * Get the pipeline user name.
   */ 
  public String
  getPipelineUser()
  {
    return (String) pProfile.get("PipelineUser");
  }

  /**
   * Validate the Pipeline Admin User. 
   * 
   * @return 
   *   The valid user ID.
   */ 
  public int
  validatePipelineUser
  (
   String user
  ) 
    throws IllegalConfigException
  {
    if((user == null) || (user.length() == 0)) 
      throw new IllegalConfigException
	("The Pipeline Admin User was illegal!");

    int uid = -1;
    try {
      String args[] = { "id", "--user", user };
      Process proc = Runtime.getRuntime().exec(args);

      InputStream in = proc.getInputStream();
      byte buf[] = new byte[1024];
      int num = in.read(buf, 0, buf.length);
      in.close();
	  
      if(num == -1) 
	throw new IOException();

      uid = Integer.valueOf(new String(buf, 0, num-1));

      if(proc.waitFor() != 0) 
	throw new IOException();
    }
    catch(Exception ex) {
      throw new IllegalConfigException
	("No Pipeline Admin User named (" + user + ") exists!");
    }

    return uid;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the pipeline group ID. 
   */ 
  public void 
  setPipelineGroupID
  (
   int gid
  ) 
    throws IllegalConfigException
  {
    pProfile.put("PipelineGroupID", gid);
  }

  /**
   * Set the pipeline group name. 
   */ 
  public void 
  setPipelineGroup
  (
   String group
  ) 
    throws IllegalConfigException
  {
    pProfile.put("PipelineGroup", group);
  }

  /** 
   * Get the pipeline group name.
   */ 
  public String
  getPipelineGroup()
  {
    return (String) pProfile.get("PipelineGroup");
  }

  /**
   * Validate the Pipeline Admin Group. 
   * 
   * @return 
   *   The valid group ID.
   */ 
  public int
  validatePipelineGroup
  (
   String user, 
   String group
  ) 
    throws IllegalConfigException
  {
    if((user == null) || (user.length() == 0)) 
      throw new IllegalConfigException
	("The Pipeline Admin User was illegal!");

    if((group == null) || (group.length() == 0)) 
      throw new IllegalConfigException
	("The Pipeline Admin Group was illegal!");

    try {
      String args[] = { "id", "--name", "--group", user };       
      Process proc = Runtime.getRuntime().exec(args);
      
      InputStream in = proc.getInputStream();
      byte buf[] = new byte[1024];
      int num = in.read(buf, 0, buf.length);
      in.close();
      
      if(num == -1) 
	throw new IOException();

      String gname = new String(buf, 0, num-1);

      if(!gname.equals(group)) 
        throw new IllegalConfigException
          ("The supplied Pipeline Group (" + group + ") is not the primary group " + 
           "(" + gname + ") of the Pipeline Admin User (" + user + ")!"); 

      if(proc.waitFor() != 0)
	throw new IOException();
    }
    catch(IllegalConfigException ex) {
      throw ex;
    }
    catch(Exception ex) {
      throw new IllegalConfigException
	("Unable to determine if the Pipeline Admin Group (" + group + ") is valid!"); 
    }

    int gid = -1;
    try {
      String args[] = { "id", "--group", user };
      Process proc = Runtime.getRuntime().exec(args);

      InputStream in = proc.getInputStream();
      byte buf[] = new byte[1024];
      int num = in.read(buf, 0, buf.length);
      in.close();
	  
      if(num == -1) 
	throw new IOException();
      
      gid = Integer.valueOf(new String(buf, 0, num-1));

      if(proc.waitFor() != 0) 
	throw new IOException();
    }
    catch(Exception ex) {
      throw new IllegalConfigException
	("The Pipeline Admin User (" + user + ") does not belong to the group " + 
	 "(" + group + ")!");
    }

    return gid;    
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
    throws IllegalConfigException
  {
    pProfile.put("HomeDirectory", 
		 validateCanonicalDir(dir, "Home Directory").getPath()); 
  }

  /** 
   * Get the root user home directory.
   */ 
  public File
  getHomeDirectory()
  {
    String dir = (String) pProfile.get("HomeDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the root temporary directory.
   */
  public void 
  setTemporaryDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("TemporaryDirectory", 
		 validateCanonicalDir(dir, "Temporary Directory").getPath()); 
  }

  /** 
   * Get the root temporary directory.
   */ 
  public File
  getTemporaryDirectory()
  {
    String dir = (String) pProfile.get("TemporaryDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the Unix client Java home directory.
   */
  public void 
  setUnixJavaHome
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("UnixJavaHome", 
		 validateCanonicalDir(dir, "(Unix) Java Home Directory").getPath()); 
  }

  /** 
   * Get the root temporary directory.
   */ 
  public File
  getUnixJavaHome()
  {
    String dir = (String) pProfile.get("UnixJavaHome");
    if(dir != null) 
      return new File(dir);
    return null; 
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
   * Get the hostname which runs plmaster(1).
   */ 
  public String
  getMasterHostname()
  {
    String host = (String) pProfile.get("MasterHostname");
    if(host == null) 
      host = pServerHostname;
    return host;
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
   * Get the network port listened to by plmaster(1).
   */
  public int
  getMasterPort() 
  {
    return (Integer) pProfile.get("MasterPort");
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
   * Get the maximum heap size of the Java VM running plmaster(1).
   */
  public long
  getMasterHeapSize()
  {
    return (Long) pProfile.get("MasterHeapSize"); 
  }


  /**
   * Set the root node directory.
   */
  public void 
  setNodeDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("NodeDirectory", 
		 validateAbsolutePath(dir, "Node Directory").getPath()); 
  }

  /**
   * Get the root node directory.
   */ 
  public File
  getNodeDirectory() 
  {
    String dir = (String) pProfile.get("NodeDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
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
   * Get the hostname which runs plfilemgr(1).
   */ 
  public String
  getFileHostname()
  {
    String host = (String) pProfile.get("FileHostname");
    if(host == null) 
      host = pServerHostname;
    return host;
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
   * Get the network port listened to by plfilemgr(1).
   */
  public int
  getFilePort() 
  {
    return (Integer) pProfile.get("FilePort");
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
   * Get the maximum heap size of the Java VM running plfilemgr(1).
   */
  public long
  getFileHeapSize()
  {
    return (Long) pProfile.get("FileHeapSize"); 
  }


  /**
   * Set the root production directory.
   */
  public void 
  setProdDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("ProductionDirectory",
		 validateAbsolutePath(dir, "Production Directory").getPath()); 
  }

  /**
   * Get the root production directory.
   */ 
  public File
  getProdDirectory() 
  {
    String dir = (String) pProfile.get("ProductionDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
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
   * Get the hostname which runs plqueuemgr(1).
   */ 
  public String
  getQueueHostname()
  {
    String host = (String) pProfile.get("QueueHostname");
    if(host == null) 
      host = pServerHostname;
    return host;
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
   * Get the network port listened to by plqueuemgr(1).
   */
  public int
  getQueuePort() 
  {
    return (Integer) pProfile.get("QueuePort");
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
   * Get the maximum heap size of the Java VM running plqueuemgr(1).
   */
  public long
  getQueueHeapSize()
  {
    return (Long) pProfile.get("QueueHeapSize"); 
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
   * Get the network port listened to by pljobmgr(1).
   */
  public int
  getJobPort() 
  {
    return (Integer) pProfile.get("JobPort");
  }


  /**
   * Set the root queue directory.
   */
  public void 
  setQueueDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("QueueDirectory", 
		 validateAbsolutePath(dir, "Queue Directory").getPath());
  }
 
  /**
   * Get the root queue directory.
   */ 
  public File
  getQueueDirectory() 
  {
    String dir = (String) pProfile.get("QueueDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
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
   * Get the hostname which runs plpluginmgr(1).
   */ 
  public String
  getPluginHostname()
  {
    String host = (String) pProfile.get("PluginHostname");
    if(host == null) 
      host = pServerHostname;
    return host;
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

  /**
   * Get the network port listened to by plpluginmgr(1).
   */
  public int
  getPluginPort() 
  {
    return (Integer) pProfile.get("PluginPort");
  }

  

  /*----------------------------------------------------------------------------------------*/

  /**
   * Set whether to support Mac OS X clients.
   */
  public void 
  setMacClients
  (
   boolean tf
  ) 
  {
    pProfile.put("MacClients", tf);
  }
  
  /**
   * Get whether to support Mac OS X clients.
   */ 
  public boolean
  getMacClients()
  {
    return (Boolean) pProfile.get("MacClients");
  }



  /**
   * Set the Mac OS X root installation directory.
   */
  public void 
  setMacRootDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("MacRootInstallDirectory", 
		 validateAbsolutePath(dir, "(Mac OS X) Root Install Directory").getPath());
  }
  
  /**
   * Get the Mac OS X root installation directory.
   */ 
  public File
  getMacRootDirectory() 
  {
    String dir = (String) pProfile.get("MacRootInstallDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }


  /**
   * Set the Mac OS X root production directory.
   */
  public void 
  setMacProdDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("MacProductionDirectory", 
		 validateAbsolutePath(dir, "(Mac OS X) Production Directory").getPath());
  }

  /**
   * Get the Mac OS X root production directory.
   */ 
  public File
  getMacProdDirectory() 
  {
    String dir = (String) pProfile.get("MacProductionDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }


  /**
   * Set the Mac OS X root user home directory.
   */
  public void 
  setMacHomeDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("MacHomeDirectory", 
		 validateAbsolutePath(dir, "(Mac OS X) Home Directory").getPath());
  }

  /**
   * Get the Mac OS X root user home directory.
   */ 
  public File
  getMacHomeDirectory() 
  {
    String dir = (String) pProfile.get("MacHomeDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }


  /**
   * Set the Mac OS X root temporary directory.
   */
  public void 
  setMacTemporaryDirectory
  (
   File dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("MacTemporaryDirectory", 
		 validateAbsolutePath(dir, "(Mac OS X) Temporary Directory").getPath());
  }

  /**
   * Get the Mac OS X root temporary directory.
   */ 
  public File
  getMacTemporaryDirectory() 
  {
    String dir = (String) pProfile.get("MacTemporaryDirectory");
    if(dir != null) 
      return new File(dir);
    return null; 
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Set whether to support Windows XP clients.
   */
  public void 
  setWinClients
  (
   boolean tf
  ) 
  {
    pProfile.put("WinClients", tf);
  }
  
  /**
   * Get whether to support Windows XP clients.
   */ 
  public boolean
  getWinClients()
  {
    return (Boolean) pProfile.get("WinClients");
  }

  
  /**
   * Set the Windows default Domain.
   */ 
  public void 
  setWinDefaultDomain
  (
   String domain
  ) 
    throws IllegalConfigException
  {
    if((domain == null) || (domain.length() == 0)) 
      throw new IllegalConfigException
        ("No Default Windows Domain was specified!");
    pProfile.put("WinDefaultDomain", domain);
  }

  /** 
   * Get the Windows default Domain.
   */ 
  public String
  getWinDefaultDomain()
  {
    return (String) pProfile.get("WinDefaultDomain");
  }


  /**
   * Set the Windows XP root installation directory.
   */
  public void 
  setWinRootDirectory
  (
   String dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("WinRootInstallDirectory", 
		 validateWindowsPath(dir, "(Windows XP) Root Install Directory"));
  }
  
  /**
   * Get the Windows XP root installation directory.
   */ 
  public String
  getWinRootDirectory() 
  {
    return (String) pProfile.get("WinRootInstallDirectory");
  }


  /**
   * Set the Windows XP root production directory.
   */
  public void 
  setWinProdDirectory
  (
   String dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("WinProductionDirectory", 
		 validateWindowsPath(dir, "(Mac OS X) Production Directory"));
  }

  /**
   * Get the Windows XP root production directory.
   */ 
  public String
  getWinProdDirectory() 
  {
    return (String) pProfile.get("WinProductionDirectory");
  }


  /**
   * Set the Windows XP root user home directory.
   */
  public void 
  setWinHomeDirectory
  (
   String dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("WinHomeDirectory", 
		 validateWindowsPath(dir, "(Windows XP) Home Directory"));
  }

  /**
   * Get the Windows XP root user home directory.
   */ 
  public String
  getWinHomeDirectory() 
  {
    return (String) pProfile.get("WinHomeDirectory");
  }


  /**
   * Set the Windows XP root temporary directory.
   */
  public void 
  setWinTemporaryDirectory
  (
   String dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("WinTemporaryDirectory", 
		 validateWindowsPath(dir, "(Windows XP) Temporary Directory"));
  }

  /**
   * Get the Windows XP root temporary directory.
   */ 
  public String
  getWinTemporaryDirectory() 
  {
    return (String) pProfile.get("WinTemporaryDirectory");
  }


  /**
   * Set the Windows XP Java home directory.
   */
  public void 
  setWinJavaHome
  (
   String dir
  ) 
    throws IllegalConfigException
  {
    pProfile.put("WinJavaHome", 
		 validateWindowsPath(dir, "(Windows XP) Java Home Directory"));
  }

  /**
   * Get the Windows XP Java home directory.
   */ 
  public String
  getWinJavaHome() 
  {
    return (String) pProfile.get("WinJavaHome");
  }


  
  /*----------------------------------------------------------------------------------------*/
 
  /**
   * Set whether to include plugins created before Pipeline (v2.0.0).
   */ 
  public void 
  setLegacyPlugins
  (
   boolean tf
  ) 
  {
    pProfile.put("LegacyPlugins", tf);
  }

  /**
   * Get whether to include plugins created before Pipeline (v2.0.0).
   */ 
  public boolean
  getLegacyPlugins() 
  {
    return (Boolean) pProfile.get("LegacyPlugins");
  }


  /*----------------------------------------------------------------------------------------*/
 
  /**
   * Get the operating system name. 
   */
  public String
  getOsName() 
  {
    return (String) pProfile.get("OS-Name"); 
  }
  
  /**
   * Get the operating system version. 
   */
  public String
  getOsVersion() 
  {
    return (String) pProfile.get("OS-Version"); 
  }

  /**
   * Get the operating system architecture. 
   */
  public String
  getOsArch() 
  {
    return (String) pProfile.get("OS-Arch"); 
  }
  
  /**
   * Validate the operating system 
   */  
  public void 
  validateOs()
    throws IllegalConfigException
  {
    String name = getOsName();
    if((name == null) || !name.equals("Linux"))
      throw new IllegalConfigException
	("The Pipeline Configuration Tool must be run on a Linux host!");
  }
  

  /*----------------------------------------------------------------------------------------*/
 
  /**
   * Get the Java home directory.
   */ 
  public String
  getJavaHome() 
  {
    return (String) pProfile.get("JavaHome"); 
  }
  
  /**
   * Get the JRE vendor name. 
   */ 
  public String
  getJavaVendor() 
  {
    return (String) pProfile.get("JavaVendor"); 
  }
  
  /**
   * Get the JRE name name. 
   */ 
  public String
  getJavaName() 
  {
    return (String) pProfile.get("JavaName"); 
  } 
  
  /**
   * Get the JRE version version. 
   */ 
  public String
  getJavaVersion() 
  {
    return (String) pProfile.get("JavaVersion"); 
  }

  /**
   * Get the JRE version version. 
   */ 
  public String
  getJavaClassVersion() 
  {
    return (String) pProfile.get("JavaClassVersion"); 
  }
  
  /**
   * Validate the Java runtime. 
   */  
  public void 
  validateJavaRuntime()
    throws IllegalConfigException
  {
    String version = getJavaClassVersion();
    if((version == null) || !version.equals("49.0"))
      throw new IllegalConfigException
	("The Class Version for the current Java Runtime must be (49.0)!");
  }



  /*----------------------------------------------------------------------------------------*/
  /*   G E N E R A L   V A L I D A T O R S                                                  */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Validate that the given string contains an absolute Windows file system path.
   * 
   * @param path
   *   The file system path to test.
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The file system path.
   */ 
  public String
  validateWindowsPath
  (
   String path, 
   String title
  ) 
    throws IllegalConfigException
  {
    if((path == null) || (path.length() == 0))
      throw new IllegalConfigException
	("No path was specified for the " + title + "!");
    
    boolean isUNC = false;
    boolean isRoot = false;
    {
      char cs[] = path.toCharArray();
      if(cs.length >= 2) {
	isUNC = ((cs[0] == '/') && (cs[1] == '/'));
	isRoot = (Character.isLetter(cs[0]) && (cs[1] == ':'));
      }
    }

    if(!isUNC && !isRoot)
      throw new IllegalConfigException
	("The (" + path + ") specified for the " + title + " was not a valid absolute " +
	 "Windows file system path!\n\n" +
	 "The path must begin with a UNC server prefix like (//) or drive letter " + 
	 "such as (C:).");

    String parts[] = path.split("/");
    int wk; 
    for(wk=0; wk<parts.length; wk++) {
      if((parts[wk].length() == 0) && (!isUNC || (wk > 1)))
	throw new IllegalConfigException
	  ("The (" + path + ") specified for the " + title + " was not a valid absolute " +
	   "Windows file system path!\n\n" +
	   "The path cannot contain empty path components denoted by two or more " + 
	   "successive (/) charcters except at the start of a UNC path!");
    }

    return path; 
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Validate that the given file is an absolute file system path.
   * 
   * @param file
   *   The file to test.
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The file system path.
   */ 
  public File
  validateAbsolutePath
  (
   File file, 
   String title
  ) 
    throws IllegalConfigException
  {
    String path = null;
    if(file != null) 
      path = file.getPath();
      
    return validateAbsolutePath(path, title);
  }

  /**
   * Validate that the given string contains an absolute file system path.
   * 
   * @param path
   *   The file system path to test.
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The file system path.
   */ 
  public File
  validateAbsolutePath
  (
   String path, 
   String title
  ) 
    throws IllegalConfigException
  {
    if((path == null) || (path.length() == 0))
      throw new IllegalConfigException
	("No path was specified for the " + title + "!");

    File file = new File(path);
    if(!file.isAbsolute()) 
      throw new IllegalConfigException
	("The (" + path + ") specified for the " + title + " was not an absolute file " + 
	 "system path!");

    return file;
  }


  /*----------------------------------------------------------------------------------------*/

  /** 
   * Validate and return canonical form of the directory specified by the given file.
   * 
   * @param file
   *   The file to test.
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The canonical directory path.
   */
  public File
  validateCanonicalDir
  (
   File file, 
   String title
  ) 
    throws IllegalConfigException
  {
    String path = null;
    if(file != null) 
      path = file.getPath();
      
    return validateCanonicalDir(path, title);
  }

  /** 
   * Validate and return canonical form of the directory specified by the given path.
   * 
   * @param path
   *   The file system path to test.
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The canonical directory path.
   */
  public File
  validateCanonicalDir
  (
   String path, 
   String title
  ) 
    throws IllegalConfigException
  { 
    if((path == null) || (path.length() == 0))
      throw new IllegalConfigException
	("No path was specified for the " + title + "!");

    File canon = null;
    try {
      File file = new File(path);
      canon = file.getCanonicalFile();
    }
    catch(IOException ex) {
      throw new IllegalConfigException
	  ("Unable to determine the absolute canonical form of the path (" + path + ") " + 
	   "to the " + title + "!");
    }

    if(!canon.exists()) 
      throw new IllegalConfigException
	  ("The path (" + path + ") specified for the " + title + " does not exist!");
    
    if(!canon.isDirectory())  
      throw new IllegalConfigException
	("The path (" + path + ") specified for the " + title + " is not a directory!");

    return canon; 
  }


  /*----------------------------------------------------------------------------------------*/

  /** 
   * Validate the given hostname string.
   * 
   * @param host
   *   The hostname. 
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The validated hostname. 
   */
  public String 
  validateHostname
  (
   String host,
   String title
  )  
    throws IllegalConfigException
  { 
    if((host == null) || (host.length() == 0)) 
      throw new IllegalConfigException
	("No " + title + " was specified!");

    try {
      InetAddress addr = InetAddress.getByName(host);
      return addr.getCanonicalHostName(); 
    }
    catch(UnknownHostException ex) {
      throw new IllegalConfigException
	("Cannot resolve the hostname (" + host + ") specified for the " + title + "!");
    }
    catch(SecurityException ex) {
      throw new IllegalConfigException
	("Security restrictions have made it impossible to resolve hostnames!");
    }
  }

  
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Validate the network port.
   * 
   * @param port
   *   The port number. 
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The validated port number. 
   */
  public int
  validatePort
  (
   Integer port,
   String title
  ) 
    throws IllegalConfigException
  { 
    if(port == null) 
      throw new IllegalConfigException
	("No " + title + " was specified!");
    
    if(port < 49152) 
      throw new IllegalConfigException
	("The " + title + " number (" + port + ") cannot be less than 49152!\n\n" + 
	 "The Well Known Ports [0,1023] and Registered Ports [1024,49151] are reserved " +
	 "for use by system services and other software.");

    return port;
  }

  /** 
   * Validate the given network ports don't conflict.
   * 
   * @param portA
   *   The port number. 
   * 
   * @param titleA
   *   The name of the parameter to include in exception messages.
   *
   * @param portB
   *   The port number. 
   * 
   * @param titleB
   *   The name of the parameter to include in exception messages.
   */
  public void
  checkPortConflict
  (
   int portA,
   String titleA,
   int portB,
   String titleB
  ) 
    throws IllegalConfigException
  { 
    if(portA == portB)
      throw new IllegalConfigException
	("The " + titleA + " number (" + portA + ") is already being used as the " + 
	 titleB + "!");
  }


  /*----------------------------------------------------------------------------------------*/

  /** 
   * Validate the heap size.
   * 
   * @param size
   *   The number of bytes. 
   * 
   * @param title
   *   The name of the parameter to include in exception messages.
   * 
   * @return 
   *   The validated heap size. 
   */
  public long
  validateHeapSize
  (
   Long size,
   String title
  ) 
    throws IllegalConfigException
  {
    if(size == null) 
      throw new IllegalConfigException
	("No value was specified for the " + title + "!");
    
    if(size < 67108864L) 
      throw new IllegalConfigException
	("The " + title + " cannot be less than 32M!");
    
    return size;
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
      
      /* batch or graphical mode */ 
      if(pIsBatchMode) {
	validate();
	generate();
	System.exit(0);
      }
      else {
	SwingUtilities.invokeLater(new MainFrameTask(this));
      }
    }
    catch(ParseException ex) {
      handleParseException(ex);
    }
    catch(Exception ex) { 
      LogMgr.getInstance().log
	(LogMgr.Kind.Arg, LogMgr.Level.Severe,
	 ex.getMessage());
    }
  }


  /*-- RUN HELPERS -------------------------------------------------------------------------*/

  /**
   * Validate and complete the Pipeline configuration. 
   */
  private void
  validate()
    throws IllegalConfigException, SocketException
  {
    /* PREREQUITIES */ 

    /* make sure the admin user name and ID are valid */ 
    String pluser = null;
    {
      pluser = getPipelineUser();
      int uid = validatePipelineUser(pluser);
      setPipelineUserID(uid);
    }

    /* make sure the admin group name and ID are valid */ 
    String plgroup = null;
    {
      plgroup = getPipelineGroup();
      int gid = validatePipelineGroup(pluser, plgroup);
      setPipelineGroupID(gid);
    }

    /* validate the operating system */ 
    validateOs();

    /* validate the Java runtime */ 
    validateJavaRuntime(); 

    
    /* SERVER CONFIGURATION */ 

    /* license period */ 
    if(pProfile.get("LicenseType") == null)
      throw new IllegalConfigException
	("One of --evaluation, --limited or --perpetual is required!");

    /* root installation directory */ 
    {
      File dir = getRootDirectory();
      if(dir == null) 
	throw new IllegalConfigException("The --root-dir option is required!");

      validateCanonicalDir(dir, "Root Install Directory"); 
    }
    
    /* master manager */ 
    {
      String host = getMasterHostname();
      validateHostname(host, "Master Hostname"); 

      Integer port = getMasterPort();
      validatePort(port, "Master Port");

      Long size = getMasterHeapSize();
      validateHeapSize(size, "Master Heap Size");
      
      File dir = getNodeDirectory();
      validateAbsolutePath(dir, "Node Directory"); 
    }

    /* file manager */ 
    {
      String host = getFileHostname();
      validateHostname(host, "File Hostname"); 

      Integer port = getFilePort();
      validatePort(port, "File Port");
      checkPortConflict(port, "File Port", getMasterPort(), "Master Port");

      Long size = getFileHeapSize();
      validateHeapSize(size, "File Heap Size");
      
      File dir = getProdDirectory();
      validateAbsolutePath(dir, "Production Directory"); 
    }

    /* queue/job manager */ 
    {
      String host = getQueueHostname();
      validateHostname(host, "Queue Hostname"); 

      Integer qport = getQueuePort();
      validatePort(qport, "Queue Port");
      checkPortConflict(qport, "Queue Port", getMasterPort(), "Master Port");
      checkPortConflict(qport, "Queue Port", getFilePort(), "File Port");
     
      Integer jport = getJobPort();
      validatePort(jport, "Job Port");
      checkPortConflict(jport, "Job Port", getMasterPort(), "Master Port");
      checkPortConflict(jport, "Job Port", getFilePort(), "File Port");
      checkPortConflict(jport, "Job Port", qport, "Queue Port");

      Long size = getQueueHeapSize();
      validateHeapSize(size, "Queue Heap Size");
      
      File dir = getQueueDirectory();
      validateAbsolutePath(dir, "Queue Directory"); 
    }

    /* plugin manager */ 
    {
      String host = getPluginHostname();
      validateHostname(host, "Plugin Hostname"); 

      Integer port = getPluginPort();
      validatePort(port, "Plugin Port");
      checkPortConflict(port, "Plugin Port", getMasterPort(), "Master Port");
      checkPortConflict(port, "Plugin Port", getMasterPort(), "Master Port");
      checkPortConflict(port, "Plugin Port", getFilePort(), "File Port");
      checkPortConflict(port, "Plugin Port", getQueuePort(), "Queue Port");
      checkPortConflict(port, "Plugin Port", getJobPort(), "Job Port");
    }

    /* check for missing hostnames */ 
    {
      String masterHost  = (String) pProfile.get("MasterHostname"); 
      String fileHost    = (String) pProfile.get("FileHostname"); 
      String queueHost   = (String) pProfile.get("QueueHostname"); 
      String pluginHost  = (String) pProfile.get("PluginHostname"); 

      if((pServerHostname == null) && 
	 ((masterHost == null) || (fileHost == null) ||
	  (queueHost == null) || (pluginHost == null)))
	throw new IllegalConfigException
	  ("If the --server-host option is not specified, then all of the host options " + 
	   "(--master-host, --queue-host, --file-host and --plugin-host) must be specified.");
      
      if(masterHost == null) 
	pProfile.put("MasterHostname", pServerHostname);

      if(fileHost == null) 
	pProfile.put("FileHostname", pServerHostname);

      if(queueHost == null) 
	pProfile.put("QueueHostname", pServerHostname);

      if(pluginHost == null) 
	pProfile.put("PluginHostname", pServerHostname);
    }

    /* host IDs */
    validateHostIDs();

    /* home directory */ 
    {
      File dir = getHomeDirectory();

      if(dir == null) {
	String home = System.getProperty("user.home");  
	if(home != null) {
	  File hdir = new File(home);
	  if((hdir != null) && hdir.isDirectory()) 
	    dir = hdir.getParentFile();
	}
      }

      if(dir == null) 
	dir = new File("/home");

      setHomeDirectory(dir);
    }
            
    /* temporary directory */ 
    {
      File dir = getTemporaryDirectory(); 
      if(dir == null) 
	dir = new File("/var/tmp");

      setTemporaryDirectory(dir); 
    }

    /* Mac OS X options */ 
    if(getMacClients()) {
      File mroot = getMacRootDirectory(); 
      if(mroot == null)
	throw new IllegalConfigException
	  ("The --mac-root-dir option is required when the --mac-clients option is given!");

      File mprod = getMacProdDirectory(); 
      if(mprod == null)
	throw new IllegalConfigException
	  ("The --mac-prod-dir option is required when the --mac-clients option is given!");
    }

    /* Windows XP options */ 
    if(getWinClients()) {
      String domain = getWinDefaultDomain();
      if(domain == null) 
	throw new IllegalConfigException
	  ("The --win-domain option is required when the --win-clients option is given!");

      String wroot = getWinRootDirectory();
      if(wroot == null)
	throw new IllegalConfigException
	  ("The --win-root-dir option is required when the --win-clients option is given!");

      String wprod = getWinProdDirectory();
      if(wprod == null)
	throw new IllegalConfigException
	  ("The --win-prod-dir option is required when the --win-clients option is given!");
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

    /* print the configuration parameters */ 
    ArrayList<String> configText = new ArrayList<String>();
    {
      configText.add("--------------------------------------------------------------------");
      configText.add(" Pipeline Configuration:");
      configText.add("--------------------------------------------------------------------");
      for(String title : pProfile.keySet()) {
	if(title.equals("HostIDs")) {
	  configText.add("  " + title + " = ");
	  
	  TreeMap<String,BigInteger> hostIDs = 
	    (TreeMap<String,BigInteger>) pProfile.get("HostIDs");
	  for(String host : hostIDs.keySet()) {
	    BigInteger cksum = hostIDs.get(host);
	    
	    StringBuffer buf = new StringBuffer();
	    buf.append("    " + host + " ");
	    
	    int wk;
	    for(wk=0; wk<(29 - host.length()); wk++) 
	      buf.append(" ");
	    
	    buf.append(cksum); 
	    
	    configText.add(buf.toString());
	  }
	}
	else {
	  configText.add("  " + title + " = " + pProfile.get(title));
	}
      }

      StringBuffer buf = new StringBuffer();
      for(String line : configText) 
	buf.append(line + "\n");

      LogMgr.getInstance().log
	(LogMgr.Kind.Ops, LogMgr.Level.Info,
	 buf.toString());
    }

    /* write the configuration parameters as a GLUE format file */ 
    writeGlue(new File(cdir, "settings.glue"));
  }


  /**
   * Write the configuration parameters as a GLUE format file.
   */
  public void 
  writeGlue
  (
   File file
  ) 
    throws IOException
  {
    TreeMap<String,Object> profile = new TreeMap<String,Object>();
    for(String title : pProfile.keySet()) {
      Object value = pProfile.get(title);
      if(title.equals("HostIDs")) {
	TreeMap<String,BigInteger> hostIDs = (TreeMap<String,BigInteger>) value;
	TreeMap<String,String> hostStrIDs = new TreeMap<String,String>();
	for(String host : hostIDs.keySet()) 
	  hostStrIDs.put(host, hostIDs.get(host).toString());
	profile.put(title, hostStrIDs);
      }
      else if((value instanceof Date) || (value instanceof File)) {
	profile.put(title, value.toString());
      }
      else {
	profile.put(title, value);
      }
    }

    try {
      GlueEncoder ge = new GlueEncoderImpl("PipelineConfig", profile);
      String glue = ge.getText();

      FileWriter out = new FileWriter(file);
      out.write(glue);
      out.flush();
      out.close();
    }
    catch(Exception ex) {
      throw new IOException
	("Unable to generate (" + file + ") containing the GLUE format representation " + 
	 "of the configuration settings!");
    }
  }

  /**
   * Read the configuration parameters in from a GLUE format file.
   */ 
  public void 
  readReconfig
  (
   File file
  ) 
    throws IllegalConfigException 
  {
    TreeMap<String,Object> profile = null; 
    try {
      FileReader in = new FileReader(file);
      GlueDecoder gd = new GlueDecoderImpl(in);
      profile = (TreeMap<String,Object>) gd.getObject();
      in.close();
    }
    catch(Exception ex) {
      throw new IllegalConfigException
	("Unable to read (" + file + ") containing the GLUE format representation " + 
	 "of previous configuration settings!");
    }

    for(String title : profile.keySet()) {
      Object value = profile.get(title);

      if(title.equals("PipelineUser")) 
	setPipelineUser((String) value);
      else if(title.equals("PipelineGroup"))
	setPipelineGroup((String) value);

      else if(title.equals("LicenseType")) {
	String ltype = (String) value;
	if(ltype.equals("60-Day Evaluation")) 
	  setEvaluationLicense();
	else if(ltype.equals("Limited")) 
	  setLimitedLicense(new Date((Long) profile.get("LicenseEndStamp")));
	else if(ltype.equals("Perpetual")) 
	  setPerpetualLicense();
      }
	    
      else if(title.equals("RootInstallDirectory"))
	setRootDirectory(new File((String) value));

      else if(title.equals("MasterHostname"))
	setMasterHostname((String) value);
      else if(title.equals("MasterPort"))
	setMasterPort((Integer) value);
      else if(title.equals("MasterHeapSize"))
	setMasterHeapSize((Long) value);
      else if(title.equals("NodeDirectory"))
	setNodeDirectory(new File((String) value));

      else if(title.equals("FileHostname"))
	setFileHostname((String) value);
      else if(title.equals("FilePort"))
	setFilePort((Integer) value);
      else if(title.equals("FileHeapSize"))
	setFileHeapSize((Long) value);
      else if(title.equals("ProductionDirectory"))
	setProdDirectory(new File((String) value));

      else if(title.equals("QueueHostname"))
	setQueueHostname((String) value);
      else if(title.equals("QueuePort"))
	setQueuePort((Integer) value);
      else if(title.equals("QueueHeapSize"))
	setQueueHeapSize((Long) value);
      else if(title.equals("JobPort"))
	setJobPort((Integer) value);
      else if(title.equals("QueueDirectory"))
	setQueueDirectory(new File((String) value));

      else if(title.equals("PluginHostname"))
	setPluginHostname((String) value);
      else if(title.equals("PluginPort"))
	setPluginPort((Integer) value);
      else if(title.equals("LegacyPlugins"))
	setLegacyPlugins((Boolean) value); 
      
      else if(title.equals("HostIDs")) {
	TreeMap<String,String> hostStrs = (TreeMap<String,String>) value;
	TreeMap<String,BigInteger> hostIDs = new TreeMap<String,BigInteger>();
	for(String host : hostStrs.keySet()) 
	  hostIDs.put(host, new BigInteger(hostStrs.get(host)));
	setHostIDs(hostIDs);
      }

      else if(title.equals("HomeDirectory"))
	setHomeDirectory(new File((String) value));
      else if(title.equals("TemporaryDirectory"))
	setTemporaryDirectory(new File((String) value));
      else if(title.equals("UnixJavaHome"))
	setUnixJavaHome(new File((String) value));

      else if(title.equals("MacClients"))
	setMacClients((Boolean) value); 
      else if(title.equals("MacRootInstallDirectory"))
	setMacRootDirectory(new File((String) value));
      else if(title.equals("MacProductionDirectory"))
	setMacProdDirectory(new File((String) value));
      else if(title.equals("MacHomeDirectory"))
	setMacHomeDirectory(new File((String) value));
      else if(title.equals("MacTemporaryDirectory"))
	setMacTemporaryDirectory(new File((String) value));

      else if(title.equals("WinClients"))
	setWinClients((Boolean) value);
      else if(title.equals("WinDefaultDomain"))
	setWinDefaultDomain((String) value);
      else if(title.equals("WinRootInstallDirectory"))
	setWinRootDirectory((String) value);
      else if(title.equals("WinProductionDirectory"))
	setWinProdDirectory((String) value);
      else if(title.equals("WinHomeDirectory"))
	setWinHomeDirectory((String) value);
      else if(title.equals("WinTemporaryDirectory"))
	setWinTemporaryDirectory((String) value);
      else if(title.equals("WinJavaHome"))
	setWinJavaHome((String) value);
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
    LogMgr.getInstance().log
      (LogMgr.Kind.Ops, LogMgr.Level.Info,
       "USAGE:\n" +     
       "  plconfig [options]\n" +
       "\n" +
       "  plconfig --help\n" +
       "  plconfig --html-help\n" +
       "  plconfig --version\n" + 
       "  plconfig --release-date\n" + 
       "  plconfig --copyright\n" + 
       "\n" + 
       "OPTIONS:\n" +
       "  [--batch] [--evaluation | --limited=... | --perpetual]\n" +
       "  [--root-dir=...] [--server-host=...] [--host-ids=...]\n" + 
       "  [--pipeline-user=...] [--pipeline-group=...]\n" + 
       "  [--home-dir=...] [--temp-dir=...]\n" + 
       "  [--master-host=...] [--master-port=...] [--master-heap-size=...]\n" +
       "  [--file-host=...] [--file-port=...] [--file-heap-size=...]\n" +  
       "  [--queue-host=...] [--queue-port=...] [--queue-heap-size=...] [--job-port=...]\n" + 
       "  [--node-dir=...] [--prod-dir=...] [--queue-dir=...]\n" + 
       "  [--plugin-host=...] [--plugin-port=...]\n" + 
       "  [--legacy-plugins]\n" +
       "  [--mac-clients] [--mac-root-dir=...] [--mac-prod-dir=...]\n" + 
       "  [--mac-home-dir=...] [--mac-temp-dir=...]\n" + 
       "  [--win-clients] [--win-root-dir=...] [--win-prod-dir=...]\n" + 
       "  [--win-home-dir=...] [--win-temp-dir=...] [--win-java-home=...]\n" + 
       "\n" +  
       "Use \"plconfig --html-help\" to browse the full documentation.\n");
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

    case ConfigOptsParserConstants.WIN_PATH_ARG:
      return "an Windows file system path";

    case ConfigOptsParserConstants.SEARCH_PATH_ARG:
      return "an directory search path";

    case ConfigOptsParserConstants.HOSTNAME:
      return "a hostname";
    
    case ConfigOptsParserConstants.USERNAME:
      return "a user/group name";

    case ConfigOptsParserConstants.DOMAIN: 
      return "a Windows Domain name";

    case ConfigOptsParserConstants.BYTE_SIZE:
      return "a byte size";

    case ConfigOptsParserConstants.KILO:
      return "\"K\" kilobytes";

    case ConfigOptsParserConstants.MEGA:
      return "\"M\" megabytes";

    case ConfigOptsParserConstants.GIGA:
      return "\"G\" gigabytes";

    case ConfigOptsParserConstants.MONTH:
      return "an integer month [01-12]";

    case ConfigOptsParserConstants.DAY:
      return "an integer day of the month [01-31]";

    case ConfigOptsParserConstants.YEAR:
      return "an integer year [2006+]";

    default: 
      if(printLiteral) 
	return ConfigOptsParserConstants.tokenImage[kind];
      else 
	return null;
    }      
  }



  /*----------------------------------------------------------------------------------------*/
  /*   U S E R   I N T E R F A C E                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Update to the selected panel. 
   */ 
  private void
  updateSelectedPanel()
  {
    pPrevButton.setEnabled(pPanelIdx > 0);
    pNextButton.setEnabled(pPanelIdx < pPanelLabels.size());

    pNextLabel.setText((pPanelIdx < (pPanelLabels.size()-1)) ? "Next" : "Finish");

    if(pPanelIdx >= 0) {
      if(pPanelIdx < pPanelLabels.size()) 
	pPanelLabels.get(pPanelIdx).setForeground(Color.yellow);
      
      if(pPanelIdx < pPanels.size()) {
	JBaseConfigPanel panel = pPanels.get(pPanelIdx);
	panel.updatePanel(); 

	CardLayout layout = (CardLayout) pCardPanel.getLayout();
	layout.show(pCardPanel, panel.getPanelTitle());
      }
    }
  }

  /**
   * Show the error dialog for the given exception.
   */ 
  public void 
  showErrorDialog
  (
   Exception ex
  ) 
  {
    pErrorDialog.setMessage(ex);
    pErrorDialog.setVisible(true);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   L I S T E N E R S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /*-- ACTION LISTENER METHODS -------------------------------------------------------------*/

  /** 
   * Invoked when an action occurs. 
   */ 
  public void 
  actionPerformed
  (
   ActionEvent e
  ) 
  {
    String cmd = e.getActionCommand();
    if(cmd.equals("prev-panel")) 
      doPrevPanel();
    else if(cmd.equals("next-panel")) 
      doNextPanel();
    else if(cmd.equals("cancel"))  
      doQuit();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Change to previous wizard panel.
   */ 
  public void 
  doPrevPanel()
  {
    if(pPanelIdx >= 0) {
      pPanelIdx--;
    
      if(pPanelIdx < pPanelLabels.size()) 
	pPanelLabels.get(pPanelIdx).setIcon(pNoneIcon);

      if((pPanelIdx+1) < pPanelLabels.size()) 
	pPanelLabels.get(pPanelIdx+1).setForeground(Color.white);
    }

    updateSelectedPanel();
  }

  /**
   * Advance to next wizard panel.
   */ 
  public void 
  doNextPanel()
  {
    if(pPanelIdx < pPanelLabels.size()) {
      if(pPanelIdx < pPanels.size()) {
	try {
	  JBaseConfigPanel panel = pPanels.get(pPanelIdx);
	  panel.updateProfile();
	}
	catch(IllegalConfigException ex) {
	  showErrorDialog(ex);
	  return;	  
	}
      }

      if(pPanelIdx >= 0) {
	pPanelLabels.get(pPanelIdx).setIcon(pCheckIcon);
	pPanelLabels.get(pPanelIdx).setForeground(Color.white);
      }

      pPanelIdx++;
    }

    updateSelectedPanel();

    if(pPanelIdx == pPanelLabels.size()) 
      doFinish();
  }

  /**
   * Perform a final validation and generate the output files.
   */ 
  public void 
  doFinish()
  {
    try {
      validate();
      generate();
      System.exit(0);
    }
    catch(Exception ex) {
      showErrorDialog(ex);
      doPrevPanel();
    }
  }

  /**
   * Exit configuration tool.
   */ 
  public void 
  doQuit()
  {
    System.exit(0);
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
  /*   I N T E R N A L   C L A S S E S                                                      */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Initialize the user interface components. 
   */ 
  private
  class MainFrameTask
    extends Thread
  { 
    MainFrameTask
    (
     ConfigApp app
    ) 
    { 
      super("ConfigApp:MainFrameTask");
      pApp = app;
    }

    public void 
    run() 
    {  
      /* load the look-and-feel */ 
      {
	try {
	  SynthLookAndFeel synth = new SynthLookAndFeel();
	  synth.load(LookAndFeelLoader.class.getResourceAsStream("synth.xml"),
		     LookAndFeelLoader.class);
	  UIManager.setLookAndFeel(synth);

	  pCheckIcon = new ImageIcon(LookAndFeelLoader.class.getResource("CheckIcon.png"));
	  pNoneIcon  = new ImageIcon(LookAndFeelLoader.class.getResource("NoneIcon.png"));
	}
	catch(java.text.ParseException ex) {
	  LogMgr.getInstance().log
	    (LogMgr.Kind.Ops, LogMgr.Level.Severe,
	     "Unable to parse the look-and-feel XML file (synth.xml):\n" + 
	     "  " + ex.getMessage());
	  System.exit(1);
	}
	catch(UnsupportedLookAndFeelException ex) {
	  LogMgr.getInstance().log
	    (LogMgr.Kind.Ops, LogMgr.Level.Severe,
	     "Unable to load the Pipeline look-and-feel:\n" + 
	     "  " + ex.getMessage());
	  System.exit(1);
	}
      }

      /* application wide UI settings */ 
      {
	JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
      }

      /* create application frame */ 
      {
	JFrame frame = new JFrame("plconfig");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setResizable(false);
      
	JPanel root = new JPanel();
	root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));  

	/* header */ 
	{
	  JPanel panel = new JPanel();
	  panel.setName("DialogHeader");
	  panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));  
	
	  {
	    JLabel label = new JLabel("Pipeline Configuration Tool");
	    label.setName("DialogHeaderLabel");	
	    
	    panel.add(label);	  
	  }

	  panel.add(Box.createHorizontalGlue());
      
	  root.add(panel);
	}	  

	/* body */ 
	{
	  JPanel body = new JPanel();
	  body.setLayout(new BoxLayout(body, BoxLayout.X_AXIS));  

	  /* panel index */ 
	  {
	    JPanel panel = new JPanel();
	    panel.setName("DarkPanel"); 
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));  
	    
	    panel.add(Box.createRigidArea(new Dimension(0, 10)));
	    
	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      
	      hbox.add(new JLabel("System Prerequisites:", JLabel.LEFT));

	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      hbox.add(Box.createHorizontalGlue());
	      
	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 3)));

	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(15, 0)));
	      
	      {
		Box vbox = new Box(BoxLayout.Y_AXIS);

		{
		  JLabel label = new JLabel("Pipeline Admin User", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("System Information", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		hbox.add(vbox);
	      }
	      
	      hbox.add(Box.createRigidArea(new Dimension(15, 0)));
	      hbox.add(Box.createHorizontalGlue());

	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 10)));

	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      
	      hbox.add(new JLabel("Server Configuration:", JLabel.LEFT));

	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      hbox.add(Box.createHorizontalGlue());
	      
	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 3)));

	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(15, 0)));
	      
	      {
		Box vbox = new Box(BoxLayout.Y_AXIS);

		{
		  JLabel label = new JLabel("Essentials", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Java Runtime", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Master Manager", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("File Manager", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Queue Manager", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Plugin Manager", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Server Host IDs", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		hbox.add(vbox);
	      }
	      
	      hbox.add(Box.createRigidArea(new Dimension(15, 0)));
	      hbox.add(Box.createHorizontalGlue());

	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 10)));

	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      
	      hbox.add(new JLabel("Operating System Paths:", JLabel.LEFT));

	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      hbox.add(Box.createHorizontalGlue());
	      
	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 3)));

	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(15, 0)));
	      
	      {
		Box vbox = new Box(BoxLayout.Y_AXIS);

		{
		  JLabel label = new JLabel("UNIX (Linux)", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Mac OS X", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		vbox.add(Box.createRigidArea(new Dimension(0, 3)));

		{
		  JLabel label = new JLabel("Windows XP", pNoneIcon, JLabel.LEFT); 
		  pPanelLabels.add(label);
		  vbox.add(label);
		}

		hbox.add(vbox);
	      }
	      
	      hbox.add(Box.createHorizontalGlue());

	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 10)));

	    {
	      Box hbox = new Box(BoxLayout.X_AXIS);
	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      
	      hbox.add(new JLabel("Write Config Files...", JLabel.LEFT));

	      hbox.add(Box.createRigidArea(new Dimension(10, 0)));
	      hbox.add(Box.createHorizontalGlue());
	      
	      panel.add(hbox);
	    }

	    panel.add(Box.createRigidArea(new Dimension(0, 10)));
	    panel.add(Box.createVerticalGlue());

	    body.add(panel);
	  }

	  {
	    JPanel spanel = new JPanel();
	    spanel.setName("Spacer");
	  
	    spanel.setMinimumSize(new Dimension(7, 0));
	    spanel.setMaximumSize(new Dimension(7, Integer.MAX_VALUE));
	    spanel.setPreferredSize(new Dimension(7, 0));
	   
	    body.add(spanel);
	  }

	  {
	    JPanel panel = new JPanel();
	    pCardPanel = panel; 

	    panel.setName("MainDialogPanel");
	    panel.setLayout(new CardLayout()); 

	    {
	      pPanels.add(new JAdminUserPanel(pApp));
	      pPanels.add(new JSystemInfoPanel(pApp));

	      pPanels.add(new JEssentialsPanel(pApp));
	      pPanels.add(new JRuntimePanel(pApp));
	      pPanels.add(new JMasterManagerPanel(pApp));
	      pPanels.add(new JFileManagerPanel(pApp));
	      pPanels.add(new JQueueManagerPanel(pApp));
	      pPanels.add(new JPluginManagerPanel(pApp));
	      pPanels.add(new JServerHostIDsPanel(pApp));

	      pPanels.add(new JUnixPanel(pApp));
	      pPanels.add(new JMacPanel(pApp));
	      pPanels.add(new JWinPanel(pApp));
	    
	      for(JBaseConfigPanel cpanel : pPanels) 
		pCardPanel.add(cpanel, cpanel.getPanelTitle());
	    }
	    
	    body.add(panel);
	  }

	  root.add(body);
	}


	/* footer buttons */ 
	{
	  JPanel panel = new JPanel();
	  panel.setName("DialogButtonPanel");
	  panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
	  
	  panel.add(Box.createRigidArea(new Dimension(20, 0)));

	  { 
	    JButton btn = new JButton();
	    pPrevButton = btn;
	    btn.setName("LeftButton");
	    
	    Dimension size = new Dimension(31, 31);
	    btn.setMinimumSize(size); 
	    btn.setMaximumSize(size);  
	    
	    btn.setActionCommand("prev-panel");
	    btn.addActionListener(pApp);
	     
	    panel.add(btn);	  
	  }
 
	  panel.add(Box.createRigidArea(new Dimension(10, 0)));	

	  {
	    JLabel label = new JLabel("Previous", JLabel.LEFT); 

	    Dimension size = new Dimension(75, 19);
	    label.setMinimumSize(size);
	    label.setMaximumSize(size);

	    panel.add(label);
	  }
	    
	  panel.add(Box.createHorizontalGlue());
	  panel.add(Box.createRigidArea(new Dimension(60, 0)));	
	  
	  {
	    JButton btn = new JButton("Cancel");
	    btn.setName("RaisedCancelButton");
	    
	    Dimension size = new Dimension(130, 31);
	    btn.setMinimumSize(size);
	    btn.setMaximumSize(size);
	    
	    btn.setActionCommand("cancel");
	    btn.addActionListener(pApp);
	    
	    panel.add(btn);	   
	  }	  

	  panel.add(Box.createRigidArea(new Dimension(60, 0)));	
	  panel.add(Box.createHorizontalGlue());
	  
	  {
	    JLabel label = new JLabel("Next", JLabel.RIGHT); 
	    pNextLabel = label;

	    Dimension size = new Dimension(75, 19);
	    label.setMinimumSize(size);
	    label.setMaximumSize(size);

	    panel.add(label);
	  }

	  panel.add(Box.createRigidArea(new Dimension(10, 0)));	

	  {  
	    JButton btn = new JButton();
	    pNextButton = btn;  
	    btn.setName("RightButton");  
	    
	    Dimension size = new Dimension(31, 31);
	    btn.setMinimumSize(size);
	    btn.setMaximumSize(size);
	    
	    btn.setActionCommand("next-panel");
	    btn.addActionListener(pApp);
	    
	    panel.add(btn);	  
	  }

	  panel.add(Box.createRigidArea(new Dimension(20, 0)));

	  root.add(panel);
	}

	updateSelectedPanel();

	frame.setContentPane(root);
	frame.pack();

	{
	  Rectangle bounds = frame.getGraphicsConfiguration().getBounds();
	  frame.setLocation(bounds.x + bounds.width/2 - frame.getWidth()/2, 
			    bounds.y + bounds.height/2 - frame.getHeight()/2);
	}

	frame.setVisible(true);
      }

      /* dialogs */ 
      pErrorDialog = new JErrorDialog();
    }

    private ConfigApp  pApp; 
  }


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The once-only NTP based time.
   */ 
  private Long  pNetTime; 

  /**
   * The customer profile table.
   */ 
  private TreeMap<String,Object>  pProfile;
  
  /**
   * The hostname of the default server.
   */
  private String  pServerHostname; 

  /**
   * Whether we are running in batch mode.
   */
  private boolean  pIsBatchMode; 


  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The index of the current panel.
   */ 
  private int pPanelIdx;



  /**
   * The labels which indicate the current panel.
   */ 
  private ArrayList<JLabel>  pPanelLabels; 

  /**
   * Index label icons. 
   */ 
  private Icon pCheckIcon;
  private Icon pNoneIcon; 


  /** 
   * The parent of all panels.
   */
  private JPanel  pCardPanel; 

  /**
   * The panels.
   */ 
  private ArrayList<JBaseConfigPanel>  pPanels; 


  /**
   * Footer buttons.
   */ 
  private JButton  pPrevButton; 
  private JLabel   pNextLabel; 
  private JButton  pNextButton; 


  /*----------------------------------------------------------------------------------------*/
  
  /** 
   * The shared error dialog.
   */ 
  private JErrorDialog  pErrorDialog;

}


