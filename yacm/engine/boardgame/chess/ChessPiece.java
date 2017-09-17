/*
 * @(#)$Id: ChessPiece.java,v 1.5 2003/05/06 16:34:30 simeng Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

/*
 * Siste endret av: $Author: simeng $
 */

/**
 * Klassen inneholder en sjakkbrikke, som selv har oversikt over hvilken
 * farge den har, og hvor på brettet den står.
 * @version     $Revision: 1.5 $, $Date: 2003/05/06 16:34:30 $
 * @author      Andreas Bach
 * @author      Morten L. Andersen
 */
public abstract class ChessPiece implements Constants, Serializable
{

	/**
	 * Om brikken er flyttet 
	 */
	protected boolean moved = false;

	/**
	 * Om brikken forsatt er i live.
	 */
	private boolean alive = true;

	/**
	 * Hvilken 'farge' brikken har.
	 */
	protected int color;

	/**
	 * Hva sjakknavnet til brikken er.
	 */
	protected String chessName;

	/**
	 * Posisjonen til brikken, hvis brikken lenger ikke
	 * er live, er dette den siste posisjonen brikken var på
	 */
	protected BoardPoint position;

	/**
	 * Flyttereglene til brikken.
	 */
	protected BoardPoint[] pieceRules;

	/**
	 * Hvilken numerisk type brikken er
	 */
	protected int type;


	/**
	 * Setter en brikke til å være død eller levende
	 * @param state Hvilken tilstand sjakkbrikken har.
	 */
	public void setAlive(boolean state)
	{
		alive = state;
	}

	/**
	 * Setter en brikke til å være flyttet eller ikke flyttet
	 * @param state Hvilken tilstand sjakkbrikken har.
	 */
	public void setMoved(boolean state)
	{
		moved = state;
	}

	/**
	 * Setter posisjonen til brikken (possibly exploit)
	 * @param point Ny posisjon for brikken.
	 */
	public void setPosition(BoardPoint point)
	{
		position = point;
	}

	/**
	 * Setter fargen til brikken
	 * @param color Ny farge for brikken.
	 */
	public void setColor(int color)
	{
		this.color = color;
	}

	/**
	 * Returnerer typen til sjakkbrikken
	 * @return Typen til brikken.
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Returnerer posisjonen til sjakkbrikken.
	 * @return Posisjonen til brikken.
	 */
	public BoardPoint getPosition()
	{
		return position;
	}
	
	/**
	 * Returnerer posisjonen til sjakkbrikken.
	 * @return X-posisjonen til brikken.
	 */
	public int getX()
	{
		return position.getX();
	}

	/**
	 * Returnerer posisjonen til sjakkbrikken.
	 * @return Y-posisjonen til brikken.
	 */
	public int getY()
	{
		return position.getY();
	}

	/**
	 * Returnerer fargen til sjakkbrikken
	 * @return Fargen til brikken.
	 */
	public int getColor()
	{
		return color;
	}

	/**
	 * Returnerer sjakk-navnet til brikken.
	 * @return Sjakk-navnet på brikken.
	 */
	public String getChessName() {
		return chessName;
	}

	/**
	 * Denne metoden returnerer de lovlige trekk som brikken har
	 * @return Lovlige trekk for brikken.
	 */
	public ArrayList getPossibleLegalMoves(boolean onlyKillingMoves)
	{
		return new ArrayList(Arrays.asList(pieceRules));
	}
	
	/**
	 * Returnerer hvorvidt brikken har blitt flyttet eller ikke.
	 * @return Tilstand om brukken har blitt flyttet eller ikke.
	 */
	public boolean isMoved() {
		return moved;
	}

	/**
	 * Returnerer hvorvidt brikken er i live eller ikke.
	 * @return Tilstands-variabel om brikken er i live eller ikke.
	 */
	public boolean isAlive() 
	{
		return alive;
	}

	/**
	 * Mottar en brikke og avgjør hvorvidt den er på samme lag som
         * this.brikke eller ikke.
	 * @param aPiece En sjakkbrikke som man ikke vet om er på lag eller ikke.
	 * @return Er brikken på samme lag som 'meg'
	 */
	public boolean isSameTeam(ChessPiece aPiece)
	{
		return aPiece != null ? this.color == aPiece.getColor() : false;
	}

	public abstract Object copy();

	/**
	 * Sjekker om et objekt er det samme som et annet
	 * @param obj Et objekt
	 * @return Hvorvidt objektet er det samme eller ikke.
 	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;

		if(obj instanceof ChessPiece)
		{
			ChessPiece aPiece = (ChessPiece) obj;
			isEqual = (
				moved == aPiece.moved
				&& alive == aPiece.alive
				&& color == aPiece.color
				&& position.equals(aPiece.getPosition())
				&& type == aPiece.type
			);
		}
		return isEqual;
	}
}
