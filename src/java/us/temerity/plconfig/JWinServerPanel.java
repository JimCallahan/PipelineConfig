// $Id: JWinServerPanel.java,v 1.2 2007/06/14 12:57:07 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   W I N   S E R V E R   P A N E L                                                        */
/*------------------------------------------------------------------------------------------*/

/**
 * The Windows XP server info.
 */ 
class JWinServerPanel
  extends JBaseConfigPanel
  implements ActionListener
{  
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Construct a new panel.
   */ 
  public
  JWinServerPanel
  (
   ConfigApp app
  ) 
  {
    super(app, "Windows Server:");
    
    /* initialize UI components */ 
    {
      {
	Box hbox = new Box(BoxLayout.X_AXIS);

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);

	  vbox.add(UIFactory.createPanelLabel("Windows Job Manager:")); 

	  vbox.add(Box.createRigidArea(new Dimension(0, 3)));
	  
	  {
	    pServerField = UIFactory.createBooleanField(sHSize);
	    
	    pServerField.addActionListener(this);
	    pServerField.setActionCommand("server-changed");
	    
	    vbox.add(pServerField); 
	  }

	  vbox.add(Box.createRigidArea(new Dimension(0, 20)));
	  
	  pPasswordComp = new JPasswordComp("Windows", sHSize);
	  vbox.add(pPasswordComp);
      
	  hbox.add(vbox);
	}

	hbox.add(Box.createRigidArea(new Dimension(40, 0)));

	{
	  Box vbox = new Box(BoxLayout.Y_AXIS);
          
	  vbox.add(Box.createRigidArea(new Dimension(0, 60)));
          
          vbox.add(UIFactory.createPanelLabel("Windows Domain:"));
      
          pDomainField = UIFactory.createIdentifierField("", sHSize, JTextField.LEFT);
          vbox.add(pDomainField);

	  vbox.add(Box.createRigidArea(new Dimension(0, 60)));

	  hbox.add(vbox);
	}

	add(hbox);
      }

      add(Box.createVerticalGlue());
      
      pNotesDialog.setMessage
	("Windows Server Parameters:", 
         "If you will be using the Pipeline Job Manager on Windows XP Professional hosts " + 
         "to execute jobs on behalf of the Pipeline queue, you need to enable Windows " + 
         "Job Manager.  This will include a standard Windows installer (MSI) for the Job " + 
         "Manager Windows Service as part of the Pipeline distribution.  When this " + 
         "service is installed, the Pipeline Admin User, Windows Domain and Windows " + 
         "Password provided will be used to install the service on each Windows XP host " + 
         "joining the queue.  If no Windows Password is supplied, it will need to be " + 
         "manually entered each time the Job Manager Windows Service is installed.\n" + 
         "\n" + 
         "Note that the Windows Password is not saved along with other configuration " + 
         "parameters in the \"settings.glue\" file to prevent the password from being " + 
         "visible as plaintext.  The Windows Password is included in the site profile " + 
         "generated by the Pipeline Configuration Tool, but is encrypted along with the " + 
         "rest of the configuration parameters.");
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   P A N E L   O P S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The unique name of the panel.
   */ 
  public String
  getPanelTitle()
  {
    return "Windows Server";
  }

  /**
   * Initialize the panel UI component values from the current site profile settings.
   */ 
  public void 
  updatePanel() 
  { 
    pServerField.setValue(pApp.getWinServer());
    doServerChanged();
  }
  
  /**
   * Validate the current UI values and update the site profile settings.
   * 
   * @throws IllegalConfigException
   *   If the current UI values are invalid.
   */ 
  public void 
  updateProfile() 
    throws IllegalConfigException
  {
    boolean enabled = pServerField.getValue(); 
    pApp.setWinServer(enabled);
    if(enabled) {
      String domain = pDomainField.getText();
      if((domain == null) || (domain.length() == 0)) 
        throw new IllegalConfigException
          ("The Windows Domain must be specified!");
      pApp.setWinDomain(domain);

      pApp.setWinPassword(pPasswordComp.validatePassword());
    }
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
    super.actionPerformed(e); 

    String cmd = e.getActionCommand();
    if(cmd.equals("server-changed")) 
      doServerChanged();
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C T I O N S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  private void
  doServerChanged() 
  {
    boolean enabled = pServerField.getValue(); 
    
    pDomainField.setEnabled(enabled);
    pPasswordComp.setEnabled(enabled);

    if(!enabled)
      return; 

    pDomainField.setText(pApp.getWinDomain());
    pPasswordComp.clearPassword();
  }


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = -8630375089080047190L;
   


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * The Windows XP fields. 
   */ 
  private JBooleanField    pServerField; 
  private JIdentifierField pDomainField; 
  private JPasswordComp    pPasswordComp; 

}



