package yacm.boardUI.gui;

import yacm.engine.boardgame.chess.ChessPiece;
import yacm.engine.boardgame.chess.Constants;
import yacm.engine.boardgame.BoardEngine;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import yacm.engine.boardgame.History;
import yacm.engine.boardgame.BoardUI;
import yacm.engine.boardgame.Player;
import yacm.engine.boardgame.chess.ChessMove;
import yacm.engine.boardgame.chess.ChessGame;
import yacm.engine.boardgame.Move;

/*
 * Siste endret av: $Author: simeng $
 */

/**
 * Denne klassen viser trekkene i spillet i et råflott strengformat.
 * @author Simen Graaten
 * @author Trond Smaavik
 * @version $Revision: 1.18 $
 *
 *
 */
public class Client extends UnicastRemoteObject implements BoardUI, Constants, java.io.Serializable
{

	private Gui gui;
	private int id;

	public Client(String hostname) throws RemoteException {

		if (hostname == null) {
				hostname = "localhost";
		}

		try{
			//System.setSecurityManager(new RMISecurityManager());
			//String url = System.getProperty("game", "rmi:///yacm");
			BoardEngine engine = (BoardEngine) Naming.lookup("rmi://" + hostname + "/yacm");

			this.gui = new Gui(this, engine, "YACM - Yet Another Chess Master");
			boolean okID = false;
			while (!okID) {
				id = (int)(Math.random()*1000000);
				okID = engine.addBoardUI((BoardUI)this);
			}

		}catch(Exception e){
			System.out.println(e);
		}
	}

	public int getID() throws RemoteException{
		return id;
	}
	
	public void initUI(ChessPiece pieces[][], History history, long clockStart[]) throws RemoteException{
		gui.initUI(pieces, history, clockStart);
	}

	public void playerChange(Player p) throws RemoteException {
		gui.playerChange(p);
	}

	public void playerUpdate(ArrayList players) throws RemoteException {
		gui.playerUpdate(players);
	}

	public void updateUI(Move m) throws RemoteException {
		gui.updateUI(m);
	}

	public void updateGameStatus(int gameStatus) throws RemoteException {
		gui.updateGameStatus(gameStatus);
	}

	public void clockTick() throws RemoteException
	{
		gui.clockTick();
	}

	public static void main(String args[])
	{
		try{
			new Client(args[0]);
		}catch(Exception e){}

	}

}
