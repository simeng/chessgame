package yacm.engine.boardgame;

import yacm.engine.boardgame.chess.ChessPiece;
import java.rmi.*;
import java.rmi.Remote;
import java.util.ArrayList;

/**
 * Et interface for å kommunisere med spillmotoren.
 * @author Morten L. Andersen
 */
public interface BoardUI extends Remote 
{
	/**
	 * Oppdaterer ui med de siste forandringen i motoren.
	 * @param b Endringen motoren skal registrere
	 */
	public void updateUI(Move move)throws RemoteException;
	
	/**
	 * Initsierer brettet med en ny tabell av sjakkbrikker.
	 * @param pieces[][] Tabellen med brikker som skal brukes.
	 */
	public void initUI(ChessPiece pieces[][], History history, long playerTime[]) throws RemoteException;
	
	/**
	 * Oppdaterer spillets status.
	 * @param gameStatus Spillets status,
	 */
	public void updateGameStatus(int gameStatus)throws RemoteException;

	/**
	 * Oppdaterer spille listen
	 * @param players ArrayListe med spiller som er inn
	 * melt
	 **/
	public void playerUpdate(ArrayList players)throws RemoteException;

	/**
	 * Sier vilken spiler som er i trekk.
	 * @param player spilleren som er i trekk
	 */
	public void playerChange(Player player)throws RemoteException;
	
	public void clockTick() throws RemoteException;

	public int getID() throws RemoteException;
}
