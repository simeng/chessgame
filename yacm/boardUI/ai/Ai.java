/*
 * @(#)$Id: Ai.java,v 1.1 2003/05/05 16:52:21 simeng Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.ai;

import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Siste endret av: $Author: simeng $
 */

/**
 * @author      Andreas Bach
 */
public class Ai {

	String navn;
	Board aiBoard;
	private ChessPiece realBoard[][] = new ChessPiece[8][8];
	private ChessPiece aiBoard[][] = new ChessPiece[8][8];
	private ChessPiece realPieces[][] = new ChessPiece[2][16];
	private ChessPiece aiPieces[][] = new ChessPiece[2][16];
	//ENGINES? Noen lyst å hjelpe meg?

	int depth; 				//Hvor langt ned vil vi?
	int x;					//Hvor mange trekk skal man regne videre på?
	ArrayList tankerekker();
	ArrayList currentTrekk();

	public Ai(String navn, int depth, int x, ChessPiece[][] realBoard, ChessPiece[][] pieces ) {
		//unødvendig crap hit
		this.navn = navn;
		this.depth = depth;
		this.x = x;
		this.realBoard = realBoard;
		realPieces = pieces;
		sync();
	
	}

// OBSOLETE
//	private scan() {
//		//finn alle brikker og pøtt dem i variabler
//	}


	//Hva sier det ekte sjakkbordet at man kan gjøre?
	private ArrayList getAllRealMoves() {
		ArrayList alleTrekk();
		HvaNåEnnDenReturnerer trekk; 
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				trekk = realPieces[i][j].finnalletrekkmetodenichessboard();
				alletrekk.add(trekk);
			}
		}
		return alleTrekk();
	}
	
	//Hvordan ser det ut på det lokale bordet?
	private ArrayList getAllAiMoves() {
	    ArrayList alleTrekk();
        HvaNåEnnDenReturnerer trekk;
	    for(int i = 0; i < 8; i++) {
	    	for(int j = 0; j < 8; j++) {
               	trekk = aiPieces[i][j].finnalletrekkmetodenichessboard();
               	alleTrekk.add(trekk);
            }
		}
		return alleTrekk();
    }
																										

	private ArrayList giAlleTrekkPoeng(inntrekkTabell) {
		ArrayList poengTabell();
		for(i = 0; i < inntrekkTabell.length(); i++) {
			double temp = giTrekkPoeng(inntrekkTabell(i));
			poengtabell.add(temp);
		}
		return poengTabell();
	}
			

	private static  giTrekkPoeng(inntrekk) {
		//yadayada, snadasnada;
		//kanskje jeg skal putte inn noe matnyttig her kanskje?
		return poengsum;
	}

	private void giTankeRekkePoeng(int tankerekkeNr, double antallPoeng) {
			tankerekker(tankerekkeNr).leggtilpoeng(antallPoeng);
	}

	private ArrayList pick(int antall-trekk, ArrayList currentTrekk) {
		//ranger og trekk ut dem som er best, basert på
		//en konstantklasse i samme dir
		return 
	}

	private dargeChess(BoardPoint hvorBlirJegAngrepetFra) {
		//Finn ut hvordan du kan unngå sjakk... 
		//Innebefatter scan() og pointingsak
	}

	private moveMyBoard(ChessPiece brikke, BoardPoint flytt) {
		//flytt lokalt
		//leke litt (reversere?)
	}

	private moveRealBoard() {
		//Fløtte på ekte
	}


	private static void sync() {
		for(int i = 0; i < 2; i++) {
	    	for(int j = 0; j < 16; i++) {
				aiPieces[i][j] = realPieces[i][j];
	        }
		}	
		for(i = 0; i < 8; i++) {
		   for(j = 0; j < 8; i++) {
		        aiBoard[i][j] = realBoard[i][j];
		    }
		}
	}//sync

	main() {
	
			//lese bordet, finne ut hvem sin tur det er
			//finne alle trekk
			//regne ut poengsum til alle
			//trekke ut x antall trekk
			//lage x antall tankerekker
						
			hurra:			
			for(i = 0; i < dybde; i++) {
				//regne ut alle motstanderens trekk ved de forskjellige x'trekkene
				
				ArrayList chosen = pick(x, currentTrekk); 	//trekke ut x antall trekk
				makeBranch(chosen);							//"branche" til nå x * x trekk
				giAlleTrekkPoeng(chosen); 					//regne ut poengsum til alle			
															//legge til poeng til den "overordnede branchen"
				//repetere til dybde
			}
			//finne branch med flest poeng
			//trekke den

			//repetere fra toppen
	
	}//main

}
