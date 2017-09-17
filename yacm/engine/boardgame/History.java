/*
 * Copyright 2003 Gruppe 10 All rights reserved.
 * @(#)$Id: History.java,v 1.16 2003/05/07 10:16:10 mortenla Exp $
 *
 */
package yacm.engine.boardgame;

import java.io.Serializable;
import yacm.engine.boardgame.chess.ChessMove;
import yacm.engine.boardgame.chess.ChessBoard;

/*
 * Siste endret av: $Author: mortenla $
 */

/**
 * Denne klassen lager en logg over alt som skjer på sjakkbrettet. Loggen er
 * en<i> linked list</i>.
 *
 * @author	Morten L. Andersen
 * @version	$Revision: 1.16 $,  $Date: 2003/05/07 10:16:10 $
 *
 */
public class History implements Serializable
{
	/**
	 * Gjeldende trekk.
	 */
	private Move currMove;
	/**
	 * Det første trekket.
	 */
	private Move firstMove;
	/**
	 * Det neste trekket
	 */
	private Move pointingOnMove;
	/**
	 * Størrelsen på loggen hittil.
	 */
	private int holding = 0;

	/**
	 * indeks til pekeren
	 */
	private int pointerIndex = 0;
	
	/**
	 * Konstruerer en ny <code>History</code>.
	 */
	public History(){}

	/**
	 * Konstruerer en ny <code>History</code>.
	 * @param history <code>History</code> objektet som skal klones.
	 */
	public History(History history)
	{
		currMove = history.currMove;
		firstMove = history.firstMove;
		pointingOnMove = history.pointingOnMove;
		holding = history.holding;
	}

	/**
	 * Henter ut størrelsen på <code>History</code>
	 * @return Størrelsen på <code>History</code>.
	 */
	public int size()
	{
		return holding;
	}

	/**
	 * Legger til et trekk i loggen.
	 * @param aMove Sjakktrekket.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean add(Move aMove)
	{
		boolean noError = false;

		if(aMove != null && aMove.isExecuted())
		{
			if(holding != 0)
			{
				currMove.setNext(aMove);
				aMove.setPrevious(currMove);
			}else{
				firstMove = aMove;
				pointingOnMove = aMove;
			}
			
			aMove.setMoveNumber(++holding);
			currMove = aMove;

			noError = true;
		}
		return noError;
	}

	/**
	 * Drenerer loggen for gamle trekk.
	 */
	public void clear()
	{
		Move nextMove = firstMove;
		Move tempMove = null;
		currMove = firstMove = null;
		holding = 0;
		pointerIndex = 0;

		/*
		 * Dette er for å ryde opp i linket listen
		 * er ikke helt nødvendig når spillet kjøre lokalt
		 * men kan ha konsekvenser hvis
		 * det er på et distriubert system.
		 */
		while(nextMove != null)
		{
			tempMove = nextMove;
				
			tempMove.setPrevious(null);
			nextMove = tempMove.getNext();
			tempMove.setNext(null);
		}
	}

	/**
	 * Henter ut siste trekk fra loggen.
	 * @return Siste trekk i loggen
	 */
	public Move getLast()
	{
		return currMove;
	}

	/**
	 * Sjekker om <code>History</code> har flere elementer.
	 * @return <code>true</code>, hvis det finnes flere.
	 */
	public boolean hasNext()
	{
		boolean noError = false;
		if(pointingOnMove != null)
		{
			if(pointerIndex < holding)
			{
				noError = true;
			}
		}
		return noError;
	}

	/**
	 * Sjekker om <code>History</code> har tidligere elementer.
	 * @return <code>true</code>, hvis det finnes tidligere.
	 */
	public boolean hasPrevious()
	{
		boolean noError = false;
		if(pointingOnMove != null)
		{
			if(pointerIndex > 0)
			{
				noError = true;
			}
		}
		return noError;
	}
	
	/**
	 * Spøler lenken tilbake.
	 */
	public void rewind()
	{
		pointingOnMove = firstMove; 
		pointerIndex = 0;
	}

	/**
	 * Henter ut forrige trekk fra loggen.
	 * @return Forrige trekk i loggen
	 */
	public Move getPrevious()
	{
		Move aMove = pointingOnMove;
		pointerIndex--;
		if(pointerIndex > 0)
		{
			pointingOnMove = pointingOnMove.getPrevious();
		}else{
			pointerIndex = 0;
		}
		return aMove;
	}

	/**
	 * Henter ut neste trekk fra loggen.
	 * @return Neste trekk i loggen.
	 */
	public Move getNext()
	{
		Move aMove = pointingOnMove;
		pointerIndex++;
		if(pointerIndex < holding)
		{
			pointingOnMove = pointingOnMove.getNext();
			
		}else{
			pointerIndex = holding;
		}
			
		return aMove;
	}

	/**
	 * Henter ut første trekk fra loggen.
	 * @return Første trekk i loggen.
	 */
	public Move getFirst()
	{
		return firstMove;
	}
}

