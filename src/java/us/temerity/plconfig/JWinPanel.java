// $Id: JWinPanel.java,v 1.4 2007/04/27 21:07:55 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   W I N   P A N E L                                                                      */
/*------------------------------------------------------------------------------------------*/

/**
 * The Windows XP paths.
 */ 
class JWinPanel
  extends JBaseConfigPanel
  implements ActionListener
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JWinPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Windows XP Paths:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  vbox.add(UIFactory.createPanelLabel("Windows Support:")); 

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));
	  
	  {
	    pSupportField = UIFactory.createBooleanField(sHSize);
	    
	    pSupportField.addActionListener(this);
	    pSupportField.setActionCommand("support-changed");
	    
	    vbox.add(pSupportField); 
	  }

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pRootDirComp = new JWindowsDirComp("Root Install Directory", sHSize);
	  vbox.add(pRootDirComp);
      
	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

	  pHomeDirComp = new JWindowsDirComp("Home Directory", sHSize);
	  vbox.add(pHomeDirComp);

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);
          
	  vbox.add(Box.createRigidArea(new Dimension(0, 60)));

	  pProdDirComp = new JWindowsDirComp("Production Directory", sHSize);
	  vbox.add(pProdDirComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

	  pTempDirComp = new JWindowsDirComp("Temporary Directory", sHSize);
	  vbox.add(pTempDirComp);

	  hbox.add(vbox);
	}

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 20)));

      pJavaHomeDirComp = new JWindowsDirComp("Java Home Directory", sSize);
      add(pJavaHomeDirComp);

      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("If you will be using Pipeline on Windows XP Professional hosts, you need to " + 
         "enable the Windows Support.  Due to differences between Linux and Windows XP " +
         "file naming conventions, standard paths and how network file systems are " +
         "accessed, the paths required by Pipeline must be seperately configured for " + 
         "Windows systems.\n" + 
	 "\n" + 
	 "All Windows directory paths passed to the following options should be specified " + 
	 "using the forward slash (/) in place of the back slash (\\).  This means that " + 
	 "in order to specify a native Windows path (C:\\foo\\bar) you will need to type " + 
	 "(C:/foo/bar).  Similarly, a native UNC path such as " + 
	 "(\\\\server\\share\\foo\\bar) will need to be specified as " + 
	 "(//server/share/foo/bar).\n" + 
	 "\n" + 
	 "The Root Install Directory and Production Directory should map to the same " + 
	 "network file system directories seen from Windows XP systems as the " +
	 "corresponing Linux paths specified earlier.  For example, a Linux path such " + 
	 "as (/base/prod) might might mapped to something like (Z:/base/prod) or " + 
	 "(//myserver/base/prod) on a Windows XP system.\n" +
	 "\n" + 
	 "The Home Directory should specify the network file share where the users home " + 
         "directory will be mapped.  We recommend using a UNC path for this like " + 
         "(//myserver/homes) to avoid problems some application have with drive letter " + 
         "mappings in this context.  Pipeline uses the Home Directory primarily to " + 
         "construct the dynamic value for the Windows APPDATA environmental variable by " + 
         "simply appending (Application Data) to the Home Directory specified.  Note that " + 
         "the generated APPDATA value does not include the name of the user, which means " + 
         "that the share specified by Home Directory should already be pointing at a " + 
         "user-specific location during user logon.\n" +
         "\n" + 
         "The Temporary Directory should reside on a local file system for optimal " + 
         "performance.\n" + 
	 "\n" + 
	 "The Java Home Directory is the path to the root directory of the local Java " +
	 "Runtime Environment (JRE) installed on Windows XP hosts at your site.  For " + 
	 "consitancy, the version of the JRE installed on the Windows XP hosts should " + 
	 "match the version used by Linux hosts.  Since the JRE will be installed in a " + 
         "much different location on Windows XP hosts, it needs to be supplied in order " + 
         "for the Pipeline client launcher scripts to function properly.");
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
    return "Windows XP";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  { 
    pSupportField.setValue(pApp.getWinSupport());
    doSupportChanged();
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
    boolean enabled = pSupportField.getValue(); 
    pApp.setWinSupport(enabled);
    if(enabled) {
      pApp.setWinRootDirectory(pRootDirComp.validateDir(pApp)); 
      pApp.setWinProdDirectory(pProdDirComp.validateDir(pApp)); 
      pApp.setWinHomeDirectory(pHomeDirComp.validateDir(pApp)); 
      pApp.setWinTemporaryDirectory(pTempDirComp.validateDir(pApp)); 
      pApp.setWinJavaHome(pJavaHomeDirComp.validateDir(pApp)); 
    }
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
    if(cmd.equals("support-changed")) 
      doSupportChanged();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  private void
  doSupportChanged() 
  {
    boolean enabled = pSupportField.getValue(); 
    
    pRootDirComp.setEnabled(enabled);
    pProdDirComp.setEnabled(enabled);
    pHomeDirComp.setEnabled(enabled);
    pTempDirComp.setEnabled(enabled);
    pJavaHomeDirComp.setEnabled(enabled);
    
    if(!enabled)
      return; 

    {
      String dir = pApp.getWinRootDirectory();
      if(dir == null) 
	dir = ("//myserver" + pApp.getRootDirectory());
      pRootDirComp.setDir(dir);
    }
    
    {
      String dir = pApp.getWinProdDirectory();
      if(dir == null) 
	dir = ("//myserver" + pApp.getProdDirectory());
      pProdDirComp.setDir(dir);	  
    }
    
    {
      String dir = pApp.getWinHomeDirectory();
      if(dir == null) 
	dir = ("C:/Documents and Settings");
      pHomeDirComp.setDir(dir);
    }
	
    {
      String dir = pApp.getWinTemporaryDirectory();
      if(dir == null) 
	dir = ("C:/WINDOWS/Temp"); 
      pTempDirComp.setDir(dir);
    }
	
    {
      String dir = pApp.getWinJavaHome();
      if(dir == null) 
	dir = ("C:/Program Files/Java/jre1.5.0_06");
      pJavaHomeDirComp.setDir(dir);
    }
  }

  

  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 4991144157867908121L;
   
  

  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The Windows XP fields. 
   */ 
  private JBooleanField    pSupportField; 
  private JWindowsDirComp  pRootDirComp; 
  private JWindowsDirComp  pProdDirComp; 
  private JWindowsDirComp  pHomeDirComp; 
  private JWindowsDirComp  pTempDirComp; 
  private JWindowsDirComp  pJavaHomeDirComp; 

}



