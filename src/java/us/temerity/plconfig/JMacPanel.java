// $Id: JMacPanel.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   M A C   P A N E L                                                                      */
/*------------------------------------------------------------------------------------------*/

/**
 * The Mac OS X paths.
 */ 
class JMacPanel
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
  JMacPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Mac OS X Paths:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  vbox.add(UIFactory.createPanelLabel("Mac Clients:")); 

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));
	  
	  {
	    pClientsField = UIFactory.createBooleanField(sHSize);
	    
	    pClientsField.addActionListener(this);
	    pClientsField.setActionCommand("clients-changed");
	    
	    vbox.add(pClientsField); 
	  }

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pRootDirComp = new JAbsoluteDirComp("Root Install Directory", sHSize, false);
	  vbox.add(pRootDirComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pHomeDirComp = new JAbsoluteDirComp("Home Directory", sHSize, false);
	  vbox.add(pHomeDirComp);

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  vbox.add(Box.createRigidArea(new Dimension(0, 60)));

	  pProdDirComp = new JAbsoluteDirComp("Production Directory", sHSize, false);
	  vbox.add(pProdDirComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pTempDirComp = new JAbsoluteDirComp("Temporary Directory", sHSize, false);
	  vbox.add(pTempDirComp);

	  hbox.add(vbox);
	}

	add(hbox);
      }
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("If you will be using Pipeline on Mac OS X hosts, you need to enable the Mac OS X" + 
	 "Clients.  Due to differences between Linux and Darwin (Mac OS X) standard paths " + 
	 "and in how network file systems are accessed, the paths required by Pipeline " + 
	 "must be seperately configured for Mac OS X systems.\n" + 
	 "\n" + 
	 "The Root Install Directory and Production Directory should map to the same " + 
	 "network file system directories seen from Mac OS X systems as the corresponing " + 
	 "Linux paths specified earlier. For example, a Linux path such as " + 
	 "(/base/prod) might might be mapped to something like " + 
	 "(/Network/Servers/myserver/base/prod) on a Mac OS X system.\n" + 
	 "\n" + 
	 "The Home Directory is not required to be shared between Linux and Mac OS X " +
	 "systems and might even reside on a local file system.   The Temporary Directory " + 
	 "should reside on a local file system for optimal performance.");
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
    return "Mac OS X";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  { 
    pClientsField.setValue(pApp.getMacClients());
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
    pApp.setMacClients(enabled);
    if(enabled) {
      pApp.setMacRootDirectory(pRootDirComp.validateDir(pApp)); 
      pApp.setMacProdDirectory(pProdDirComp.validateDir(pApp)); 
      pApp.setMacHomeDirectory(pHomeDirComp.validateDir(pApp)); 
      pApp.setMacTemporaryDirectory(pTempDirComp.validateDir(pApp)); 
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
    
    pRootDirComp.setEnabled(enabled);
    pProdDirComp.setEnabled(enabled);
    pHomeDirComp.setEnabled(enabled);
    pTempDirComp.setEnabled(enabled);
    
    if(!enabled)
      return; 

    {
      File dir = pApp.getMacRootDirectory();
      if(dir == null) 
	dir = new File("/Network/Servers/myserver" + pApp.getRootDirectory());
      pRootDirComp.setDir(dir);
    }
    
    {
      File dir = pApp.getMacProdDirectory();
      if(dir == null) 
	dir = new File("/Network/Servers/myserver" + pApp.getProdDirectory());
      pProdDirComp.setDir(dir);	  
    }
    
    {
      File dir = pApp.getMacHomeDirectory();
      if(dir == null) 
	dir = new File("/Users");
      pHomeDirComp.setDir(dir);
    }
	
    {
      File dir = pApp.getMacTemporaryDirectory();
      if(dir == null) 
	dir = new File("/var/tmp");
      pTempDirComp.setDir(dir);
    }
  }


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -5057981793263078739L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The Mac OS X fields. 
   */ 
  private JBooleanField     pClientsField; 
  private JAbsoluteDirComp  pRootDirComp; 
  private JAbsoluteDirComp  pProdDirComp; 
  private JAbsoluteDirComp  pHomeDirComp; 
  private JAbsoluteDirComp  pTempDirComp; 

}



