package yacm.boardUI.gui;

import yacm.engine.boardgame.chess.*;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.Serializable;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * Viser en dialog hvor en kan velge hvilken brikke man vil
 * bonden skal forvandles til,
 * @author Simen Graaten
 * @version $Revision: 1.3 $
 */
class GuiPawnPromotion extends JDialog implements ActionListener, Serializable {
	private GuiSquare squares[];
	private JFrame parent;
	private ChessPiece choice = null;

	/**
	 * Konstruerer dialogen som skal brukes.
	 * @param parent Eiren av dialogen.
	 */
	public GuiPawnPromotion(JFrame parent) {
		super(parent, "Bondeforvandling", true);
		this.parent = parent;
		Container pawnDialog = getContentPane();
		pawnDialog.setLayout(new FlowLayout());
		this.setSize(new Dimension(450,70));
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		pawnDialog.add(new JLabel("Velg ønsket brikke:"));

		squares = new GuiSquare[4];
		squares[0] = new GuiSquare(new Rook(0,null),0,0);
		squares[1] = new GuiSquare(new Knight(0,null),0,0);
		squares[2] = new GuiSquare(new Bishop(0,null),0,0);
		squares[3] = new GuiSquare(new Queen(0,null),0,0);
		
		for (int i=0; i<squares.length; i++) {
			squares[i].addActionListener(this);
			pawnDialog.add(squares[i]);
		}
	}

	/**
	 * Henter brukerens valg, hvis ingen brikke er valgt returneres <code>null</code>.
	 * @return ChessPiece Valgt brikke eller null.
	 */
	public ChessPiece getChoice() {
		ChessPiece choice = this.choice;

		if (choice != null) {
			this.choice = null;
		}

		return choice;
	}

	public void actionPerformed(ActionEvent e) {
		for (int i=0; i<squares.length; i++) {
			if (e.getSource() == squares[i]) {
				choice = ((GuiSquare)e.getSource()).getChessPiece();
				setVisible(false);
			}
		}
	}
}

