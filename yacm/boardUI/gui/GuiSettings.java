package yacm.boardUI.gui;

import java.util.ArrayList;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Klassen inneholder alle valg brukeren kan gjøre.
 * Menyen og dialogboksene  henvender seg mot denne klassen
 * når de gjør endringer. En del andre klasser henter bare ut
 * data fra denne klassen.
 *
 * @version $Revision: 1.2 $
 * @author Trond Smaavik
 */
class GuiSettings implements Serializable {

	/**
	 * Benytt Ai
	 */
	public boolean useAi = false;

	/**
	 * Vis gyldige trekk på brettet.
	 */
	public boolean showMoves = false;

	/**
	 * Vis loggen i grensesnittet.
	 */
	public boolean showLog = true;

	/**
	 * Vis klokken i grensesnittet.
	 */
	public boolean showClock = true;

	/**
	 * Tidsinstilling for hvit spiller.
	 */
	public int whiteMaxTime = 60;

	/**
	 * Tidsinstilling for hvit spiller.
	 */
	public int blackMaxTime = 60;

	/**
	 * Konstruerer instillingsobjektet.
	 */
	public GuiSettings() {
	}

	public GuiSettings load() {
		GuiSettings temp = null;
		try {
			FileInputStream fis = new FileInputStream("settings.cfg");
			ObjectInputStream ois = new ObjectInputStream(fis);
			temp = (GuiSettings)ois.readObject();
			ois.close();
		} catch(Exception e) { }
		return temp;
	}

	/**
	 * Lagrer brukerens instillinger til harddisk.
	 */
	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream("settings.cfg");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch(Exception e) {
			System.out.println("Klarte ikke lagre settings: " + e);
		}
	}
}
