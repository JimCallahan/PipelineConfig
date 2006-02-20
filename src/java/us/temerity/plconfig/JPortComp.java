// $Id: JPortComp.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   P O R T   C O M P                                                                      */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for specifying a port.
 */ 
class JPortComp
  extends JPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JPortComp
  (
   String title, 
   int width
  ) 
  {
    super();
   
    pTitle = title;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 

    add(UIFactory.createPanelLabel(pTitle + ":"));

    add(Box.createRigidArea(new Dimension(0, 3)));

    pField = UIFactory.createIntegerField(null, width, JTextField.LEFT);
    add(pField); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the port. 
   */ 
  public void 
  setPort
  (
   Integer port
  ) 
  {
    pField.setValue(port);
  }

  /**
   * Get the validated port. 
   */ 
  public int
  validatePort
  ( 
   ConfigApp app
  ) 
    throws IllegalConfigException
  {
    return app.validatePort(pField.getValue(), pTitle);
  }
  


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -1532959767135759015L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The title of the setting.
   */ 
  private String  pTitle; 

  /**
   * The port field. 
   */ 
  private JIntegerField  pField; 

}



