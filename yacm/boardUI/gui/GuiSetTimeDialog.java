package yacm.boardUI.gui;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import yacm.engine.boardgame.chess.Constants;
import java.io.Serializable;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * Klassen lager dialogboksen for endring av maksimalt tidsforbruk.
 * @author Trond Smaavik
 * @version $Revision: 1.3 $
 */
class GuiSetTimeDialog extends JDialog implements ActionListener, Serializable, Constants{
	
	/**
	 * Behold instillinger?
	 */
	private boolean keepChanges = false;
	
	/**
	 * Oppbevaringsboks som inneholder swing-elementer.
	 */
	private Container timeDialog;
	
	/**
	 * Oppbevaringsboks som inneholder swing-elementer.
	 */
	private Container parent;
	
	/**
	 * Tekstfelt som viser Spiller 1
	 */
	private JTextField timePlayer1 = new JTextField(20);
	
	/**
	 * Tekstfelt som viser Spiller 1
	 */
	private JTextField timePlayer2 = new JTextField(20);
	
	/**
	 * Ok knappen som benyttes for å lagre instillingene.
	 */
	private JButton ok;
	
	/**
	 * Tallet man ønsker at makstiden skal være for hvit.
	 */
	private int whiteMaxTime;
	
	/**
	 * Tallet man ønsker at makstiden skal være for svart.
	 */
	private int blackMaxTime;

	/**
	 * Konstruerer og viser tidsdialogen som kan brukes til å justere maksimumstiden til
	 * sjakkpartiet.
	 * @param parent Eieren av dette vinduet.
	 */
	public GuiSetTimeDialog(JFrame parent) {
		super(parent, "Sett maksimalt tidsforbruk", true);
		this.setSize(new Dimension(300,100));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.parent = parent;
		timeDialog = getContentPane();
		timeDialog.setLayout(new GridLayout(3,3));
		timeDialog.add(new JLabel("Spiller 1:"));
		timeDialog.add(timePlayer1);
		timeDialog.add(new JLabel("minutter"));
		timeDialog.add(new JLabel("Spiller 2:"));
		timeDialog.add(timePlayer2);
		timeDialog.add(new JLabel("minutter"));
		ok = new JButton("Ok");
		ok.addActionListener(this);
		timeDialog.add(ok);
		JButton cancel = new JButton("Avbryt");
		cancel.addActionListener(this);
		timeDialog.add(cancel);
	}

	/**
	 * Sjekker at ingen av tekstboksene er tomme.
	 * @return <code>true</code>, hvis og bare hvis begge tekstboksene inneholder tekst. Elles false.
	 */
	private boolean checkInput() {
		int temp = 0;
		if(timePlayer1.getText() != null && !timePlayer1.getText().equals("")) temp++;
		if(timePlayer2.getText() != null && !timePlayer2.getText().equals("")) temp++;
		try {
			whiteMaxTime = Integer.parseInt(timePlayer1.getText().trim());
			blackMaxTime = Integer.parseInt(timePlayer2.getText().trim());
			temp++;
		} catch(Exception e) {
			JOptionPane.showMessageDialog(parent,"Minutter oppgis i form av tall.\n Prøv igjen.", "Feil.", JOptionPane.ERROR_MESSAGE);
		}

		if(temp == 3) return true;
		else return false;
	}

	/**
	 * Viser dialogboksen og venter på inndata.
	 * @return <code>true</code> hvis brukeren trykker "Ok". false for "Avbryt"
	 */
	public boolean showDialog(GuiSettings settings) {
		timePlayer1.setText(Integer.toString(settings.whiteMaxTime));
		timePlayer2.setText(Integer.toString(settings.blackMaxTime));
		setVisible(true);
		if(keepChanges) {
				settings.whiteMaxTime = whiteMaxTime;
				settings.blackMaxTime = blackMaxTime;
				return true;
		}
		return false;
	}

	/**
	 * Knappelytter,
	 * @param e Knappetrykket.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			if(checkInput()) {
				keepChanges = true;
				setVisible(false);
			}
		} else {
			keepChanges = false;
			setVisible(false);
		}
	}
}
