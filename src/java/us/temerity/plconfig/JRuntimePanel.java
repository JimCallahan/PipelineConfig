// $Id: JRuntimePanel.java,v 1.2 2007/01/05 21:37:55 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   R U N T I M E   P A N E L                                                              */
/*------------------------------------------------------------------------------------------*/

/**
 * Java Runtime information panel.
 */ 
class JRuntimePanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JRuntimePanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Java Runtime Support:"); 

    /* initialize UI components */ 
    {
      {
	JPanel panel = new JPanel();
	panel.setName("InsetPanel");
	
	Component comps[] = UIFactory.createTitledPanels();
	JPanel tpanel = (JPanel) comps[0];
	JPanel vpanel = (JPanel) comps[1];
	
	pJavaHomeField = 
	  UIFactory.createTitledTextField(tpanel, "Java Home:", sTSize, 
					  vpanel, "-", sVSize);
	
	UIFactory.addVerticalSpacer(tpanel, vpanel, 12);

	pJavaVendorField = 
	  UIFactory.createTitledTextField(tpanel, "Java Vendor:", sTSize, 
					  vpanel, "-", sVSize);
	
	UIFactory.addVerticalSpacer(tpanel, vpanel, 3);

	pJavaNameField = 
	  UIFactory.createTitledTextField(tpanel, "Java Name:", sTSize, 
					  vpanel, "-", sVSize);
	 
	UIFactory.addVerticalSpacer(tpanel, vpanel, 3);

	pJavaVersionField = 
	  UIFactory.createTitledTextField(tpanel, "Java Version:", sTSize, 
					  vpanel, "-", sVSize);
	 
	UIFactory.addVerticalSpacer(tpanel, vpanel, 12);

	pClassVersionField = 
	  UIFactory.createTitledTextField(tpanel, "Class Version:", sTSize, 
					  vpanel, "-", sVSize);

	panel.add((JComponent) comps[2]); 
	panel.setMaximumSize(panel.getPreferredSize());

	add(panel);
      } 
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());

      addNotes
	("Information about the Java Runtime Environment (J2SE 5.0) used to to run this " + 
	 "configuration tool.  It is assumed that you intend to run all Pipeline server " + 
	 "daemons and clients programs in an identical Java environment as displayed " + 
	 "above. To insure that this is the case, we recommend running plconfig(1) on the " +
	 "host where you intend to the Master server deamon plmaster(1).\n" + 
	 "\n" +
	 "J2SE 5.0 can be downloaded from: http://java.sun.com/j2se/1.5.0/download.jsp\n");
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
    return "Java Runtime Support"; 
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    pJavaHomeField.setText(pApp.getJavaHome());
    pJavaVendorField.setText(pApp.getJavaVendor());
    pJavaNameField.setText(pApp.getJavaName());
    pJavaVersionField.setText(pApp.getJavaVersion());
    pClassVersionField.setText(pApp.getJavaClassVersion());
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
    pApp.validateJavaRuntime(); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -4357643956935803655L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Value fields.
   */ 
  private JTextField  pJavaHomeField;
  private JTextField  pJavaVendorField;
  private JTextField  pJavaNameField;
  private JTextField  pJavaVersionField;
  private JTextField  pClassVersionField;

}



