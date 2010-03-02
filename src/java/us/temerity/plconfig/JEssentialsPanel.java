// $Id: JEssentialsPanel.java,v 1.6 2008/01/10 01:30:39 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   E S S E N T I A L S   P A N E L                                                        */
/*------------------------------------------------------------------------------------------*/

/**
 * The essential site configuration settings.
 */ 
class JEssentialsPanel
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
  JEssentialsPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Essentials:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  vbox.add(UIFactory.createPanelLabel("License Type:"));
      
	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));

	  {
	    ArrayList<String> values = new ArrayList<String>();
	    values.add("-");
	    values.add("60-Day Evaluation");
	    values.add("Limited"); 
	    values.add("Perpetual");
	    
	    JCollectionField field = 
	      UIFactory.createCollectionField(values, sHSize);
	    pLicenseTypeField = field;

	    field.addActionListener(this);
	    field.setActionCommand("license-changed");
	    
	    vbox.add(pLicenseTypeField);
	  }

	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));
	
	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  vbox.add(UIFactory.createPanelLabel("License Expiration Date:"));
	  
	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));
	  
	  {
	    JDateField field = 
	      UIFactory.createDateField(null, sHSize, JTextField.CENTER);
	    pLicenseEndField = field;
	    
	    field.setEnabled(false);
	    field.addActionListener(this);
	    field.setActionCommand("date-entered");
	    
	    vbox.add(field);
	  }

	  hbox.add(vbox);
	}

	add(hbox);
      }

      add(Box.createRigidArea(new Dimension(0, 50)));

      pRootDirComp = new JAbsoluteDirComp("Root Install Directory", sSize);
      add(pRootDirComp);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      pNotesDialog.setMessage
	("Essential Parameters:", 
         "You must choose the type of license for your site.  A 60-Day Evaluation " +
	 "license expires 60 days from now, while a Limited license expires at the date " + 
	 "specified.  Finally, a Perpetual license will never expire.\n\n" + 
	 "You must also specify the location where Pipeline will be installed at your " + 
	 "site as the Root Install Directory.  This directory should be on a network " + 
	 "filesystem and be accessed using this given path on all hosts at your site.");
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
    return "Essentials";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  { 
    String ltype = pApp.getLicenseType(); 
    if(ltype == null) {
      pLicenseTypeField.setSelectedIndex(0);
      pLicenseEndField.setDate(null);       
    }
    else if(ltype.equals("60-Day Evaluation")) {
      pLicenseTypeField.setSelectedIndex(1);
      pLicenseEndField.setDate(pApp.getLicenseEnd());
    }
    else if(ltype.equals("Limited")) {
      pLicenseTypeField.setSelectedIndex(2);
      pLicenseEndField.setDate(pApp.getLicenseEnd());
    }
    else if(ltype.equals("Perpetual")) {
      pLicenseTypeField.setSelectedIndex(3);
      pLicenseEndField.setDate(null); 
    }

    {
      File dir = pApp.getRootDirectory();
      if(dir == null) 
	dir = new File("/base/apps");
      pRootDirComp.setDir(dir);
    }
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
    validateLicense(); 
    if(pLicenseTypeField.getSelectedIndex() == 0) 
      throw new IllegalConfigException
	("A license type must be selected!");
    
    pApp.setRootDirectory(pRootDirComp.validateDir(pApp)); 
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
    super.actionPerformed(e); 

    String cmd = e.getActionCommand();
    if(cmd.equals("license-changed")) 
      doLicenseChanged();
    else if(cmd.equals("date-entered")) 
      doDateEntered();
  }


  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Update the UI to reflect a change in license type.
   */
  public void 
  doLicenseChanged() 
  {
    try {
      validateLicense();
    }
    catch(IllegalConfigException ex) {
      pApp.showErrorDialog(ex);
      pLicenseTypeField.setSelectedIndex(0);
    }
  }

  /**
   * Validate the license end field.
   */
  public void 
  doDateEntered() 
  {
    Date date = pLicenseEndField.getDate();
    if(date == null) 
      date = new Date();
    pLicenseEndField.setDate(date);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   H E L P E R S                                                                        */
  /*----------------------------------------------------------------------------------------*/
  
  /** 
   * Validate the license type/date.
   */
  private void
  validateLicense() 
    throws IllegalConfigException
  {
    try {
      switch(pLicenseTypeField.getSelectedIndex()) {
      case 0:
	pApp.clearLicense();
	pLicenseEndField.setDate(null);
	pLicenseEndField.setEnabled(false);
	break;

      case 1:
	pApp.setEvaluationLicense();
	pLicenseEndField.setDate(pApp.getLicenseEnd());
	pLicenseEndField.setEnabled(false);
	break;
	
      case 2: 
	{
	  Date date = pLicenseEndField.getDate();
	  if(date == null) {
	    Date now = null; 
	    try {
	      now = new Date(pApp.getNetTime());
	    }
	    catch(IllegalConfigException ex) {
	      now = new Date();
	    }

	    String ltype = pApp.getLicenseType();
	    if((ltype != null) && ltype.equals("Limited")) 
	      date = pApp.getLicenseEnd();

	    if((date == null) || (date.compareTo(now) < 0)) 
	      date = new Date(now.getTime() + 31536000000L);
	  }

	  pApp.setLimitedLicense(date);
	  pLicenseEndField.setDate(pApp.getLicenseEnd());
	  pLicenseEndField.setEnabled(true);
	}
	break;
	
      case 3:
	pApp.setPerpetualLicense();
	pLicenseEndField.setDate(null);
	pLicenseEndField.setEnabled(false);
      }
    }
    catch(Exception ex) {
      throw new IllegalConfigException(ex.getMessage());
    }
  }




  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 391622839332332306L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The essential fields. 
   */ 
  private JCollectionField   pLicenseTypeField; 
  private JDateField         pLicenseEndField; 
  private JAbsoluteDirComp   pRootDirComp; 

}



