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
/*   A B S O L U T E   J A R S   C O M P                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for specifying a list of absolute paths to JAR files.
 */ 
class JAbsoluteJarsComp
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
  JAbsoluteJarsComp
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
  JAbsoluteJarsComp
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
      pList = new JList(new DefaultListModel());
      pList.setCellRenderer(new JListCellRenderer());      

      {
        JScrollPane scroll = 
          UIFactory.createScrollPane
          (pList, 
           ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED, 
           ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
           new Dimension(150, 150), new Dimension(width, 19*5), null);
        
        add(scroll);
      }
    }

    add(Box.createRigidArea(new Dimension(0, 10)));

    {
      Box hbox = new Box(BoxLayout.X_AXIS);
      
      {
        JButton btn = new JButton("Add...");
        pAddButton = btn;
	
        btn.setName("ValuePanelButton");
        
        Dimension size = new Dimension(120, 23);
        btn.setMinimumSize(size);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size); 
	
        btn.addActionListener(this);
        btn.setActionCommand("add-jar");
        
        hbox.add(btn);
      }
      
      hbox.add(Box.createRigidArea(new Dimension(10, 0)));

      {
        JButton btn = new JButton("Remove");
        pRemoveButton = btn;
	
        btn.setName("ValuePanelButton");
        
        Dimension size = new Dimension(120, 23);
        btn.setMinimumSize(size);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size); 
	
        btn.addActionListener(this);
        btn.setActionCommand("remove-jars");
        
        hbox.add(btn);
      }
      
      hbox.add(Box.createHorizontalGlue());
      
      add(hbox);
    }

    if(browseable) {
      pBrowseDialog = 
        new JFileSelectDialog("Select Java Library", "Select Library:", 
                              "Library:", 54, "Select");
      pBrowseDialog.updateTargetFile(new File("/"));
    }
    else {
      pAddDialog = 
        new JNewPathDialog((Frame) null, "Add Java Library", "Add Library:", null, "Add"); 
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the paths to the JAR libraries.
   */ 
  public void 
  setJars
  (
   Collection<File> files
  ) 
  {
    DefaultListModel model = (DefaultListModel) pList.getModel();
    model.clear();

    for(File file : files) 
      model.addElement(file);
  }

  /**
   * Get the validated the absolute paths to the JAR libraries. 
   */ 
  public Collection<File>
  validateJars
  ( 
   ConfigApp app
  ) 
    throws IllegalConfigException
  {
    LinkedList<File> jars = new LinkedList<File>();

    DefaultListModel model = (DefaultListModel) pList.getModel();
    int wk; 
    for(wk=0; wk<model.getSize(); wk++) {
      File file = (File) model.getElementAt(wk); 
      jars.add(app.validateAbsolutePath(file, pTitle)); 
    }

    return jars; 
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
    pList.setEnabled(enabled);
    pAddButton.setEnabled(enabled);
    pRemoveButton.setEnabled(enabled);
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
    if(cmd.equals("add-jar")) {
      File jar = null;
      if(pBrowseDialog != null) {
        pBrowseDialog.setVisible(true);
        if(pBrowseDialog.wasConfirmed()) 
          jar = pBrowseDialog.getSelectedFile();          
      }
      else if(pAddDialog != null) {
        pAddDialog.setVisible(true);
        if(pAddDialog.wasConfirmed()) {
          String path = pAddDialog.getPath(); 
          if(path != null) 
            jar = new File(path);
        }
      }
      
      if(jar != null) {
        if(jar.getPath().endsWith(".jar")) {
          DefaultListModel model = (DefaultListModel) pList.getModel();
          model.addElement(jar);
        }
        else {
          UIFactory.showErrorDialog
            ("Illegal Path", "The given Java Library path (" + jar + ") is not a JAR file."); 
        }
      }
    }
    else if(cmd.equals("remove-jars")) { 
      DefaultListModel model = (DefaultListModel) pList.getModel();

      LinkedList<File> jars = new LinkedList<File>();
      int wk; 
      for(wk=0; wk<model.getSize(); wk++) {
        if(!pList.isSelectedIndex(wk)) {
          File file = (File) model.getElementAt(wk); 
          if(file != null) 
            jars.add(file);
        }
      }

      model.clear();
      for(File file : jars) 
        model.addElement(file);
    }
  }




  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 2412182176294989300L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The title of the setting.
   */ 
  private String  pTitle; 

  /**
   * The list of JARs component.
   */ 
  private JList  pList; 

  /**
   * The JAR add/remove buttons.
   */ 
  private JButton  pAddButton; 
  private JButton  pRemoveButton; 

  /**
   * The JAR browsing dialog.
   */ 
  private JFileSelectDialog  pBrowseDialog;

  /**
   * JAR naming dialog.
   */ 
  private JNewPathDialog  pAddDialog; 

}



