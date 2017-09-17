/*
 * @(#)$Id: ChessClock.java,v 1.13 2003/05/07 00:50:58 mortenla Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 *
 */

package yacm.engine.boardgame.chess;

import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.Timer;
//import javax.swing.event.EventListenerList;


/**
 * Denne klassen inneholder klokkene som blir brukt.
 * 
 * @author Morten Andersen
 * @author Andreas Bach
 */

public class ChessClock implements ActionListener, Constants, java.io.Serializable
{
	/**
	 * Konstant for et minutt.
	 */
	private final long ONE_MIN = 60L;

	/**
	 * Konstant for en time.
	 */
	private final long ONE_HOUR = ONE_MIN * 60L;
	
	/**
	 * Konstant for standard-tid.
	 */
	private final long DEFALT_TIME = ONE_HOUR;

	/**
	 * Konstant for minimums-tid.
	 */
	private final long MIN_TIME = ONE_MIN;

	/**
	 * Timeren som klokka bruker.
	 */
	private Timer tick;
	
	/**
	 * Sjakkspillet klokka hører til.
	 */
	private ChessGame game;

	/**
	 * Spilleren i spill 
	 */
	private int playerNumber = 0;

	/**
	 * Hvor lang tid spillet har tatt
	 */
	private long gameTime;
	
	/**
	 * Spillerens betenkningstid(er)
	 */
	private long playerClocks[];

	/**
	 * Spilerens maks betenkningstid
	 **/
	private long playerMaxTime[];

	/**
	 * Variabel som bestemmer hvorvidt den logger eller ikke
	 */
	private boolean log = false;
	
	/**
	 * Denne variabelen inneholder loggen
	 */
	private ArrayList lapTimeLog[];
	
	/**
	 * Denne variabelen inneholder en omgangs betenkningstid
	 */
	private long lapTime = 0;

	/**
	 * Oppretter og initierer en klokke
	 * @param game Hvilket spill den tilhører
	 */
	public ChessClock(ChessGame game)
	{
		this.game = game;
		tick = new Timer(1000, this);
		initClock(2, false);
	}

	/**
	 * Initierer et x antall klokker
	 * @param numberOfClocks Antallet klokker som skal bli initiert
	 * @param log om man skal logge runde tiden eller ikke 
	 */
	public void initClock(int numberOfClocks, boolean log)
	{
		playerNumber = 0;

		this.log = log;

		gameTime = 0;
		lapTime = 0;

		playerClocks = new long[numberOfClocks];
		playerMaxTime = new long[numberOfClocks];
		lapTimeLog = new ArrayList[numberOfClocks];

		for(int i = 0; i < lapTimeLog.length; i++)
		{
			lapTimeLog[i] = new ArrayList();
		}

		//seter en default tid
		for(int i = 0; i < playerMaxTime.length; i++)
		{
			playerMaxTime[i] = DEFALT_TIME;
		}
	}

	/**
	 * Denne metoden setter maks tid, i minutter
	 * @param time Tiden i minutter
	 * @return Hvor lang tid man har på seg
	 **/
	public long setMaxTime(long time, int playerNumber)
	{
		if(playerNumber < playerClocks.length && playerNumber >= 0)
		{
			if(time > 0)
			{
				time = time < MIN_TIME ? MIN_TIME : time;
				playerMaxTime[playerNumber] = time;
			}else{
				time = playerMaxTime[playerNumber];
			}
		}
		return time;
	}

	/**
	 * Denne metoden sjekker om klokka går
	 * @return Om den går eller ikke
	 */
	public boolean isRunning()
	{
		return tick.isRunning();
	}

	/**
	 * Denne metoden stopper klokka
	 */
	public void stopClock()
	{
		if(tick.isRunning())
		{
			tick.stop();
		}
	}

	/**
	 * Denne metoden starter klokka
	 */
	public void startClock()
	{
		if(!tick.isRunning())
		{
			tick.start();
		}
	}

	/**
	 * Denne metoden går til neste spiller/klokke
	 */
	public void nextPlayer()
	{
		if(tick.isRunning())
		{
			tick.restart();
			playerNumber = playerNumber == WHITE ? BLACK : WHITE;
			if(log)
			{
				lapTimeLog[playerNumber].add(new Long(lapTime));
			}
			lapTime = 0;
		}else{
			tick.start();
		}
	}

	/**
	 * Denne oppdater kl for vært sek.
	 * @param e Hva som skjedde.
	 */
	public void actionPerformed(ActionEvent e)
	{
		gameTime++;
		lapTime++;
		game.fireClockTick();
		if(playerClocks[playerNumber]++ >= playerMaxTime[playerNumber])
		{
			tick.stop();
			game.timerInt();
			
		}
	}

	/**
	 * Denne metoden returnerer hvor lang tid spillet har tatt
	 * @return Hvor lang tid spillet har tatt
	 */
	public long getGameTime()
	{
		return gameTime;
	}

	/**
	 * Denne metoden returnerer hvor lang tid en spiller har tenkt
	 * @return En arraylist med tidene som spilleren har brukt
	 */
	public long[] getPlayerTime()
	{
		long playerTime[] = new long[playerClocks.length];
		
		for(int i = 0; i < playerClocks.length; i++)
		{
			playerTime[i] = playerClocks[i];
		}
		return playerTime;
	}

	/**
	 * Denne metoden returnerer loggen over tidene brukt
	 * @return Loggen over tidene spilleren har brukt
	 */
	public ArrayList[] getLog()
	{
		return lapTimeLog;
	}
}
