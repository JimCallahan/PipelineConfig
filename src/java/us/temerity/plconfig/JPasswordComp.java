// $Id: JPasswordComp.java,v 1.1 2007/03/21 20:51:46 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   P A S S W O R D   C O M P                                                              */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for specifying and confirming a password.
 */ 
class JPasswordComp
  extends JPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JPasswordComp
  (
   String title, 
   int width
  ) 
  {
    super();
   
    pTitle = title;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 

    add(UIFactory.createPanelLabel(pTitle + " Password:"));

    add(Box.createRigidArea(new Dimension(0, 3)));

    pPasswordField = UIFactory.createPasswordField(width, JTextField.LEFT);
    add(pPasswordField); 

    add(Box.createRigidArea(new Dimension(0, 20)));
    
    add(UIFactory.createPanelLabel("Confirm Password:"));

    add(Box.createRigidArea(new Dimension(0, 3)));
    
    pConfirmField = UIFactory.createPasswordField(width, JTextField.LEFT);
    add(pConfirmField); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Clear the existing passwords.
   */ 
  public void 
  clearPassword() 
  {
    pPasswordField.setText(null);
    pConfirmField.setText(null);
  }

  /**
   * Get the validated password.
   */ 
  public String
  validatePassword() 
    throws IllegalConfigException
  {
    char[] pw1 = null;
    try {
      pw1 = pPasswordField.getPassword();
    }
    catch(NullPointerException ex) {
    }

    char[] pw2 = null;
    try {
      pw2 = pConfirmField.getPassword();
    }
    catch(NullPointerException ex) {
    }

    try {
      if((pw1 == null) || (pw1.length == 0)) 
        throw new IllegalConfigException
          ("You must supply a " + pTitle + " Password!");
      
      if((pw2 == null)  || (pw2.length == 0)) 
        throw new IllegalConfigException
          ("You must confirm your " + pTitle + " Password!");
      
      if(!Arrays.equals(pw1, pw2)) 
        throw new IllegalConfigException
          ("The supplied " + pTitle + " Passwords do not match!");
    }
    catch(IllegalConfigException ex) {
      clearPassword();
      throw ex; 
    }
    
    return new String(pw1);
  }
  


  /*----------------------------------------------------------------------------------------*/
  /*   J C O M P O N E N T   O V E R R I D E S                                              */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Sets whether or not this component is enabled.
   */ 
  public void 
  setEnabled
  (
   boolean enabled
  )
  {
    pPasswordField.setEnabled(enabled);
    pConfirmField.setEnabled(enabled);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  //private static final long serialVersionUID = 
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The title of the setting.
   */ 
  private String  pTitle; 

  /**
   * The password fields. 
   */ 
  private JPasswordField  pPasswordField; 
  private JPasswordField  pConfirmField; 

}



