// $Id: JMasterManagerPanel.java,v 1.2 2007/03/06 04:28:57 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   M A S T E R   M A N A G E R   P A N E L                                                */
/*------------------------------------------------------------------------------------------*/

/**
 * The Master Manager daemon panel. 
 */ 
class JMasterManagerPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JMasterManagerPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Master Manager:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pHostnameComp = new JHostnameComp("Master Hostname", sHSize);
	  vbox.add(pHostnameComp);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pHeapSizeComp = new JHeapSizeComp("Master Heap Size", sHSize);
	  vbox.add(pHeapSizeComp);

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));
	  
	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pPortComp = new JPortComp("Master Port", sHSize);
	  vbox.add(pPortComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 60)));

	  hbox.add(vbox);
	}	

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 40)));

      pNodeDirComp = new JAbsoluteDirComp("Node Directory", sSize);
      add(pNodeDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("The Master Manager daemon is responsible for managing all Pipeline node " + 
	 "operations including dispatching jobs.  You must specify the host where this " +
	 "daemon will be run, the network port on which it will listen and the maxmimum " + 
	 "size of the Java Runtime heap.  The Master Hostname must be reachable by all " + 
	 "hosts at your site which will run Pipeline client programs.   The heap size " + 
	 "should be at least (128M), but for optimal performance should be set as large " + 
	 "as possible based on the amount of memory the host which will run the Master " + 
	 "Manager can dedicate to this daemon.\n" +
	 "\n" + 
	 "The Node Directory is where the Master Manager daemon stores the persistent " +
	 "copies of Pipeline nodes and other database information.  This directory should " + 
	 "reside on a local filesystem of the host which will run the Master Manager.");
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
    return "Master Manager";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    {
      String host = pApp.getMasterHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getServerHostname();
      pHostnameComp.setHostname(host);
    }

    pHeapSizeComp.setHeapSize(pApp.getMasterHeapSize());
    pPortComp.setPort(pApp.getMasterPort());
    pNodeDirComp.setDir(pApp.getNodeDirectory());
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
    pApp.setMasterHostname(pHostnameComp.validateHostname(pApp));
    pApp.setMasterHeapSize(pHeapSizeComp.validateHeapSize(pApp));
    pApp.setMasterPort(pPortComp.validatePort(pApp));    
    pApp.setNodeDirectory(pNodeDirComp.validateDir(pApp)); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 8849513683495959117L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The user/group name fields. 
   */ 
  private JHostnameComp     pHostnameComp;
  private JPortComp         pPortComp;
  private JHeapSizeComp     pHeapSizeComp;
  private JAbsoluteDirComp  pNodeDirComp;
  
}



