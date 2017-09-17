package yacm.boardUI.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Container;
import yacm.engine.boardgame.BoardUI;
import yacm.engine.boardgame.BoardEngine;
import yacm.engine.boardgame.chess.Constants;
import java.io.Serializable;

/**
 * En ikke-modal dialogboks som vises når spillet er slutt.
 * Brukeren får muligheten til å spille et nytt parti, lagre logg eller gjøre ingenting.
 * Infomasjon om hvorfor spillet er slutt vises også.
 *
 * @version $Revision: 1.4 $
 * @author Trond Smaavik
 */
class GuiEndGameDialog  extends JDialog implements ActionListener, Serializable, Constants{

	/**
	 * Knapp for å velge nytt parti.
	 */
	private JButton newRound;

	/**
	 * Knapp for å lukke dialogboks.
	 */
	private JButton close;

	/**
	 * Knapp for å lagre logg.
	*/
	private JButton saveLog;

	/**
	 * Etikett for å vis grunn til spillets slutt.
	 */
	private JLabel message;

	/**
	 * Spillmotoren
	 */
	private BoardEngine engine;

	/**
	 * Navn på spillere og slikt
	 */
	private GuiPlayers guiPlayers;

	/**
	 * For å kunne lagre loggen må man ha tilgang til den.
	 */
	private GuiLog guiLog;

	private BoardUI ui;

	/**
	 * Oppretter en dialogboks med informasjon om hvorfor spillet er slutt og valg til brukeren.
	 * @param parent Foreldrevindu til dialogen.
	 * @param type Type avsluttning av spillet.
	 * @param guiPlayers For å ha tilgang til spillernavn.
	 * @param guiLog For å kunne lagre loggen.
	 */
	public GuiEndGameDialog(JFrame parent, BoardUI ui, BoardEngine engine, GuiPlayers guiPlayers, GuiLog guiLog) {
		super((JFrame)parent, "Spillet er slutt", false);
		this.engine = engine;
		this.guiPlayers = guiPlayers;
		this.guiLog = guiLog;
		Container endDialog = this.getContentPane();
		endDialog.setLayout(new FlowLayout());
		setSize(300,100);

		message = new JLabel();
		message.setSize(90,10);
		endDialog.add(message);

		this.ui = ui;

		newRound = new JButton("Nytt parti");
		newRound.addActionListener(this);
		close = new JButton("Lukk");
		close.addActionListener(this);
		saveLog = new JButton("Lagre logg");
		saveLog.addActionListener(this);
		endDialog.add(newRound);
		endDialog.add(close);
		endDialog.add(saveLog);
	}

	/**
	 * Viser dialogboksen.
	 * @param type Type avsluttning av spillet
	 */
	 public void showDialog(int type) {
		String spacing = "              ";
		switch(type) {
			case CHECKMATE:
				message.setText(spacing + "Sjakkmatt - " + guiPlayers.getCurrentPlayer().getName() + " vinner." + spacing);
				break;
			case PATT:
				message.setText(spacing + "Patt - Spillet ender uavgjort." + spacing);
				break;
			case REMI:
				message.setText(spacing + "Remi - Spillet ender uavgjort." + spacing);
				break;
			default:
				message.setText(spacing + "Klaffen har falt for " + guiPlayers.getCurrentPlayer().getName() + spacing);
		}
		setVisible(true);
	 }


	/**
	 * Kjøres når det trykkes på en knapp.
	 * @param e Hendelsen som skjedde da knappen ble trykket på.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == newRound) {
		try{
			engine.stopGame((BoardUI)ui);
			engine.restartGame();
		}catch(Exception re){}
		}
		if(e.getSource() == close) {
		}
		if(e.getSource() == saveLog) {
			guiLog.save();
		}
		setVisible(false);
	}
}
