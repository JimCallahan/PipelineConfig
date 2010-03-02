// $Id: JQueueManangerPanel.java,v 1.2 2007/03/06 04:28:57 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   Q U E U E   M A N A G E R   P A N E L                                                  */
/*------------------------------------------------------------------------------------------*/

/**
 * The Queue Manager daemon panel. 
 */ 
class JQueueManagerPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JQueueManagerPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Queue Manager:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pHostnameComp = new JHostnameComp("Queue Hostname", sHSize);
	  vbox.add(pHostnameComp);

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pHeapSizeComp = new JHeapSizeComp("Queue Heap Size", sHSize);
	  vbox.add(pHeapSizeComp);

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));
	  
	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  pPortComp = new JPortComp("Queue Port", sHSize);
	  vbox.add(pPortComp);
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));

	  pJobPortComp = new JPortComp("Job Port", sHSize);
	  vbox.add(pJobPortComp);

	  hbox.add(vbox);
	}	

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 40)));

      pQueueDirComp = new JAbsoluteDirComp("Queue Directory", sSize);
      add(pQueueDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      pNotesDialog.setMessage
	("Queue Manager Parameters:", 
         "The Queue Manager daemon assigns jobs from the Pipeline queue to the hosts " + 
	 "running Job Manager daemons.  It also provides information about the status of " + 
	 "these jobs to both the Master Manager and Pipeline client programs.  You must " +
	 "specify the host where the Queue Manager will run and the network ports listened " +
	 "to be both the Queue and Job Manager daemons.  You may also specify a size of " + 
	 "the Java Runtime heap.  The heap size should be at least (128M), but may need to " +
	 "be larger depending on the size of your facility and the available memory on the " +
	 "host which will run the Queue Manager.  For optimal performance, as much memory " + 
         "as possible should be dedicated to this daemon.\n" +
	 "\n" + 
	 "The Queue Directory is where the Queue Manager daemon stores the persistent " + 
	 "copies of jobs and other queue related database information.  This directory " + 
	 "should reside on a local filesystem of the host which will run the Queue " + 
	 "Manager.  Usually this directory is set identically to the Node Directory.  " + 
	 "Note that even if the Master Manager and Queue Manager are run on the same " + 
	 "host, it is perfectly ok and normal for the Node Directory and Queue Directory " +
	 "to be identical.");
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
    return "Queue Manager";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    {
      String host = pApp.getQueueHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getServerHostname();
      if((host == null) || (host.length() == 0)) 
	host = pApp.getMasterHostname();
      pHostnameComp.setHostname(host);
    }

    pHeapSizeComp.setHeapSize(pApp.getQueueHeapSize());
    pPortComp.setPort(pApp.getQueuePort());
    pJobPortComp.setPort(pApp.getJobPort());
    pQueueDirComp.setDir(pApp.getQueueDirectory());
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
    pApp.setQueueHostname(pHostnameComp.validateHostname(pApp));
    pApp.setQueueHeapSize(pHeapSizeComp.validateHeapSize(pApp));

    {
      int port = pPortComp.validatePort(pApp);
      pApp.checkPortConflict(port, "Queue Port", pApp.getMasterPort(), "Master Port");
      pApp.checkPortConflict(port, "Queue Port", pApp.getFilePort(), "File Port");
      pApp.setQueuePort(port);
    }

    {
      int port = pJobPortComp.validatePort(pApp);
      pApp.checkPortConflict(port, "Job Port", pApp.getMasterPort(), "Master Port");
      pApp.checkPortConflict(port, "Job Port", pApp.getFilePort(), "File Port");
      pApp.checkPortConflict(port, "Job Port", pApp.getQueuePort(), "Queue Port");
      pApp.setJobPort(port);
    }

    pApp.setQueueDirectory(pQueueDirComp.validateDir(pApp)); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -3488808426166381462L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The user/group name fields. 
   */ 
  private JHostnameComp     pHostnameComp;
  private JPortComp         pPortComp;
  private JPortComp         pJobPortComp;
  private JHeapSizeComp     pHeapSizeComp;
  private JAbsoluteDirComp  pQueueDirComp;
  
}



