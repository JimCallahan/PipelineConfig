// $Id: IdApp.java,v 1.2 2004/08/27 22:18:57 jim Exp $

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
/*   I D   A P P                                                                            */
/*------------------------------------------------------------------------------------------*/

/**
 * The top-level class for the <B>plid</B>(1) program.
 */ 
public
class IdApp
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
    pName = "plid";
    setPackedArgs(args);
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
      
      generateID();

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
   * Generate the hardware ID for the local machine.
   */
  private void
  generateID() 
    throws IOException 
  {
    /* determine the hardware ID */ 
    String cksum = null;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      
      /* get the network card MAC addresses */ 
      {
	FileReader reader = new FileReader("/proc/net/arp");
	boolean done = false;
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
	  
	  String addr = null;

	  String[] fields = buf.toString().split(" ");
	  int wk, cnt;
	  for(wk=0, cnt=0; wk<fields.length; wk++) {
	    if(fields[wk].length() > 0) {
	      if((cnt == 3) && (!fields[wk].equals("00:00:00:00:00:00"))) {
		addr = fields[wk];
	      }
	      else if((cnt == 5) && (fields[wk].equals("eth0")) && (addr != null)) {
		md.update(addr.getBytes());
		break;
	      }

	      cnt++;
	    }
	  }
	}

	reader.close();
      }
      
      /* get the CPU, PCI amd SCSCI info */ 
      {
	String[] files = {
	  "/proc/cpuinfo"
	  /*	  "/proc/pci", 
		  "/proc/scsi/scsi" */ 
	};
	  
	byte[] buf = new byte[4096];
	int wk;
	for(wk=0; wk<files.length; wk++) {
	  try {
	    FileInputStream in = new FileInputStream(files[wk]);
	    
	    while(true) {
	      int cnt = in.read(buf);
	      if(cnt == -1) 
		break;
	      
	      md.update(buf, 0, cnt);
	    }
	    
	    in.close();
	  }
	  catch(IOException ex) {
	  }    
	}
      }  

      BigInteger big = new BigInteger(md.digest());
      cksum = big.toString();
    }
    catch(Exception ex) {
      throw new IOException("Unable to determine local host ID.");
    }      
    assert(cksum != null);

    /* lookup the names the machine is known by */ 
    TreeSet<String> hosts = new TreeSet<String>(); 
    try {
      Enumeration nets = NetworkInterface.getNetworkInterfaces();  
      while(nets.hasMoreElements()) {
	NetworkInterface net = (NetworkInterface) nets.nextElement();
	Enumeration addrs = net.getInetAddresses();
	while(addrs.hasMoreElements()) {
	  InetAddress addr = (InetAddress) addrs.nextElement();
	  String ip = addr.getHostAddress();
	  if(!ip.equals("127.0.0.1")) {
	    hosts.add(addr.getCanonicalHostName());
	  }	    
	}
      }
    }
    catch(Exception ex) {
      throw new IOException 
	("Could not determine the name of this machine!");
    }

    if(hosts.isEmpty()) 
      throw new IOException 
	("Could not determine the name of this machine!");

    StringBuffer buf = new StringBuffer();
    for(String host : hosts) {
      buf.append(host + " ");
      
      int wk;
      for(wk=0; wk<(29 - host.length()); wk++) 
	buf.append(" ");

      buf.append(cksum);
      buf.append("\n");    
    }

    if(pHostIDs == null) {
      System.out.print(buf.toString());
    }
    else {
      FileWriter writer = new FileWriter(pHostIDs, true); 
      writer.write(buf.toString());
      writer.close();
    }
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
       "  plid [options]\n" + 
       "\n" +
       "  plid --graphical\n" +
       "\n" + 
       "  plid --help\n" +
       "  plid --html-help\n" +
       "  plid --version\n" + 
       "  plid --release-date\n" + 
       "  plid --copyright\n" + 
       "\n" + 
       "OPTIONS:\n" +
       "  [--append]\n" +
       "\n" +  
       "Use \"plid --html-help\" to browse the full documentation.\n");
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
   * The name of the host IDs file.
   */ 
  private File pHostIDs;

}


