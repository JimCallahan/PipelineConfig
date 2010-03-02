// $Id: JErrorDialog.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig.ui;

import us.temerity.plconfig.*;

import java.awt.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   N O T E S     D I A L O G                                                              */
/*------------------------------------------------------------------------------------------*/

/**
 * Displays notes. 
 */ 
public 
class JNotesDialog
  extends JBaseDialog
{
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct a new dialog.
   */ 
  public 
  JNotesDialog() 
  {
    super((Frame) null, "Notes", false);

    /* create dialog body components */ 
    {
      JPanel body = new JPanel(new BorderLayout());
      body.setName("MainDialogPanel");

      body.setMinimumSize(new Dimension(300, 180));

      {
	JTextArea area = new JTextArea(15, 45); 
	pMessageArea = area;

	area.setName("TextArea");

	area.setLineWrap(true);
	area.setWrapStyleWord(true);
	area.setEditable(false);

	area.setFocusable(true);
      }
      
      {
        JScrollPane scroll = 
          UIFactory.createScrollPane
          (pMessageArea, 
           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, 
           JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
           null, null, null); 
	
	body.add(scroll);
      }

      super.initUI("Notes:", body, null, null, null, "Close");
      pack();
    }  
  }


  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the title and text of the error message.
   */ 
  public void 
  setMessage
  (
   String title, 
   String msg
  ) 
  {
    pHeaderLabel.setText(title);
    pMessageArea.setText(msg);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -2581681349282137012L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The error message text.
   */ 
  private JTextArea pMessageArea;

  /** 
   * The scroll panel containing the message text.
   */ 
  private JScrollPane pScroll; 

}
