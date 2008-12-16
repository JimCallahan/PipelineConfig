// $Id: JFileManagerPanel.java,v 1.3 2008/12/16 07:56:54 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   F I L E   M A N A G E R   P A N E L                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * The File Manager daemon panel. 
 */ 
class JFileManagerPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JFileManagerPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "File Manager:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pHostnameComp = new JHostnameComp("File Hostname", sHSize);
	  vbox.add(pHostnameComp);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pHeapSizeComp = new JHeapSizeComp("File Heap Size", sHSize);
	  vbox.add(pHeapSizeComp);

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));
	  
	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pPortComp = new JPortComp("File Port", sHSize);
	  vbox.add(pPortComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 60)));

	  hbox.add(vbox);
	}	

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 40)));

      pProdDirComp = new JAbsoluteDirComp("Production Directory", sSize);
      add(pProdDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("The File Manager daemon is responsible performing all file system operations on " +
	 "behalf of Master Manager daemon.  You must specify the host where this daemon " + 
	 "will be run, the network port on which it will listen and the maxmimum size of " +
	 "the Java Runtime heap.  When run as a seperate process from the Master Manager, " +
	 "this deamon should be run on the network file server or on a host which has a " + 
	 "high bandwidth and low latency connection to the file server.  The File Hostname " +
	 "must be reachable by the Master Manager daemon.  If run as a subprocess of the " + 
	 "Master Manager, these settings will be ignored.  The default heap size of " + 
	 "(128M) is usually adequate.\n" +
	 "\n" + 
	 "The Production Directory is the absolute path to the root directory under " + 
	 "which all production data files will reside.  This directory should reside on " + 
	 "a network accessible file system such that all machines which will run Pipeline " + 
	 "related programs can access the directory using this path.");
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
    return "File Manager";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    {
      String host = pApp.getFileHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getServerHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getMasterHostname();
      pHostnameComp.setHostname(host);
    }

    pHeapSizeComp.setHeapSize(pApp.getFileHeapSize());
    pPortComp.setPort(pApp.getFilePort());
    pProdDirComp.setDir(pApp.getProdDirectory());
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
    pApp.setFileHostname(pHostnameComp.validateHostname(pApp));
    pApp.setFileHeapSize(pHeapSizeComp.validateHeapSize(pApp));

    {
      int port = pPortComp.validatePort(pApp);
      pApp.checkPortConflict(port, "File Port", pApp.getMasterPort(), "Master Port");
      pApp.setFilePort(port);
    }

    pApp.setProdDirectory(pProdDirComp.validateDir(pApp)); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 6952263007920677825L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The user/group name fields. 
   */ 
  private JHostnameComp     pHostnameComp;
  private JPortComp         pPortComp;
  private JHeapSizeComp     pHeapSizeComp;
  private JAbsoluteDirComp  pProdDirComp;
  
}



