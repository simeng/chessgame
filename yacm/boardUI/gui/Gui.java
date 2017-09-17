package yacm.boardUI.gui;

import yacm.engine.boardgame.chess.ChessPiece;
import yacm.engine.boardgame.chess.Constants;
import yacm.engine.boardgame.BoardEngine;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.*;
import java.rmi.*;
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
public class Gui extends JFrame implements WindowListener, ActionListener, Constants, java.io.Serializable
{

	/**
	 * Objektet som inneholder alle kanppene som oppgjør sjakkbrettet.
	 */
	private GuiBoard guiBoard;

	/**
	 * Loggen som viser trekkene vi har gjort hittil.
	 */
	private GuiLog guiLog;

	/**
	 * Objektet som inneholder klokkene som viser tiden til spillerne.
	 */
	private GuiClockSet guiClockSet;

	/**
	 * Viser <code>JLabel</code> etikettene som inneholder navnet på spillerne.
	 */
	private GuiPlayers guiPlayers;

	/**
	 * Inneholder instillingene til brukeren.
	 */
	private GuiSettings settings;

	/**
	 * Knapp for å starte og pause spill.
	 */
	private JButton startButton;

	/**
	 * Elemntet som lar deg legge til nye og fjerne spillere.
	 */
	private GuiAddPlayerDialog guiAddPlayerDialog;

	/**
	 * Siste move som er sendt fra engine.
	 */
	private Move lastMove;

	/**
	 * <i>Interface</i> som brukes til å kommunisere med sjakklogikken.
	 */
	private BoardEngine engine;

	/**
	 * Dialogboks som gir spilleren info og valg ved spillets slutt.
	 */
	private GuiEndGameDialog guiEndGameDialog;

	private BoardUI ui;
	/**
	 * Holder oversikt over brettstatuset.
	 */
	private int gameStatus;


	/**
	 * Konstruerer en ny Gui.
	 * @param engine Sjakkmotoren Gui skal kobles mot.
	 * @param title Vindustittelen.
	 */
	public Gui(BoardUI ui, BoardEngine engine, String title)
	{
		super(title);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.frameInit();
		this.getContentPane().setLayout(new GridBagLayout());
		this.setSize(new Dimension(600,400));
		this.setResizable(false);

		this.addWindowListener(this);
		this.engine = engine;
		this.ui = ui;

		this.settings = new GuiSettings();
		GuiSettings loadedSettings = settings.load();

		if (loadedSettings != null)
			this.settings = loadedSettings;

		//Oppretter alle gui-objektene.
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		guiLog = new GuiLog();
		guiPlayers = new GuiPlayers(this, ui);
		guiClockSet = new GuiClockSet(guiPlayers, settings, startButton);
		guiBoard = new GuiBoard(this, engine, guiPlayers, guiLog, settings);
		guiAddPlayerDialog = new GuiAddPlayerDialog(this, ui, engine, guiPlayers, settings);
		guiEndGameDialog = new GuiEndGameDialog(this, ui, engine, guiPlayers, guiLog);
		GuiMenu guiMenu = new GuiMenu(this, ui, engine, guiAddPlayerDialog, guiPlayers, settings, guiLog);
		// Lytte etter forandringer i brettet.

		//Setter opp layouten
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;

		c.gridy = 0;
		c.gridheight = 2;
		c.anchor = c.CENTER;
		this.getContentPane().add(guiBoard,c);

		c.gridx = 1;

		c.anchor = c.CENTER;
		this.getContentPane().add(guiClockSet, c);

		c.gridx = 2;

		c.anchor = c.CENTER;
		this.getContentPane().add(guiLog, c);

		this.setJMenuBar(guiMenu);
		updateSettings();

		this.pack();
		this.show();
	}

	/**
	 * Aktiviserer grensesnittet.
	 * @param pieces[][] Sjakkbrikkene som skal fylle brettet.
	 * @param history <code>History</code> objektet som skal benyttes.
	 */
	public void initUI(ChessPiece pieces[][], History history, long clockStart[]) {
		ArrayList startPieces = new ArrayList();

		for (int i=0; i<pieces.length; i++)
		    for (int j=0; j<pieces[i].length; j++)
				if (pieces[i][j].isAlive())
					startPieces.add(pieces[i][j]);

		guiBoard.removeAllPieces();
		guiBoard.setupBoard(startPieces);

		guiLog.clearLog();
		guiClockSet.set(clockStart);

		// inisaliserer guiLog
		history.rewind();
		while(history.hasNext())
		{
			ChessMove aMove = (ChessMove) history.getNext();
			guiLog.updateLog(aMove);
		}

		guiAddPlayerDialog.showDialog();
	}

	/**
	 * Metoden kalles hver gang spillet bytter tur.
	 * p Spilleren som nå skal spille.
	 */
	public void playerChange(Player p) {
		if(p != null)
		{
			guiPlayers.setCurrentPlayer(p);
		}
	}

	/**
	 * Oppdaterer spillerinformasjonen i de forskjellige elementene og lagrer forandringene.
	 * @param players En <code> ArrayList</code> med spillerne som skal kobles til spillet.
	 */
	public void playerUpdate(ArrayList players) {
		guiPlayers.setAllPlayers(players);

		guiPlayers.updateSettings();
		guiClockSet.updateSettings();
		guiAddPlayerDialog.updateSettings();
	}

	/**
	 * Kjøres når det skjer en BoardChangeEvent
	 * @param m Flyttet som er skjedd på brettet.
	 */
	public void updateUI(Move m) {
		ChessMove cm = (ChessMove)m;
		lastMove = cm;

		// oppdaterer brettet
		guiBoard.updateMove(cm);
	}

	/**
	 * Oppdaterer brettstatuset.
	 * @param gameStatus Det nye statuset.
	 */
	public void updateGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
		switch (gameStatus) {
			case INIT:
				guiLog.clearLog();
				guiClockSet.reset();
				break;
			case READY:
				startButton.setText("Start");
				break;
			case RUNNING:
				startButton.setText("Pause");
				break;
			case STOPPED:
				startButton.setText("Nytt parti");
				if (lastMove != null)
					guiEndGameDialog.showDialog(lastMove.getResult());
				break;
		}
	}

	public void updateSettings() {
		guiAddPlayerDialog.updateSettings();
	}

	/**
	 * Fjerner et grensesnitt
	 */
	public void close() {
		try{
			engine.stopGame(ui);
			engine.removeBoardUI(ui);
		}catch(Exception e){}
		settings.save();
		System.exit(0);
	}

	/**
	 * Kalles hvert sekund eller når startknappen trykkes på.
	 * @param e Hendelsen som skjedde.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == startButton) {
			try{
			switch(gameStatus) {
				case INIT:
					break;
				case READY:
					engine.startGame((BoardUI)ui);
					break;
				case RUNNING:
					engine.stopGame((BoardUI)ui);
					break;
				case STOPPED:
					engine.stopGame((BoardUI)ui);
					engine.restartGame();
					break;
			}
			}catch(Exception re){}
		} 
	}

	public void clockTick() 
	{
		if(guiPlayers.getCurrentPlayer() != null)
		{
			guiClockSet.tick();
		}
		
	}

	/**
	 * Sjekker om brukeren ønsker å lukke vinduet
	 */
	public void windowClosing(WindowEvent w) {
		if (w.getSource() == this) {
			close();
		}
	}

	public void windowOpened(WindowEvent w) {
		guiAddPlayerDialog.showDialog();
	}

	public void windowActivated(WindowEvent w) {}
	public void windowDeactivated(WindowEvent w) {}
	public void windowClosed(WindowEvent w) {}
	public void windowIconified(WindowEvent w) {}
	public void windowDeiconified(WindowEvent w) {}
}
