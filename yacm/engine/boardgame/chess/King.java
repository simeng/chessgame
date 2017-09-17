/*
 * @(#)$Id: King.java,v 1.7 2003/05/05 23:49:26 mortenla Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.BoardPoint;
import yacm.engine.TestTools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/*
 * Sist endret av: $Author: mortenla $
 */

/**
 * Dette er en konge. En konge vet hvordan den kan flytte.
 *
 * @version     $Revision: 1.7 $, $Date: 2003/05/05 23:49:26 $
 * @author	Trond Smaavik
 * @author	Morten Løkke Andersen
 * @author	Andreas Bach
 */
public class King extends ChessPiece
{
	private ChessBoard board;
	/**
	 * Oppretter og inititerer en konge
	 * @param color Hvilken 'farge' kongen har
	 * @param position Hvor den skal stå på brettet
	 * @param board Hvilket brett den skal stå på
	 */
	public King(int color, BoardPoint position, ChessBoard board) {
		this.type = KING;
		this.color = color;
		this.position = position;
		this.board = board;
		this.chessName = "king";

		pieceRules = new BoardPoint[8];

		pieceRules[0] = new BoardPoint(0, 1);
		pieceRules[1] = new BoardPoint(0, -1);
		pieceRules[2] = new BoardPoint(1, 0);
		pieceRules[3] = new BoardPoint(-1, 0);
		pieceRules[4] = new BoardPoint(-1, 1);
		pieceRules[5] = new BoardPoint(1, 1);
		pieceRules[6] = new BoardPoint(-1,-1);
		pieceRules[7] = new BoardPoint(1, -1);
	}

	/**
	 * Returnerer lovlige trekk i henhold til sjakkreglene.
	 * @param onlyKillingMoves Hvorvidt man bare vil ha tilbake trekk som dreper
	 * <ER DENNE I BRUK?!> ------------!!!!!!!!!!!-------------
	 * @return En tabell av BoardPoint med lovlig trekk for brikken.
	 */
	public ArrayList getPossibleLegalMoves(boolean onlyKillingMoves)
	{
		ArrayList legalMoves = new ArrayList();

		int x = position.getX();
		int y = position.getY();

		//Finner ut vilken trekk kongen kan gøre av sine vanlige
		//trekk
		for(int i = 0; i < pieceRules.length; i++)
		{
			int dx = pieceRules[i].getX();
			int dy = pieceRules[i].getY();

			if(board.isValidCoordinate(x + dx, y + dy)
				&& board.getPointingAtPosition(x + dx, y + dy, color, true) == null)
			{

				legalMoves.add(pieceRules[i]);
			}
		}

		//Finner ut om en rokade kan gjenom føres.
		if(!moved && board.getPointingAtPosition(this) == null)
		{
			BoardPoint rokadeVectors[] = {new BoardPoint(-1, 0, false), new BoardPoint(1, 0, false)};
			BoardPoint spesialMoves[] = {new BoardPoint(-2, 0), new BoardPoint(2, 0)};
			int type[] = {ROOK_QUEEN, ROOK_KING};

			for(int i = 0; i < 2; i++)
			{

				boolean ok = true;
				//sjekker at det ikke står noe i mellom kongen å tårnet
				ChessPiece aRook = board.getPiece(type[i], color);
				if(!aRook.isMoved() && board.findFirstPieceInDirection(this, rokadeVectors[i]) == aRook)
				{
					//sjekker at rutene som kongen må flytte seg over ikke
					//er sjakket av motstanderen
					int testX = x;
					int dx = rokadeVectors[i].getX();
					for(int j = 0; j < 2; j++)
					{
						if(board.getPointingAtPosition(testX += dx, y, color, true) != null)
						{
							ok = false;
							break;
						}
					}
				}else{
					ok = false;
				}

				//hvis rokanden er lov så legger vi den til lovlige flytt
				if(ok)
				{
					legalMoves.add(spesialMoves[i]);
				}
			}
		}
		return legalMoves;
	}

	/**
	 * Denne metoden kopierer brikken. 
	 * @return Brikke-kopien
	 */
	public Object copy()
	{
		ChessPiece aPiece = new King(color, position, board);
		aPiece.moved = moved;
		aPiece.setAlive(this.isAlive());
		aPiece.chessName = chessName;

		return aPiece;
	}

	/**
	 * Tostring-metode
	 * @return String-svar av hva dette er
	 */
	public String toString()
	{
		return "K";
	}


	public static void main(String[] args)
	{
		ChessBoard board = new ChessBoard();

		String[] testVars = {"King"};
		ChessPiece[] kings = {
			new King(WHITE, new BoardPoint(4,0), board),
			new King(BLACK, new BoardPoint(4,7),board),
			new King(WHITE, new BoardPoint(3,3), board),
			new King(BLACK, new BoardPoint(5,4), board)
		};


		ChessPiece[][] layout = new ChessPiece[2][16];
		layout[0][0] = new Rook(WHITE, new BoardPoint(0,0));
		layout[0][7] = new Rook(WHITE, new BoardPoint(4,3));
		layout[1][0] = new Rook(BLACK, new BoardPoint(6,2));
		layout[1][7] = new Rook(BLACK, new BoardPoint(7,7));
		board.initBoard(layout);

		String[] expRes = {
			"(2,0) (3,0) (3,1) (4,1) (5,1) (5,0)|",
			"(3,7) (3,6) (5,6) (5,7) (6,7)|",
			"(2,3) (2,4) (3,4) (4,4)|",
			"(4,3) (5,5) (6,5) (6,4)|"
		};
		String[][] testResult = new String[expRes.length][testVars.length + 2];
		for(int i = 0; i < kings.length; i++) {
			testResult[i][0] = "(" + kings[i].getPosition().getX() + "," + kings[i].getPosition().getY() + ")";
			testResult[i][1] = expRes[i];
			ArrayList pos = kings[i].getPossibleLegalMoves(false);
			testResult[i][2] = pos == null ? null : "";
			if(pos != null) {
				int x = kings[i].getPosition().getX();
				int y = kings[i].getPosition().getY();
				for(int k = 0; k < pos.size(); k++) {
					int dx = ((BoardPoint)pos.get(k)).getX();
					int dy = ((BoardPoint)pos.get(k)).getY();
					testResult[i][2] += "(" + (x + dx) + "," + (y + dy) + ") ";
				}
			}
		}

		board.printBoard();
		TestTools.printTestResult("public ArrayList getPossibleLegalMoves(boolean onlyKillingMoves) for King", testVars, testResult);

	}
}
