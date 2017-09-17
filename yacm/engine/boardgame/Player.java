package yacm.engine.boardgame;

import java.io.Serializable;
/*
 * Siste endret av: $Author: epoxy $
 */

/**
 * En brettspiller.
 * @author Morten L. Andersen
 * @version $Revision: 1.17 $
 */
public class Player implements Serializable
{
	/**
	 * Navnet til spilleren.
	 */
	private String name;
	/**
	 * Grensesnittet til spilleren.
	 */
	private int id;
	private int playerID;

	/**
	 * En dummmyvariabel som brukes av AI.
	 */
	private boolean dummy;

	/**
	 * Maksimumstiden spilleren bruker på et parti
	 */
	private long maxTime;

	/**
	 * Konstruerer en ny spiller.
	 * @param playerName Navnet til spilleren.
	 * @param ui Grensesnittet til spilleren.
	 * @param dummy En dummmyvariabel som brukes av AI.
	 */
	public Player(String playerName, int id, int playerID, long maxTime, boolean dummy)
	{
		this.name = playerName;
		this.id = id;
		this.playerID = playerID;
		this.maxTime = maxTime;
		this.dummy = dummy;
	}

	/**
	 * Henter ut navnet til spilleren.
	 * @return Navnet til spilleren.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Henter ut grensesnittet til spilleren.
	 * @return Grensesnittet til spilleren.
	 */
	public int getID()
	{
		return id;
	}

	public int getPlayerID()
	{
		return playerID;
	}

	/**
	 * Sjekker om spilleren har dummy satt.
	 * @return Er den det?
	 */
	public boolean isDummy()
	{
		return dummy;
	}

	/**
	 * Setter spillerens maksimumstid.
	 * @param time Tiden spilleren skal bruke.
	 */
	public void setMaxTime(long time)
	{
		maxTime = time;
	}

	/**
	 * Henter ur spillerens maksimumstid.
	 * @return Maksimumstiden.
	 */
	public long getMaxTime()
	{
		return maxTime;
	}

	public boolean equals(Object obj)
	{
		boolean isEqual = false;

		if(obj instanceof Player)
		{
			Player aPlayer = (Player) obj;

			isEqual = (id == aPlayer.id && playerID == aPlayer.playerID);
		}
		return isEqual;
	}
}
