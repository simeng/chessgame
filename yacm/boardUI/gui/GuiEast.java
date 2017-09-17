package yacm.boardUI.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.Dimension;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * Østre panel i hovedvinduet.
 * @author Trond Smaavik
 * @author Kristian Berg
 * @version $Revision: 1.2 $
 */
class GuiEast extends JPanel implements java.io.Serializable {

	private GuiLog guiLog;
	private GuiSettings settings;

	public GuiEast(GuiMiddle guiMiddle, GuiLog guiLog, GuiSettings settings) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setPreferredSize(new Dimension(300,400));
		this.guiLog = guiLog;
		this.settings = settings;
		add(guiMiddle);
		add(guiLog);
	}

	/**
	 * Slår av eller på visning av logg-vinduet.
	 * @param show true eller false for henholdsvis visning på eller visning av.
	 */
	public void updateSettings() {
		guiLog.setVisible(settings.showLog);
	}
}
