package yacm.boardUI.gui;

import yacm.engine.boardgame.Player;
import yacm.engine.boardgame.chess.Constants;
import yacm.engine.boardgame.BoardUI;
import java.util.ArrayList;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.io.Serializable;

/**
 * Klassen tar vare på spillere på guisiden. Det skilles mellom lokale og
 * andre spillre. Lokale spillere er de som er meldt inn fra brettet instansen
 * av GuiPlayers hører til. Andre spillere hører da naturlig nok til et annet brett.
 *
 * @version $Revision: 1.6 $
 * @author Trond Smaavik
 * @author Simen Graaten
 */
 class GuiPlayers implements Constants, Serializable{
	/**
	 * Farge på aktiv spiller.
	 * Brukes ved visning av spilernavn på brettet.
	 */
	private final Color DARK_GREEN = new Color(0, 128, 0);

	/**
	 * Etikettene med spillernavn som vises på brettet.
	 */
	private JLabel[] playerLabels = new JLabel[2];

	/**
	 * Nummer på startende spiller.
	 */
	private int startingPlayer = 0;

	/**
	 * Nummer på spillende spiller.
	 */
	private int playerNr;

	/**
	 * Spilleren som er i spiller som er i trekk.
	 */
	private Player currentPlayer;

	/**
	 * Alle spillere lokale og andre.
	 */
	private ArrayList allPlayers = new ArrayList();

	private Gui gui;
	private BoardUI ui;

	/**
	 * Opprettet etikettene som vises på brettet
	 */
	public GuiPlayers(Gui gui, BoardUI ui) {
		Font font = new Font("SansSerif", Font.BOLD, 12);
		Dimension dim = new Dimension(100,25);
		this.gui = gui;
		this.ui = ui;
		for(int i = 0; i < 2; i++) {
			playerLabels[i] = new JLabel("<ingen spiller>");
			playerLabels[i].setFont(font);
			playerLabels[i].setMaximumSize(dim);
			playerLabels[i].setMinimumSize(dim);
			playerLabels[i].setPreferredSize(dim);
		}
		playerNr = startingPlayer;
		//playerLabels[0].setForeground(DARK_GREEN);
		//playerLabels[1].setForeground(Color.RED);
	}

	/**
	 * Endrer navn på spillerne.
	 * Endrer navnet som vises i etikettene på brettet.
	 */
	public void updateSettings() {
		for (int i = 0; i < MAX_PLAYERS; i++) {
			String name = "<ingen spiller>";
			Player aPlayer = getPlayer(i);
			if(!allPlayers.isEmpty() && aPlayer != null) {
				name = aPlayer.getName();
			}
			playerLabels[i].setText(name);

		}
	}

	/**
	 * Finner ut om en spiller er lokal eller ikke.
	 * @param playernum Nummeret til spilleren som skal kontrolleres.
	 * @return <code>true</code> om spillernummeret tilhører dette UIet.
	 * @see yacm.boardUI.gui.GuiPlayers#isLocalPlayer(Player p)
	 */
	public boolean isLocalPlayer(int playernum) {
		boolean value = false;
		if (playernum < allPlayers.size())
			try{
			value = (((Player)allPlayers.get(playernum)).getID() == ui.getID());
			}catch(Exception e){}
		return value;
	}

	/**
	 * Finner ut om brettet har en spiller knyttet til seg.
	 * Med andre ord en lokal spiller.
	 * @param p	Spilleren som søkes.
	 * @return <code>true</code> om spilleren tilhører dette UI'et.
	 */
	public boolean isLocalPlayer(Player p) {
		boolean value = false;
		if (p != null)
		{
			try{
			value = (p.getID() == ui.getID());
			}catch(Exception e){}
		}
		return value;

	}


	public int getPlayerNumber(Player p) {
		int num = allPlayers.indexOf(p);
		//if (num == -1) System.out.println("FEIL: Spilleren som ble slått opp er ikke i spillerlisten");
		return num;
	}


	/**
	 * Setter en liste over alle spillere.
	 * @param players	Liste med spillere som skal settes.
	 */
	public void setAllPlayers(ArrayList players) {
		allPlayers = players;
	}

	/**
	 * Henter ut spiller fra "alle spillere".
	 * @param p	Nummer på spiller som skal hentes.
	 * @return Spiller med gitt nummer.
	 */
	public Player getPlayer(int playernum) {
		Player player = null;
		if( playernum < allPlayers.size() ) {
			player = (Player)this.allPlayers.get(playernum);
		}
		return player;
	}

	/**
	 * Setter spiller i trekk.
	 * @param p Spiller i trekk.
	 */
	public void setCurrentPlayer(Player p) {
		currentPlayer = p;
	}

	/**
	 * Henter du spiller i trekk.
	 * @return Spilleren i trekk.
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Henter ut en eventuell lokal spiller som er i trekk.
	 * @return Den lokale spilleren i trekk, eller null
	 */
	public Player getCurrentLocalPlayer() {
		Player ret = null;
		if (isLocalPlayer(currentPlayer)) {
			ret = currentPlayer;
		}
		return ret;
	}

	public int getCurrentLocalPlayerID() {
		int ret = 0;
		if (isLocalPlayer(currentPlayer)) {
			ret = currentPlayer.getID();
		}
		return ret;
	}

	/**
	 * Henter ut nummeret til spiller i trekk.
	 * @return Spillernummeret til spiller i trekk.
	 */
	public int getCurrentPlayerNumber() {
		return allPlayers.indexOf(currentPlayer);
	}

	/**
	 * Henter ut motsatt spiller.
	 * @param Spilleren du trenger motstanderen til.
	 * @return Motstanderen til spilleren man oppgir.
	 */
	public Player getOpponent(Player p) {
		if (p == null)
			System.out.println("FEIL: GuiPlayers.getOpponent ble kjørt med null som parameter");
		int current = allPlayers.indexOf(p);
		return (current == 0) ? (Player)allPlayers.get(1) : (Player)allPlayers.get(0);
	}


	/**
	 * Finner ut om man trenger flere spillere til spillet.
	 * @return <code>true</code> om man trenger flere spillere, <code>false</code> ellers.
	 */
	public boolean needsMorePlayers() {
		return (allPlayers.size() < MIN_PLAYERS) ? true : false;
	}

	/**
	 * Henter ut navn på gitt spiller.
	 * @param playerNr Nummer på spiller.
	 * @return Navnet på spilleren med nummer playerNr.
	 */
	public String getName(int playerNr) {
		if(playerNr >= 0 && playerNr < playerLabels.length) {
			return playerLabels[playerNr].getText();
		} else {
			return "Ugyldig spillernummer.";
		}
	}

	/**
	 * Setter startende spiller ved å bytte om slik av startende spiller alltid er
	 * spilleren til venste i navnepanelet.
	 * @param playerNr Nummer på startende spiller.
	 */
	public void setStartingPlayer(int playerNr) {
		playerLabels[0].setForeground(Color.RED);
		playerLabels[1].setForeground(DARK_GREEN);
		startingPlayer = playerNr;
	}

	/**
	 * Indikerer hvem sin tur det er ved å bytte farge på navnet til spillerne.
	 * @param playerNr Nummeret på spilleren som nå får sin tur.
	 */
	public void newTurn(int playerNr) {
		this.playerNr = playerNr;
		switch (playerNr) {
			case 0:
				playerLabels[0].setForeground(Color.RED);
				playerLabels[1].setForeground(DARK_GREEN);
				break;
			case 1:
				playerLabels[0].setForeground(DARK_GREEN);
				playerLabels[1].setForeground(Color.RED);
			break;
			default:
				playerLabels[0].setForeground(Color.GRAY);
				playerLabels[1].setForeground(Color.GRAY);
		}

	}

	/**
	 * Henter ut navneetiketten for gitt spiller.
	 * @param player	Nummer på spilleren man ønsker etiketten til.
	 * @return Etiketten for visning av spillernavn.
	 */
	public JLabel getPlayerLabel(int player)
	{
		return playerLabels[player];
	}
}
