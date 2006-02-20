// $Id: JAbsoluteDirComp.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   A B S O L U T E   D I R   C O M P                                                      */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for setting an absolute directory path.
 */ 
class JAbsoluteDirComp
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
  JAbsoluteDirComp
  (
   String title, 
   int width
  ) 
  {
    this(title, width, true);
  }

  /** 
   * Construct a new panel.
   */ 
  public
  JAbsoluteDirComp
  (
   String title, 
   int width, 
   boolean browseable
  ) 
  {
    super();
   
    pTitle = title;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); 

    add(UIFactory.createPanelLabel(pTitle + ":"));

    add(Box.createRigidArea(new Dimension(0, 3)));

    {
      if(browseable) {
	JComponent[] comps = 
	  UIFactory.createBrowsablePathField(null, width, JTextField.LEFT, this, "browse");
	pField = (JPathField) comps[0];
	
	add(comps[2]); 
      }   
      else {
	pField = UIFactory.createPathField(null, width, JTextField.LEFT);
	add(pField); 
      }
    
      pField.addActionListener(this);
      pField.setActionCommand("entered");
    }

    if(browseable) 
      pBrowseDialog = 
	new JFileSelectDialog("Select Directory", "Select " + pTitle + ":", "Select");
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
   File file
  ) 
  {
    File dir = new File("/");

    if(file != null) {
      if(file.isAbsolute()) 
	dir = file; 
    }
    
    pField.setText(dir.getPath());  
    if(pBrowseDialog != null) 
      pBrowseDialog.updateTargetFile(dir);
  }

  public void 
  setDir
  (
   String path
  ) 
  {
    if(path != null) 
      setDir(new File(path));
    else 
      setDir((File) null);
  }

  /**
   * Get the validated the absolute directory path. 
   */ 
  public File
  validateDir
  ( 
   ConfigApp app
  ) 
    throws IllegalConfigException
  {
    return app.validateAbsolutePath(pField.getText(), pTitle);
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
    if(cmd.equals("browse")) {
      pBrowseDialog.setVisible(true);
      if(pBrowseDialog.wasConfirmed()) 
	setDir(pBrowseDialog.getSelectedFile());
    }
    else if(cmd.equals("entered")) { 
      setDir(pField.getText());
    }
  }




  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -1482539682335580518L;
  


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
  private JPathField  pField; 

  /**
   * The directory browsing dialog.
   */ 
  private JFileSelectDialog  pBrowseDialog;

}



