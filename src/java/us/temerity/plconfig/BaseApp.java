// $Id: BaseApp.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import java.io.*; 
import java.util.*;
import java.util.logging.*;

/*------------------------------------------------------------------------------------------*/
/*   B A S E   A P P                                                                        */
/*------------------------------------------------------------------------------------------*/

/**
 * The abstract base class for all Pipeline applications. 
 */ 
public abstract 
class BaseApp
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct an application with the given name and command-line arguments.
   * 
   * @param name 
   *   The name of the application executable.
   */ 
  protected
  BaseApp
  ( 
   String name
  )
  {
    if(name == null) 
      throw new IllegalArgumentException("The name of the application cannot be (null)!");
    pName = name;
  }


  /*----------------------------------------------------------------------------------------*/
  /*   O P T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The implementation of the <CODE>--help</CODE> command-line option.
   */ 
  public abstract void
  help();

  /**
   * The implementation of the <CODE>--html-help</CODE> command-line option.
   */ 
  public void
  htmlHelp()
  {
    showURL("file://" + PackageInfo.sDocsDir + "/" + pName + ".html");
  }

  /**
   * The implementation of the <CODE>--version</CODE> command-line option.
   */ 
  public void
  version()
  {
    LogMgr.getInstance().log
      (LogMgr.Kind.Ops, LogMgr.Level.Info,
       PackageInfo.sVersion);
  }

  /**
   * The implementation of the <CODE>--release-date</CODE> command-line option.
   */  
  public void
  releaseDate()
  {
    LogMgr.getInstance().log
      (LogMgr.Kind.Ops, LogMgr.Level.Info,
       PackageInfo.sRelease);
  }
    
  /**
   * The implementation of the <CODE>--copyright</CODE> command-line option.
   */ 
  public void
  copyright()
  {
    LogMgr.getInstance().log
      (LogMgr.Kind.Ops, LogMgr.Level.Info,
       PackageInfo.sCopyright);
  }

    

  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   O P S                                                                  */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Open the given URL using the default web browser for the local environment.
   */ 
  public static void
  showURL
  (
   String url 
  ) 
  {
    Map<String,String> env = System.getenv();

    ExecPath epath = new ExecPath(env.get("PATH"));
    File mozilla = epath.which("mozilla");
    File firefox = epath.which("firefox");
    
    if((mozilla != null) && isBrowserRunning("mozilla"))
      displayURL("mozilla", url);
    else if((firefox != null) && isBrowserRunning("firefox"))
      displayURL("firefox", url);
    else if(mozilla != null)
      launchURL("mozilla", url);
    else if(firefox != null)
      launchURL("firefox", url);
    else 
      LogMgr.getInstance().log
	(LogMgr.Kind.Sub, LogMgr.Level.Warning,
	 "Unable to find either firefox(1) or mozilla(1) on your system to " +
	 "display URLs!");
  }

  /**
   * Returns whether a web browser is currently running.
   * 
   * @param browser
   *   The browser program name (mozilla or firefox).
   */    
  private static boolean
  isBrowserRunning
  (
   String browser
  )
  {
    int exitCode = -1;
    try {
      String args[] = {
	browser, "-remote", "ping()"
      };      
      
      Process proc = Runtime.getRuntime().exec(args);
      
      try {
	exitCode = proc.waitFor();
      }
      catch(InterruptedException ex) {
      }
    }
    catch(IOException ex) {
      LogMgr.getInstance().log
	(LogMgr.Kind.Sub, LogMgr.Level.Severe,
	 ex.getMessage());
    }
    
    return (exitCode == 0);
  }

  /**
   * Direct a running browser to display the given URL.
   * 
   * @param browser
   *   The browser program name (mozilla or firefox).
   * 
   * @param url
   *   The URL to display.
   */ 
  private static void
  displayURL
  (
   String browser, 
   String url 
  )
  {
    try {
      String args[] = {
	browser, "-remote", ("openURL(" + url + ", new-tab)")
      }; 
    
      Process proc = Runtime.getRuntime().exec(args);
      
      int exitCode = -1;
      try {
	exitCode = proc.waitFor();
      }
      catch(InterruptedException ex) {
      }
    }
    catch(IOException ex) {
      LogMgr.getInstance().log
	(LogMgr.Kind.Sub, LogMgr.Level.Severe,
	 ex.getMessage());
    }
  }
  
  /**
   * Launch a new browser process to display the given URL.
   * 
   * @param browser
   *   The browser program name (mozilla or firefox).
   * 
   * @param url
   *   The URL to display.
   */ 
  private static void
  launchURL
  (
   String browser, 
   String url 
  )
  {
    try {
      String args[] = {
	browser, url
      };
      
      Process proc = Runtime.getRuntime().exec(args);
      
      int exitCode = -1;
      try {
	exitCode = proc.waitFor();
      }
      catch(InterruptedException ex) {
      }
    }
    catch(IOException ex) {
      LogMgr.getInstance().log
	(LogMgr.Kind.Sub, LogMgr.Level.Severe,
	 ex.getMessage());
    }
  }
    


  /*----------------------------------------------------------------------------------------*/
  /*   H E L P E R S                                                                        */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Concatentates the command-line arguments into a single <CODE>String</CODE> suitable 
   * for parsing by the command-line parser of the application subclass. <P> 
   * 
   * Arguments are seperated by (\0) characters.
   * 
   * @param args 
   *   The command-line arguments.
   */
  protected void
  packageArguments
  (
   String[] args
  )  
  {  
    pArgs = args;

    StringBuffer buf = new StringBuffer();
    
    int wk;
    for(wk=0; wk<args.length; wk++) 
      buf.append(args[wk] + "\0");
    
    pPackedArgs = buf.toString();
  }


  /*----------------------------------------------------------------------------------------*/

  /** 
   * Generate a string containing both the exception message and stack trace. 
   * 
   * @param ex 
   *   The thrown exception.   
   */ 
  protected String 
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
    LogMgr.getInstance().log
      (LogMgr.Kind.Arg, LogMgr.Level.Severe,
       buf.toString());
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
    return null;
  }
 
  /**
   * Convert non-printable characters in the given <CODE>String</CODE> into ASCII literals.
   */ 
  protected String 
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
  protected String pName;        

  /**
   * The command-line arguments.
   */
  protected String pArgs[]; 

  /**
   * The single concatenated command-line argument string.
   */
  protected String pPackedArgs;  

}


