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
/*   T H U M B N A I L   M A N A G E R   P A N E L                                          */
/*------------------------------------------------------------------------------------------*/

/**
 * The Thumbnail Manager daemon panel. 
 */ 
class JThumbnailManagerPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JThumbnailManagerPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Thumbnail Manager:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pHostnameComp = new JHostnameComp("Thumbnail Hostname", sHSize);
	  vbox.add(pHostnameComp);

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));
	  
	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pPortComp = new JPortComp("Thumbnail Port", sHSize);
	  vbox.add(pPortComp);

	  hbox.add(vbox);
	}	

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 40)));

      pThumbnailDirComp = new JAbsoluteDirComp("Thumbnail Directory", sSize);
      add(pThumbnailDirComp);

      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
       
      pNotesDialog.setMessage
	("Thumbnail Manager Parameters:", 
         "The Thumbnail Manager daemon is responsible for generating the thumbnail " + 
         "images used to represent nodes in all Pipeline programs.  Typically, this " + 
	 "daemon is run on a dedicated host, but this is not strictly required.\n" + 
	 "\n" + 
         "The Thumbnail Directory is where the Thumbnail Manager daemon saves the " + 
         "thumbnail images it generates. This directory should reside on a local " + 
         "filesystem of the host which will run the Thumbnail Manager."); 
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
    return "Thumbnail Manager";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    {
      String host = pApp.getThumbnailHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getServerHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getMasterHostname();
      pHostnameComp.setHostname(host);
    }

    pPortComp.setPort(pApp.getThumbnailPort());
    pThumbnailDirComp.setDir(pApp.getThumbnailDirectory());
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
    pApp.setThumbnailHostname(pHostnameComp.validateHostname(pApp));

    {
      int port = pPortComp.validatePort(pApp);
      pApp.checkPortConflict(port, "Thumbnail Port", pApp.getMasterPort(), "Master Port");
      pApp.checkPortConflict(port, "Thumbnail Port", pApp.getFilePort(), "File Port");
      pApp.checkPortConflict(port, "Thumbnail Port", pApp.getQueuePort(), "Queue Port");
      pApp.checkPortConflict(port, "Thumbnail Port", pApp.getJobPort(), "Job Port");
      pApp.checkPortConflict(port, "Thumbnail Port", pApp.getPluginPort(), "Plugin Port");
      pApp.setThumbnailPort(port);
    }

    pApp.setThumbnailDirectory(pThumbnailDirComp.validateDir(pApp));
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  //private static final long serialVersionUID =



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The user/group name fields. 
   */ 
  private JHostnameComp     pHostnameComp;
  private JPortComp         pPortComp;
  private JAbsoluteDirComp  pThumbnailDirComp;
  
}



