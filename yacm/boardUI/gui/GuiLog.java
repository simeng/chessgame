package yacm.boardUI.gui;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.JScrollPane;
import yacm.engine.boardgame.chess.ChessPiece;
import yacm.engine.boardgame.chess.ChessBoard;
import yacm.engine.boardgame.chess.Constants;
import javax.swing.JScrollBar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Font;
import yacm.engine.boardgame.BoardPoint;
import yacm.engine.boardgame.chess.ChessMove;
import yacm.engine.boardgame.Move;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;


/*
 * Siste endret av: $Author: simeng $
 */

/**
 * Denne klassen viser trekkene i spillet i et råflott strengformat.
 * @author Simen Graaten
 * @author Kristian Berg
 * @author Morten Løkke Andersen
 * @version $Revision: 1.6 $
 *
 *
 */
public class GuiLog extends Box implements Constants, Serializable

{
	/**
	 * Listen som inneholder alle postene i loggen.
	 */
	private ArrayList liste = new ArrayList();
	/**
	 * Tekstfeltet som vises i GUI.
	 */
	private JTextArea textarea;
	/**
	 * Layout'en som vi putter <code>JTextArea</code> i.
	 */
	private JScrollPane log;

	/**
	 * Tabell med sjakkfeltene.
	 */
	private String HorizLetters[] = { "a","b","c","d","e","f","g","h" };
	/**
	 * Tabell med sjakkfeltene.
	 */
	private String VertNumbers[] = { "1","2","3","4","5","6","7","8" };
	/**
	 * Tabell for de forskjellige tegnene for brettstatuset.
	 */
	private String states[] = { "", "+", "=", "=", "++"};
	/**
	 * Strengen som vi pakker ny rundeinformasjon i.
	 */
	private String entry;
	/**
	 * Kalender for å holde rede på når en runde starter.
	 */
	private GregorianCalendar cal = new GregorianCalendar();
	
	/**
	 * Konstruerer en GuiLog.
	 */
	public GuiLog()
	{
		super(BoxLayout.Y_AXIS);
		this.setPreferredSize(new Dimension(180, 350));
		entry = "" + cal.getTime();
		entry = entry.substring(0, 16);
		textarea = new JTextArea(entry + "\n" + " - yarc -\n", 20, 10);
		Font font = new Font("SansSerif", Font.BOLD, 12);
		textarea.setFont(font);
		textarea.setEditable(false);
		log = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(Box.createVerticalStrut(10));
		add(log);
		//this.history = history;
	}
	/**
	 * Fjerner alle postene i teksområdet.
	 */
	public void clearLog()
	{
		textarea.setText(null);
		entry = "" + cal.getTime();
		entry = entry.substring(0, 16) + "\n";
		textarea.append(entry + " - yarc -\n");
		liste = new ArrayList ();
	}

	/**
	 * Henter ut en post fra loggen.
	 * @param linje Linjenummeret vi ønsker å hente.
	 * @return Streng med loggposten.
	 */
	protected String getEntry(int linje)
	{
		return (String)liste.get(linje);
	}
	/**
	 * Finner lengden på loggen.
	 * @return Logglengden.
	 */
	protected int getLogLength()
	{
		return liste.size();
	}
	/**
	 * Lagrer loggen på harddisken. Bruker <code>JFileChooser</code> slik at brukeren
	 * får selv velge hvor han vil lagre filen.
	 */
	public void save() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("YAL - Yet Another Log...");
		chooser.setCurrentDirectory(null);
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				return true;
			}
			public String getDescription() {
				return "YAL - Yet Another Log";
			}
			});
		int ret = chooser.showSaveDialog(this);
		switch(ret) {
			case JFileChooser.APPROVE_OPTION:
				try
				{
					FileWriter fw = new FileWriter(chooser.getSelectedFile());
					int y = this.getLogLength();
					for(int i=0; i<y; i++)
					{
						fw.write(this.getEntry(i));
					}
					fw.close();
				} catch(IOException e){
					System.out.println("Klarte ikke skrive logg: " + e);
				}
				break;
			default:
		}
	}

	/**
	 * Legger til et nytt trekk i loggen.
	 * @param aMove Trekket som skal registreres.
	 */
	public void updateLog(Move aMove)
	{
		ChessMove runde = (ChessMove) aMove;
		if(runde != null)
		{
			int trekk = runde.getMoveNumber();
			ChessPiece brikken = runde.getPiece();
			ChessPiece stakkar = runde.getCasualty();
			BoardPoint start = runde.getOrig();
			BoardPoint slutt = runde.getDest();
			int result = runde.getResult();
			String status = null;
			switch(result)
			{
				case NORMAL:
					status = "";
					break;
				case CHECK:
					status = "+";
					break;
				case REMI:
					status = "=";
					break;
				case PATT:
					status = "=";
					break;
				case CHECKMATE:
					status = "++";
					break;
			}

			switch(runde.getOutcome())
			{
				case 0:
					if(brikken.getType() == PAWN)
					{
						if(stakkar == null)
						{
							entry = HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + status + "\n";
						}else{
							entry = HorizLetters[start.getX()] + "x" + HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + status + "\n";
						}
					}else{

						if(stakkar == null)
						{
							entry = "" + brikken + HorizLetters[start.getX()] + VertNumbers[start.getY()] + HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + status + "\n";
						}else{
							if(runde.isAmbiguous())
							{
								entry = "" + brikken + HorizLetters[start.getX()] + VertNumbers[start.getY()] + "x" + HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + status + "\n";
							}else{
								entry = "" + brikken + "x" + HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + status + "\n";
							}
						}
					}
					break;

				case PAWN_PROMOTION:
					if(stakkar == null)
					{
						entry = HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + runde.getPromotion() + status + "\n";
					}else{
						entry = "x" + HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + runde.getPromotion() + status + "\n";
					}
					break;

				case ROKADE_KING:
					entry = "0-0" + status + "\n";
					break;

				case ROKADE_QUEEN:
					entry = "0-0-0" + status + "\n";
					break;

				case ENPASSANT:
					entry = HorizLetters[slutt.getX()] + VertNumbers[slutt.getY()] + "e.p." + status + "\n";
					break;
			}
			liste.add(entry);
			String logtekst = "" + trekk + ": " + entry;
			textarea.append(logtekst);
			textarea.setCaretPosition( textarea.getText().length() );
		}
	}

}


