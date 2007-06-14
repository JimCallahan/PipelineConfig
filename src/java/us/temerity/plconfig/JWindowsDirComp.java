// $Id: JWindowsDirComp.java,v 1.2 2007/06/14 12:57:07 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   W I N D O W S   D I R   C O M P                                                        */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for setting a Windows XP directory path.
 */ 
class JWindowsDirComp
  extends JPanel
  implements ActionListener
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JWindowsDirComp
  (
   String title, 
   int width
  ) 
  {
    this(title, width, false);
  }

  /** 
   * Construct a new panel.
   */ 
  public
  JWindowsDirComp
  (
   String title, 
   int width, 
   boolean isNullOk
  ) 
  {
    super();
   
    pTitle = title;
    pIsNullOk = isNullOk;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 

    add(UIFactory.createPanelLabel(pTitle + ":"));

    add(Box.createRigidArea(new Dimension(0, 3)));

    {
      pField = UIFactory.createEditableTextField(null, width, JTextField.LEFT);
    
      pField.addActionListener(this);
      pField.setActionCommand("entered");

      add(pField); 
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the directory.
   */ 
  public void 
  setDir
  (
   String path
  ) 
  {
    String dir = "C:/";
    if((path != null) || pIsNullOk)
      dir = path; 
    
    pField.setText(dir);  
  }

  /**
   * Get the validated the absolute directory path. 
   */ 
  public String
  validateDir
  ( 
   ConfigApp app
  ) 
    throws IllegalConfigException
  {
    return app.validateWindowsPath(pField.getText(), pTitle, pIsNullOk);
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
    pField.setEnabled(enabled);
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
    if(cmd.equals("entered")) { 
      setDir(pField.getText());
    }
  }




  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 2124936709008506606L; 
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The title of the setting.
   */ 
  private String  pTitle; 

  /**
   * The directory path field. 
   */ 
  private JTextField  pField; 

  /**
   * Whether it is valid to not specify any path.
   */ 
  private boolean  pIsNullOk; 

}



