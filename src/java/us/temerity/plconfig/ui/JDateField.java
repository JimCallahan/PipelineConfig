// $Id: JDateField.java,v 1.1 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig.ui;

import java.io.*;
import java.util.*;
import java.text.*; 
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

/*------------------------------------------------------------------------------------------*/
/*   D A T E   F I E L D                                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * A text field which only allows input of legal dates strings. <P> 
 * 
 * A date must be of the form: [0-9]+ "-" [0-9]+ "-" [0-9]+ <P> 
 */ 
public 
class JDateField
  extends JTextField 
{
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Construct a new field.
   */ 
  public 
  JDateField() 
  {
    super();
  }

  
  /*----------------------------------------------------------------------------------------*/
  /*   A C C E S S                                                                          */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Set the date.
   */ 
  public void 
  setDate
  (
   Date date
  ) 
  {
    if(date != null) {
      DecimalFormat fmt = new DecimalFormat("00");
      Calendar cal = new GregorianCalendar();
      cal.setTime(date);
      
      setText(cal.get(Calendar.YEAR) + "-" + 
	      (cal.get(Calendar.MONTH)+1) + "-" + 
	      cal.get(Calendar.DAY_OF_MONTH)); 
    }
    else {
      setText(null);
    }
  }

  /**
   * Get the date.
   */ 
  public Date
  getDate() 
  {
    String str = getText();
    if(str != null) {
      String parts[] = str.split("-"); 
      if(parts.length == 3) {
	try {
	  Calendar cal = new GregorianCalendar();
	  cal.set(Integer.parseInt(parts[0]), 
		  Integer.parseInt(parts[1])-1, 
		  Integer.parseInt(parts[2]));
	  return cal.getTime();
	}
	catch(Exception ex) {
	}
      }
    }

    return null;
  }

  

  /*----------------------------------------------------------------------------------------*/
  /*   T E X T   F I E L D   O V E R R I D E S                                              */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Creates the default implementation of the model to be used at construction if one 
   * isn't explicitly given.
   */ 
  protected Document 	
  createDefaultModel()
  {
    return new DateDocument();
  }


 
  /*----------------------------------------------------------------------------------------*/
  /*   I N T E R N A L   C L A S S E S                                                      */
  /*----------------------------------------------------------------------------------------*/

  private 
  class DateDocument 
    extends PlainDocument 
  {
    /**
     * Inserts some content into the document. 
     */ 
    public void 
    insertString
    (
     int offset, 
     String str, 
     AttributeSet attr
    ) 
      throws BadLocationException 
    {
      if(str == null) {
	return;
      }
      
      char[] cs = str.toCharArray();
      int wk;
      for(wk=0; wk<cs.length; wk++) {
	if(!(Character.isDigit(cs[wk]) || (cs[wk] == '-'))) {
	  Toolkit.getDefaultToolkit().beep();
	  return;
	}
      }
      
      super.insertString(offset, str, attr);
    }

    private static final long serialVersionUID = -1023451070038528106L;
  }


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/
  
  private static final long serialVersionUID = 1969531347911076604L;

}
