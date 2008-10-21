// $Id: JHostnameComp.java,v 1.2 2008/10/21 00:41:01 jim Exp $

package us.temerity.plconfig;

import us.temerity.plconfig.ui.*;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*------------------------------------------------------------------------------------------*/
/*   H O S T N A M E   C O M P                                                              */
/*------------------------------------------------------------------------------------------*/

/**
 * The UI components for specifying a hostname.
 */ 
class JHostnameComp
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
  JHostnameComp
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

    {
      pField = UIFactory.createIdentifierField(null, width, JTextField.LEFT);

      pField.addActionListener(this);
      pField.setActionCommand("entered");
      
      add(pField); 
    }
  }



  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the hostname. 
   */ 
  public void 
  setHostname
  (
   String host
  ) 
  {
    pField.setText(host); 
  }

  /**
   * Get the validated hostname. 
   */ 
  public String
  validateHostname
  ( 
   ConfigApp app
  ) 
    throws IllegalConfigException
  {
    return app.validateHostname(pField.getText(), pTitle);
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
      String host = pField.getText();
      if((host != null) && (host.length() > 1)) {
	try {
	  InetAddress addr = InetAddress.getByName(host);
	  pField.setText(addr.getCanonicalHostName().toLowerCase(Locale.ENGLISH));
	  return;
	}
	catch(Exception ex) {
	}
      }
      
      try {
	Enumeration nets = NetworkInterface.getNetworkInterfaces();  
	while(nets.hasMoreElements()) {
	  NetworkInterface net = (NetworkInterface) nets.nextElement();
	  Enumeration addrs = net.getInetAddresses();
	  while(addrs.hasMoreElements()) {
	    InetAddress addr = (InetAddress) addrs.nextElement();
	    if((addr instanceof Inet4Address) && !addr.isLoopbackAddress()) {
	      pField.setText(addr.getCanonicalHostName().toLowerCase(Locale.ENGLISH));
	      return;
	    }
	  }
	}
      }
      catch(Exception ex) {
	pField.setText(null);
      }      
    }
  }


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private static final long serialVersionUID = 5987615811304307263L;
  


  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L S                                                                    */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The title of the setting.
   */ 
  private String  pTitle; 

  /**
   * The hostname field. 
   */ 
  private JIdentifierField  pField; 

}



