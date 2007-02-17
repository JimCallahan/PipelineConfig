// $Id: IdApp.java,v 1.10 2007/02/17 14:06:19 jim Exp $

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


/*------------------------------------------------------------------------------------------*/
/*   I D   A P P                                                                            */
/*------------------------------------------------------------------------------------------*/

/**
 * The top-level class for the <B>plid</B>(1) program.
 */ 
public
class IdApp
  extends BaseApp
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
    IdApp app = new IdApp(args);
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
  IdApp
  ( 
   String[] args
  )
  {
    super("plid");
    packageArguments(args);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                         */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the name of the host IDs file. 
   */
  public void 
  setHostIDs
  (
   File file
  ) 
  {
    pHostIDs = file;
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
      IdOptsParser parser = 
	new IdOptsParser(new StringReader(pPackedArgs));
      parser.setApp(this);
      parser.CommandLine();
      
      TreeMap<String,BigInteger> IDs = generateIDs();
      {
	StringBuffer buf = new StringBuffer();
	for(String host : IDs.keySet()) {
	  buf.append(host + " ");
	  
	  int wk;
	  for(wk=0; wk<(29 - host.length()); wk++) 
	    buf.append(" ");
	  
	  buf.append(IDs.get(host).toString());
	  buf.append("\n");    
	}
	
	if(pHostIDs == null) {
	  LogMgr.getInstance().log
	    (LogMgr.Kind.Ops, LogMgr.Level.Info,
	     buf.toString());
	}
	else {
	  FileWriter writer = new FileWriter(pHostIDs, true); 
	  writer.write(buf.toString());
	  writer.close();
	}
      }

      success = true;
    }
    catch(ParseException ex) {
      handleParseException(ex);
    }
    catch(Exception ex) {
      LogMgr.getInstance().log
	(LogMgr.Kind.Sub, LogMgr.Level.Severe,
	 ex.getMessage());
    }

    System.exit(success ? 0 : 1);
  }


  /*-- RUN HELPERS -------------------------------------------------------------------------*/

  /**
   * Generate the hardware IDs for the local machine.
   */
  private TreeMap<String,BigInteger>
  generateIDs() 
    throws IOException 
  {
    /* the names of this host indexed by IP address */ 
    TreeMap<String,String> hostnames = new TreeMap<String,String>();
    try {
      Enumeration nets = NetworkInterface.getNetworkInterfaces();  
      while(nets.hasMoreElements()) {
	NetworkInterface net = (NetworkInterface) nets.nextElement();
	Enumeration addrs = net.getInetAddresses();
	while(addrs.hasMoreElements()) {
	  InetAddress addr = (InetAddress) addrs.nextElement();
	  if((addr instanceof Inet4Address) && !addr.isLoopbackAddress()) 
	    hostnames.put(addr.getHostAddress(), addr.getCanonicalHostName());
	}
      }
    }
    catch(Exception ex) {
      throw new IOException 
	("Could not determine the name of this machine!");
    }

    if(hostnames.isEmpty()) 
      throw new IOException 
	("Could not determine the name of this machine!");

    /* determine the hardware IDs */ 
    TreeMap<String,BigInteger> IDs = new TreeMap<String,BigInteger>();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      
      /* get the network card MAC addresses */ 
      TreeSet<String> found = new TreeSet<String>();
      {
	String str = null;
	try {
	  String args[] = new String[1];
	  args[0] = "/sbin/ifconfig";
	  
	  String env[] = new String[0];
	  
	  Process proc = Runtime.getRuntime().exec(args, env);
	  
	  StringBuffer buf = new StringBuffer();
	  InputStreamReader in = new InputStreamReader(proc.getInputStream());
	  char cs[] = new char[1024];
	  while(true) {
	    int cnt = in.read(cs);
	    if(cnt == -1) 
	      break;
	    
	    buf.append(cs, 0, cnt);
	  }

	  int exitCode = proc.waitFor();
	  if(exitCode != 0)
	    throw new IOException();

	  str = buf.toString();
	}
	catch(Exception ex) {
	  throw new IOException();
	}      
	
	if(str == null)
	  throw new IOException();

	{
	  boolean hwNext = false;
	  boolean ipNext = false;

	  String tok[] = str.split(" ");
	  int wk; 
	  for(wk=0; wk<tok.length; wk++) {
	    if(tok[wk].length() > 0) {
	      if(hwNext) {
		md.update(tok[wk].getBytes());
		hwNext = false;
	      }
	      else if(ipNext) {
		if(tok[wk].startsWith("addr:")) {
		  String addr = tok[wk].substring(5);
		  String hname = hostnames.get(addr);
		  if(hname != null)
		    found.add(hname);
		}
		ipNext = false;		
	      }
	      else if(tok[wk].equals("HWaddr")) {
		hwNext = true;
	      }
	      else if(tok[wk].equals("inet")) {
		ipNext = true;
	      }
	    }
	  }
	  
	  if(found.isEmpty()) 
	    throw new IOException();
	}
      }
      
      /* get the CPU info */ 
      {
	char[] cs = new char[4096];
	StringBuffer buf = new StringBuffer();

	FileReader in = new FileReader("/proc/cpuinfo");
	while(true) {
	  int cnt = in.read(cs);
	  if(cnt == -1) 
	    break;
	  
	  int wk;
	  for(wk=0; wk<cnt; wk++) {
	    if(cs[wk] == '\n') {
	      String line = buf.toString();
	      if(!line.startsWith("cpu MHz") && !line.startsWith("bogomips")) 
		md.update(line.getBytes());
	      buf = new StringBuffer();
	    }
	    else {
	      buf.append(cs[wk]);
	    }
	  }
	}
      }

      BigInteger hardwareID = new BigInteger(md.digest());
      for(String host : found) 
	IDs.put(host, hardwareID);
    }
    catch(Exception ex) {
      throw new IOException("Unable to determine local host ID." + 
			    "\n" + getFullMessage(ex));
    }      

    return IDs;
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
       "  plid [options]\n" + 
       "\n" + 
       "  plid --help\n" +
       "  plid --html-help\n" +
       "  plid --version\n" + 
       "  plid --release-date\n" + 
       "  plid --copyright\n" + 
       "\n" + 
       "OPTIONS:\n" +
       "  [--append=...]\n" +
       "\n" +  
       "Use \"plid --html-help\" to browse the full documentation.\n");
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

    case ConfigOptsParserConstants.PATH_ARG:
      return "an file system path";
   
    default: 
      if(printLiteral) 
	return ConfigOptsParserConstants.tokenImage[kind];
      else 
	return null;
    }      
  }



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /*
   * The name of the host IDs file.
   */ 
  private File pHostIDs;

}


