// $Id: TimeService.java,v 1.3 2006/02/20 20:12:04 jim Exp $

package us.temerity.plconfig;

import java.io.*;
import java.net.*;

/*------------------------------------------------------------------------------------------*/
/*   T I M E   S E R V I C E                                                                */
/*------------------------------------------------------------------------------------------*/

/**
 * A very simple NTP client which bypasses the local system to determine the current time.
 */ 
class TimeService
{  
  /*----------------------------------------------------------------------------------------*/
  /*   O P S                                                                                */
  /*----------------------------------------------------------------------------------------*/
  
  /**
   * Get the current time according to the given NTP server.
   * 
   * @param hostname [<B>in<B>]
   *   The hostname of the NTP server.
   * 
   * @throws IOException 
   *   If unable to successfully query the NTP server.
   */ 
  public static long
  getTime
  (
   String hostname
  ) 
    throws IOException
  {
    try {
      DatagramSocket socket = new DatagramSocket();
      InetAddress address = InetAddress.getByName(hostname);
      
      byte[] buf = new byte[48];
      buf[0] = (3 << 3 | 3);
      
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);
      socket.send(packet);
      
      socket.setSoTimeout(5000);
      socket.receive(packet);
    
      return decode(packet.getData(), 40);
    }
    catch(Exception ex) {
      throw new IOException("Unable to get the time from (" + hostname + ").");
    }      
  }
    

  /**
   * Get the current time from one of a trusted list of NTP time servers.
   *
   * @throws IOException 
   *   If unable to successfully query any of the NTP servers.
   */ 
  public static long
  getTime() 
    throws IOException
  {
    StringBuffer buf = new StringBuffer();

    int wk;
    for(wk=0; wk<sNtpServers.length; wk++) {
      try {
	return getTime(sNtpServers[wk]);
      }
      catch(IOException ex) {
	buf.append("  " + ex.getMessage() + "\n");
      }
    }

    throw new IOException
      ("While attempting to contact a known NTP server:\n" + 
       buf.toString());
  }
  
   

  /*----------------------------------------------------------------------------------------*/
  /*   H E L P E R S                                                                        */
  /*----------------------------------------------------------------------------------------*/

  /** 
   * Convert from a UTC timestamp to a 64-bit unsigned NPT timestamp format.
   */
  private static void 
  encode
  (
   byte[] buf, 
   int idx, 
   long stamp
  ) 
  {
    double dstamp = ((double) (stamp + 2208988800L)) / 1000.0;

    int wk;
    for(wk=0; wk<8; wk++) {
      double base = Math.pow(2, (3-wk)*8);
      buf[idx+wk] = (byte) (dstamp / base);
      dstamp = dstamp - (double) (ubyteToShort(buf[idx+wk]) * base);
    }
		
    buf[idx+7] = (byte) (Math.random()*255.0);
  }

  /** 
   * Convert from a 64-bit unsigned NPT timestamp format to a UTC timestamp.
   */
  private static long
  decode
  (
   byte[] buf, 
   int idx
  ) 
  {
    double dstamp = 0.0;
    
    int wk;
    for(wk=0; wk<8; wk++) 
      dstamp += ubyteToShort(buf[idx+wk]) * Math.pow(2, (3-wk)*8);
    
    return (long) ((dstamp - 2208988800.0) * 1000.0);
  }

  /**
   * Convert from a unsigned byte to a short.
   */
  private static short 
  ubyteToShort
  ( 
   byte b
  ) 
  {
    if((b & 0x80)==0x80) 
      return (short) (128 + (b & 0x7f));
    return (short) b;
  }


  /*----------------------------------------------------------------------------------------*/
  /*   S T A T I C   I N T E R N A L S                                                      */
  /*----------------------------------------------------------------------------------------*/

  /**
   * The IP addresses of some trusted NTP servers.
   */
  private static final String[] sNtpServers = {
    "0.pool.ntp.org", 
    "1.pool.ntp.org", 
    "2.pool.ntp.org"
  };


}


