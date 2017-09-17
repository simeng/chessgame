/*
 * @(#)$Id: Constants.java,v 1.5 2003/05/05 18:00:47 falxx Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame.chess;

import java.awt.Color;

/*
 * Siste endret av: $Author: falxx $
 */

/**
 * En samling konstanter.
 *
 * @version     $Revision: 1.5 $, $Date: 2003/05/05 18:00:47 $
 * @author	Kristian Berg
 * @author	Morten Løkke Andersen
 * @author	Andreas Bach
 */
public interface Constants
{
	/*MLA*
	 * ikke forandre på denne rekke følgen uten å fortelle meg
	 * om det, for det jeg benytter meg av at NORMAL og CHECK er de
	 * to minste tallene. i klassen ChessBoard
	 */

	/**
	 * Tallkonstant for at spillet har tilstand normal 
 	 */
	public final int NORMAL = 0;
	
	/**
	 * Tallkonstant for at spillet står i sjakk
	 */
	public final int CHECK = 1;
	
	/**
	 * Tallkonstant for at spillet står i remi
	 */
	public final int REMI = 2;
	
	/**
	 * Tallkonstant for at spillet står i patt
	 */
	public final int PATT = 3;
	
	/**
	 * Tallkonstant for at spillet står i sjakk-matt
	 */
	public final int CHECKMATE = 4;

	/**
	 * Tallkonstant for at spillet står i initiering
	 */
	public final int INIT = 0;
	
	/**
	 * Tallkonstant for at spillet er klart
	 */
	public final int READY = 1;
	
	/**
	 * Tallkonstant for at spillet er i gang
	 */
	public final int RUNNING = 2;
	
	/**
	 * Tallkonstant for at spillet er i gang
	 */
	public final int STOPPED = 3;


	/**
	 * Tallkonstant for maks antall spillere
	 */
	public final int MAX_PLAYERS = 2;
	
	/**
	 * Tallkonstant for minimum antall spillere
	 */
	public final int MIN_PLAYERS = MAX_PLAYERS;


	// Konstanter for de forskellige brikkene
	// beskriver også rekke følgen de blir lakt inn i
	// tabeller

	/**

	 * Tallkonstant for når brikken tårn skal legges inn i tabeller
	 */
	public final int ROOK = 0;
	
	/**
	 * Tallkonstant for når brikken hest skal legges inn i tabeller
	 */
	public final int KNIGHT = 1;
	/**
	 * Tallkonstant for når brikken løper skal legges inn i tabeller
	 */
	public final int BISHOP = 2;
	/** 
	 * Tallkonstant for når brikken tårn (på dronning-side) skal legges inn i tabeller
	 */ 
	public final int ROOK_QUEEN = ROOK;
	/**
	 * Tallkonstant for når brikken hest (på dronning-side) skal legges inn i tabeller
	 */ 
	public final int KNIGHT_QUEEN = KNIGHT;
	/**
	 * Tallkonstant for når brikken løper (på dronning-side) skal legges inn i tabeller
	 */ 
	public final int BISHOP_QUEEN = BISHOP;
	/**
	 * Tallkonstant for når brikken dronning skal legges inn i tabeller
	 */ 
	public final int QUEEN = 3;
	/**
	 * Tallkonstant for når brikken konge skal legges inn i tabeller
	 */ 
	public final int KING = 4;
	/**
	 * Tallkonstant for når brikken løper (på konge-side) skal legges inn i tabeller
	 */ 
	public final int BISHOP_KING = 5;
	/**
	 * Tallkonstant for når brikken hest (på konge-side) skal legges inn i tabeller
	 */ 
	public final int KNIGHT_KING = 6;
	/**
	 * Tallkonstant for når brikken tårn (på konge-side) skal legges inn i tabeller
	 */ 
	public final int ROOK_KING = 7;
	/**
	 * Tallkonstant for når brikken bonde skal legges inn i tabeller
	 */ 
	public final int PAWN = 8;
	/** 
	 * Tallkonstant for når den første bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_1 = PAWN;
	/**
	 * Tallkonstant for når den andre bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_2 = 9;
	/**
	 * Tallkonstant for når den tredje bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_3 = 10;
	/**
	 * Tallkonstant for når den fjerde bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_4 = 11;
	/**
	 * Tallkonstant for når den femte bondebrikken skal legges inn i tabeller
	 */
	public final int PAWN_5 = 12;
	/**
	 * Tallkonstant for når den sjette bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_6 = 13;
	/**
	 * Tallkonstant for når den sjuende bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_7 = 14;
	/**
	 * Tallkonstant for når den åttende bondebrikken skal legges inn i tabeller
	 */ 
	public final int PAWN_8 = 15;

	/**
	 * Tallverdi for 'fargen'/siden hvit
	 */
	public final int WHITE = 0;
	
	/**
	 * Tallverdi for 'fargen'/siden svart
	 */
	public final int BLACK = 1;

	/**
	 * Booleansk konstant for gjennomsiktighet av trekk
	 */
	public final boolean TRANSPARENT = true;
	
	/**
	 * Booleansk konstant for manglene på gjennomsiktighet av trekk
	 */
	public final boolean NOT_TRANSPARENT = false;

	/**
	 * Tallverdi for forfremmelse av bonde
	 */
	public final int PAWN_PROMOTION = 1;
	
	/**
	 * Tallverdi for rokkade på konge-side
	 */
	public final int ROKADE_KING = 2;
	
	/**
	 * Tallverdi for rokkade på dronning-side
	 */
	public final int ROKADE_QUEEN = 3;
	
	/**
	 * Tallverdi for enpassant
	 */
	public final int ENPASSANT = 4;
}
