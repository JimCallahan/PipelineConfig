// $Id: TestNtpApp.java,v 1.1 2004/03/18 23:55:26 jim Exp $

import us.temerity.plconfig.TimeService;

import java.net.*; 
import java.io.*; 
import java.util.*;
import java.math.*;

/*------------------------------------------------------------------------------------------*/
/*   T E S T   o f   N T P                                                                  */
/*------------------------------------------------------------------------------------------*/

class TestNtpApp
{  
  /*----------------------------------------------------------------------------------------*/
  /*   M A I N                                                                              */
  /*----------------------------------------------------------------------------------------*/

  public static void 
  main
  (
   String[] args  /* IN: command line arguments */
  )
  {
    try {
      TestNtpApp app = new TestNtpApp();
      app.run();
    } 
    catch (Exception ex) {
      ex.printStackTrace();
      System.exit(1);
    } 
 
    System.exit(0);
  }


  public
  TestNtpApp()
  {}


  public void 
  run() 
    throws Exception 
  {
    test(null);
    test("sgi.com");
    
    System.exit(0);
  }


  private void 
  test
  (
   String hostname
  ) 
  {
    try {
      Date time = null;
      if(hostname == null) 
	time = new Date(TimeService.getTime());
      else 
	time = new Date(TimeService.getTime(hostname));

      System.out.print("---------------------------------------------------------\n" + 
		       "SERVER: " + hostname + "\n" + 
		       "\n" + 
		       "  Time = " + time + "\n\n");
    }
    catch(IOException ex) {
      System.out.print("Caught: " + ex.getMessage() + "\n");
    }
  }

}
