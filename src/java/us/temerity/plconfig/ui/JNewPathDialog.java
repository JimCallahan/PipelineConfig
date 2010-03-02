// $Id: JNewIdentifierDialog.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig.ui;

import us.temerity.plconfig.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   N E W   P A T H   D I A L O G                                                          */
/*------------------------------------------------------------------------------------------*/

/**
 * Queries the user for a path.
 */ 
public 
class JNewPathDialog
  extends JBaseDialog
  implements DocumentListener
{
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct a new dialog owned by another dialog. <P> 
   * 
   * @param owner
   *   The parent frame.
   * 
   * @param title
   *   The title of the dialog.
   * 
   * @param fieldTitle
   *   The title of the text field.
   * 
   * @param path
   *   The initial path. 
   * 
   * @param confirm
   *   The title of the confirm button.
   */ 
  public 
  JNewPathDialog
  (
   Frame owner,       
   String title,  
   String fieldTitle, 
   String path, 
   String confirm
  )
  {
    super(owner, title);
    initUI(fieldTitle, path, confirm);
  }

  /**
   * Construct a new dialog owned by another dialog. <P> 
   * 
   * @param owner
   *   The parent dialog.
   * 
   * @param title
   *   The title of the dialog.
   * 
   * @param fieldTitle
   *   The title of the text field.
   * 
   * @param path
   *   The initial path. 
   * 
   * @param confirm
   *   The title of the confirm button.
   */ 
  public 
  JNewPathDialog
  (
   Dialog owner,       
   String title,  
   String fieldTitle, 
   String path, 
   String confirm
  )
  {
    super(owner, title);
    initUI(fieldTitle, path, confirm);
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Initialize the common user interface components. <P> 
   ( 
   * @param fieldTitle
   *   The title of the text field.
   * 
   * @param path
   *   The initial path. 
   * 
   * @param confirm
   *   The title of the confirm button.
   */ 
  private void 
  initUI
  (      
   String fieldTitle, 
   String path, 
   String confirm
  ) 
  {
    /* create dialog body components */ 
    {
      JPanel body = new JPanel();
      body.setName("MainDialogPanel");

      body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

      body.add(UIFactory.createPanelLabel(fieldTitle));
      
      body.add(Box.createRigidArea(new Dimension(0, 4)));
      
      {
	JPathField field = UIFactory.createPathField(path, 350, JLabel.LEFT);
	pPathField = field;
	
	field.getDocument().addDocumentListener(this);
	
	body.add(field);
      }
	  
      super.initUI(null, body, confirm, null, null, "Cancel");
    }  

    pConfirmButton.setEnabled((path != null) && (path.length() > 0));
    setResizable(false);
  }


  /*----------------------------------------------------------------------------------------*/
  /*   L I S T E N E R S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /*-- DOCUMENT LISTENER METHODS -----------------------------------------------------------*/

  /**
   * Gives notification that an attribute or set of attributes changed.
   */ 
  public void 
  changedUpdate(DocumentEvent e) {}

  /**
   * Gives notification that there was an insert into the document.
   */
  public void
  insertUpdate
  (
   DocumentEvent e
  )
  {
    String path = pPathField.getText();
    pConfirmButton.setEnabled((path != null) && (path.length() > 0));
  }
  
  /**
   * Gives notification that a portion of the document has been removed.
   */
  public void 
  removeUpdate
  (
   DocumentEvent e
  )
  {
    String path = pPathField.getText();
    pConfirmButton.setEnabled((path != null) && (path.length() > 0));    
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Get the new identifier path. 
   */ 
  public String
  getPath() 
  {
    return (pPathField.getText());
  }




  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 8065240837306127465L; 




  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The field containing the new path. <P> 
   */
  protected JPathField  pPathField;

}
