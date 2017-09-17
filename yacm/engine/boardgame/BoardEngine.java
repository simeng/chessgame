package yacm.engine.boardgame;

import java.rmi.*;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import yacm.engine.boardgame.chess.ChessPiece;

/*
 * Siste endret av: $Author: mortenla $
 */

/**
 * Et interface for klasser som skal kommunisere med yacm.engine.chess.
 * @author Kristian Berg
 * @version $Revision: 1.4 $
 *
 */
public interface BoardEngine extends Remote
{
	/**
	 * Starter partiet.
	 * @param aBoard Grensesnittet vi vil bruke til å kommunisere med spillmotoren.
	 * @return Det nye statuset til brettet.
	 */
	public int startGame(BoardUI aBoard)throws RemoteException;
	/**
	 * Stopper partiet.
	 * @param aBoard Grensesnittet som stopper spillet.
	 * @return Det nye statuset til brettet.
	 */
	public int stopGame(BoardUI aBoard)throws RemoteException;
	
	/**
	 * Ny runde sjakk.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean restartGame() throws RemoteException;
	
	/**
	 * Nytt spill.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean newGame()throws RemoteException;
	
	/**
	 * Legger til en ny spiller.
	 * @param player Spilleren som skal legges til.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean addPlayer(Player player)throws RemoteException;

	/**
	 * Fjerner en spiller.
	 * @param p Spilleren som skal fjernes.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean removePlayer(Player player)throws RemoteException;

	/**
	 * Legger til et nytt grensesnitt.
	 * @param boardUI Grensesnittet som skal legges til.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean addBoardUI(BoardUI boardUI)throws RemoteException;

	/**
	 * Fjerner et grensesnitt.
	 * @param boardUI Grensesnitet som skal fjernes.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean removeBoardUI(BoardUI boardUI)throws RemoteException;

	/**
	 * Henter ut hittil brukte tid i sekunder.
	 * @return Tiden partiet tok.
	 */
	public long getGameTime()throws RemoteException;

	/**
	 * Gir beskjed til brettet at spilleren har valgt brikken på <i>point</i>.
	 * @param point 	Et <code>BoardPoint</code> som angir hvilken brikke som er valgt.
	 * @return En <code>ArrayList</code> med <code>BoardPoint</code> over alle gyldige trekk brikken ta.
	 */
	public ArrayList selectPiece(BoardPoint point, Player aPlayer)throws RemoteException;
	
	/**
	 * Flytter en valgt brikke til et felt angitt av <i>point</i>
	 * @param point 	Et <code>BoardPoint</code> som angir hvor vi skal flytte.
	 * @return En <code>integer</code> som beskriver hvilket type trekk vi tok. Se Constants for mer informasjon.
	 */
	public int movePiece(BoardPoint point, Player aPlayer)throws RemoteException;

	/**
	 * Sender en melding om hvilken brikke vi forvandler bonden til.
	 * @param type En <code>integer</code> som beskriver hvilken <code>ChessPiece</code> vi velger. Se Constants for mer informasjon.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	public boolean promotePawn(int type, Player aPlayer)throws RemoteException;
}
