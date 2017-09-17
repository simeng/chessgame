/*
 * @(#)$Id: BoardPoint.java,v 1.14 2003/04/29 02:51:16 ixan Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.engine.boardgame;

import java.io.Serializable;

/*
 * Siste endret av: $Author: ixan $
 */

/**
 * En BoardPoint representerer en lokalisasjon eller en rettning relativt til en bestemt lokalisasjon på brettet.
 * Til. eks. lokalisasjonen til en brikke på et sjakkbrett eller ruten a1 på et sjakkbrett.
 *
 * @version	$Revision: 1.14 $, $Date: 2003/04/29 02:51:16 $
 * @author  Kristian Berg
 * @author	Andreas Bach
 * @author	Morten L. Andersen
 */
public class BoardPoint implements Serializable
{
	/**
	 * <i>x</i> koordianten.
	 *
	 * @serial
	 */
	private int x;

	/**
	 * <i>y</i> koordianten.
	 *
	 * @serial
	 */
	private int y;

	/**
	 * fixed er <code>true</code> så er instansen et fast punkt,  hvis ikke er
	 * instansen en rettning.
	 *
	 * @serial
	 */
	private boolean fixed = true;

	/**
	 * Oppretter og initsialiserer et punkt. Hvis fixed er <code>true</code> så beskriver x og y et
	 * fast punkt, hvis fixed er <code>false</code> så beskriver x og y en rettning.
	 *
	 * @param	x	<i>x</i> koordianten
	 * @param	y	<i>y</i> koordianten
	 * @param	fixed	<i>fixed</i> om instansen er en et fast punkt eller en rettning.
	 */
	public BoardPoint(int x, int y, boolean fixed)
	{
		this.x = x;
		this.y = y;
		this.fixed = fixed;
	}

	/**
	 * Oppretter og inisialiserer fast punkt
	 *
	 * @param	x	<i>x</i> koordianten
	 * @param	y	<i>y</i> koordianten
	 * @param	fixed	<i>fixed</i> om instansen er et fast punkt eller en rettning.
	 */
	public BoardPoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Oppretter og initsierer et punkt i henold til et annet punkt.
	 * @param aPoint 	Lager et nytt BoardPoint objekt basert på <i>aPoint</i> BoardPoint objektet
	 */
	public BoardPoint(BoardPoint aPoint)
	{
		this.x = aPoint.getX();
		this.y = aPoint.getY();
		this.fixed = aPoint.isFixed();
	}

	/**
	 * Returnerer X-koordinaten.
	 * @return X-koordinaten.
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Returnerer Y-koordinaten.
	 * @return Y-koordinaten.
	 */
	public int getY()
	{
		return y;
	}


	/**
	 * Sjekker om BoardPoint er fixed.
	 * @return Returnerer <code>true</code> hvis punktet er fixed.
	 */
	public boolean isFixed()
	{
		return fixed;
	}

	/**
	 * Finner ut om en instans av <code>BoardPoint</code> er
	 * lik med dette punktet. To instanser av <code>BoardPoint</code> er like
	 * om verdiene av deres <code>x</code>, <code>y</code> og <code>fixtd</code>
	 * memberfields, er like.
	 *
	 * @param	obj	Object som skal sammenlignes med dette <code>BoardPoint</code>
	 * @return	<code>true</code> om det objektet som skal bli sammen lignes med
	 * 			er en instans av <code>BoardPoint</code> og har har de
	 * 			samme verdiene; <code>false</code> ellers.
	 */
	public boolean equals(Object obj)
	{
		boolean isEqual = false;
		if(obj instanceof BoardPoint)
		{
			BoardPoint pt = (BoardPoint)obj;
			isEqual = ( x == pt.getX() && y == pt.getY() && fixed == pt.isFixed());
		}

		return isEqual;
	}

	/**
	 * En metode for å returnere et <code>BoardPoint</code> i streng-format.
	 * @return En streng.
	 */
	public String toString()
	{
		return ("(" + getX() + "," + getY() + ") " + isFixed());
	}
	/**
	 * Starter en test av <code>BoardPoint</code> klassen.
	 * @param args[] Kommandolinje argumenter som den neppe bruker.
	 */
	public static void main(String args[])
	{
		BoardPoint a = new BoardPoint(1, 2, true);
		BoardPoint b = new BoardPoint(1, 2);
		BoardPoint c = new BoardPoint(2, 3, false);
		BoardPoint d = new BoardPoint(2, 3, true);
		BoardPoint e = new BoardPoint(2, 3, false);

		/*
		 * Sjekker om det som blir skrevet inn er det samme som det som
		 * blir skrvet ut
		 **/
		System.out.println("x inn = 1, x ut = " + a.getX());
		System.out.println("y inn = 2, y ut = " + a.getY());
		System.out.println("fixed inn = true, fixed ut = " + (a.isFixed()?"true":"false"));

		/*
		 * Sjekker om to instanser er like eller ikke
		 **/
		System.out.println("Resultatet skal være true " + (a.equals(b)?"true":"false"));
		System.out.println("Resultatet skal være false " + (a.equals(c)?"true":"false"));
		System.out.println("Resultatet skal være false " + (c.equals(d)?"true":"false"));
	}
}

