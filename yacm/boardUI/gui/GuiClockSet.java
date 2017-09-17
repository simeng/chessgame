package yacm.boardUI.gui;

import yacm.engine.boardgame.Player;
import yacm.engine.boardgame.chess.Constants;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Box;
import java.awt.Dimension;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * Lager et sett av klokker og tilknytter en Timer som sørger for at klokkene går.
 * @author Trond Smaavik
 * @version $Revision: 1.7 $
 */
public class GuiClockSet extends JPanel implements Constants, java.io.Serializable {

	private GuiClock[] clocks = new GuiClock[2];
	private GuiPlayers guiPlayers;
	private GuiSettings settings;
	private GuiPlayers players;
	private boolean ticking;

	public GuiClockSet(GuiPlayers players, GuiSettings settings, JButton startButton) {
		this.guiPlayers = players;
		this.settings = settings;
		this.players = players;
		setPreferredSize(new Dimension(110,400));
		setLayout(new BorderLayout());

		JPanel clockPanel1, clockPanel2, buttonPanel, spacePanel1, spacePanel2, spacePanel3, spacePanel4, spacePanel5;

		clockPanel1 = new JPanel();
		clockPanel1.setLayout(new BorderLayout());

		clockPanel2 = new JPanel();
		clockPanel2.setLayout(new BorderLayout());

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		
		spacePanel1 = new JPanel();
		spacePanel1.setLayout(new BorderLayout());
		spacePanel1.setPreferredSize(new Dimension(1,25));

		spacePanel2 = new JPanel();
		spacePanel2.setLayout(new BorderLayout());
		spacePanel2.setPreferredSize(new Dimension(1,25));
		
		spacePanel3 = new JPanel();
		spacePanel3.setLayout(new BorderLayout());
		spacePanel3.setPreferredSize(new Dimension(10,1));
		
		spacePanel4 = new JPanel();
		spacePanel4.setLayout(new BorderLayout());
		spacePanel4.setPreferredSize(new Dimension(0,25));
		
		spacePanel5 = new JPanel();
		spacePanel5.setLayout(new BorderLayout());
		spacePanel5.setPreferredSize(new Dimension(0,25));
		
		clocks[0] = new GuiClock();
		clocks[1] = new GuiClock();

		clockPanel1.add(spacePanel4, BorderLayout.NORTH);
		clockPanel1.add(guiPlayers.getPlayerLabel(1), BorderLayout.CENTER);
		clockPanel1.add(clocks[BLACK], BorderLayout.SOUTH);

		clockPanel2.add(guiPlayers.getPlayerLabel(0), BorderLayout.NORTH);
		clockPanel2.add(clocks[WHITE], BorderLayout.CENTER);
		clockPanel2.add(spacePanel5, BorderLayout.SOUTH);

		buttonPanel.add(spacePanel1, BorderLayout.NORTH);
		buttonPanel.add(startButton, BorderLayout.CENTER);
		buttonPanel.add(spacePanel2, BorderLayout.SOUTH);
		buttonPanel.add(spacePanel3, BorderLayout.EAST);

		add(clockPanel1, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
		add(clockPanel2, BorderLayout.SOUTH);
	}

	/*
	 * Henter ut brukt tid i sekunder fra klokke tilhørende playerNr.
	 * @param playerNr Spillernummer.
	 * @return Forbrukt tid i sekunder for gitt spiller.
	 */
	public long getUsedTime(int playerNr) {
		return clocks[guiPlayers.getCurrentPlayerNumber()].getSecs();
	}

	/**
	 * Setter maksimaltid for klokkene
	 */
	public void updateSettings() {
		for(int i = 0; i < MAX_PLAYERS; i++) {
			Player aPlayer = guiPlayers.getPlayer(i);
			if(aPlayer != null) {
				clocks[i].setMaxTime(aPlayer.getMaxTime() / 60);
			} else {
				clocks[i].reset();
			}
		}
	}

	/**
	 * Finner ut om gitt spiller har mer spilletid.
	 * @param playerNr Spillernummer
	 * @return true om spilleren har mer tid. false ellers.
	 */
	public boolean hasTimeLeft(int playerNr) {
		return clocks[playerNr].hasTimeLeft();
	}

	/**
	 * Resetter begge av klokkene
	 */
	public void reset() {
		clocks[WHITE].reset();
		clocks[BLACK].reset();
	}

	/**
	 * Gjør klokkene klare for nytt parti.
	 * Tar vare på maksimaltid.
	 */
	public void newGame() {
		clocks[WHITE].newGame();
		clocks[BLACK].newGame();
	}

	/**
	 * Resetter en av klokkene
	 * @param clock Nummer på klokke som skal resettes.
	 */
	public void reset(int clock) {
		clocks[clock].reset();
	}

	/**
	 * Setter klokkenes startposisjon
	 * @param starttid Klokkenes starttid.
	 */
	public void set(long startTime[]) {
		clocks[WHITE].set(startTime[WHITE]);
		clocks[BLACK].set(startTime[BLACK]);
	}

	/**
	 * Sørger for at klokke til den aktive spilleren går.
	 */
	public void tick() {
		clocks[players.getCurrentPlayerNumber()].tick();
	}
}
