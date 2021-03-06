// $Id: ByteSize.java,v 1.2 2006/12/01 18:40:28 jim Exp $

package us.temerity.plconfig;

/*------------------------------------------------------------------------------------------*/
/*   B Y T E   S I Z E   F I E L D                                                          */
/*------------------------------------------------------------------------------------------*/

/**
 * Some static methods for converting between String and Long representations of byte
 * sizes.<P> 
 */ 
public 
class ByteSize
{
  /*----------------------------------------------------------------------------------------*/
  /*   C O N V E S I O N                                                                    */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Convert the given byte size String to a Long value.
   * 
   * @param text
   *   The byte size string.
   * 
   * @return 
   *   The value or <CODE>null</CODE> if the given string is <CODE>null</CODE>, empty or 
   *   "-".
   * 
   * @throws NumberFormatException
   *   If the given string is invalid.
   */ 
  public static Long
  stringToLong
  (
   String text
  )
    throws NumberFormatException
  {
    if((text != null) && (text.length() > 0) && !text.equals("-")) {
      String istr = text;
      long scale = 1;
      if(text.endsWith("K")) {
	istr = text.substring(0, text.length()-1);
	scale = 1024L;
      }
      else if(text.endsWith("M")) {
	istr = text.substring(0, text.length()-1);
	scale = 1048576L;
      }
      else if(text.endsWith("G")) {
	istr = text.substring(0, text.length()-1);
	scale = 1073741824L;
      }

      Long value = new Long(istr);
      if(value < 0) 
	throw new NumberFormatException();

      return (value * scale);
    }

    return null;
  }

  /**
   * Convert the given Long value into a byte size String.
   * 
   * @param value
   *   The integer value.
   * 
   * @return 
   *   The value or <CODE>"-"</CODE> if the given value is <CODE>null</CODE>..
   */ 
  public static String
  longToString
  (
   Long value
  ) 
  {
    if(value != null) {
      if((value % 1073741824L) == 0) 
	return ((value / 1073741824L) + "G");
      else if((value % 1048576L) == 0) 
	return ((value / 1048576L) + "M");
      else if((value % 1024L) == 0) 
	return ((value / 1024L) + "K");
      else 
	return (value.toString());
    }
    else {
      return ("-");
    }
  }

  /**
   * Generates a formatted floating point string representation of a large integer number.
   * 
   * @param value
   *   The integer value.
   * 
   * @return 
   *   The value or <CODE>"-"</CODE> if the given value is <CODE>null</CODE>..
   */ 
  public static String
  longToFloatString
  (
   Long value
  ) 
  {
    if(value == null) 
      return "-";

    if(value < 1024) {
      return value.toString();
    }
    else if(value < 1048576) {
      double k = ((double) value) / 1024.0;
      return String.format("%1$.1fK", k);
    }
    else if(value < 1073741824) {
      double m = ((double) value) / 1048576.0;
      return String.format("%1$.1fM", m);
    }
    else {
      double g = ((double) value) / 1073741824.0;
      return String.format("%1$.1fG", g);
    }
  }
}
