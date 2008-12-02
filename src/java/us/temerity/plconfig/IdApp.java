// $Id: IdApp.java,v 1.13 2008/12/02 20:56:23 jim Exp $

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
    /* the name of this host */ 
    String hostname = null;
    try {
      InetAddress addr = InetAddress.getLocalHost();
      if((addr instanceof Inet4Address) && !addr.isLoopbackAddress()) 
        hostname = addr.getCanonicalHostName().toLowerCase(Locale.ENGLISH);
    }
    catch(Exception ex) {
      throw new IOException 
	("Could not determine the name of this machine!");
    }

    if(hostname == null) 
      throw new IOException 
        ("Could not determine the name of this machine!");

    /* determine the hardware IDs */ 
    TreeMap<String,BigInteger> IDs = new TreeMap<String,BigInteger>();
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");

      /* get OS release/version info */ 
      {
	StringBuffer buf = new StringBuffer();

	FileReader in = new FileReader("/proc/version");
        try {
          char[] cs = new char[4096];
          while(true) {
            int cnt = in.read(cs);
            if(cnt == -1) 
              break;
            
            buf.append(cs);
          }
        }
        finally {
          in.close();
        }

        md.update(buf.toString().getBytes());
      }

      /* get the CPU info */ 
      {
	FileReader in = new FileReader("/proc/cpuinfo");
        try {
          StringBuffer buf = new StringBuffer();
          char[] cs = new char[4096];
          while(true) {
            int cnt = in.read(cs);
            if(cnt == -1) 
              break;
            
            int wk;
            for(wk=0; wk<cnt; wk++) {
              if(cs[wk] == '\n') {
                String line = buf.toString();
                if(!line.startsWith("cpu MHz") && 
                   !line.startsWith("bogomips") &&
                   !line.startsWith("core id")) 
                  md.update(line.getBytes());
                buf = new StringBuffer();
              }
              else {
                buf.append(cs[wk]);
              }
            }
          }
        }
        finally {
          in.close();
        }
      }

      BigInteger hardwareID = new BigInteger(md.digest());
      IDs.put(hostname, hardwareID);
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


