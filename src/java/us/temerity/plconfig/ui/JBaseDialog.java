// $Id: JBaseDialog.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig.ui;

import us.temerity.plconfig.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*------------------------------------------------------------------------------------------*/
/*   B A S E   D I A L O G                                                                  */
/*------------------------------------------------------------------------------------------*/

/**
 * Base class of all application dialogs.
 */ 
public 
class JBaseDialog
  extends JDialog
  implements ActionListener
{
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Construct a new dialog owned by the main application frame. 
   * 
   * @param title
   *   The title of the dialog window.
   * 
   * @param modal
   *   Is the dialog modal?
   */ 
  protected
  JBaseDialog
  (
   String title,    
   boolean modal
  ) 
  {
    super((JFrame) null, title, modal);
  }

  /**
   * Construct a new dialog owned by another dialog.
   * 
   * @param owner
   *   The parent dialog window.
   * 
   * @param title
   *   The title of the dialog.
   * 
   * @param modal
   *   Is the dialog modal?
   */ 
  protected
  JBaseDialog
  (
   Dialog owner,        
   String title,  
   boolean modal
  ) 
  {
    super(owner, title, modal);
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Initialize the common user interface components. <P> 
   * 
   * The button title arguments <CODE>confirm</CODE>, <CODE>apply</CODE> and 
   * <CODE>cancel</CODE> may be <CODE>null</CODE> to omit the button(s) from the dialog. 
   * 
   * @param header
   *   The text displayed in the dialog header. 
   * 
   * @param modal
   *   Is the dialog modal?
   * 
   * @param body
   *   The component containing the body of the dialog.
   * 
   * @param confirm
   *   The title of the confirm button.
   * 
   * @param apply
   *   The title of the apply button.
   * 
   * @param extra
   *   An array of title/action-command strings pairs used to create extra buttons.
   * 
   * @param cancel
   *   The title of the cancel button.
   * 
   * @return 
   *   The array of created extra buttons or <CODE>null</CODE> if extra was <CODE>null</CODE>.
   */ 
  protected JButton[]
  initUI
  (
   String header, 
   boolean modal, 
   JComponent body, 
   String confirm, 
   String apply, 
   String[][] extra,
   String cancel
  ) 
  {
    JPanel root = new JPanel();
    root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));  

    if(header != null) {
      JPanel panel = new JPanel();
      panel.setName(modal ? "ModalDialogHeader" : "DialogHeader");	
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      
      {
	JLabel label = new JLabel(header);
	pHeaderLabel = label;

	label.setName("DialogHeaderLabel");	

	panel.add(label);	  
      }
      
      panel.add(Box.createHorizontalGlue());
      
      root.add(panel);
    }	  
    
    if(body != null) 
      root.add(body);

    JButton[] extraBtns = null;
    {
      JPanel panel = new JPanel();
      panel.setName((body != null) ? "DialogButtonPanel" : "DialogButtonPanel2");
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      
      panel.add(Box.createHorizontalGlue());
      panel.add(Box.createRigidArea(new Dimension(20, 0)));

      if(confirm != null) {
	JButton btn = new JButton(confirm);
	pConfirmButton = btn;
	btn.setName("RaisedConfirmButton");
	
	Dimension size = btn.getPreferredSize();
	btn.setMinimumSize(new Dimension(108, 31));
	btn.setMaximumSize(new Dimension(size.width, 31));
	
	btn.setActionCommand("confirm");
	btn.addActionListener(this);
	
	panel.add(btn);	  
      }
	
      if((confirm != null) && (apply != null))
	panel.add(Box.createRigidArea(new Dimension(20, 0)));
     
      if(apply != null) {
	JButton btn = new JButton(apply);
	pApplyButton = btn;
	btn.setName("RaisedButton");
	
	Dimension size = btn.getPreferredSize();
	btn.setMinimumSize(new Dimension(108, 31));
	btn.setMaximumSize(new Dimension(size.width, 31));
	
	btn.setActionCommand("apply");
	btn.addActionListener(this);
	
	panel.add(btn);	  
      }

      if(((confirm != null) || (apply != null)) && (extra != null)) 
	panel.add(Box.createRigidArea(new Dimension(20, 0)));

      if(extra != null) {
	extraBtns = new JButton[extra.length];

	int wk;
	for(wk=0; wk<extra.length; wk++) {
	  if(extra[wk] != null) {
	    JButton btn = new JButton(extra[wk][0]);
	    extraBtns[wk] = btn;
	    btn.setName("RaisedButton");
	    
	    Dimension size = btn.getPreferredSize();
	    btn.setMinimumSize(new Dimension(108, 31));
	    btn.setMaximumSize(new Dimension(size.width, 31));
	    
	    btn.setActionCommand(extra[wk][1]);
	    btn.addActionListener(this);

	    panel.add(btn);	  
	  }

	  if(wk<(extra.length-1)) 
	    panel.add(Box.createRigidArea(new Dimension(20, 0)));	     
	}
      }

      if(((confirm != null) || (apply != null) || (extra != null)) && (cancel != null))
	panel.add(Box.createRigidArea(new Dimension(40, 0)));
     
      if(cancel != null) {
	JButton btn = new JButton(cancel);
	pCancelButton = btn;
	btn.setName("RaisedCancelButton");

	Dimension size = btn.getPreferredSize();
	btn.setMinimumSize(new Dimension(108, 31));
	btn.setMaximumSize(new Dimension(size.width, 31));

	btn.setActionCommand("cancel");
	btn.addActionListener(this);

	panel.add(btn);	  
      }
      
      panel.add(Box.createRigidArea(new Dimension(20, 0)));
      panel.add(Box.createHorizontalGlue());
      
      root.add(panel);
    }

    setContentPane(root);

    pack();

    {
      Window owner = getOwner();
      if(owner != null) {
	setLocationRelativeTo(owner);
      }
      else {
	Rectangle bounds = getGraphicsConfiguration().getBounds();
	setLocation(bounds.x + bounds.width/2 - getWidth()/2, 
		    bounds.y + bounds.height/2 - getHeight()/2);		    
      }
    }

    return extraBtns;
  }
     

  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Was the dialog confirmed?
   */ 
  public boolean 
  wasConfirmed()
  {
    return pConfirmed;
  }



  /*----------------------------------------------------------------------------------------*/
  /*   C O M P O N E N T   O V E R R I D E S                                                */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Shows or hides this component.
   */ 
  public void 
  setVisible
  (
   boolean isVisible
  )
  {
    if(isVisible)
      pConfirmed = false;

    super.setVisible(isVisible);
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
    if(cmd.equals("confirm")) 
      doConfirm();
    else if(cmd.equals("apply")) 
      doApply();
    else if(cmd.equals("cancel")) 
      doCancel();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Apply changes and close. 
   */ 
  public void 
  doConfirm()
  {
    pConfirmed = true;
    setVisible(false);
  }

  /**
   * Apply changes and continue. 
   */ 
  public void 
  doApply()
  {}

  /**
   * Cancel changes and close.
   */ 
  public void 
  doCancel()
  {
    setVisible(false);
  }



  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -5264874588692597213L;



  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
 
  /**
   * The state of the dialog upon closing.
   */
  protected boolean  pConfirmed;


  /**
   * The dialog header label.
   */ 
  protected JLabel  pHeaderLabel;


  /**
   * The footer buttons.
   */ 
  protected JButton  pConfirmButton;
  protected JButton  pApplyButton;
  protected JButton  pCancelButton;
}