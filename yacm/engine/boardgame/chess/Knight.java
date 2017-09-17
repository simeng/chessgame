package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;

/**
 * Knight sjakkbrikken
 * @author      Kristian Berg
 * @author	Trond Smaavik
 * @author	Andreas Bach
 */
public class Knight extends ChessPiece{

	/**
	 * Oppretter og initierer en hest.
	 * @param color Hvilken farge brikken skal ha
	 * @param position Hvor den skal stå
	 */
	public Knight(int color, BoardPoint position)
	{
		this.type = KNIGHT;
		this.color = color;
		this.position = position;
		this.chessName = "knight";
		pieceRules = new BoardPoint[8];
		pieceRules[0] = new BoardPoint(-2, -1);
		pieceRules[1] = new BoardPoint(-2, 1);
		pieceRules[2] = new BoardPoint(-1,-2);
		pieceRules[3] = new BoardPoint(-1, 2);
		pieceRules[4] = new BoardPoint(1, -2);
		pieceRules[5] = new BoardPoint(1, 2);
		pieceRules[6] = new BoardPoint(2,-1);
		pieceRules[7] = new BoardPoint(2, 1);
	}

	/**
	 * Denne metoden kopierer brikken
	 * @return Kopiert brikke
	 */
	public Object copy()
	{
		ChessPiece aPiece = new Knight(color, position);
		aPiece.moved = moved;
		aPiece.setAlive(this.isAlive());
		aPiece.chessName = chessName;

		return aPiece;
	}

	/**
	 * toString-metoden 
	 * @return String-verdi for dette objektet
	 */
	public String toString()
	{
		return "N";
	}

	public static void main(String args[]) {
		Knight knight = new Knight(Constants.WHITE, new BoardPoint(1,0));
		ArrayList vectors = knight.getPossibleLegalMoves(false);

		System.out.println("Vektorer for Knights flytt:");
		System.out.println("(X,Y), fixed");
		System.out.println("------------");
		for(int i = 0; i < vectors.size(); i++) {
			System.out.println(vectors.get(i));
		}
	}
}


