/*
 *  @(#)$Id: ChessBoardState.java,v 1.4 2003/05/05 15:11:59 falxx Exp $
 * 
 *  Coppyright 2003 Gruppe 10 All rights reserved.
 **/

package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.BoardPoint;

/**
 * Denne klassen inneholder all informasjon om hvilken tilstand brettet har.
 * 
 * @author      Morten L. Andersen
 * @author      Andreas Bach
 */


class ChessBoardState implements java.io.Serializable
{
	/**
	 * Spillets telleverk
	 */
	private int counter = 1;
	
	/**
	 * Mellomvariabel for lagring av passant-koordinater
	 */
	private BoardPoint enpassantCoordinat;
	
	/**
	 * Variabel for mellomlagring av brikker
	 */
	private ChessPiece pieces[];
	
	/**
	 * Hvilken spiller er den aktive nå?
	 */
	private int inPlay;


	/**
	 * Oppretter og initierer en tilstand for brettet
	 * @param enpassantCoordinat Koordinatene til et evt enpassant-trekk
	 * @param pieces[][] Alle brikkene
	 * @param inplay Hvilken spiller som er aktiv
	 */
	ChessBoardState(BoardPoint enpassantCoordinat, ChessPiece pieces[][], int inPlay)
	{
		this.enpassantCoordinat = enpassantCoordinat;
		this.inPlay = inPlay;
		addAllAlivePieces(pieces);
		
	}

	/**
	 * Denne metoden legger til 1(én) til counteren og returnerer summen
	 * @return tallet 'counter' akkurat nå
	 */
	public int addCounter()
	{
		return ++counter;
		
	}
	
	/**
	 * Denne metoden legger til alle brikker som er i live, fra en todimensjonal tabell
	 * @param pieces[][] Tabellen som skal scannes
	 */
	private void addAllAlivePieces(ChessPiece pieces[][])
	{
		ChessPiece aPiece = null;
		ChessPiece newPieces[] = new ChessPiece[32];
		int index = 0;

		for(int i = 0; i < pieces.length; i++)
		{
			for(int j = 0; j < pieces[i].length; j++)
			{
				aPiece = pieces[i][j];
				if(aPiece.isAlive())
				{
					newPieces[index] = (ChessPiece) aPiece.copy();
					index++;
				}
			}
		}

		this.pieces = new ChessPiece[index];

		System.arraycopy(newPieces, 0, this.pieces, 0, index);
	}

	/**
	 * Denne metoden sammenligner de lokale brikkene med en array med andre brikker
	 * @param pieces[] Brikker som skal sammenlignes
	 * @return Var de like eller ikke?
	 */
	private boolean commpareArray(ChessPiece pieces[])
	{
		boolean isEqual = false;
		if(this.pieces.length == pieces.length)
		{
			isEqual = true;
			for(int i = 0; i < this.pieces.length; i++)
			{
				if(!this.pieces[i].equals(pieces[i]))
				{
					isEqual = false;
					break;
				}
			}
		}
		return isEqual;
	}

	/**
	 * Denne metoden sammenligner en Passant-koordinat med en annen
	 * @param enpassantCoordinat
	 * @return Er de like eller ikke?
	 */
	private boolean commparePassantCoordinat(BoardPoint enpassantCoordinat)
	{
		boolean isEqual = false;
		if(this.enpassantCoordinat != null)
		{
			isEqual = this.enpassantCoordinat.equals(enpassantCoordinat);
		}else{
			isEqual = enpassantCoordinat == null;
		}

		return isEqual;
	}

	/**
	 * Denne metoden sjekker hvorvidt dette objektet er likt et annet
	 * @param obj Objekt man vil sjekke mot
	 * @return Er det likt eller ikke?
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if(obj instanceof ChessBoardState)
		{
			ChessBoardState aState = (ChessBoardState) obj;

			isEqual = this.inPlay == aState.inPlay && commpareArray(aState.pieces)
				&& commparePassantCoordinat(aState.enpassantCoordinat);
		}
		return isEqual;
	}
}
