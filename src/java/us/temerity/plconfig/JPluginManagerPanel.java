// $Id: JPluginManagerPanel.java,v 1.6 2009/02/17 00:13:13 jlee Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   P L U G I N   M A N A G E R   P A N E L                                                */
/*------------------------------------------------------------------------------------------*/

/**
 * The Plugin Manager daemon panel. 
 */ 
class JPluginManagerPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JPluginManagerPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Plugin Manager:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pHostnameComp = new JHostnameComp("Plugin Hostname", sHSize);
	  vbox.add(pHostnameComp);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  vbox.add(UIFactory.createPanelLabel("Local Vendor:"));

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));

	  pVendorField = UIFactory.createIdentifierField(null, sHSize, JTextField.LEFT);
	  vbox.add(pVendorField); 

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));
	  
	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pPortComp = new JPortComp("Plugin Port", sHSize);
	  vbox.add(pPortComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

	  vbox.add(UIFactory.createPanelLabel("Legacy Plugins:"));
	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));
	  pLegacyField = UIFactory.createBooleanField(sHSize);
	  vbox.add(pLegacyField);

	  hbox.add(vbox);
	}	

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 40)));

      pPluginDirComp = new JAbsoluteDirComp("Plugin Directory", sSize);
      add(pPluginDirComp);

      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("The Plugin Manager daemon is responsible for loading plugin classes and " + 
	 "providing loaded plugins to the other Pipeline programs.  Typically, this " + 
	 "daemon is run on the same host as the Master Manager, but this is not strictly " +
	 "required.\n" + 
	 "\n" + 
	 "For sites which have previously installed a version Pipeline before the 2.0.0 " +
	 "release, this option adds the plugins required by these older Pipeline " + 
	 "releases.  Sites which have only installed Pipeline 2.0.0 or later releases " + 
	 "should not select this option.  All functionality of the older plugins exists " + 
	 "in current plugin versions as well.  These legacy plugins are provides soley " + 
	 "for backward compatability.\n" + 
         "\n" + 
         "The Local Vendor specifies the name of the default vendor of all locally " + 
         "created plugins.  Plugins are identified by their name, version ID and vendor. " + 
         "The vendor is a short name for the creating entity of the plugin.  All of the " + 
         "standard plugins shipped with Pipeline have a vendor of \"Temerity\".  If " + 
         "studios create their own plugins, they should use a different vendor name to " + 
         "avoid name conflicts.   Without using a special override command-line option, " + 
         "the tool used to install plugins (plpluginmgr) will reject plugins not created " + 
         "by the default vendor.  This helps to avoid mistakes where plugin developers " + 
         "copy the source code from a Temerity (or other vendor) plugin and forget to " + 
         "change the vendor.\n" + 
         "\n" + 
         "The Plugin Directory is where the Plugin Manager daemon saves a " + 
         "list of all the plugins installed.  When the Plugin Manager daemon starts " + 
         "up, it checks that all the plugins are loaded.  This directory should reside on " + 
         "a local filesystem of the host which will run the Plugin Manager."); 
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
    return "Plugin Manager";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    {
      String host = pApp.getPluginHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getServerHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getMasterHostname();
      pHostnameComp.setHostname(host);
    }

    pPortComp.setPort(pApp.getPluginPort());
    pLegacyField.setValue(pApp.getLegacyPlugins());
    pVendorField.setText(pApp.getLocalVendor()); 
    pPluginDirComp.setDir(pApp.getPluginDirectory());
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
    pApp.setPluginHostname(pHostnameComp.validateHostname(pApp));

    {
      int port = pPortComp.validatePort(pApp);
      pApp.checkPortConflict(port, "Plugin Port", pApp.getMasterPort(), "Master Port");
      pApp.checkPortConflict(port, "Plugin Port", pApp.getFilePort(), "File Port");
      pApp.checkPortConflict(port, "Plugin Port", pApp.getQueuePort(), "Queue Port");
      pApp.checkPortConflict(port, "Plugin Port", pApp.getJobPort(), "Job Port");
      pApp.setPluginPort(port);
    }

    pApp.setLegacyPlugins(pLegacyField.getValue());
    pApp.setLocalVendor(pVendorField.getText()); 

    pApp.setPluginDirectory(pPluginDirComp.validateDir(pApp));
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -1522751944717545590L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The user/group name fields. 
   */ 
  private JHostnameComp     pHostnameComp;
  private JPortComp         pPortComp;
  private JBooleanField     pLegacyField; 
  private JIdentifierField  pVendorField; 
  private JAbsoluteDirComp  pPluginDirComp;
  
}



