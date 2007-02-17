// $Id: JWinPanel.java,v 1.2 2007/02/17 14:06:19 jim Exp $

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

	  vbox.add(UIFactory.createPanelLabel("Windows Clients/Servers:")); 

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));
	  
	  {
	    pClientsField = UIFactory.createBooleanField(sHSize);
	    
	    pClientsField.addActionListener(this);
	    pClientsField.setActionCommand("clients-changed");
	    
	    vbox.add(pClientsField); 
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
          
          vbox.add(UIFactory.createPanelLabel("Default Windows Domain:"));
      
          vbox.add(Box.createRigidArea(new Dimension(0, 3)));

          pDefaultDomainField = UIFactory.createIdentifierField("", sHSize, JTextField.LEFT);
          vbox.add(pDefaultDomainField);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

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
	("If you will be using Pipeline on Windows XP hosts, you need to enable the " + 
	 "Windows Clients/Servers.  Due to differences between Linux and Windows XP file " +
         "naming conventions, standard paths and how network file systems are accessed, " + 
         "the paths required by Pipeline must be seperately configured for Windows " +
         "systems.\n" + 
	 "\n" + 
	 "All Windows directory paths passed to the following options should be specified " + 
	 "using the forward slash (/) in place of the back slash (\\).  This means that " + 
	 "in order to specify a native Windows path (C:\\foo\\bar) you will need to type " + 
	 "(C:/foo/bar).  Similarly, a native UNC path such as " + 
	 "(\\\\server\\share\\foo\\bar) will need to be specified as " + 
	 "(//server/share/foo/bar).\n" + 
	 "\n" + 
         "The Default Windows Domain is used by Pipeline as a default value when querying " + 
         "users for Windows authentication information.  When using Pipeline with Windows " + 
         "based render farms, users must provide authentication credentials which include " + 
         "their Domain/User and Password.  You should set this to the Windows Domain most " + 
         "frequently used at your site as a convenience for users.  Users may override " + 
         "this setting on a per-user basis.\n" + 
         "\n" +
	 "The Root Install Directory and Production Directory should map to the same " + 
	 "network file system directories seen from Windows XP systems as the " +
	 "corresponing Linux paths specified earlier.  For example, a Linux path such " + 
	 "as (/base/prod) might might mapped to something like (Z:/base/prod) or " + 
	 "(//myserver/base/prod) on a Windows XP system.\n" +
	 "\n" + 
	 "The Home Directory is not required to be shared between Linux and Windows XP " +
	 "systems and might even reside on a local file system.  The Temporary Directory " + 
	 "should reside on a local file system for optimal performance.\n" + 
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
    pClientsField.setValue(pApp.getWinClients());
    doClientsChanged();
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
    boolean enabled = pClientsField.getValue(); 
    pApp.setWinClients(enabled);
    if(enabled) {
      pApp.setWinDefaultDomain(pDefaultDomainField.getText());
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
    if(cmd.equals("clients-changed")) 
      doClientsChanged();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  private void
  doClientsChanged() 
  {
    boolean enabled = pClientsField.getValue(); 
    
    pDefaultDomainField.setEnabled(enabled);
    pRootDirComp.setEnabled(enabled);
    pProdDirComp.setEnabled(enabled);
    pHomeDirComp.setEnabled(enabled);
    pTempDirComp.setEnabled(enabled);
    pJavaHomeDirComp.setEnabled(enabled);
    
    if(!enabled)
      return; 

    pDefaultDomainField.setText(pApp.getWinDefaultDomain());

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
  private JIdentifierField pDefaultDomainField; 
  private JBooleanField    pClientsField; 
  private JWindowsDirComp  pRootDirComp; 
  private JWindowsDirComp  pProdDirComp; 
  private JWindowsDirComp  pHomeDirComp; 
  private JWindowsDirComp  pTempDirComp; 
  private JWindowsDirComp  pJavaHomeDirComp; 
}



