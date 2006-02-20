// $Id: JHeapSizeComp.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   H E A P   S I Z E   C O M P                                                            */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for specifying a heap size.
 */ 
class JHeapSizeComp
  extends JPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JHeapSizeComp
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

    pField = UIFactory.createByteSizeField(null, width, JTextField.LEFT);
    add(pField); 
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the heap size. 
   */ 
  public void 
  setHeapSize
  (
   Long size
  ) 
  {
    pField.setValue(size);
  }

  /**
   * Get the validated heap size. 
   */ 
  public long
  validateHeapSize
  ( 
   ConfigApp app
  ) 
    throws IllegalConfigException
  {
    return app.validateHeapSize(pField.getValue(), pTitle);
  }
  


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -1534844886807815788L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The title of the setting.
   */ 
  private String  pTitle; 

  /**
   * The heap size field. 
   */ 
  private JByteSizeField  pField; 

}



