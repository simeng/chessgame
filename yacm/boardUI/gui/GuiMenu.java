package yacm.boardUI.gui;

import yacm.engine.boardgame.*;
import yacm.boardUI.gui.GuiAbout;
import yacm.boardUI.gui.Gui;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JDialog;
import java.awt.event.*;
import java.awt.Dimension;
import java.io.Serializable;


/*
 * Sist endret av: $Author: epoxy $
 */

/**
 * Dette er menyen i det grafiske brukergresesnittet.
 * @author Trond Smaavik
 * @author Kristian Berg
 * @version $Revision: 1.4 $
 */
class GuiMenu extends JMenuBar implements Serializable {

	private BoardEngine engine;
	private GuiAbout aboutBox;
	private GuiHelpDialog helpDialog;
	private JMenu game, options, helpMenu, newMenu;
	private JMenuItem newGame, addPlayer, exit, help, about, saveLog, newBoard;
	private JCheckBoxMenuItem showMoves;
	private GuiAddPlayerDialog addPlayerDialog;
	private Gui gui;
	private GuiSettings settings;
	private GuiPlayers players;
	private BoardUI ui;
	private GuiLog guiLog;

	public GuiMenu(Gui gui, BoardUI ui, BoardEngine engine, GuiAddPlayerDialog addPlayerDialog, GuiPlayers players, GuiSettings settings, GuiLog guiLog) {
		MenuItemListener mil = new MenuItemListener();
		CheckItemListener cil = new CheckItemListener();

		this.addPlayerDialog = addPlayerDialog;
		this.engine = engine;
		this.gui = gui;
		this.settings = settings;
		this.players = players;
		this.guiLog = guiLog;
		this.ui = ui;

		//Ny-meny
		newMenu = new JMenu("Ny");
		newGame = new JMenuItem("Nytt spill");
		newGame.addActionListener(mil);
		newBoard = new JMenuItem("Nytt brett");
		newBoard.addActionListener(mil);

		newMenu.add(newGame);
		newMenu.add(newBoard);


		//Spillmeny
		game = new JMenu("Spill");
		addPlayer = new JMenuItem("Spillere");
		addPlayer.addActionListener(mil);
		saveLog = new JMenuItem("Lagre logg");
		saveLog.addActionListener(mil);
		exit = new JMenuItem("Avslutt");
		exit.addActionListener(mil);

		game.add(newMenu);
		game.add(addPlayer);
		game.add(saveLog);
		game.addSeparator();
		game.add(exit);

		//Valgmeny
		options = new JMenu("Valg");
		showMoves = new JCheckBoxMenuItem("Vis mulige trekk", settings.showMoves);
		showMoves.addItemListener(cil);

		options.add(showMoves);

		//Hjelpmeny
		helpMenu = new JMenu("Hjelp");
		help = new JMenuItem("Hjelp");
		help.addActionListener(mil);
		about = new JMenuItem("Om YACM");
		about.addActionListener(mil);

		aboutBox = new GuiAbout(gui);
		helpDialog = new GuiHelpDialog(gui);

		helpMenu.add(help);
		helpMenu.addSeparator();
		helpMenu.add(about);

		//Legg til menyene
		this.add(game);
		this.add(options);
		this.add(helpMenu);

	}

    //Lyttere for menyene
	class MenuItemListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == newGame) {
				try{
				engine.stopGame(ui);
				engine.newGame();
				}catch(Exception re){}
			}
			if(e.getSource() == exit) {
				gui.close();
			}
			if(e.getSource() == help) {
				helpDialog.setVisible(true);
			}
			if(e.getSource() == about) {
				aboutBox.reset();
				aboutBox.setVisible(true);
			}
			if(e.getSource() == addPlayer) {
				addPlayerDialog.showDialog();
			}
			if(e.getSource() == saveLog) {
				guiLog.save();
			}
			if(e.getSource() == newBoard)
			{
				try {
					new Client();
				} catch(Exception re) { }
			}
			gui.updateSettings();
		}
	}

	class CheckItemListener implements ItemListener{
		public void itemStateChanged(ItemEvent e) {
			if(e.getSource() == showMoves) {
				settings.showMoves = !settings.showMoves;
			}
			gui.updateSettings();
		}
	}
}
