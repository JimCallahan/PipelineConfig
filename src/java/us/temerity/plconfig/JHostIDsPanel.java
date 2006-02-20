// $Id: JHostIDsPanel.java,v 1.2 2006/02/20 20:13:21 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;
import java.math.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   S E R V E R   H O S T   I D S   P A N E L                                              */
/*------------------------------------------------------------------------------------------*/

/**
 * The host IDs.
 */ 
class JServerHostIDsPanel
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
  JServerHostIDsPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Server Host IDs:");
    
    /* initialize UI components */ 
    {
      {
	JPanel panel = new JPanel();
	pIDsPanel = panel;
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 
	
	{
	  Component comps[] = UIFactory.createTitledPanels();
	  JPanel tpanel = (JPanel) comps[0];
	  JPanel vpanel = (JPanel) comps[1];
	  
	  tpanel.add(Box.createRigidArea(new Dimension(sTSize, 40)));
	  vpanel.add(Box.createRigidArea(new Dimension(sVSize, 40))); 
	  
	  UIFactory.addVerticalGlue(tpanel, vpanel);

	  panel.add(comps[2]);
	}

	{
	  JScrollPane scroll = new JScrollPane(panel);
	  
	  scroll.setHorizontalScrollBarPolicy
	    (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	  scroll.setVerticalScrollBarPolicy
	    (ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

	  Dimension size = new Dimension(600, 180);
	  scroll.setMinimumSize(size); 
	  scroll.setPreferredSize(size);
	  scroll.setMaximumSize(size); 

	  scroll.getVerticalScrollBar().setUnitIncrement(23);

	  add(scroll);
	}
      }

      add(Box.createRigidArea(new Dimension(0, 10)));

      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  JButton btn = new JButton("Compute IDs...");
	  
	  btn.setName("ValuePanelButton");

	  Dimension size = new Dimension(120, 23);
	  btn.setMinimumSize(size);
	  btn.setPreferredSize(size);
	  btn.setMaximumSize(size); 
	 
	  btn.addActionListener(this);
	  btn.setActionCommand("compute");

	  hbox.add(btn);
	}

	hbox.add(Box.createRigidArea(new Dimension(10, 0)));

	{
	  JButton btn = new JButton("Load IDs...");
	  
	  btn.setName("ValuePanelButton");

	  Dimension size = new Dimension(120, 23);
	  btn.setMinimumSize(size);
	  btn.setPreferredSize(size);
	  btn.setMaximumSize(size); 
	 
	  btn.addActionListener(this);
	  btn.setActionCommand("browse");

	  hbox.add(btn);
	}

	hbox.add(Box.createHorizontalGlue());

	add(hbox);
      }
      
      add(Box.createRigidArea(new Dimension(0, 40)));
      add(Box.createVerticalGlue());
      
      addNotes
	("You must provide Pipeline Host IDs for hosts which will run the Master Manager, " + 
	 "File Manager and Queue Manager server daemons.  These IDs are generated by " + 
	 "running plid(1) on each of these server hosts.\n" + 
	 "\n" +
	 "If you can login to all of the specified Pipeline servers using SSH without " + 
	 "supplying a password, you can simply press the Compute IDs button to determine " + 
	 "the host IDs.  Otherwise, you will need to manually create a host IDs file " + 
	 "which can then be read by pressing the Load IDs button.  See the man page for " + 
	 "plconfig(1) and plid(1) for details about manual generation of host IDs.");
    }
    
    pBrowseDialog = 
      new JFileSelectDialog("Load Host IDs", "Select Host IDs File:", 
			    "Host IDs:", 60, "Load");
    pBrowseDialog.updateTargetFile(new File(System.getProperty("user.dir"))); 
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
    return "Server Host IDs";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  { 
    pIDsPanel.removeAll();

    Component comps[] = UIFactory.createTitledPanels();
    JPanel tpanel = (JPanel) comps[0];
    JPanel vpanel = (JPanel) comps[1];
      
    TreeMap<String,BigInteger> hostIDs = pApp.getHostIDs(); 
    if(hostIDs == null) 
      hostIDs = new TreeMap<String,BigInteger>();
    if(hostIDs.isEmpty()) {
      hostIDs.put(pApp.getMasterHostname(), null);
      hostIDs.put(pApp.getFileHostname(), null);
      hostIDs.put(pApp.getQueueHostname(), null);
    }

    {
      boolean first = true;
      for(String host : hostIDs.keySet()) {
	if(!first) 
	  UIFactory.addVerticalSpacer(tpanel, vpanel, 3);
	first = false;

	BigInteger id = hostIDs.get(host);
	String value = "-"; 
	if(id != null) 
	  value = id.toString();

	UIFactory.createTitledTextField(tpanel, host + ":", sTSize, 
					vpanel, value, sVSize);
      }

      UIFactory.addVerticalGlue(tpanel, vpanel);
    }

    pIDsPanel.add(comps[2]);
    
    pIDsPanel.revalidate();
    pIDsPanel.repaint();
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
    pApp.validateHostIDs();
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
    if(cmd.equals("browse")) 
      doBrowse();
    else if(cmd.equals("compute")) 
      doCompute();
  }


  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Browse for a 
   */
  public void 
  doBrowse() 
  {
    pBrowseDialog.setVisible(true);

    if(pBrowseDialog.wasConfirmed()) {
      try {
	pApp.readHostIDs(pBrowseDialog.getSelectedFile());
      }
      catch(IllegalConfigException ex) {
	pApp.showErrorDialog(ex);
      }
    }

    updatePanel();
  }

  /**
   * Run plid(1) on the servers using SSH and collect the results.
   */
  public void 
  doCompute()
  {
    try {
      TreeSet<String> hosts = new TreeSet<String>();
      hosts.add(pApp.getMasterHostname());
      hosts.add(pApp.getFileHostname());
      hosts.add(pApp.getQueueHostname());
      
      TreeMap<String,BigInteger> hostIDs = new TreeMap<String,BigInteger>();
      for(String host : hosts) {
	try {
	  String args[] = { 
	    "ssh", host, 
	    ("PATH=/usr/bin:" + pApp.getJavaHome() + "/bin; plid")
	  };
	  
	  Process proc = Runtime.getRuntime().exec(args);
	  
	  InputStream in = proc.getInputStream();
	  byte buf[] = new byte[8192];
	  int num = in.read(buf, 0, buf.length);
	  in.close();
	  
	  if(num == -1) 
	    throw new IOException();
	  
	  String output = new String(buf, 0, num-1);
	  String lines[] = output.split("\n");
	  int lk;
	  for(lk=0; lk<lines.length; lk++) {
	    String id = null;
	    String parts[] = lines[lk].split(" ");
	    int wk;
	    for(wk=0; wk<parts.length; wk++) {
	      if(parts[wk].length() > 0) {
		if(id == null) {
		  id = parts[wk];
		}
		else {
		  hostIDs.put(id, new BigInteger(parts[wk]));
		  id = null;
		}
	      }
	    }
	  }
	  
	  if(proc.waitFor() != 0) 
	    throw new IOException();
	}
	catch(Exception ex) {
	  throw new IllegalConfigException
	    ("Unable to run plid(1) on the server (" + host + ") using SSH!");
	}
      }

      pApp.setHostIDs(hostIDs);
    }
    catch(IllegalConfigException ex) {
      pApp.showErrorDialog(ex);
    }

    updatePanel();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -4449925918785933033L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The parent of the hostname/IDs panel.
   */ 
  private JPanel  pIDsPanel; 

  /**
   * The file browser dialog.
   */ 
  private JFileSelectDialog  pBrowseDialog; 

}



