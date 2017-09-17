/*
 * @(#)$Id: Rook.java,v 1.4 2003/05/05 15:21:18 falxx Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame.chess;

/*
 * Sist endret av: $Author: falxx $
 */

import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;

/**
 * Dette er et tårn. Et tårn vet hvordan det kan flytte.
 *
 * @version     $Revision: 1.4 $, $Date: 2003/05/05 15:21:18 $
 * @author	Trond Smaavik
 * @author	Kristian Berg
 * @author	Morten Løkke Andersen
 * @author	Andreas Bach
 */
public class Rook extends ChessPiece
{
	/**
	 * Oppretter og initierer et tårn
	 * @param color Fargen til brikken
	 * @param position Posisjonen til brikken
	 */
	public Rook(int color, BoardPoint position)
	{
		this.type = ROOK;
		this.color = color;
		this.position = position;
		this.chessName = "rook";
		pieceRules = new BoardPoint[4];
		pieceRules[0] = new BoardPoint(0, 1, false);
		pieceRules[1] = new BoardPoint(0, -1, false);
		pieceRules[2] = new BoardPoint(1, 0, false);
		pieceRules[3] = new BoardPoint(-1, 0, false);
	}

       /**
	* Denne metoden kopierer objektet
	* @return Den kopierte brikken
	*/ 
	public Object copy()
	{
		ChessPiece aPiece = new Rook(color, position);
		aPiece.moved = moved;
		aPiece.setAlive(this.isAlive());
		aPiece.chessName = chessName;

		return aPiece;
	}
	/**
	 * toString-metode for objektet
	 * @return String for objektet
	 */
	public String toString()
	{
		return "R";
	}

	public static void main(String args[]) {
		Rook rook = new Rook(WHITE, new BoardPoint(0, 0));
		ArrayList vectors = rook.getPossibleLegalMoves(false);

		System.out.println("Vektorer for Rooks flytt:");
		System.out.println("(X,Y), fixed");
		System.out.println("------------");
		for(int i = 0; i < vectors.size(); i++) {
			System.out.println(vectors.get(i));
		}
	}
}



