package yacm.boardUI.gui;

import yacm.engine.boardgame.*;
import yacm.engine.boardgame.chess.*;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.ClassLoader;
import java.io.InputStreamReader;
import java.io.Serializable;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * En representasjon av en rute i sjakkbrettet. Kan inneholde en brikke eller være tom.
 * @author Simen Graaten
 * @author Trond Smaavik
 * @version $Revision: 1.5 $
 */
class GuiSquare extends JButton implements Constants, Serializable {

	/**
	 * Koordinaten til ruta.
	 */
	private int x, y;
	
	/**
	 * Sjakkbrikken som okkuperer ruta.
	 */
	private ChessPiece piece;
	
	/**
	 * Velger ruta.
	 */
	private boolean selected;
	
	/**
	 * Lyser opp ruta.
	 */
	private boolean hilighted;

	/**
	 * Fargekonstanter.
	 */
	public final Color COLOR_BOARD_SELECTED = new Color(230,230,100);
	public final Color COLOR_BOARD_HILIGHTED = new Color(120,150,210);
	public final Color COLOR_BOARD_DARK = new Color(120,120,120);
	public final Color COLOR_BOARD_LIGHT = new Color(220,220,220);

	/**
	 * Konstruerer ei rute.
	 * @param p Brikken som skal okkupere ruta.
	 * @param x X-koordinaten til ruta.
	 * @param y Y-koordinaten til ruta.
	 */
	public GuiSquare(ChessPiece p, int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.setChessPiece(p);
	}

	/**
	 * Henter rutens X-koordinat.
	 * @return x Sjakkrutens X-koordinat.
	 */
	public int getXPos() {
		return x;
	}

	/**
	 * Henter rutens Y-koordinat.
	 * @return y Sjakkrutens X-koorinat.
	 */
	public int getYPos() {
		return y;
	}

	/**
	 * Henter rutens punkt.
	 * @return Rutens posisjon som <code>BoardPoint</code>.
	 */
	public BoardPoint getPoint() {
		return new BoardPoint(x,y);
	}

	/**
	 * Henter brikken som okkuperer ruten.
	 * @return Brikkern som står på ruten. Er ruten tom, så returneres <code>null</code>.
	 */
	public ChessPiece getChessPiece() {
		return piece;
	}

	/**
	 * Fargen til spiller som befinner seg på ruten
	 * @return WHITE = 0, BLACK = 1
	 */
	public int getPlayer() {
		return piece.getColor();
	}

	/**
	 * Lyser opp ruten for å f.eks indikere et gyldig trekk.
	 * @return Bekreftelse på operasjonen.
	 */
	public boolean isHilighted() {
		return hilighted;
	}

	/**
	 * Sjekker om brukeren har valgt feltet.
	 * @return Bekreftelse på operasjonen.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Angir om ruten er valgt.
	 * @param state Velg ruten.
	 */
	public void setSelected(boolean state) {
		selected = state;
		this.repaint();
	}

	/**
	 * Putter en brikke i sjakkbrettruten (setter ikonet til knappen).
	 * @param piece Sjakkbrikken som skal okkupere ruten.
	 */
	public void setChessPiece(ChessPiece piece) {
		this.piece = piece;
		if(piece == null) {
			this.setIcon(null);
		}
		else {
			ImageIcon icon;
			String colorString[] = new String[2];
			colorString[WHITE] = "w";
			colorString[BLACK] = "b";

			icon = new ImageIcon(getClass().getResource("/yacm/boardUI/gui/pieces/" + colorString[piece.getColor()] + "-" + piece.getChessName() + ".gif"));
			this.setIcon(icon);
		}
		this.repaint();
	}

	/**
	 * Lyser opp ruta.
	 * @param h Let there be light.
	 */
	public void setHilight(boolean h) {
		hilighted = h;
		this.repaint();
	}

	/**
	 * Setter koordinatentil sjakkruten.
	 * @param p Den nye koordinaten til ruta.
	 */
	public void setPoint(BoardPoint p) {
		this.x = (int)p.getX();
		this.y = (int)p.getY();
	}

	/**
	 * Tegner opp ruten og setter bakgrunnfarge etter koordinaten
	 * og valg-statusen til ruta.
	 * @param g Bildet som skal settes på ruta.
	 */
	public void paint(Graphics g) {
		super.paint(g);
		if(selected) {
			this.setBackground(COLOR_BOARD_SELECTED);
		}
		else if (hilighted)
		{
			this.setBackground(COLOR_BOARD_HILIGHTED);
		} else {
			if((x+y)%2 == 0) {
				this.setBackground(COLOR_BOARD_DARK);
			} else {
				this.setBackground(COLOR_BOARD_LIGHT);
			}
		}
	}

	/**
	 * Tekstiserer ruta.
	 * @return Rutetekstinfosakgreie.
	 */
	public String toString() {
		return "Brikkenavn: " + piece.getChessName() + ", Spiller: " + piece.getColor() + ", X: " + x + ", Y: " + y;
	}
}
