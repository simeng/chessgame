/*
 * @(#)$Id: ChessGame.java,v 1.27 2003/05/07 10:29:26 mortenla Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */

package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.BoardEngine;
import yacm.engine.boardgame.History;
import yacm.engine.boardgame.BoardPoint;
import yacm.engine.boardgame.Player;
import yacm.engine.boardgame.BoardUI;
import yacm.engine.boardgame.Board;
import yacm.engine.boardgame.Move;

import yacm.boardUI.ai.DBSC;
//import yacm.boardUI.gui.Gui;

import yacm.engine.TestTools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.awt.event.ActionListener;
import java.rmi.*;
import java.rmi.server.*;
import java.net.ServerSocket;
import java.net.*;
import java.net.InetAddress.*;
import java.util.Date;

/*
 * Siste endret av: $Author: mortenla $
 */

/**
 * Dette er hovedklassen i yacm-pakken. Den gjør matnyttige ting som å
 * starte gui og sjakkmotoren. Kort sagt så starter den spillet.
 *
 * @version     $Revision: 1.27 $, $Date: 2003/05/07 10:29:26 $
 * @author 	Morten L. Andersen
 * @author 	Simen Graaten
 * @author 	Kristian Berg
 * @author	Andreas Bach
 */

public class ChessGame extends UnicastRemoteObject implements BoardEngine, Constants, java.io.Serializable
{
//--- Atributter ---------------------------------------------------

	/**
	 * Et brett av typen sjakk-brett
	 */
	private Board board = new ChessBoard();

	/**
	 * Her lagres loggen over trekkene som er gjort
	 */
	private History history = new History();

	/**
	 * Mellomlagrings-variabel av det flyttet som er aktivt
	 */
	private ChessMove moveInPlay = null;

	/**
	 * Klokken som dette spillet skal bruke
	 */
	private ChessClock chessClock = new ChessClock(this);

	/**
	 * Hvilken av spillerene som er aktiv/har trekk.
	 */
	private int inPlay = WHITE;

	/**
	 * Denne variabelen inneholder status til spillet.
	 */
	private int gameStatus = INIT;

	/**
	 * Er spillet under start?
	 */
	private boolean start = false;

	/**
	 * Denne variabelen blir satt til true for å indikere at timeren har
	 * blitt forstyrret
	 */
	private boolean timerInterupted = false;

	/**
	 * Denne variabelen inneholder spillerene
	 */
	private ArrayList players = new ArrayList();

	/**
	 * Denne variabelen inneholder UI'ene som brukes til å spille på
	 */
	private ArrayList boardUIs = new ArrayList();

	//historje teting
	private ArrayList testDataSett = new ArrayList();
	private boolean historyTesting = false;

	/**
	 * Initierer spillet
	 */
	public ChessGame()throws RemoteException
	{
		this.initGame();
		updateStatus();
	}

	/**
	 * Denne metoden returnerer hvor mange ledige plasser det er til et spill
	 * @return Hvor mange plasser som er ledige for f.x. ai
	 */
	private int getNumberOfFreeDummySlots()
	{
                int number = 0;
	        Iterator itr = players.iterator();
		while(itr.hasNext())
		{

			if(itr.next() == null)
			{
				number++;
			}
		}
		return number;
	}

	/**
	 * Denne metoden returnerer antall spillere som er med.
	 * @return Antall spillere som er med i dette spillet.
	 */
	private int getNumberOfPlayersInPlay()
	{
		int number = 0;
		Iterator itr = players.iterator();
		while(itr.hasNext())
		{
			if(itr.next() != null)
			{
				number++;
			}
		}
		return number;
	}

	/**
	 * Denne metoden starter spillet
	 * @param aBoard GUI-brettet man spiller på
	 * @return Status for spillet
	 */
	public int startGame(BoardUI aBoard) throws RemoteException
	{
		if(hasPlayers(aBoard) || (getNumberOfPlayersInPlay() - getNumberOfDummysInPlay() == 0))
		{
			//lager eventuelle ledige slotter for Dummy Spillere
			if(getNumberOfPlayersInPlay() < MIN_PLAYERS)
			{
				for(int i = getNumberOfPlayersInPlay(); i < MIN_PLAYERS; i++)
				{
					players.add(null);
				}
			}

			start = true;
			setUpChessClock();
			firePlayerChangeEvent();
			updateStatus();
		}
		return gameStatus;
	}

	/**
	 * Denne metoden stopper spillet
	 * @param aBoard GUI-brettet som skal stoppes
	 * @return Status for spillet
	 */
	public int stopGame(BoardUI aBoard) throws RemoteException
	{

		if(hasPlayers(aBoard) || (getNumberOfPlayersInPlay() - getNumberOfDummysInPlay()) == 0)
		{
			start = false;
			updateStatus();
		}
		return gameStatus;

	}

	/**
	 * Kalles ved tidsavbrudd pga overskredete maksimaltid.
	 */
	protected void timerInt()
	{
		timerInterupted = true;
		updateStatus();
	}

	/**
	 * Setter opp klokkene for registrerte spillere.
	 */
	private void setUpChessClock()
	{
		if(getNumberOfPlayersInPlay() == MIN_PLAYERS)
		{
			ListIterator itr = players.listIterator();
			while(itr.hasNext())
			{
				int playerNumber = itr.nextIndex();
				Player aPlayer = (Player) itr.next();
				if(aPlayer != null)
				{
					aPlayer.setMaxTime(chessClock.setMaxTime(aPlayer.getMaxTime(), playerNumber));
				}
			}
		}else{
			chessClock.stopClock();
		}
	}

	/**
	 * Finner ut hvor mange ikke-menneskelige spillere som spiller.
	 * @return Antall programmerte spillere
	 */
	private int getNumberOfDummysInPlay()
	{
		int numberOfDummys = 0;

		Iterator itr = players.iterator();
		while(itr.hasNext())
		{
			Player aDummy = (Player) itr.next();
			if(aDummy != null && aDummy.isDummy())
			{
				numberOfDummys++;
			}
		}
		return numberOfDummys;
	}

	/**
	 * Finner ut om en spiller er registrert eller ikke.
	 * @param aPlayer	Spilleren man leter etter.
	 * @return <code>true</code> om spilleren er registrert, <code>false</code> ellers.
	 */
	private boolean hasPlayer(Player aPlayer)
	{
		boolean noError = false;

		if(aPlayer != null)
		{
			Iterator itr = players.iterator();
			while(itr.hasNext())
			{
				Player pl = (Player) itr.next();

				try{
					if(aPlayer.getID() == pl.getID() && aPlayer.getPlayerID() == pl.getPlayerID())
					{
						noError = true;
						break;
					}
				}catch(Exception e){}
			}

		}
		return noError;
	}

	private int getPlayerIndex(Player aPlayer)
	{

		int index = -1;	

		if(aPlayer != null)
		{
			ListIterator itr = players.listIterator();
			while(itr.hasNext())
			{
				int i = itr.nextIndex();
				
				Player pl = (Player) itr.next();

				try{
					if(aPlayer.getID() == pl.getID() && aPlayer.getPlayerID() == pl.getPlayerID())
					{
						index = i;
						break;
					}
				}catch(Exception e){}
			}

		}
		return index;
	}
	/**
	 * Denne metoden legger til en spiller
	 * @param aPlayer Spilleren som skal legges til
	 * @return Gikk det bra å legge til en spiller?
	 */
	public synchronized boolean addPlayer(Player aPlayer) throws RemoteException
	{
		boolean noError = false;
		if(!hasPlayer(aPlayer))
		{
			if(gameStatus != RUNNING)
			{
				if(getNumberOfPlayersInPlay() < MIN_PLAYERS)
				{
					players.add(aPlayer);
					noError = true;
				}
			}
			else
			{
				ListIterator itr = players.listIterator();
				while(itr.hasNext())
				{
					Player aDummy = (Player) itr.next();
					if(aPlayer != null)
					{
						if(!aPlayer.isDummy())
						{
							if(aDummy == null || aDummy.isDummy())
							{
								itr.set(aPlayer);
								noError = true;
								break;
							}
						}
						else if(aDummy == null)
						{
							itr.set(aPlayer);
							noError = true;
							break;
						}
					}

				}
			}

			if(noError)
			{

				firePlayerUpdate();
				firePlayerChangeEvent();
			}
		}





		return noError;
	}

	/**
	 * Denne metoden setter hvilket bord det spilles på
	 * @param aBoard Brettet det skal spilles på
	 * @return Gikk det bra å legge til brettet?
	 */
	public synchronized boolean addBoardUI(BoardUI aBoardUI) throws RemoteException
	{
		boolean noError = false;

		if(!boardUIExist(aBoardUI))
		{
			boardUIs.add(aBoardUI);

			ArrayList players = new ArrayList(this.players);
			Iterator itr = players.listIterator();

			while(itr.hasNext())
			{
				if(itr.next() == null)
				{
					itr.remove();
				}
			}
			aBoardUI.playerUpdate(players);
			aBoardUI.initUI(board.getPieces(), new History(history), chessClock.getPlayerTime());
			aBoardUI.updateGameStatus(gameStatus);
			aBoardUI.playerChange(getPlayerInPlay());
			//chessClock.addTickerListener((ActionListener) aBoardUI);
			noError = true;
		}
		return noError;
	}

	private boolean boardUIExist(BoardUI aBoardUI)
	{
		boolean noError = false;

		if(aBoardUI != null)
		{
			Iterator itr = boardUIs.iterator();
			while(itr.hasNext())
			{
				BoardUI ui = (BoardUI) itr.next();

				try{
					if(ui.getID() == aBoardUI.getID())
					{
						noError = true;
						break;
					}
				}catch(Exception e){}
			}

		}
		return noError;
	}

	/**
	 * Denne metoden fjerner en spiller
	 * @param aPlayer Spilleren som skal fjernes
	 * @return Gikk det bra å fjerne spilleren?
	 */
	public synchronized boolean removePlayer(Player aPlayer) throws RemoteException
	{

		boolean noError = false;

		ArrayList players = new ArrayList(this.players);

		int slotNumber = getPlayerIndex(aPlayer);

		if(slotNumber != -1)
		{
			// ferner evetnelt en spiller når spillet er i ganng
			// men bare hvis det ikke føre till at det er inngen innmelte spiller
			// dummu spiller er også regnet som en spiller
			if(gameStatus == RUNNING && !aPlayer.isDummy())
			{
				players.set(slotNumber, null);
				noError = true;

			}
			else if(gameStatus != RUNNING)
			{
				players.remove(aPlayer);
				noError = true;
				updateStatus();
			}

			if(noError)
			{
				this.players = players;
				firePlayerUpdate();
			}
		}
		return noError;
	}

	/**
	 * Denne metoden sjekker hvorvidt et spesifikt bord har spillere
	 * @param aBoard Brettet som skal sjekkes
	 * @return Har den spillere eller ikke?
	 */
	private boolean hasPlayers(BoardUI aBoard)
	{
		boolean itHasPlayers = false;
		if(boardUIExist(aBoard))
		{
			Iterator itr = players.iterator();
			while(itr.hasNext())
			{
				Player aPlayer = (Player) itr.next();
				try{
					if(aPlayer != null && aBoard.getID() == aPlayer.getID())
					{
						itHasPlayers = true;
						break;
					}
				}catch(Exception e){}
			}
		}
		return itHasPlayers;
	}

	/**
	 * Denne metoden fjerner et bord
	 * @param aBoard Brettet som skal fjernes
	 * @return Gikk det bra å fjerne bordet?
	 */
	public synchronized boolean removeBoardUI(BoardUI aBoard) throws RemoteException
	{
		boolean noError = false;
		if(boardUIExist(aBoard))
		{
			boardUIs.remove(aBoard);
			//chessClock.removeTickerListener(aBoard);

			Iterator itr = players.iterator();
			while(itr.hasNext())
			{
				Player aPlayer = (Player) itr.next();
				try{
					if(aPlayer != null && aBoard.getID() == aPlayer.getID())
					{
						removePlayer(aPlayer);
					}
				}catch(Exception e){}
			}
			updateStatus();
			noError = false;
		}
		return noError;
	}

	/**
	 * Denne metoden returnerer hvilken spiller sin tur det er
	 * @return Spilleren som har tur
	 */
	private Player getPlayerInPlay()
	{
		Player aPlayer = null;

		if(players.size() != 0 && inPlay < players.size())
		{
			aPlayer = (Player) players.get(inPlay);
		}
		return aPlayer;
	}

	/**
	 * Kjøres når det har skjedd en endring på sjakkbrettet.
	 * Kaller updateUI() i all innmeldte brett.
	 */
	private void fireBoardChangeEvent()
	{
		if(moveInPlay != null)

		{
			ListIterator itr = boardUIs.listIterator();
			while(itr.hasNext())
			{
				int index = itr.nextIndex();
				BoardUI aBoardUI = (BoardUI) itr.next();

				try{
				aBoardUI.updateUI(moveInPlay);
				}catch(Exception e){}
			}
		}

	}

	protected void fireClockTick()
	{
		ListIterator itr = boardUIs.listIterator();
		while(itr.hasNext())
		{
			int index = itr.nextIndex();
			BoardUI aBoardUI = (BoardUI) itr.next();

			try{
				aBoardUI.clockTick();
			}catch(Exception e){}
		}
	}
	
	/**
	 * Kjøres når spillet bytter tur.
	 * Kaller playerChange() i alle innmeldte brett.
	 */
	private void firePlayerChangeEvent()
	{
		Player playerInPlay = getPlayerInPlay();
		//if(playerInPlay != null)
		//{
			Iterator itr = boardUIs.iterator();
			while(itr.hasNext())
			{
				BoardUI aBoardUI = (BoardUI) itr.next();
				try{
				aBoardUI.playerChange(playerInPlay);
				}catch(Exception e){}
			}
		//}
	}


	/**
	 * Denne metoden kjører oppdatering til alle bord
	 */
	private void fireInitBoardUI()
	{
		Iterator itr = boardUIs.iterator();
		while(itr.hasNext())
		{
			BoardUI aBoardUI = (BoardUI) itr.next();

			try{
			aBoardUI.initUI(board.getPieces(), new History(history), chessClock.getPlayerTime());
			}catch(Exception e){}
		}
	}

	/**
	 * Denne metoden kjører oppdatering til alle spillere
	 */
	private void firePlayerUpdate()
	{

		setUpChessClock();

		ArrayList players = new ArrayList(this.players);
		Iterator itr = players.listIterator();

		while(itr.hasNext())
		{
			if(itr.next() == null)
			{
				itr.remove();
			}
		}

		itr = boardUIs.iterator();
		while(itr.hasNext())
		{
			BoardUI aBoardUI = (BoardUI) itr.next();
			try{
			aBoardUI.playerUpdate(players);
			}catch(Exception e){}
		}

		if(getNumberOfPlayersInPlay() == MIN_PLAYERS && gameStatus == RUNNING)
		{
			chessClock.startClock();
		}


	}

	/**
	 * Denne metoden kjører oppdatering til alle spill
	 */
	private void fireGameStatusEvent()
	{
		Iterator itr = boardUIs.iterator();
		while(itr.hasNext())
		{
			try{
			((BoardUI)itr.next()).updateGameStatus(gameStatus);
			}catch(Exception e){}
		}
	}

	/**
	 * Denne metoden oppdaterer status for spillet
	 * @return Hva status for spillet er nå
	 */
	private int updateStatus()
	{
		if(gameStatus == INIT)
		{
			gameStatus = READY;
		}
		else if(gameStatus == READY && start)
		{
			if(getNumberOfPlayersInPlay() == MIN_PLAYERS)
			{
				chessClock.startClock();
			}
			gameStatus = RUNNING;
		}
		else if((gameStatus == RUNNING || gameStatus == STOPPED) && !start)
		{
			chessClock.stopClock();
			gameStatus = READY;
		}

		if(timerInterupted || (moveInPlay != null && moveInPlay.getResult() > CHECK))
		{
			chessClock.stopClock();
			timerInterupted = false;
			gameStatus = STOPPED;
		}


		//dette kjører history testn
		if(gameStatus == STOPPED && historyTesting)
		{
			test1();
		}
		fireGameStatusEvent();
		return gameStatus;
	}

	/**
	 * Klargjør gui og sjakkbrettet.
	 *
	 * @return   a boolean
	 *
	 */
	private boolean initGame()
	{
		boolean noError = false;
		if(gameStatus != RUNNING)
		{
			gameStatus = INIT;
			fireGameStatusEvent();
			
			moveInPlay = null;
			history.clear();
			inPlay = WHITE;
			timerInterupted = false;

			board.initBoard();
			chessClock.initClock(MIN_PLAYERS, false);
			
			fireInitBoardUI();
			noError = true;
		}
		return noError;
	}

	public boolean restartGame() throws RemoteException
	{
		boolean noError = false;
		if(start == false)
		{
			if(initGame())
			{
				firePlayerUpdate();
				updateStatus();
				noError = true;
			}
		}
		return noError;
	}

	/**
	 * Denne metoden starter et nytt spill
	 * @return Gikk det bra å starte et nytt spill?
	 */
	public boolean newGame() throws RemoteException
	{
		boolean noError = false;
		if(initGame())
		{
			players.clear();
			firePlayerUpdate();
			updateStatus();
			noError = true;
		}
		return noError;
	}

	/**
	 * Tar i mot et kall fra gui'et om å flytte en brikke og sender kallet videre
	 * til ChessBoard.
	 *
	 * @param    point               a  BoardPoint
	 * @return   a boolean
	 */
	public int movePiece(BoardPoint position, Player aPlayer) throws RemoteException
	{
		int outcome = 0;

		if(moveInPlay != null && gameStatus == RUNNING && moveInPlay.setDest(position) && aPlayer != null && aPlayer.equals(getPlayerInPlay()))
		{
			moveInPlay.execute();
			outcome = moveInPlay.getOutcome();
			if(outcome != PAWN_PROMOTION)
			{
				endTurne();
			}
		}
		return outcome;
	}

	/**
	 * Denne metoden avslutter en omgang/trekk.
	 */
	private void endTurne()
	{

		if(historyTesting)
		{
			testDataSett.add(moveInPlay);
		}

		history.add(moveInPlay);
		inPlay = ((ChessBoard) board).getOpponentColor(inPlay);
		chessClock.nextPlayer();
		fireBoardChangeEvent();
		if(updateStatus() != STOPPED)
		{
			firePlayerChangeEvent();
		}
		moveInPlay = null;
		
	}

	/**
	 * Velger en brikke på brettet. Returnerer en tabell med gyldige trekk for
	 * valgt brikke slik at gui ved hvilke valg som er gyldige.
	 *
	 * @param    point               a  BoardPoint
	 * @return   ArrayList
	 */
	public ArrayList selectPiece(BoardPoint position, Player aPlayer) throws RemoteException
	{
		ChessMove aMove = new ChessMove(board, position, inPlay);
		ArrayList legalMoves = null;

		if(moveInPlay == null && gameStatus == RUNNING && aPlayer != null && aPlayer.equals(getPlayerInPlay()))
		{
			legalMoves = board.selectPiece(aMove);
			if(legalMoves != null)
			{
				moveInPlay = aMove;
			}
		}
		return legalMoves;
	}

	/**
	 * Denne metoden forfremmer en bonde til en gitt type
	 * @param type Typen brikke den skal forfremmes til
	 * @param aPlayer Spilleren som eier brikken
	 * @return Tilstand, gikk dette bra?
	 */
	public boolean promotePawn(int type, Player aPlayer) throws RemoteException
	{
		boolean noError = false;
		if(board.promotePawn(type, moveInPlay) && aPlayer != null && aPlayer.equals(getPlayerInPlay()))
		{
			endTurne();
		}
		return noError;
	}

	/**
	 * Denne metoden returnerer hvor lang tid spillet har vart
	 * @return Hvor lenge spillet har vart
	 */
	public long getGameTime() throws RemoteException
	{
		return chessClock.getGameTime();
	}

	public void test1()
	{
		String testVariables[] = {"trekk"};

		String testResult[][]  = new String[testDataSett.size() * 2][3];


		ListIterator lItr = testDataSett.listIterator();

		history.rewind();

		int setNo = 0;
		while(history.hasNext())
		{
			Move aMove = (Move) lItr.next();
			testResult[setNo][0] = "" + aMove;
			testResult[setNo][1] = "" + aMove;
			testResult[setNo][2] = "" + history.getNext();
			setNo++;
		}

		while(history.hasPrevious())
		{
			Move aMove = (Move) lItr.previous();
			testResult[setNo][0] = "" + aMove;
			testResult[setNo][1] = "" + aMove;
			testResult[setNo][2] = "" + history.getPrevious();
			setNo++;
		}

		TestTools.printTestResult("testing av historje klassen", testVariables, testResult);
	}

	public static void main(String args[])
	{
		try{
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress();
			Date dato = new Date();
			System.out.println("   ###############################################");
			System.out.println("   #  Server started at IP "+ip+"          #");
			System.out.println("   #  "+dato+"              #");
			System.out.println("   #                                             #");
			System.out.println("   ###############################################");
			System.out.println();
			//System.out.println("Starting rmiregistry");
			//Runtime.getRuntime().exec("rmiregistry");
			for(int i=0; i<10000000; i++){}
			System.out.println("Staring server");
			BoardEngine server = new ChessGame();
			//String name = System.getProperty("gamename", "yacm");
			Naming.rebind("yacm", server);
			new DBSC();
		}catch(Exception e){}

	}
}
