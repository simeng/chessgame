/*
 * @(#)$Id: Pawn.java,v 1.6 2003/05/05 15:21:18 falxx Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame.chess;

/*
 * Sist endret av: $Author: falxx $
 */

import yacm.engine.boardgame.BoardPoint;
import yacm.engine.TestTools;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Dette er en bonde.
 *
 * @version     $Revision: 1.6 $, $Date: 2003/05/05 15:21:18 $
 * @author	Andreas Bach
 * @author	Morten Løkke Andersen
 * @author	Trond Smaavik
 */
public class Pawn extends ChessPiece implements Constants
{
	private ChessBoard board;
	/**
	 * Oppretter og initierer en bondebrikke
	 * @param color Fargen til brikken
	 * @param position Posisjonen til brikken
	 * @param board Brettet den skal stå på
	 */
        public Pawn(int color, BoardPoint position, ChessBoard board)
	{
		this.type = PAWN;
		this.board = board;
		this.color = color;
		this.chessName = "pawn";
		this.position = position;
		pieceRules = new BoardPoint[3];

		int moveDirection = (color == WHITE) ? 1: -1;

		pieceRules[0] = new BoardPoint(0,  1 * moveDirection);
		pieceRules[1] = new BoardPoint(-1, 1 * moveDirection);
		pieceRules[2] = new BoardPoint(1, 1 * moveDirection);
	}

	/**
	 * Denne metoden returnerer lovlige trekk den kan gjøre
	 * @param onlyKillingMoves Vil vi bare ha trekk som dreper?
	 * @return Lovlige trekk
	 */
	public ArrayList getPossibleLegalMoves(boolean onlyKillingMoves)
	{
		ArrayList legalMoves = null;
		if(onlyKillingMoves)
		{
			legalMoves = new ArrayList();
			legalMoves.add(pieceRules[1]);
			legalMoves.add(pieceRules[2]);
		}else{
			legalMoves = getPossibleLegalMoves();
		}

		return legalMoves;
	}

	/**
	 * Returner lovlige trekk i henhold til sjakkreglene.
	 * @return En tabell av BoardPoint med lovlige trekk for brikken.
	 */
        private ArrayList getPossibleLegalMoves()
        {
		ArrayList legalMoves = new ArrayList();

		int x = getPosition().getX();
		int y = getPosition().getY();

		for(int i = 0; i < pieceRules.length; i++)
		{
			int dx = pieceRules[i].getX();
			int dy = pieceRules[i].getY();


			switch(i)
			{
				case 0:

					// sjekker om man har lov til å flytte en fram
					if(board.getStandingOnPosition(x + dx, y + dy) == null)
					{

						legalMoves.add(pieceRules[i]);

						// sjekker om man også har lov til å flyttet to fremm
						if(!moved)
						{
							int moveDirection = (color == WHITE) ? 1: -1;
							BoardPoint spesialMove  = new BoardPoint(0, 2 * moveDirection);

							dy = spesialMove.getY();

							if(board.getStandingOnPosition(x, y + dy) == null)
							{
								legalMoves.add(spesialMove);
							}
						}
					}
					break;
				case 1:
				case 2:
					ChessPiece aPiece = board.getStandingOnPosition(x + dx, y + dy);
					if( (aPiece != null && !isSameTeam(aPiece))
						|| (new BoardPoint(x + dx, y +dy)).equals(board.getEnpassantCoordinat()) )
					{
						legalMoves.add(pieceRules[i]);
					}
			}
		}
                return legalMoves;
        }

	/**
	 * Denne metoden kopierer brikken
	 * @return Den kopierte brikken
	 */
        public Object copy()
	{
		ChessPiece aPiece = new Pawn(color, position,  board);
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
		return "P";
	}

	public static void main(String args[])
	{
		ChessBoard board = new ChessBoard();

		String[] testVars = {"Pawn", "onlyKilling"};

		ChessPiece[] pawns = {
			new Pawn(WHITE, new BoardPoint(2,1), board),
			new Pawn(BLACK, new BoardPoint(2,6), board),
			new Pawn(WHITE, new BoardPoint(5,2), board),
			new Pawn(BLACK, new BoardPoint(6,4), board)
		};
		pawns[2].setMoved(true);

		boolean[] killing = {true, false};

		ChessPiece[][] layout = new ChessPiece[2][16];
		layout[0][0] = new Rook(WHITE, new BoardPoint(3,5));
		layout[1][0] = new Rook(BLACK, new BoardPoint(3,2));
		layout[1][2] = new Bishop(BLACK, new BoardPoint(6,3));
		layout[1][7] = new Rook(BLACK, new BoardPoint(2,2));
		board.initBoard(layout);

		String[] expRes = {
			"(3,2) (1,2)|","(3,2)|",
			"(1,5) (3,5)|","(2,5) (2,4) (3,5)|",
			"(4,3) (6,3)|","(5,3) (6,3)|",
			"(5,3) (7,3)|","null|"
		};

		int setNo = 0;
		String[][] testResult = new String[expRes.length][testVars.length + 2];
		for(int i = 0; i < pawns.length; i++) {
			for(int j = 0; j < killing.length; j++) {
				testResult[setNo][0] = "(" + pawns[i].getPosition().getX() + "," + pawns[i].getPosition().getY() + ")";
				testResult[setNo][1] = Boolean.toString(killing[j]);
				testResult[setNo][2] = expRes[setNo];
				ArrayList pos = pawns[i].getPossibleLegalMoves(killing[j]);
				testResult[setNo][3] = pos.isEmpty() ? "null" : "";
				if(pos != null) {
					int x = pawns[i].getPosition().getX();
					int y = pawns[i].getPosition().getY();
					for(int k = 0; k < pos.size(); k++) {
						int dx = ((BoardPoint)pos.get(k)).getX();
						int dy = ((BoardPoint)pos.get(k)).getY();
						testResult[setNo][3] += "(" + (x + dx) + "," + (y + dy) + ") ";
					}
				}
				setNo++;
				System.out.println(setNo);
			}

		}

		board.printBoard();
		TestTools.printTestResult("public ArrayList getPossibleLegalMoves(boolean onlyKillingMoves) for Pawn", testVars, testResult);
	}
}


