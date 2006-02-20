// $Id: JSystemInfoPanel.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   S Y S T E M  I N F O   P A N E L                                                       */
/*------------------------------------------------------------------------------------------*/

/**
 * Operating system information panel.
 */ 
class JSystemInfoPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JSystemInfoPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "System Info:"); 

    /* initialize UI components */ 
    {
      {
	JPanel panel = new JPanel();
	panel.setName("InsetPanel");
	
	Component comps[] = UIFactory.createTitledPanels();
	JPanel tpanel = (JPanel) comps[0];
	JPanel vpanel = (JPanel) comps[1];
	
	pOsNameField = 
	  UIFactory.createTitledTextField(tpanel, "OS Name:", sTSize, 
					  vpanel, "-", sVSize);
	
	UIFactory.addVerticalSpacer(tpanel, vpanel, 3);

	pOsVersionField = 
	  UIFactory.createTitledTextField(tpanel, "OS Version:", sTSize, 
					  vpanel, "-", sVSize);
	 
	UIFactory.addVerticalSpacer(tpanel, vpanel, 3);

	pOsArchField = 
	  UIFactory.createTitledTextField(tpanel, "OS Architecture:", sTSize, 
					  vpanel, "-", sVSize);
	
	panel.add((JComponent) comps[2]); 
	panel.setMaximumSize(panel.getPreferredSize());

	add(panel);
      } 
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());

      addNotes
	("The operating system configuration information for the host running this " +
	 "configuration tool.  It is assumed that all Linux based hosts at your site " + 
	 "will have a similar or compatable configuration.  We recommend running " +
	 "plconfig(1) on the host where you intend to run the Master server deamon " + 
	 "plmaster(1).\n" + 
	 "\n" + 
	 "The information displayed will be used to build the custom version of Pipeline " +
	 "for your site.  No actions need to be performed.");
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
    return "System Information";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    pOsNameField.setText(pApp.getOsName());
    pOsVersionField.setText(pApp.getOsVersion());
    pOsArchField.setText(pApp.getOsArch());
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
    pApp.validateOs(); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 8444669666050048909L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Value fields.
   */ 
  private JTextField  pOsNameField;
  private JTextField  pOsVersionField;
  private JTextField  pOsArchField;


}



