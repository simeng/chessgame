package yacm.boardUI.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/*
 * Sist endret av: $Author: simeng $
 */

/**
* Klassen oppretter en sjakk-klokke som holder oversikt over forbrukt tid hos en spiller.
*
* @author Trond Smaavik
* @version $Revision: 1.5 $
*/
class GuiClock extends JComponent implements java.io.Serializable {
	private final int SIZE = 100;
	private final int DIA = (int)(SIZE / 2);
	private final int SEC_PER_MIN = 60;
	private final int SEC_PER_HOUR = 3600;
	private final double SEC_MOVE = 2 * Math.PI / SEC_PER_MIN;
	private final double MIN_MOVE = 2 * Math.PI / SEC_PER_HOUR;
	private final String filename = "/yacm/boardUI/gui/clockBg.gif";
	private Image bgImage = null;
	private long secs = 0;
	private int maxTime = 60 * SEC_PER_MIN;

	public GuiClock() {
		super();
		try {
			bgImage = ImageIO.read(getClass().getResource(filename));
		} catch(Exception e) {
			System.out.println("Fant ikke klokkebilde");
		}
		setPreferredSize(new Dimension(100,100));
	}

	/**
	 * Tegner opp en sjakklokke.
	 * Om det finnes legges et bilde av en urskive inn, ellers tegnes kun en sirkel. Klokken har minutt- og sekund-viser
	 * samt en viser som angir maksimalt tidsforbruk. Denne viseren kan flyttes til ønsket posisjon.
	 *
	 * @param clock Objektet det skal tegnes på.
	 */
	public void paintComponent(Graphics clock) {
		super.paintComponent(clock);
		if (bgImage != null) {
			clock.drawImage(bgImage,0,0,getBackground(),this);
		} else {
			clock.drawOval(0,0,SIZE,SIZE);
		}

		// Sekundviser
		clock.setColor(Color.GREEN);
		clock.drawLine(DIA,DIA,(int)(DIA+(0.8*DIA*Math.sin(secs*SEC_MOVE))),(int)(DIA+(-1*DIA*Math.cos(secs*SEC_MOVE))));
		//Minuttviser
		clock.setColor(Color.BLACK);
		clock.drawLine(DIA,DIA,(int)(DIA+(0.7*DIA*Math.sin(secs*MIN_MOVE))),(int)(DIA+(-1*0.9*DIA*Math.cos(secs*MIN_MOVE))));
		//Viser for maks. tidsforbruk.
		clock.setColor(Color.RED);
		clock.drawLine(DIA,DIA,(int)(DIA+(0.7*DIA*Math.sin(maxTime*MIN_MOVE))),(int)(DIA+(-1*0.9*DIA*Math.cos(maxTime*MIN_MOVE))));
	}

	/**
	 * Returner antall sekunder klokka har gått
	 * @return Antall sekunder klokka har gått.
	 */
	public long getSecs() {
		return secs;
	}

	/**
	 * Setter maksimalt tidsforbruk.
	 * @param min Antall minutter en spiller har til rådighet
	 */
	public void setMaxTime(long min) {
		maxTime = (int)min * SEC_PER_MIN;
		update(getGraphics());
	}

	/**
	 * Returner maksimalt tidsforbruk i minutter
	 * @return Maksimalt tidsforbruk.
	 */
	public int getMaxTime() {
		return (int)(maxTime / 60);
	}

	/**
	 * Finner ut om en spiller har mer spilletid.
	 * @return true om en spiller har mer spilletid, false ellers.
	 */
	public boolean hasTimeLeft() {
		return (secs < maxTime) ? true : false;
	}

	/**
	 * Returnerer antall sekunder klokka har gått på "leselig" form.
	 * @return h time(r), m minutt(er) og s sekund(er).
	 */
	public String toString() {
		int hours = (int)(secs / SEC_PER_HOUR);
		int min = (int)((secs % SEC_PER_HOUR) / SEC_PER_MIN);
		int sec = (int)((secs % SEC_PER_HOUR) % SEC_PER_MIN);

		return hours + " time(r), "	+ min + " minutt(er) og " + sec + " sekund(er)";
	}

	/**
	 * Nullstiller klokken, men beholder makstid.
	 */
	public void newGame() {
		secs = 0;
		repaint();
	}

	/**
	 * Setter starttid til klokken.
	 */
	public void set(long startTime) {
		secs = startTime;
	}

	/**
	 * Nullstiller klokken og makstid.
	 */
	public void reset() {
		secs = 0;
		maxTime = 0;
		repaint();
	}

	/**
	 * Lar klokken gå ett sekund.
	 */
	public void tick() {
		secs++;
		repaint();
	}
}
