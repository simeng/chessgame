/*
 * @(#)$Id: Bishop.java,v 1.5 2003/05/05 13:50:20 mortenla Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;

/*
 * Siste endret av: $Author: mortenla $

/**
 * Bishop sjakkbrikken
 * @version     $Revisio$, $Date: 2003/05/05 13:50:20 $
 * @author  	        Kristian Berg
 * @author		Morten Løkke Andersen
 * @author		Trond Smaavik
 * @author		Andreas Bach
 */
public class Bishop extends ChessPiece 
{

	/**
	 * Oppretter og initialiserer en løper-brikke
	 *
	 * @param	color		Fargen på brikken
	 * @param	position	initial posisjon til brikken
	 */
	public Bishop(int color, BoardPoint position)
	{
		this.color = color;
		this.position = position;
		this.chessName = "bishop";
		this.type = BISHOP;
		pieceRules = new BoardPoint[4];
		pieceRules[0] = new BoardPoint(-1, 1, false);
		pieceRules[1] = new BoardPoint(1, 1, false);
		pieceRules[2] = new BoardPoint(-1,-1, false);
		pieceRules[3] = new BoardPoint(1, -1, false);
	}

	/**
	 * Kopierer brikken
	 * @return Kopien av brikken
	 */
	public Object copy()
	{
		ChessPiece aPiece = new Bishop(color, position);
		aPiece.moved = moved;
		aPiece.setAlive(this.isAlive());
		aPiece.chessName = chessName;

		return aPiece;
	}

	public static void main(String args[]) {
		Bishop bishop = new Bishop(WHITE, new BoardPoint(2, 0));
		ArrayList vectors = bishop.getPossibleLegalMoves(false);

		System.out.println("Vektorer for Bishops flytt:");
		System.out.println("(X,Y), fixed");
		System.out.println("------------");
		for(int i = 0; i < vectors.size(); i++) {
			System.out.println(vectors.get(i));
		}
	}

	/**
	 * toString-metode for objektet
	 * @return String String for objektet
	 */
	public String toString()
	{
		return "B";
	}
}
