 /*
  * @(#)$Id: Board.java,v 1.17 2003/05/05 14:37:44 ixan Exp $
  *
  * Coppyright 2003 Gruppe 10 All rights reserved.
  */
package yacm.engine.boardgame;

import java.util.ArrayList;

/*
 * Siste endret av: $Author: ixan $
 */

import yacm.engine.boardgame.chess.ChessPiece;

/**
 * Et interface for å kommunisere med brettet.
 * @author Morten L. Andersen
 */
public interface Board 
{

	/**
	 * Initsialiserer brettet med standardoppsett
	 */
	public void initBoard();

	/**
	 * Initsialiserer brettet med brikkene som blir sent inn
	 */
	public void initBoard(ChessPiece pieces[][]);

	/**
	 * Henter ut statusen til brettet.
	 * @return brettstatuset
	 */
	public int getBoardState();
	
	/**
	 * Forteller brettet av vi har valgt en brikke
	 * @param aMove Trekket vi har valgt.
	 * @return En liste over gyldige trekk for valgte brikke.
	 * @see yacm.engine.chess.Constants#NORMAL
	 * @see yacm.engine.chess.Constants#CHECK
	 * @see yacm.engine.chess.Constants#REMI
	 * @see yacm.engine.chess.Constants#PATT
	 * @see yacm.engine.chess.Constants#CHECKMATE
	 */
	
	public ArrayList selectPiece(Move aMove);
	/**
	 * Ber brettet om å gjennomføre et trekk.
	 * @param aMove Trekket vi ønsker å gjøre.
	 * @return Trekket som skal bli gjennomført.
	 */
	public int move(Move aMove);
	
	/**
	 * Forteller brettet at en bondeforvandling har blitt gjennomført og hvilken type brikke har blitt valgt.
	 * @param type Type brikke vi har valgt
	 * @param aMove Hvilket trekk vi gjennomførte for å sette bonden i stand til en bondeforvangling.
	 * @return Godkjent trekk hvis <code>true</code>.
	 */
	public boolean promotePawn(int type, Move aMove);

	/**
	 * Henter ut en tabell med sjakkbrikkene.
	 * @return Tabellen med sjakkbrikkene.
	 */
	public ChessPiece[][] getPieces();
}
