/*
 * @(#)$Id: Queen.java,v 1.4 2003/05/05 15:21:18 falxx Exp $
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
 * Dette er ei dronning. Ei dronning vet hvordan den kan flytte.
 *
 * @version     $Revision: 1.4 $, $Date: 2003/05/05 15:21:18 $
 * @author	Trond Smaavik
 * @author	Kristian Berg
 * @author	Morten Løkke Andersen
 * @author	Andreas Bach
 */
public class Queen extends ChessPiece {

	/**
	 * Oppretter og initierer en dronning-brikke
	 * @param color Fargen til brikken
	 * @param position Posisjonen til brikken
	 */
	public Queen(int color, BoardPoint position) {
		this.type = QUEEN;
		this.color = color;
		this.position = position;
		this.chessName = "queen";
		pieceRules = new BoardPoint[8];
		pieceRules[0] = new BoardPoint(0, 1, false);
		pieceRules[1] = new BoardPoint(0, -1, false);
		pieceRules[2] = new BoardPoint(1, 0, false);
		pieceRules[3] = new BoardPoint(-1, 0, false);
		pieceRules[4] = new BoardPoint(-1, 1, false);
		pieceRules[5] = new BoardPoint(1, 1, false);
		pieceRules[6] = new BoardPoint(-1,-1, false);
		pieceRules[7] = new BoardPoint(1, -1, false);

	}

	/**
	 * Kopierer brikken
	 * @return Kopi av brikken
	 */
	public Object copy()
	{
		ChessPiece aPiece = new Queen(color, position);
		aPiece.moved = moved;
		aPiece.setAlive(this.isAlive());
		aPiece.chessName = chessName;

		return aPiece;
	}
	
	/**
	 * toString-metode for dette objektet
	 * @return String for dette objektet
	 */
	public String toString()
	{
		return "Q";
	}

	public static void main(String args[]) {
		Queen queen = new Queen(WHITE, new BoardPoint(3, 0));
		ArrayList vectors = queen.getPossibleLegalMoves(false);

		System.out.println("Vektorer for Queens flytt:");
		System.out.println("(X,Y), fixed");
		System.out.println("------------");
		for(int i = 0; i < vectors.size(); i++) {
			System.out.println(vectors.get(i));
		}
	}
}
