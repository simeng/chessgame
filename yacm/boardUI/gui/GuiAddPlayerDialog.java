package yacm.boardUI.gui;

import yacm.engine.boardgame.Player;
import yacm.engine.boardgame.BoardEngine;
import yacm.engine.boardgame.chess.Constants;
import yacm.engine.boardgame.BoardUI;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Graphics;

/*
 * Sist endret av: $Author: epoxy $
 */

/**
 * Klassen lager dialogboksen for endring av navn på sjakkspillerne,
 * @author Trond Smaavik
 * @version $Revision: 1.7 $
 */
class GuiAddPlayerDialog extends JDialog implements ActionListener, Constants, java.io.Serializable {
	private boolean keepChanges = false;
	private Container playerDialog;
	private JLabel[] namePlayer;
	private JLabel[] timePlayer;
	private JButton[] removePlayer;
	private JButton addPlayer, start, cancel;
	private JTextField playerToAdd;
	private JTextField timeToAdd;
	private GuiSettings settings;
	private Gui parent;
	private BoardEngine engine;
	private GuiPlayers guiPlayers;
	private Player[] players = new Player[MAX_PLAYERS];
	private BoardUI ui;
	private long maxTime = 0;

	public GuiAddPlayerDialog(Gui parent, BoardUI ui, BoardEngine engine, GuiPlayers guiPlayers, GuiSettings settings) {
		super(parent, "Spillere", false);

		this.parent = parent;
		this.engine = engine;
		this.guiPlayers = guiPlayers;
		this.settings = settings;
		this.setSize(new Dimension(300,120));
		this.playerDialog = getContentPane();
		this.ui = ui;
		playerDialog.setLayout(new GridLayout(MAX_PLAYERS + 3,4));

		namePlayer = new JLabel[MAX_PLAYERS];
		timePlayer = new JLabel[MAX_PLAYERS];
		removePlayer = new JButton[MAX_PLAYERS];

		playerDialog.add(new JLabel());
		playerDialog.add(new JLabel("Navn"));
		playerDialog.add(new JLabel("Maks. tid (min)"));
		playerDialog.add(new JLabel());

		for (int i = 0; i < 2; i++) {
			namePlayer[i] = new JLabel();
			timePlayer[i] = new JLabel();
			removePlayer[i] = new JButton("Fjern");
			removePlayer[i].addActionListener(this);
			playerDialog.add(new JLabel("Spiller " + (i+1) + ":"));
			playerDialog.add(namePlayer[i]);
			playerDialog.add(timePlayer[i]);
			playerDialog.add(removePlayer[i]);
		}

		playerDialog.add(new JLabel("Ny spiller:"));
		playerToAdd = new JTextField();
		playerDialog.add(playerToAdd);

		timeToAdd = new JTextField("60");
		playerDialog.add(timeToAdd);

		addPlayer = new JButton("Legg til");
		addPlayer.addActionListener(this);
		playerDialog.add(addPlayer);

		playerDialog.add(new JLabel());
		playerDialog.add(new JLabel());
		playerDialog.add(new JLabel());

		cancel = new JButton("Lukk");
		cancel.addActionListener(this);
		playerDialog.add(cancel);

		pack();
	}

	/**
	 * Sjekker at ingen av tekstboksene er tomme.
	 * @return true hvis og bare hvis begge tekstboksene inneholder tekst. Elles false.
	 */
	private boolean checkInput() {
		int temp = 0;
		if(playerToAdd.getText() != null && !playerToAdd.getText().trim().equals("")) temp++;
		try {
			maxTime = Long.parseLong(timeToAdd.getText().trim()) * 60;
			temp++;
		} catch(Exception e) {
			JOptionPane.showMessageDialog(parent,"Minutter oppgis i form av tall.\n Prøv igjen.", "Feil.", JOptionPane.ERROR_MESSAGE);
			timeToAdd.setText("");
		}
		return (temp == 2) ? true : false;
	}

	/**
	 * Oppdaterer etikettene med navn og maksimaltid.
	 * Om spilleren er lokal blir det mulig å fjerne den.
	 */
	public void updateSettings() {
		for(int i = 0; i < MAX_PLAYERS; i++) {
			Player aPlayer = guiPlayers.getPlayer(i);
			namePlayer[i].setText(((aPlayer == null) ? "" : aPlayer.getName()));
			timePlayer[i].setText(((aPlayer == null) ? "" : Long.toString(aPlayer.getMaxTime() / 60)));
			if(guiPlayers.isLocalPlayer(aPlayer)) {
				removePlayer[i].setVisible(true);
			} else {
				removePlayer[i].setVisible(false);
			}
		}
	}

	/**
	 * Viser dialogboksen og venter på inndata.
	 * @return true hvis brukeren trykker "Ok". false for "Avbryt"
	 */
	public boolean showDialog() {
		updateSettings();
		setVisible(true);
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		try{
		if(e.getSource() == removePlayer[0]) {
			engine.removePlayer(guiPlayers.getPlayer(0));
		}
		if(e.getSource() == removePlayer[1]) {
			engine.removePlayer(guiPlayers.getPlayer(1));
		}
		if(e.getSource() == addPlayer) {
			if(checkInput()) {
				int playerID = (int)(Math.random()*1000000);
				Player player = new Player(playerToAdd.getText(), ui.getID(), playerID, maxTime, false);
				if (engine.addPlayer(player)) {
					playerToAdd.setText("");
					timeToAdd.setText("60");
				} else {
					JOptionPane.showMessageDialog(parent, "Spillet er fullt.", "Feil",JOptionPane.ERROR_MESSAGE);
					timeToAdd.setText("60");
					playerToAdd.setText("");
				}
			}
		}
		}catch(Exception re){}
		if(e.getSource() == cancel) {
			setVisible(false);
		}

	}
}

