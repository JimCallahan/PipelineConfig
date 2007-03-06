// $Id: JUnixPanel.java,v 1.3 2007/03/06 04:28:57 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   U N I X   P A N E L                                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * The UNIX paths.
 */ 
class JUnixPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JUnixPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "UNIX (Linux) Paths:");
    
    /* initialize UI components */ 
    {
      pHomeDirComp = new JCanonicalDirComp("Home Directory", sSize);
      add(pHomeDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));

      pTempDirComp = new JCanonicalDirComp("Temporary Directory", sSize);
      add(pTempDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));

      pJavaHomeDirComp = new JCanonicalDirComp("Java Home Directory", sSize);
      add(pJavaHomeDirComp);

      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("The Home Directory is the absolute path to the parent directory of all user " + 
	 "home directories.  Usually this directory resides on a network file system and " +
	 "may not be (/home) at your site.\n" +
	 "\n" + 
	 "The Temporary Directory is absolute path to the directory used to write various " + 
	 "temporary files generated by Pipeline programs.  For optimal performance, this " +
	 "directory should reside on a local file system.\n" + 
         "\n" + 
	 "The Java Home Directory is the path to the root directory of the local Java " +
	 "Runtime Environment (JRE) installed on Linux artist workstations used to run " +
         "Pipeline client programs.  By default, this is set to the JRE used to run this " +
         "configuration tool and the Pipeline server daemons in the Java Runtime section " + 
         "above.  If you wish to use a different JRE for workstation clients than is used " + 
         "for the server daemons, you can specify the location of the alternative JRE " + 
         "here."); 
    }
  }


  /*----------------------------------------------------------------------------------------*/
  /*   P A N E L   O P S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The unique name of the panel.
   */ 
  public String
  getPanelTitle()
  {
    return "UNIX (Linux)";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  { 
    {
      File dir = pApp.getHomeDirectory();

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

      pHomeDirComp.setDir(dir);
    }

    {
      File dir = pApp.getTemporaryDirectory();
      if(dir == null) 
	dir = new File("/var/tmp");
      pTempDirComp.setDir(dir);
    }

    {
      File dir = pApp.getUnixJavaHome();
      if(dir == null) 
	dir = new File(pApp.getJavaHome());
      pJavaHomeDirComp.setDir(dir);
    }
  }
  
  /**
   * Validate the current UI values and update the site profile settings.
   * 
   * @throws IllegalConfigException
   *   If the current UI values are invalid.
   */ 
  public void 
  updateProfile() 
    throws IllegalConfigException
  {
    pApp.setHomeDirectory(pHomeDirComp.validateDir(pApp)); 
    pApp.setTemporaryDirectory(pTempDirComp.validateDir(pApp)); 
    pApp.setUnixJavaHome(pJavaHomeDirComp.validateDir(pApp)); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 1133869483107442070L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The UNIX fields. 
   */ 
  private JCanonicalDirComp  pHomeDirComp; 
  private JCanonicalDirComp  pTempDirComp; 
  private JCanonicalDirComp  pJavaHomeDirComp; 

}



