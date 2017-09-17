package yacm.boardUI.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serializable;

/**
 * Dette er panelet som inneholder navneetikettene og klokkene
 * @version $Revision: 1.2 $
 * @author Trond Smaavik
 */
class GuiMiddle extends JPanel implements Serializable {

	private GuiClockSet clockSet;
	private GuiSettings settings;
	public GuiMiddle(GuiPlayers players, GuiClockSet clockSet, GuiSettings settings) {
		BorderLayout layout = new BorderLayout();
		layout.setVgap(20);
		setLayout(layout);
		setPreferredSize(new Dimension(100,400));
		this.clockSet = clockSet;
		this.settings = settings;

		add(players.getPlayerLabel(1), BorderLayout.NORTH);
		add(clockSet, BorderLayout.CENTER);
		add(players.getPlayerLabel(0), BorderLayout.SOUTH);
	}

	/**
	 * Slår av eller på visning av sjakklokken.
	 * @param show true eller false for henholdsvis visning på eller visning av.
	 */
	public void updateSettings() {
		clockSet.setVisible(settings.showClock);
	}

}

