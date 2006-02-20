// $Id: JBaseConfigPanel.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   B A S E   C O N F I G   P A N E L                                                      */
/*------------------------------------------------------------------------------------------*/

/**
 * Base class of all site profile configuration panels.
 */
abstract 
class JBaseConfigPanel
  extends JPanel
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JBaseConfigPanel
  (
   ConfigApp app, 
   String title
  ) 
  {
    super();

    pApp = app;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 

    {
      Box hbox = new Box(BoxLayout.X_AXIS);  
      
      {
	JLabel label = new JLabel(title);
	label.setName("DialogHeaderLabel");
	
	hbox.add(label);
      }
      
      hbox.add(Box.createHorizontalGlue());
      
      add(hbox);
    }

    add(Box.createRigidArea(new Dimension(0, 20)));
    
  }


  /*----------------------------------------------------------------------------------------*/
  /*   P A N E L   O P S                                                                    */
  /*----------------------------------------------------------------------------------------*/
    
  /**
   * The unique name of the panel.
   */ 
  public abstract String
  getPanelTitle(); 

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public abstract void 
  updatePanel();   
  
  /**
   * Validate the current UI values and update the site profile settings.
   * 
   * @throws IllegalConfigException
   *   If the current UI values are invalid.
   */ 
  public abstract void 
  updateProfile() 
    throws IllegalConfigException; 



  /*----------------------------------------------------------------------------------------*/
  /*   H E L P E R S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Add the panel notes.
   */ 
  protected void
  addNotes
  (
   String text 
  ) 
  {
    JPanel body = new JPanel(new BorderLayout());

    Dimension size = new Dimension(600, 120);
    body.setMinimumSize(size);
    body.setMaximumSize(size);
    body.setPreferredSize(size);

    {
      JTextArea area = new JTextArea(text, 0, 0); 
      
      area.setName("TextArea");
      
      area.setLineWrap(true);
      area.setWrapStyleWord(true);
      area.setEditable(false);
      
      area.setFocusable(true);

    
      {
	JScrollPane scroll = new JScrollPane(area);
	scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	
	body.add(scroll);
      } 
    }

    add(body);
  }


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  protected static final int sSize  = 600; 
  protected static final int sHSize = 270; 
  protected static final int sTSize = 160; 
  protected static final int sVSize = 400; 
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The parent application instance. 
   */ 
  protected ConfigApp  pApp; 

}



 
