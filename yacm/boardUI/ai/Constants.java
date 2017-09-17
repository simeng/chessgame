/*
 * @(#)$Id: Constants.java,v 1.1 2003/05/05 16:52:21 simeng Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.ai;

import java.lang.Double;
import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Siste endret av: $Author: simeng $
 */

/**
 * @author      Andreas Bach
 */
public class Constants {
	public double[][] brettplass = new double[8][8];
	
	public Konstanter() {

		//skala fra 0-10
		brettplass[0][0] = 4.0 ;
		brettplass[0][1] = 4.0 ;
		brettplass[0][2] = 4.0 ;
		brettplass[0][3] = 4.0 ;
		brettplass[0][4] = 4.0 ;
		brettplass[0][5] = 4.0 ;
		brettplass[0][6] = 4.0 ;
		brettplass[0][7] = 4.0 ;
		brettplass[1][0] = 4.0 ;
		brettplass[1][1] = 4.0 ;
		brettplass[1][2] = 4.0 ;
		brettplass[1][3] = 4.0 ;
		brettplass[1][4] = 4.0 ;
	        brettplass[1][5] = 4.0 ;
	        brettplass[1][6] = 4.0 ;
	        brettplass[1][7] = 4.0 ;
	        brettplass[2][0] = 4.0 ;
	        brettplass[2][1] = 4.0 ;
		brettplass[2][2] = 4.0 ;
		brettplass[2][3] = 4.0 ;
                brettplass[2][4] = 4.0 ;
		brettplass[2][5] = 4.0 ;
                brettplass[2][6] = 4.0 ;
		brettplass[2][7] = 4.0 ;
                brettplass[2][0] = 4.0 ;
		brettplass[3][0] = 4.0 ;
		brettplass[3][1] = 4.0 ;
		brettplass[3][2] = 4.0 ;
		brettplass[3][3] = 4.0 ;	
		brettplass[3][4] = 4.0 ;
		brettplass[3][5] = 4.0 ;
		brettplass[3][6] = 4.0 ;
		brettplass[3][7] = 4.0 ;
		brettplass[4][0] = 4.0 ;
		brettplass[4][1] = 4.0 ;
		brettplass[4][2] = 4.0 ;
		brettplass[4][3] = 4.0 ;
                brettplass[4][4] = 4.0 ;
                brettplass[4][5] = 4.0 ;
		brettplass[4][6] = 4.0 ;
                brettplass[4][7] = 4.0 ;
                brettplass[4][0] = 4.0 ;
		brettplass[5][1] = 4.0 ;
                brettplass[5][2] = 4.0 ;
                brettplass[5][3] = 4.0 ;
		brettplass[5][4] = 4.0 ;
                brettplass[5][5] = 4.0 ;
                brettplass[5][6] = 4.0 ;
		brettplass[5][7] = 4.0 ;
                brettplass[5][0] = 4.0 ;
		brettplass[6][0] = 4.0 ;
                brettplass[6][1] = 4.0 ;
                brettplass[6][2] = 4.0 ;
		brettplass[6][3] = 4.0 ;
                brettplass[6][4] = 4.0 ;
                brettplass[6][5] = 4.0 ;
		brettplass[6][6] = 4.0 ;
                brettplass[6][7] = 4.0 ;
                brettplass[7][0] = 4.0 ;
		brettplass[7][1] = 4.0 ;
                brettplass[7][2] = 4.0 ;
                brettplass[7][3] = 4.0 ;
		brettplass[7][4] = 4.0 ;
                brettplass[7][5] = 4.0 ;
                brettplass[7][6] = 4.0 ;
		brettplass[7][7] = 4.0 ;
	/*
	    *   ------------------------------------------------\
		* 8 | 4   |   2 |   2 |   1 |   1 |   2 |   2 |   2 |
		*   |-----------------------------------------------|
		* 7 | 2   |   2 |   2 |   2 |   2 |   2 |   2 |   2 |
		*   |-----------------------------------------------|
    	* 6 | 2   |   7 |   2 |   2 |   2 |   2 |   7 |   2 |
      	*   |-----------------------------------------------|
	  	* 5 | 2   |   2 |   2 |  10 |  10 |   2 |   2 |   2 |
        *   |-----------------------------------------------|
	    * 4 | 2   |   2 |   2 |  10 |  10 |   2 |   2 |   2 |
	    *   |-----------------------------------------------|
		* 3 | 2   |   2 |   2 |   2 |   2 |   2 |   2 |   2 |
        *   |-----------------------------------------------|
		* 2 | 2   |   2 |   2 |   2 |   2 |   2 |   2 |   2 |
        *   |-----------------------------------------------|
		* 1 | 4   |   2 |   2 |   1 |   1 |   2 |   2 |   2 |
		*   |-----------------------------------------------|
		*   A      B     C     D     E     F     G     H		
		* 
	    */
	}

}
