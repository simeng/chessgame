package yacm.boardUI.gui;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.text.*;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import yacm.engine.boardgame.chess.Constants;
import java.awt.Color;
import java.io.Serializable;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * Viser en meget hyggelig sjakkhjelpvindu. Virkelig informativ.
 * @author Simen Graaten
 * @version $Revision: 1.3 $
 */
class GuiHelpDialog extends JDialog implements ActionListener, Constants, Serializable{
	private JFrame parent;
	private JButton ok;
	private ClassLoader cl;

	/**
	 * KOnstruerer det nye vinduet,
	 * @param parent Eieren  av vinduet.
	 */
	public GuiHelpDialog(JFrame parent) {
		super(parent, "Hjelp", true);
		this.parent = parent;
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		this.setSize(new Dimension(330,400));

		JTextPane textPanel = createTextPanel();

		textPanel.setEditable(false);

		ok = new JButton("Ok");
		ok.addActionListener(this);
		
		c.add(new JScrollPane(textPanel));
		c.add(ok);
	}

	/**
	 * Oppretter tekstpanelet som inneholder hjelpeteksten. Leser inn ei tekstfil, så
	 * pynter den på linjer som inneholder spesielle tegn. Råflotte greier!
	 */
	public JTextPane createTextPanel() {
		JTextPane t = new JTextPane();
		Font font = new Font("Sans", Font.PLAIN, 12);
		t.setFont(font);

		// legge til stiler
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		Style regular = t.addStyle("regular", def);
		Style s = t.addStyle("bold", regular);
		Style i = t.addStyle("italic", regular);
		Style u = t.addStyle("underline", regular);
				
		StyleConstants.setBold(s, true);
		StyleConstants.setItalic(i, true);
		StyleConstants.setUnderline(u, true);
		StyleConstants.setForeground(u, Color.BLUE);
		// hente dokumentet fra textpane'n
		Document d = t.getDocument();

		try {
			BufferedReader b = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/yacm/boardUI/gui/help/help.txt")));
			String line = "";
			while ((line = b.readLine()) != null) {
				String format = "regular";
	
				if (line.length() < 1) {
				}
				// "test£ <- '£' tilslutt - setter teksten bold"
				else if (line.charAt(line.length()-1) == '£')
				{
					format = "bold";
					line = line.substring(0, line.length()-1);
				}
				// "test$ <- '$' tilslutt - setter teksten kursiv"
				else if (line.charAt(line.length()-1) == '$')
				{
					format = "italic";
					line = line.substring(0, line.length()-1);
				
				}
				// "test§ <- '§' tilslutt - setter teksten underline"
				else if (line.charAt(line.length()-1) == '§')
				{
					format = "underline";
					line = line.substring(0, line.length()-1);
				}
				// "<yacm/boardUI/gui/help/test.gif> <- putter inn bildet"
				else if (line.charAt(0) == '<' && line.charAt(line.length()-1) == '>') {
					String filename = line.substring(1,line.length()-1);
					format = "bilde";
					s = t.addStyle("bilde", regular);
					StyleConstants.setIcon(s, new ImageIcon(filename));
				}
	
				try {
					d.insertString(d.getLength(), line + "\n", t.getStyle(format));
				} catch(Exception e) {
					System.out.println("Feil i hjelptekst: " + e);
				}

			}
		} catch(Exception e) {
			System.out.println("Fant ikke hjelpteksten: " + e);
		}

		return t;
	}

	/**
	 * Knappelytter.
	 * @param e Knappetrykket.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			this.hide();
		}
	}
}

