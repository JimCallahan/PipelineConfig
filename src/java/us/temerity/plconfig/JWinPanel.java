// $Id: JWinPanel.java,v 1.5 2007/06/14 12:57:07 jim Exp $

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
    super(app, "Windows Client Paths:");
    
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

          pJavaHomeDirComp = new JWindowsDirComp("Java Home Directory", sHSize);
          vbox.add(pJavaHomeDirComp);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

 	  pUserProfileDirComp = new JWindowsDirComp("User Profile Directory", sHSize);
 	  vbox.add(pUserProfileDirComp);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

 	  pAppDataDirComp = new JWindowsDirComp("Application Data Directory", sHSize, true);
 	  vbox.add(pAppDataDirComp);

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

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

	  vbox.add(UIFactory.createPanelLabel("User Profile Needs Username:"));

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));

          pUserProfileNeedsUserField = UIFactory.createBooleanField(sHSize);
          vbox.add(pUserProfileNeedsUserField);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

	  vbox.add(UIFactory.createPanelLabel("Application Data Needs Username:"));

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));

          pAppDataNeedsUserField = UIFactory.createBooleanField(sHSize);
          vbox.add(pAppDataNeedsUserField);

	  hbox.add(vbox);
	}

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 40)));
      
      pJavadocDirComp = new JWindowsDirComp("Local Vendor Javadoc Directory", sSize);
      add(pJavadocDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      
      pExtraJavaLibsComp = new JWindowsJarsComp("Local Java Libraries", sSize);
      add(pExtraJavaLibsComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
    }
      
    pNotesDialog.setMessage
      ("Windows Client Parameters:", 
       "If you will be using Pipeline on Windows XP Professional hosts, you need to " + 
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
       "The Java Home Directory is the path to the root directory of the local Java " +
       "Runtime Environment (JRE) installed on Windows XP hosts at your site.  For " + 
       "consitancy, the version of the JRE installed on the Windows XP hosts should " + 
       "match the version used by Linux hosts.  Since the JRE will be installed in a " + 
       "much different location on Windows XP hosts, it needs to be supplied in order " + 
       "for the Pipeline client launcher scripts to function properly.\n" +
       "\n" +
       "The Temporary Directory should reside on a local file system for optimal " + 
       "performance.\n" + 
       "\n" + 
       "The User Profile Directory should specify the location of the Windows user " + 
       "profile hive.  Depending on the value of User Profile Needs Username, this " + 
       "path may or may not need to have the specific user name appended to it in order " + 
       "to specify the actual directory where the user's profile lives.  Depending on " + 
       "site configuration, the user profile directory may be either local or on a " + 
       "network share.  In either case, it will contain at least the following well " + 
       "known Windows user directories:\n" +
       "\n" + 
       "  Application Data\n" + 
       "  Desktop\n" + 
       "  Favorites\n" +
       "  My Documents\n" + 
       "  Start Menu\n" +
       "\n" + 
       "Some site configurations may use a home share (UNC path) which does not " +
       "contain the user's name, but is set by Windows to point to the specific users " + 
       "profile directory on the network file server.  In cases like this, we recommend " + 
       "using a UNC path like (//myserver/homes) to avoid problems some application have " +
       "with drive letter mappings in this context and set User Profile Needs Username " + 
       "to \"no\".\n" + 
       "\n" +
       "In most cases the Windows \"Application Data\" directory is located simply " + 
       "under the user profile directory and need not be specified.  However, some sites " +
       "may wish to store this application specific data on a network share unrelated " + 
       "to the user profile.  In cases like this, the Application Data Directory will " + 
       "specify alternative directory containing the \"Applicata Data\" directory for " + 
       "users.  Like the User Profile Directory, the specified directory may be " + 
       "configured at the site to automatically already point to a current user specific " +
       "share or require that the username be appended to the specified path.  The " +
       "Application Data Needs Username parameter controls this behavior.\n" +
       "\n" + 
       "The Local Vendor Javadoc Directory is the path to the root directory where the " + 
       "documentation generated by javadoc for locally created Pipeline plugins and " + 
       "standalone utils is located.  Providing this will allow users to easily access " +
       "the documentation for locally created plugins from Pipeline's user interface.\n" +
       "\n" + 
       "The Local Java Libraries is a optional set of JAR files containing common Java " + 
       "class shared by a large number of locally created plugins and/or standalone " + 
       "utils.  Any JARs provided will be added to the Java classpath for all Pipeline " + 
       "programs.  This is typically used for relatively large 3rd party libraries for " + 
       "things such as XML parsing or SQL database connections which would otherwise " + 
       "need to be embedded in many different plugin JARs.  Since these libraries " + 
       "be dynamically updated (like plugins can), they are not suitable for anything " + 
       "which might need to modified without a Pipeline shutdown.  This feature is " + 
       "only an optimization to reduce plugin size and memory footprint but is not " + 
       "required in order to use 3rd party Java libraries with Pipeline."); 
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
    return "Windows Client";
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
      pApp.setWinJavaHome(pJavaHomeDirComp.validateDir(pApp)); 
      pApp.setWinTemporaryDirectory(pTempDirComp.validateDir(pApp)); 
      pApp.setWinUserProfileDirectory(pUserProfileDirComp.validateDir(pApp)); 
      pApp.setWinUserProfileNeedsUser(pUserProfileNeedsUserField.getValue());
      pApp.setWinAppDataDirectory(pAppDataDirComp.validateDir(pApp)); 
      pApp.setWinAppDataNeedsUser(pAppDataNeedsUserField.getValue());   
      pApp.setWinLocalJavadocDirectory(pJavadocDirComp.validateDir(pApp));
      pApp.setWinLocalJavaLibraries(pExtraJavaLibsComp.validateJars(pApp));   
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
    super.actionPerformed(e); 

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
    pTempDirComp.setEnabled(enabled);
    pJavaHomeDirComp.setEnabled(enabled);
    pUserProfileDirComp.setEnabled(enabled);
    pUserProfileNeedsUserField.setEnabled(enabled);
    pAppDataDirComp.setEnabled(enabled);
    pAppDataNeedsUserField.setEnabled(enabled);
    pJavadocDirComp.setEnabled(enabled);
    pExtraJavaLibsComp.setEnabled(enabled);

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

    {
      String dir = pApp.getWinUserProfileDirectory();
      if(dir == null) 
	dir = ("C:/Documents and Settings"); 
      pUserProfileDirComp.setDir(dir);	  
    }
    
    {
      Boolean tf = pApp.getWinUserProfileNeedsUser();
      pUserProfileNeedsUserField.setValue((tf != null) && tf);
    }

    pAppDataDirComp.setDir(pApp.getWinAppDataDirectory());

    {
      Boolean tf = pApp.getWinUserProfileNeedsUser();
      pAppDataNeedsUserField.setValue((tf != null) && tf);
    }

    pJavadocDirComp.setDir(pApp.getWinLocalJavadocDirectory());
    pExtraJavaLibsComp.setJars(pApp.getWinLocalJavaLibraries()); 
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
  private JWindowsDirComp  pTempDirComp; 
  private JWindowsDirComp  pJavaHomeDirComp; 
  private JWindowsDirComp  pUserProfileDirComp;
  private JBooleanField    pUserProfileNeedsUserField;  
  private JWindowsDirComp  pAppDataDirComp;
  private JBooleanField    pAppDataNeedsUserField;
  private JWindowsDirComp  pJavadocDirComp; 
  private JWindowsJarsComp pExtraJavaLibsComp;

}



