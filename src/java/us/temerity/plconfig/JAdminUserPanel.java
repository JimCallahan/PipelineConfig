// $Id: JAdminUserPanel.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   A D M I N   U S E R   P A N E L                                                        */
/*------------------------------------------------------------------------------------------*/

/**
 * The Pipeline administrator user. 
 */ 
class JAdminUserPanel
  extends JBaseConfigPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JAdminUserPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Pipeline Admin User:");
    
    /* initialize UI components */ 
    {
      add(UIFactory.createPanelLabel("User Name:"));
      
      add(Box.createRigidArea(new Dimension(0, 3)));

      pUserField =
	UIFactory.createIdentifierField("", sSize, JTextField.LEFT);
      add(pUserField);

      add(Box.createRigidArea(new Dimension(0, 20)));

      add(UIFactory.createPanelLabel("Group Name:"));

      add(Box.createRigidArea(new Dimension(0, 3)));

      pGroupField =
	UIFactory.createIdentifierField("", sSize, JTextField.LEFT);
      add(pGroupField);
      
      add(Box.createRigidArea(new Dimension(0, 20)));
      add(Box.createVerticalGlue());
      
      addNotes
	("This special account is used to run the various Pipeline server daemons and " + 
	 "to perform privileged administrative tasks using one of the client programs. " + 
	 "It is important that this account is not used for any other purpose, in " + 
	 "particular that it is not a normal user account.");
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
    return "Pipeline Admin User";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  {
    pUserField.setText(pApp.getPipelineUser());
    pGroupField.setText(pApp.getPipelineGroup());
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
    String user = pUserField.getText();
    int uid = pApp.validatePipelineUser(user);
    pApp.setPipelineUser(user);
    pApp.setPipelineUserID(uid);

    String group = pGroupField.getText();
    int gid = pApp.validatePipelineGroup(user, group);
    pApp.setPipelineGroup(group);
    pApp.setPipelineGroupID(gid);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 7944714558690848906L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The user/group name fields. 
   */ 
  private JIdentifierField  pUserField; 
  private JIdentifierField  pGroupField; 

}



