// $Id: UIFactory.java,v 1.3 2007/03/21 20:51:46 jim Exp $

package us.temerity.plconfig.ui;

import us.temerity.plconfig.*;
import us.temerity.plconfig.laf.LookAndFeelLoader;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;
import java.text.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.synth.*;


/*------------------------------------------------------------------------------------------*/
/*   U I   F A C T O R Y                                                                    */
/*------------------------------------------------------------------------------------------*/

/**
 * A static set of methods used to create standardized user interface components
 * and dialogs.
 */ 
public 
class UIFactory
{
  /*----------------------------------------------------------------------------------------*/
  /*   C O N S T R U C T O R                                                                */
  /*----------------------------------------------------------------------------------------*/

  private void 
  UIFactory() 
  {}


  /*----------------------------------------------------------------------------------------*/
  /*   D I A L O G S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Show an error message dialog for the given exception.
   */ 
  public static void 
  showErrorDialog
  (
   Exception ex
  ) 
  {
    sErrorDialog.setMessage(ex);
    sErrorDialog.setVisible(true);
  }

  /**
   * Show an error message dialog with the given title and message.
   */ 
  public static void 
  showErrorDialog
  (
   String title, 
   String msg
  ) 
  {
    sErrorDialog.setMessage(title, msg);
    sErrorDialog.setVisible(true);
  }




  /*----------------------------------------------------------------------------------------*/
  /*   C O M P O N E N T   C R E A T I O N                                                  */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new label. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The label text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JLabel
  createLabel
  (
   String text, 
   int width,
   int align
  )
  {
    return createLabel(text, width, align, null);
  }
  
  /**
   * Create a new label with a tooltip. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The label text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JLabel
  createLabel
  (
   String text, 
   int width,
   int align, 
   String tooltip
  )
  {
    JLabel label = new JLabel(text);

    Dimension size = new Dimension(width, 19);
    label.setMinimumSize(size);
    label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    label.setPreferredSize(size);
    
    label.setHorizontalAlignment(align);

    if(tooltip != null) 
      label.setToolTipText(formatToolTip(tooltip));
    
    return label;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new fixed size label. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The label text.
   * 
   * @param width
   *   The fixed width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JLabel
  createFixedLabel
  (
   String text, 
   int width,
   int align
  )
  {
    return createFixedLabel(text, width, align, null);
  }
  
  /**
   * Create a new fixed size label with a tooltip. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The label text.
   * 
   * @param width
   *   The fixed width.
   * 
   * @param align
   *   The horizontal alignment.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JLabel
  createFixedLabel
  (
   String text, 
   int width,
   int align,
   String tooltip
  )
  {
    JLabel label = createLabel(text, width, align);
    label.setMaximumSize(new Dimension(width, 19));
    
    if(tooltip != null) 
      label.setToolTipText(formatToolTip(tooltip));
    
    return label;
  }
  

  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new panel title label. <P> 
   * 
   * @param text
   *   The label text.
   */ 
  public static Component
  createPanelLabel
  (
   String text
  )
  {
    Box hbox = new Box(BoxLayout.X_AXIS);
	
    hbox.add(Box.createRigidArea(new Dimension(4, 0)));
    
    {
      JLabel label = new JLabel(text);
      label.setName("PanelLabel");

      hbox.add(label);
    }
    
    hbox.add(Box.createHorizontalGlue());
    
    return hbox;
  }

  

  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new dialog confirm button. <P> 
   * 
   * @param text
   *   The button text.
   * 
   * @param actionCommand
   *    The command to pass to the ActionListener or <CODE>null</CODE> to ignore.
   * 
   * @param actionListener
   *    The listener which will handle the the event of the button being pressed or 
   *    <CODE>null</CODE> to ignore.
   * 
   * @param tooltip
   *   The tooltip text or <CODE>null</CODE> to ignore.
   */ 
  public static JButton
  createConfirmButton
  (
   String text, 
   String actionCommand, 
   ActionListener actionListener, 
   String tooltip
  )
  {
    return createButtonHelper(text, "RaisedConfirmButton", 
                              actionCommand, actionListener, tooltip);
  }

  /**
   * Create a new dialog button. <P> 
   * 
   * @param text
   *   The button text.
   * 
   * @param actionCommand
   *    The command to pass to the ActionListener or <CODE>null</CODE> to ignore.
   * 
   * @param actionListener
   *    The listener which will handle the the event of the button being pressed or 
   *    <CODE>null</CODE> to ignore.
   * 
   * @param tooltip
   *   The tooltip text or <CODE>null</CODE> to ignore.
   */ 
  public static JButton
  createDialogButton
  (
   String text, 
   String actionCommand, 
   ActionListener actionListener, 
   String tooltip
  )
  {
    return createButtonHelper(text, "RaisedButton", 
                              actionCommand, actionListener, tooltip);
  }

  /**
   * Create a new dialog cancel button. <P> 
   * 
   * @param text
   *   The button text.
   * 
   * @param actionCommand
   *    The command to pass to the ActionListener or <CODE>null</CODE> to ignore.
   * 
   * @param actionListener
   *    The listener which will handle the the event of the button being pressed or 
   *    <CODE>null</CODE> to ignore.
   * 
   * @param tooltip
   *   The tooltip text or <CODE>null</CODE> to ignore.
   */ 
  public static JButton
  createCancelButton
  (
   String text, 
   String actionCommand, 
   ActionListener actionListener, 
   String tooltip
  )
  {
    return createButtonHelper(text, "RaisedCancelButton", 
                              actionCommand, actionListener, tooltip);
  }

  /**
   * Create a new raised button suitable for use in dialogs. <P> 
   * 
   * @param text
   *   The button text.
   * 
   * @param style
   *    Name of the synth style to apply to the button.
   * 
   * @param actionCommand
   *    The command to pass to the ActionListener or <CODE>null</CODE> to ignore.
   * 
   * @param actionListener
   *    The listener which will handle the the event of the button being pressed or 
   *    <CODE>null</CODE> to ignore.
   * 
   * @param tooltip
   *   The tooltip text or <CODE>null</CODE> to ignore.
   */ 
  private static JButton
  createButtonHelper
  (
   String text, 
   String style, 
   String actionCommand, 
   ActionListener actionListener, 
   String tooltip
  )
  {
    JButton btn = new JButton(text);
    btn.setName(style); 
    btn.setHorizontalTextPosition(SwingConstants.LEFT);

    Dimension size = btn.getPreferredSize();
    size.setSize(size.width, 31);
    btn.setPreferredSize(size);
    btn.setMinimumSize(size); 
    btn.setMaximumSize(size); 
	
    if((actionCommand != null) && (actionListener != null)) {
      btn.setActionCommand(actionCommand);
      btn.addActionListener(actionListener);
    }
  
    if(tooltip != null) 
      btn.setToolTipText(UIFactory.formatToolTip(tooltip)); 

    return btn;
  }
  
  
  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new non-editable text field. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JTextField
  createTextField
  (
   String text, 
   int width,
   int align
  )
  {
    JTextField field = new JTextField(text);
    field.setName("TextField");

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(false);
    
    return field;
  }

  /**
   * Create a new editable text field. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JTextField
  createEditableTextField
  (
   String text, 
   int width,
   int align
  )
  {
    JTextField field = createTextField(text, width, align);
    field.setName("EditableTextField");

    field.setEditable(true);
    
    return field;
  }

  /**
   * Create a new editable text field which can only contain identifiers. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JIdentifierField
  createIdentifierField
  (
   String text, 
   int width,
   int align
  )
  {
    JIdentifierField field = new JIdentifierField();
    field.setName("EditableTextField");

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setText(text);
    
    return field;
  }

  /**
   * Create a new password field. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JPasswordField
  createPasswordField
  (
   int width,
   int align
  )
  {
    JPasswordField field = new JPasswordField();
    field.setName("PasswordField");

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    return field;
  }

  /**
   * Create a new editable text field which can only contain identifier paths. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JPathField
  createPathField
  (
   String text, 
   int width,
   int align
  )
  {
    JPathField field = new JPathField();
    field.setName("EditableTextField");

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setText(text);
    
    return field;
  }

  /**
   * Create a new editable text field which can only contain dates. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JDateField
  createDateField
  (
   String text, 
   int width,
   int align
  )
  {
    JDateField field = new JDateField();
    field.setName("EditableTextField");

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setText(text);
    
    return field;
  }

  /**
   * Create a new editable text field which can only contain identifier paths and 
   * a directory browser button used to set the path. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   * 
   * @param listener
   *   The action listener for the browse button.
   * 
   * @param command
   *   The action command associated with pressing the browse button.
   * 
   * @return 
   *   The created components: [JPathField, JButton, Box]
   */ 
  public static JComponent[] 
  createBrowsablePathField
  (
   String text, 
   int width,
   int align, 
   ActionListener listener, 
   String command
  )
  {
    JComponent comps[] = new JComponent[3];

    Box box = new Box(BoxLayout.X_AXIS); 
    comps[2] = box;

    {
      JPathField field = new JPathField();
      comps[0] = field;
      field.setName("EditableTextField");

      Dimension size = new Dimension(Math.max(0, width-19), 19);
      field.setMinimumSize(size);
      field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));

      field.setHorizontalAlignment(align);
      field.setEditable(true);
      field.setText(text);

      box.add(field);
    }
    
    box.add(Box.createRigidArea(new Dimension(4, 0)));
    
    {
      JButton btn = new JButton();
      comps[1] = btn;
      btn.setName("BrowseButton");
      
      Dimension size = new Dimension(15, 19);
      btn.setMinimumSize(size);
      btn.setMaximumSize(size);
      btn.setPreferredSize(size);

      btn.addActionListener(listener);      
      btn.setActionCommand(command);
      
      box.add(btn);
    }
    
    return comps;
  }

  /**
   * Create a new editable text field which can only contain alphanumeric characters. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JAlphaNumField
  createAlphaNumField
  (
   String text, 
   int width,
   int align
  )
  {
    JAlphaNumField field = new JAlphaNumField();
    field.setName("EditableTextField");

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setText(text);
    
    return field;
  }

  /**
   * Create a new editable text field which can only contain integers. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param value
   *   The initial value.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JIntegerField
  createIntegerField
  (
   Integer value,
   int width,
   int align
  )
  {
    JIntegerField field = new JIntegerField();

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setValue(value);

    return field;
  }

  /**
   * Create a new editable text field which can only contain integer byte sizes. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param value
   *   The initial value.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JByteSizeField
  createByteSizeField
  (
   Long value,
   int width,
   int align
  )
  {
    JByteSizeField field = new JByteSizeField();

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setValue(value);

    return field;
  }

  /**
   * Create a new editable text field which can only contain float values. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param value
   *   The initial value.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JFloatField
  createFloatField
  (
   Float value,  
   int width,
   int align
  )
  {
    JFloatField field = new JFloatField();

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setValue(value);

    return field;
  }

  /**
   * Create a new editable text field which can only contain double values. <P> 
   * 
   * See {@link JLabel#setHorizontalAlignment JLabel.setHorizontalAlignment} for valid
   * values for the <CODE>align</CODE> argument.
   * 
   * @param value
   *   The initial value.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param align
   *   The horizontal alignment.
   */ 
  public static JDoubleField
  createDoubleField
  (
   Double value,  
   int width,
   int align
  )
  {
    JDoubleField field = new JDoubleField();

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);
    
    field.setHorizontalAlignment(align);
    field.setEditable(true);
    
    field.setValue(value);

    return field;
  }

  /**
   * Create a new non-editable text area.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param rows
   *   The initial number of rows.
   */ 
  public static JTextArea
  createTextArea
  (
   String text, 
   int width,
   int rows
  )
  {
    JTextArea area = new JTextArea(text, rows, 0);
    area.setName("TextArea");

    area.setLineWrap(true);
    area.setWrapStyleWord(true);

    area.setEditable(false);
    
    return area;
  }

  /**
   * Create a new editable text area.
   * 
   * @param text
   *   The initial text.
   * 
   * @param width
   *   The minimum and preferred width.
   * 
   * @param rows
   *   The initial number of rows.
   */ 
  public static JTextArea
  createEditableTextArea
  (
   String text, 
   int width,
   int rows
  )
  {
    JTextArea area = createTextArea(text, width, rows);
    area.setName("EditableTextArea");

    area.setEditable(true);
    
    return area;
  }

  /**
   * Create a collection field.
   * 
   * @param values
   *   The initial collection values.
   * 
   * @param width
   *   The minimum and preferred width of the field.
   */ 
  public static JCollectionField
  createCollectionField
  (
   Collection<String> values,
   int width
  ) 
  {
    return createCollectionField(values, null, width);
  }

  /**
   * Create a collection field.
   * 
   * @param values
   *   The initial collection values.
   * 
   * @param parent
   *   The parent dialog or <CODE>null</CODE> the field is not a child of a dialog.
   * 
   * @param width
   *   The minimum and preferred width of the field.
   */ 
  public static JCollectionField
  createCollectionField
  (
   Collection<String> values,
   JDialog parent, 
   int width
  ) 
  {
    JCollectionField field = new JCollectionField(values, parent);

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);

    return field;
  }

  /**
   * Create a boolean field.
   * 
   * @param width
   *   The minimum and preferred width of the field.
   */ 
  public static JBooleanField
  createBooleanField
  (
   int width
  ) 
  {
    JBooleanField field = new JBooleanField();

    Dimension size = new Dimension(width, 19);
    field.setMinimumSize(size);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));
    field.setPreferredSize(size);

    return field;
  }

  /**
   * Create a boolean field.
   * 
   * @param value
   *   The initial value.
   * 
   * @param width
   *   The minimum and preferred width of the field.
   */ 
  public static JBooleanField
  createBooleanField
  (
   Boolean value, 
   int width
  ) 
  {
    JBooleanField field = createBooleanField(width);
    field.setValue(value);

    return field;
  }



  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Create the title/value panels.
   * 
   * @return 
   *   The title panel, value panel and containing box.
   */   
  public static Component[]
  createTitledPanels()
  {
    Component comps[] = new Component[3];
    
    Box body = new Box(BoxLayout.X_AXIS);
    comps[2] = body;
    {
      {
	JPanel panel = new JPanel();
	comps[0] = panel;
	
	panel.setName("TitlePanel");
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      
	body.add(panel);
      }
      
      {
	JPanel panel = new JPanel();
	comps[1] = panel;
	
	panel.setName("ValuePanel");
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	body.add(panel);
      }
    }

    return comps;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new non-editable text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text field.
   */ 
  public static JTextField
  createTitledTextField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth
  )
  {
    return createTitledTextField(tpanel, title, twidth, vpanel, text, vwidth, null);
  }

  /**
   * Create a new non-editable text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JTextField
  createTitledTextField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JTextField field = createTextField(text, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }
    
    
  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new editable text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text field.
   */ 
  public static JTextField
  createTitledEditableTextField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth
  )
  {
    return createTitledEditableTextField(tpanel, title, twidth, vpanel, text, vwidth, null);
  }

  /**
   * Create a new editable text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JTextField
  createTitledEditableTextField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JTextField field = createEditableTextField(text, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new identifier text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   */ 
  public static JIdentifierField
  createTitledIdentifierField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth
  )
  {
    return createTitledIdentifierField(tpanel, title, twidth, vpanel, text, vwidth, null);
  }

  /**
   * Create a new identifier text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JIdentifierField
  createTitledIdentifierField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JIdentifierField field = createIdentifierField(text, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Create a new alphanumeric text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   */ 
  public static JAlphaNumField
  createTitledAlphaNumField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth
  )
  {
    return createTitledAlphaNumField(tpanel, title, twidth, vpanel, text, vwidth, null);
  }

  /**
   * Create a new alphanumeric text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JAlphaNumField
  createTitledAlphaNumField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JAlphaNumField field = createAlphaNumField(text, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new path text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the path field.
   */ 
  public static JPathField
  createTitledPathField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth
  )
  {    
    return createTitledPathField(tpanel, title, twidth, vpanel, text, vwidth, null);
  }

  /**
   * Create a new path text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the path field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JPathField
  createTitledPathField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JPathField field = createPathField(text, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }

  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new path text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the path field.
   * 
   * @param listener
   *   The action listener for the browse button.
   * 
   * @param command
   *   The action command associated with pressing the browse button.
   *
   * @return 
   *   The created components: [JPathField, JButton, Box]
   */ 
  public static JComponent[] 
  createTitledBrowsablePathField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth,
   ActionListener listener, 
   String command
  )
  {    
    return createTitledBrowsablePathField
      (tpanel, title, twidth, vpanel, text, vwidth, listener, command, null);
  }

  /**
   * Create a new path text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the path field.
   * 
   * @param listener
   *   The action listener for the browse button.
   * 
   * @param command
   *   The action command associated with pressing the browse button.
   * 
   * @param tooltip
   *   The tooltip text.
   * 
   * @return 
   *   The created components: [JPathField, JButton, Box]
   */ 
  public static JComponent[] 
  createTitledBrowsablePathField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth, 
   ActionListener listener, 
   String command,
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JComponent[] comps = 
      createBrowsablePathField(text, vwidth, JLabel.CENTER, listener, command);
    vpanel.add(comps[2]);

    return comps;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new integer text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   */ 
  public static JIntegerField
  createTitledIntegerField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Integer value, 
   int vwidth
  )
  {
    return createTitledIntegerField(tpanel, title, twidth, vpanel, value, vwidth, null);
  }

  /**
   * Create a new integer text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JIntegerField
  createTitledIntegerField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Integer value, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JIntegerField field = createIntegerField(value, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new byte size text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   */ 
  public static JByteSizeField
  createTitledByteSizeField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Long value, 
   int vwidth
  )
  {
    return createTitledByteSizeField(tpanel, title, twidth, vpanel, value, vwidth, null);
  }

  /**
   * Create a new byte size text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JByteSizeField
  createTitledByteSizeField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Long value, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JByteSizeField field = createByteSizeField(value, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new float text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   */ 
  public static JFloatField
  createTitledFloatField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Float value, 
   int vwidth
  )
  {
    return createTitledFloatField(tpanel, title, twidth, vpanel, value, vwidth, null);
  }

  /**
   * Create a new float text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JFloatField
  createTitledFloatField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Float value, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JFloatField field = createFloatField(value, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new double text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   */ 
  public static JDoubleField
  createTitledDoubleField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Double value, 
   int vwidth
  )
  {
    return createTitledDoubleField(tpanel, title, twidth, vpanel, value, vwidth, null);
  }
  
  /**
   * Create a new double text field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param value
   *   The initial value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the identifier field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JDoubleField
  createTitledDoubleField
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   Double value, 
   int vwidth, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JDoubleField field = createDoubleField(value, vwidth, JLabel.CENTER);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new non-editable text area with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text area.
   * 
   * @param rows
   *   The initial number of rows.
   */ 
  public static JTextArea
  createTitledTextArea
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth,
   int rows, 
   boolean isScrolled
  )
  {
    return createTitledTextArea
      (tpanel, title, twidth, vpanel, text, vwidth, rows, isScrolled, null);
  }
  
  /**
   * Create a new non-editable text area with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text area.
   * 
   * @param rows
   *   The initial number of rows.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JTextArea
  createTitledTextArea
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth,
   int rows, 
   boolean isScrolled, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));
    tpanel.add(Box.createRigidArea(new Dimension(0, 19*(rows-1))));

    JTextArea area = createTextArea(text, vwidth, rows);
    if(isScrolled) {
      area.setName("ScrolledTextArea");

      JScrollPane scroll = new JScrollPane(area);
	
      scroll.setHorizontalScrollBarPolicy
	(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setVerticalScrollBarPolicy
	(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      
      vpanel.add(scroll);
    }
    else {
      Dimension size = new Dimension(vwidth, 19*rows);
      area.setMinimumSize(size);
      area.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
      area.setPreferredSize(size);

      vpanel.add(area);
    }

    return area;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a new editable text area with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text area.
   * 
   * @param rows
   *   The initial number of rows.
   */ 
  public static JTextArea
  createTitledEditableTextArea
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth,
   int rows, 
   boolean isScrolled
  )
  {
    return createTitledEditableTextArea
      (tpanel, title, twidth, vpanel, text, vwidth, rows, isScrolled, null);
  }

  /**
   * Create a new editable text area with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param text
   *   The initial text.
   * 
   * @param vwidth
   *   The minimum and preferred width of the text area.
   * 
   * @param rows
   *   The initial number of rows.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JTextArea
  createTitledEditableTextArea
  (
   JPanel tpanel, 
   String title,  
   int twidth,
   JPanel vpanel, 
   String text, 
   int vwidth,
   int rows, 
   boolean isScrolled, 
   String tooltip
  )
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));
    tpanel.add(Box.createRigidArea(new Dimension(0, 19*(rows-1))));

    JTextArea area = createEditableTextArea(text, vwidth, rows);
    if(isScrolled) {
      area.setName("ScrolledTextArea");

      JScrollPane scroll = new JScrollPane(area);
      
      scroll.setHorizontalScrollBarPolicy
	(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scroll.setVerticalScrollBarPolicy
	    (ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
      
      vpanel.add(scroll);
    }
    else {
      Dimension size = new Dimension(vwidth, 19*rows);
      area.setMinimumSize(size);
      area.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
      area.setPreferredSize(size);

      vpanel.add(area);
    }

    return area;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a slider with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param vmin
   *   The minimum slider value.
   * 
   * @param vmax
   *   The maximum slider value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   */ 
  public static JSlider 
  createTitledSlider
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel, 
   int vmin, 
   int vmax,
   int vwidth
  ) 
  {
    return createTitledSlider(tpanel, title, twidth, vpanel, vmin, vmax, vwidth, null);
  }
  
  /**
   * Create a slider with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param vmin
   *   The minimum slider value.
   * 
   * @param vmax
   *   The maximum slider value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JSlider 
  createTitledSlider
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel, 
   int vmin, 
   int vmax,
   int vwidth, 
   String tooltip
  ) 
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JSlider slider = new JSlider(vmin, vmax, vmin);
      
    Dimension size = new Dimension(vwidth, 19);
    slider.setMinimumSize(size);
    slider.setMaximumSize(size);
    slider.setPreferredSize(size);

    slider.setPaintLabels(false);
    slider.setPaintTicks(false);
    slider.setPaintTrack(true);

    vpanel.add(slider);

    return slider;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a floating-point slider with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param vmin
   *   The minimum slider value.
   * 
   * @param vmax
   *   The maximum slider value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   */ 
  public static JSlider 
  createTitledSlider
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel, 
   double vmin, 
   double vmax,
   int vwidth
  ) 
  {
    return createTitledSlider(tpanel, title, twidth, vpanel, vmin, vmax, vwidth, null);
  }

  /**
   * Create a floating-point slider with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param vmin
   *   The minimum slider value.
   * 
   * @param vmax
   *   The maximum slider value.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JSlider 
  createTitledSlider
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel, 
   double vmin, 
   double vmax,
   int vwidth, 
   String tooltip
  ) 
  {
    return createTitledSlider(tpanel, title, twidth, 
			      vpanel, (int)(vmin*1000.0), (int)(vmax*1000.0), vwidth, 
			      tooltip);
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a collection field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param values
   *   The initial collection values.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   */ 
  public static JCollectionField
  createTitledCollectionField
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel,
   Collection<String> values,
   int vwidth
  ) 
  {
    return createTitledCollectionField(tpanel, title, twidth, vpanel, values, vwidth, null);
  }

  /**
   * Create a collection field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param values
   *   The initial collection values.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JCollectionField
  createTitledCollectionField
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel,
   Collection<String> values,
   int vwidth, 
   String tooltip
  ) 
  {
    return createTitledCollectionField(tpanel, title, twidth, 
				       vpanel, values, null, vwidth, 
				       tooltip);
  }

  /**
   * Create a collection field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param values
   *   The initial collection values.
   * 
   * @param parent
   *   The parent dialog or <CODE>null</CODE> the field is not a child of a dialog.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JCollectionField
  createTitledCollectionField
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel,
   Collection<String> values,
   JDialog parent, 
   int vwidth, 
   String tooltip
  ) 
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));
    
    JCollectionField field = createCollectionField(values, parent, vwidth);
    vpanel.add(field);

    return field;
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create a boolean field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   */ 
  public static JBooleanField
  createTitledBooleanField
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel, 
   int vwidth
  ) 
  {
    return createTitledBooleanField(tpanel, title, twidth, vpanel, vwidth, null);
  }

  /**
   * Create a boolean field with a title and add them to the given panels.
   * 
   * @param tpanel
   *   The titles panel.
   * 
   * @param twidth
   *   The minimum and preferred width of the title.
   * 
   * @param title
   *   The title text.
   * 
   * @param vpanel
   *   The values panel.
   * 
   * @param vwidth
   *   The minimum and preferred width of the value field.
   * 
   * @param tooltip
   *   The tooltip text.
   */ 
  public static JBooleanField
  createTitledBooleanField
  (
   JPanel tpanel, 
   String title, 
   int twidth,
   JPanel vpanel, 
   int vwidth, 
   String tooltip
  ) 
  {
    tpanel.add(createFixedLabel(title, twidth, JLabel.RIGHT, tooltip));

    JBooleanField field = createBooleanField(vwidth);
    vpanel.add(field);

    return field;
  }

  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Create a new vertical scrollpane with no horizontal scrollbar.
   * 
   * @param view 
   *   The component to display in the scrollpanes viewport.
   */ 
  public static JScrollPane
  createVertScrollPane
  (
   Component view
  ) 
  {
    return createVertScrollPane(view, null, null);
  }

  /**
   * Create a new vertical scrollpane with no horizontal scrollbar and 
   * a minimum/preferred size.
   * 
   * @param view 
   *   The component to display in the scrollpanes viewport.
   *
   * @param width
   *   The minimum and preferred width of the scrollpane or 
   *   <CODE>null</CODE> to ignore.
   * 
   * @param height
   *   The minimum and preferred height of the scrollpane or 
   *   <CODE>null</CODE> to ignore.
   */ 
  public static JScrollPane
  createVertScrollPane
  (
   Component view, 
   Integer width, 
   Integer height
  ) 
  {
    Dimension size = null;
    if((width != null) && (height != null)) 
      size = new Dimension(width, height);

    return createScrollPane(view, 
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
                            size, size, null);
  }

  /**
   * Create a new scrollpane.
   * 
   * @param view 
   *   The component to display in the scrollpanes viewport.
   * 
   * @param hsbPolicy 
   *   An integer that specifies the horizontal scrollbar policy.
   *
   * @param vsbPolicy
   *   An integer that specifies the vertical scrollbar policy.
   * 
   * @param minSize
   *   The minimum size scrollpane or <CODE>null</CODE> to ignore.
   * 
   * @param prefSize
   *   The preferred size scrollpane or <CODE>null</CODE> to ignore.
   * 
   * @param maxSize
   *   The maximum size scrollpane or <CODE>null</CODE> to ignore.
   */ 
  public static JScrollPane
  createScrollPane
  (
   Component view, 
   int hsbPolicy, 
   int vsbPolicy, 
   Dimension minSize, 
   Dimension prefSize, 
   Dimension maxSize
  ) 
  {
    JScrollPane scroll = new JScrollPane(view, vsbPolicy, hsbPolicy);
    
    if(minSize != null)
      scroll.setMinimumSize(minSize);
    if(prefSize != null)
      scroll.setPreferredSize(prefSize);    
    if(maxSize != null)
      scroll.setMaximumSize(maxSize);
    
    scroll.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    scroll.getVerticalScrollBar().setUnitIncrement(22);

    return scroll;
  }



  /*----------------------------------------------------------------------------------------*/

  /**
   * Add vertical space into the given panels.
   */ 
  public static void 
  addVerticalSpacer
  (
   JPanel tpanel, 
   JPanel vpanel, 
   int theight, 
   int vheight
  ) 
  {
    tpanel.add(Box.createRigidArea(new Dimension(0, theight)));
    vpanel.add(Box.createRigidArea(new Dimension(0, vheight)));
  }
  /**
   * Add vertical space into the given panels.
   */ 
  public static void 
  addVerticalSpacer
  (
   JPanel tpanel, 
   JPanel vpanel, 
   int height
  ) 
  {
    addVerticalSpacer(tpanel, vpanel, height, height);
  }

  /**
   * Add vertical glue into the given panels.
   */ 
  public static void 
  addVerticalGlue
  (
   JPanel tpanel, 
   JPanel vpanel
  ) 
  {
    tpanel.add(Box.createVerticalGlue());
    vpanel.add(Box.createVerticalGlue());
  }


  /*----------------------------------------------------------------------------------------*/

  /**
   * Create list panel components.
   * 
   * @param box
   *   The parent horizontal box.
   * 
   * @param title
   *   The title of the list.
   * 
   * @param size
   *   The preferred size of the list.
   */ 
  public static JList 
  createListComponents
  (
   Box box, 
   String title, 
   Dimension size
  ) 
  {
    return createListComponents(box, title, size, true, true);
  }

  /**
   * Create list panel components.
   * 
   * @param box
   *   The parent horizontal box.
   * 
   * @param title
   *   The title of the list.
   * 
   * @param size
   *   The preferred size of the list.
   * 
   * @param headerSpacer
   *   Add a vertical header spacer?
   * 
   * @param footerSpacer
   *   Add a vertical footer spacer?
   */ 
  public static JList 
  createListComponents
  (
   Box box, 
   String title, 
   Dimension size, 
   boolean headerSpacer, 
   boolean footerSpacer
  ) 
  {
    Box vbox = new Box(BoxLayout.Y_AXIS);	

    if(headerSpacer)
      vbox.add(Box.createRigidArea(new Dimension(0, 20)));
    
    vbox.add(createPanelLabel(title));
    
    vbox.add(Box.createRigidArea(new Dimension(0, 4)));

    JList lst = null;
    {
      lst = new JList(new DefaultListModel());
      lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      lst.setCellRenderer(new JListCellRenderer());

      {
	JScrollPane scroll = new JScrollPane(lst);
	
	scroll.setMinimumSize(new Dimension(150, 150));
	scroll.setPreferredSize(size);
	
	scroll.setHorizontalScrollBarPolicy
	  (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	scroll.setVerticalScrollBarPolicy
	  (ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
	
	vbox.add(scroll);
      }
    }

    if(footerSpacer) 
      vbox.add(Box.createRigidArea(new Dimension(0, 20)));

    box.add(vbox);

    return lst;
  }


  /*----------------------------------------------------------------------------------------*/
  /*   C O M P O N E N T   U T I L I T I E S                                                */
  /*----------------------------------------------------------------------------------------*/

  /**
   * Formats tool tip text as HTML, breaking up long tool tips into multiple lines. 
   * 
   * @param text 
   *   The unformatted tool tip text.
   */ 
  public static String
  formatToolTip
  (
   String text
  ) 
  {
    if(text == null) 
      return null;

    int line = 85;
    if(text.length() < line) {
      return ("<html><font color=\"#000000\">" + text + "</font></html>");
    }
    else {
      StringBuffer buf = new StringBuffer();
      buf.append("<html><font color=\"#000000\">");
      int wk, cnt;
      String words[] = text.split("\\s");
      for(wk=0, cnt=0; wk<words.length; wk++) {
	int wlen = words[wk].length();
	if(wlen > 0) {
	  if(cnt == 0) { 
	    buf.append(words[wk]);
	    cnt = wlen;
	  }
	  else if((cnt+wlen+1) < line) {
	    buf.append(" " + words[wk]);
	    cnt += wlen + 1;
	  }
	  else {
	    buf.append("<br>" + words[wk]);
	    cnt = wlen;
	  }
	}
      }
      buf.append("</font></html>");

      return buf.toString();
    }
  }
  


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C    I N T E R N A L S                                                     */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The error message dialog.
   */ 
  private static JErrorDialog  sErrorDialog = new JErrorDialog();

}
