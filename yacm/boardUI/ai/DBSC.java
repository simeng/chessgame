package yacm.boardUI.ai;

import yacm.engine.boardgame.BoardPoint;
import yacm.engine.boardgame.BoardUI;
import yacm.engine.boardgame.Player;
import yacm.engine.boardgame.BoardEngine;
import yacm.engine.boardgame.History;
import yacm.engine.boardgame.Move;
import yacm.engine.boardgame.chess.ChessPiece;
import yacm.engine.boardgame.chess.ChessMove;
import yacm.engine.boardgame.chess.Constants;
import yacm.engine.boardgame.chess.ChessGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.ActionEvent;
import java.rmi.*;
import java.rmi.server.*;

/**
 * Deep Blue's Second Cousin (den nyfødte)
 */
public class DBSC extends UnicastRemoteObject implements BoardUI, Constants, Runnable
{
	private Thread t;
	
	private BoardEngine engine;
	//ArrayList AiPlayers = new ArrayList();
	private ArrayList playersInPlay = new ArrayList();
	private ChessPiece pieces[][];
	private ChessMove lastMove = null;
	private Player currentPlayer = null;
	private int gameState = NORMAL;
	private String aiNames[] = { "DBSC", "Robot", "Mr.Bush", "Cordelia", "Duke",
							"/bin/laden", "Ernest", "Sid", "Mr.Potato" };
	private int id;

	public DBSC() throws RemoteException
	{
		t = new Thread(this, "Ai Thread");
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

		boolean okID = false;
		try{
			do{
				engine = (BoardEngine) Naming.lookup("//localhost/yacm");
				id = (int)(Math.random()*1000000);
				okID = engine.addBoardUI((BoardUI)this);
			}while(!okID);
		}catch(Exception e){}
	}

	public int getID() throws RemoteException
	{
		return id;
	}

	public void run()
	{
		while(true)
		{
			if (Thread.currentThread() == t) {
				try {
					Thread.sleep(1100);
				}catch(InterruptedException e){}
			
			
				if (gameState == RUNNING) {
					if(hasPlayer(currentPlayer) && (lastMove == null || lastMove.getResult() < REMI))
					{
						int playerNumber = playersInPlay.indexOf(currentPlayer);
						calculateMove(playerNumber, currentPlayer);
					}
					addAiPlayer();
				}
			}
		}
	}

	public void updateUI(Move move) throws RemoteException
	{
		lastMove = (ChessMove) move;

		if(lastMove != null) { oppdatePieces(lastMove); }

	}

	public void playerChange(Player player) throws RemoteException
	{
		currentPlayer = player;	
	}

	private void oppdatePieces(ChessMove m)
	{
		int player = m.getOwner();
		int index = indexOfPiece(m.getOrig(), player);

		int outcome = m.getOutcome();
		switch(outcome)
		{
			case PAWN_PROMOTION:
				pieces[player][index] = (ChessPiece) m.getPromotion().copy();
				break;
			case ROKADE_KING:
			case ROKADE_QUEEN:
				int rookIndex = indexOfPiece(m.getRookOrig(), player);
				pieces[player][rookIndex].setPosition(m.getRookDest());
			case ENPASSANT:
			default:
				pieces[player][index].setPosition(m.getDest());
				pieces[player][index].setMoved(true);
		}
		ChessPiece casualty = m.getCasualty();
		if(casualty != null)
		{
			int opponent = getOpponetColor(player);
			index = indexOfPiece(casualty.getPosition(),opponent);
			pieces[opponent][index].setAlive(false);
		}
	}

	private int getOpponetColor(int color)
	{
		return color == 0 ? 1 : 0;
	}

	private int indexOfPiece(BoardPoint pos, int color)
	{
		int index = -1;
		for(int i =  0; i < pieces[color].length; i++)
		{
			if(pieces[color][i].getPosition().equals(pos) && pieces[color][i].isAlive())
			{
				index = i;
				break;
			}
		}
		return index;
	}

	private boolean hasPlayer(Player p)
	{
		boolean noError = false;
		if(p != null && p.getID() == id)
		{
			noError = true;
		}
		return noError;
	}

	public void initUI(ChessPiece pieces[][], History history, long playerTime[]) throws RemoteException
	{
		lastMove = null;
		this.pieces = pieces;
	}

	public void updateGameStatus(int gameState) throws RemoteException
	{
		this.gameState = gameState;

	}

	public void playerUpdate(ArrayList players) throws RemoteException
	{
		playersInPlay = players;
	}	

	private void addAiPlayer()
	{
		if(gameState == ChessGame.RUNNING && playersInPlay.size() < MIN_PLAYERS)
		{
			try{
			int choice = (int)(Math.random()*aiNames.length);
			Player AiPlayer = new Player(aiNames[choice], id, (playersInPlay.size() + 1),0, true);
			engine.addPlayer(AiPlayer);
			}catch(Exception e){}
		}
	}

	private void calculateMove(int playerNumber, Player AiPlayer)
	{

		ArrayList availablePieces = new ArrayList();

		for(int i = 0; i < pieces[playerNumber].length; i++)
		{
			ChessPiece aPiece = pieces[playerNumber][i];
			if(aPiece.isAlive())
			{
				availablePieces.add(pieces[playerNumber][i]);
			}
		}



		if(availablePieces.size() > 0)
		{
			ArrayList legalMoves = null;

			// velger en tilfeldig lovlig brikke
			do{
				
				int chosenPiece = (int)(Math.random()*availablePieces.size());

				ChessPiece aPiece = (ChessPiece) availablePieces.get(chosenPiece);
				BoardPoint aPoint = aPiece.getPosition();

				try{

				legalMoves = engine.selectPiece(aPoint, AiPlayer);
				}catch(Exception e){}


			} while(legalMoves == null && playersInPlay.indexOf(AiPlayer) != -1);


			int chosenPoint = -1;
			// velger en tilfeldig lovlig kordinat for å flytte til
			for (int i=0; i<legalMoves.size(); i++) {
				ChessPiece p = getPiece((BoardPoint)legalMoves.get(i), getOpponetColor(playerNumber));
				if (p != null) {
					chosenPoint = i;
				}
			}

			if (chosenPoint == -1)
				chosenPoint = (int)(Math.random()*legalMoves.size());

			BoardPoint aPoint = (BoardPoint) legalMoves.get(chosenPoint);
			try{
			int outcom = engine.movePiece(aPoint, AiPlayer);

			if(outcom == PAWN_PROMOTION)
			{
				engine.promotePawn(QUEEN, AiPlayer);
			}
			}catch(Exception e){}
		}
	}

	public ChessPiece getPiece(BoardPoint p, int playerNumber) {
		for (int i=0; i < pieces[playerNumber].length; i++)
			if (pieces[playerNumber][i].getPosition().equals(p))
				if (pieces[playerNumber][i].isAlive())
					return pieces[playerNumber][i];

		return null;
	}

	/*
	 * skal ikke gjøre noe
	 */
	public void clockTick() throws RemoteException{}
}

