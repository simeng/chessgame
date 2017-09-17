package yacm.boardUI.gui;

import yacm.engine.boardgame.*;
import yacm.engine.boardgame.chess.*;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JOptionPane;

/**
 * Guielementet som inneholder knappene som ligner gruelig på et sjakkbrett
 * Denne klassen behandler også brikkeflyttene som brukeren ønsker å utføre.
 */
public class GuiBoard extends JPanel implements ActionListener, Constants, java.io.Serializable {

	/**
	 * Inneholder ruita som brukeren har valgt.
	 */
	private GuiSquare squareSelected;
	/**
	 * Inneholder alle rutene på sjakkbrettet.
	 */
	private GuiSquare squares[][];
	/**
	 * En dialog for å velge brikken man ønsker å forvandle bonden til.
	 */
	private GuiPawnPromotion pawnPromotion;
	/**
	 * Eieren av dette panelet.
	 */
	private JFrame parent;
	/**
	 * En plass å registrere lovlige knappetrykk.
	 */
   	private ArrayList movePoints;
	/**
	 * Sjakkmotoren som det kommuniseres med.
	 */
	private BoardEngine engine;
	/**
	 * Spillerobjektet som inneholder informasjon om spillerne.
	 */
	private GuiPlayers players;
	/**
	 * Loggen som brukes i under spillet.
	 */
	private GuiLog guiLog;
	/**
	 * Instillingene som skal benyttes i spillet.
	 */
	private GuiSettings settings;

	/**
	 * En tabell som inneholder feltnavnene.
	 */
	String HorizLetters[] = { "a","b","c","d","e","f","g","h" };

	/**
	 * En tabell som inneholder feltnavnene.
	 */
	String VertNumbers[] = { "8","7","6","5","4","3","2","1" };

	/**
	 * Bakgrunnsfarge.
	 */
	public final Color COLOR_BOARD_BACKGROUND = new Color(180,50,180);


	/**
	 * Konstruerer et nytt sjakkbrettfelt.
	 * @param parent Eieren av dette sjakkbrrettet.
	 * @param engine Sjakkmotoren som Gui'et skal arbeide mot.
	 * @param players Spillerne som skal være med i spillet.
	 * @param guiLog Loggen som skal inneholde trekkene.
	 * @param settings Innstillingene som skal benyttes.
	 */
    public GuiBoard(JFrame parent, BoardEngine engine, GuiPlayers players, GuiLog guiLog, GuiSettings settings) {

		this.parent = parent;
		this.engine = engine;
		this.players = players;
		this.guiLog = guiLog;
		this.settings = settings;
		squares = new GuiSquare[8][8];

		pawnPromotion = new GuiPawnPromotion(parent);

        this.setLayout(new GridLayout(10, 10));
		this.setPreferredSize(new Dimension(400,400));

        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
                if ((y == 0 || y == 9) && (x > 0 && x < 9)) {
                    JLabel l = new JLabel(HorizLetters[x-1]);
                    l.setVerticalAlignment(JLabel.CENTER);
                    l.setHorizontalAlignment(JLabel.CENTER);
                    l.setBackground(COLOR_BOARD_BACKGROUND);
                    this.add(l);
                }
                else if ((y > 0 && y < 9) && (x == 0 || x == 9)) {
                    JLabel l = new JLabel(VertNumbers[y-1]);
                    l.setVerticalAlignment(JLabel.CENTER);
                    l.setHorizontalAlignment(JLabel.CENTER);
                    l.setBackground(COLOR_BOARD_BACKGROUND);
                    this.add(l);
                }
                else if ((x == 0 && y == 0) || (x == 0 && y == 9) ||
                         (x == 9 && y == 0) || (x == 9 && y == 9)) {
                    // hjørnene
                    JLabel l = new JLabel();
                    l.setBackground(COLOR_BOARD_BACKGROUND);
                    this.add(l);
                }
	            else {
					//FY! Gjør om. Noe mystisk. Neida, fint som bare det
					//Vi må justere tabellnummer pga bokstavene og tallene
					//oeverst paa sjakkbrettet.
					int tempX = x-1;
					// Vi begynner i bunnen av brettet og teller oppover
					int tempY = 7-(y-1);

					GuiSquare square;

					square = new GuiSquare(null, tempX, tempY);
					square.setOpaque(true);
					square.addActionListener(this);

                    squares[tempY][tempX] = square;
                	this.add(square);
                }

			}
		}
    }

	/**
	 * Stiller opp brikkene på sjakkbrettet.
	 * @param pieces Brukkene som skal benyttes.
	 */
	public void setupBoard(ArrayList pieces) {
		Iterator it = pieces.iterator();
		while (it.hasNext()) {
			ChessPiece piece = (ChessPiece)it.next();

			int x = piece.getPosition().getX();
			int y = piece.getPosition().getY();

			this.setPiece(piece, squares[y][x]);
		}
	}

	/**
	 * Flytter brikken som er valgt med <code>selectPiece</code> til
	 * posisjonen <code>p</code> på brettet.
	 * @param p Posisjon på brettet
	 */
	public boolean movePiece(BoardPoint p) {

		GuiSquare square = null;
		GuiSquare tempSquare = null;
        ChessPiece tempPiece = null;
		boolean allowedMove = false;

		square = this.getSquare(p);

        if (square == null) {

            return false;
        }

		if (square.isSelected()) {
		    return false;
		}

		Iterator it = movePoints.iterator();
		while (it.hasNext()) {
			BoardPoint point = (BoardPoint)it.next();

			// hvis punktet vi flytter til er i listen av lovlige trekk?
			if (point.getX() == p.getX() &&
				point.getY() == p.getY()) {

				/* fjerne hilighting */
				if (settings.showMoves) {
		        	Iterator iter = movePoints.iterator();
		        	while (iter.hasNext()) {
		            	BoardPoint pnt = (BoardPoint)iter.next();
						tempSquare = this.getSquare(pnt);
						tempSquare.setHilight(false);
					}
				}

				squareSelected.setSelected(false);
				allowedMove = true;
				squareSelected = null;

				try {
					int outcome = engine.movePiece(point, players.getCurrentLocalPlayer());

				switch (outcome) {
					case PAWN_PROMOTION:
						ChessPiece choice = null;
						pawnPromotion.show();
						this.setEnabled(false);
						// vente på at bruker velger brikke
						while ((choice = pawnPromotion.getChoice()) == null);
						this.setEnabled(true);
						try {
							engine.promotePawn(choice.getType(), players.getCurrentLocalPlayer());
						} catch(Exception e) { }
					break;
				}

				} catch(Exception e) { }

			}
		}
		return allowedMove;

	}

	/**
	 * Velger brikken på punktet <code>p</code>, om det ikke er noen brikke
	 * returners false.
	 * @param p posisjon på brettet.
	 * @return <code>true</code>, om valget var gyldig.
	 */
	public boolean selectPiece(BoardPoint p) {

		GuiSquare square;
        	GuiSquare tempSquare;

		square = this.getSquare(p);

		// Forhåndstest i GUI av garantert ulovlige trekk
		if (square.getChessPiece() == null || square.isSelected()) {
			return false;
        }


	try {
        	movePoints = engine.selectPiece(p, players.getCurrentLocalPlayer());
	} catch(Exception e) { }

        if (movePoints == null || movePoints.size() == 0) {

            return false;
        }

		// Hilighting av lovlige flytt
		if (settings.showMoves) {
        	Iterator it = movePoints.iterator();
			while (it.hasNext()) {
				tempSquare = this.getSquare((BoardPoint)it.next());
				tempSquare.setHilight(true);
			}
		}

		squareSelected = square;
		square.setSelected(true);

		return true;
	}

	/**
	 * Oppdaterer sjakkbrettet basert på trekket som er gjennomført.
	 * @param move Sjakktrekket som er gjort.
	 */
	public void updateMove(ChessMove move) {

		Player player = players.getPlayer(move.getOwner());

		if (move != null) {

			BoardPoint origin = move.getOrig();
			BoardPoint destination = move.getDest(); ChessPiece piece = (ChessPiece)move.getPiece();
			ChessPiece casualtyPiece = (ChessPiece)move.getCasualty();
			ChessPiece promotedPiece = (ChessPiece)move.getPromotion();
			GuiSquare origSquare = getSquare(origin);
			GuiSquare destSquare = getSquare(destination);
			ChessPiece tempPiece;

			guiLog.updateLog(move);

			int outcome = move.getOutcome();

			switch (outcome) {
				case PAWN_PROMOTION:
					promotedPiece.setPosition(destination); // kan denne fjernes?
					destSquare.setChessPiece(promotedPiece);

					origSquare.setChessPiece(null);
					break;
				case ROKADE_KING:

					origSquare.setChessPiece(null);
					destSquare.setChessPiece(piece);

					// putter tårn i temp, setter tårn til null og putter tårn inn i venstresiden av kongen
					tempPiece = squares[destSquare.getYPos()][destSquare.getXPos()+1].getChessPiece();
					squares[destination.getY()][destination.getX()+1].setChessPiece(null);
					squares[destination.getY()][destination.getX()-1].setChessPiece(tempPiece);
					break;
				case ROKADE_QUEEN:

					origSquare.setChessPiece(null);
					destSquare.setChessPiece(piece);

					// putter tårn i temp, setter til null og flytter til høyre for kongen
					tempPiece = squares[destination.getY()][destination.getX()-2].getChessPiece();
					squares[destination.getY()][destination.getX()-2].setChessPiece(null);
					squares[destination.getY()][destination.getX()+1].setChessPiece(tempPiece);
					break;
				case ENPASSANT:
					origSquare.setChessPiece(null);
					squares[casualtyPiece.getY()][casualtyPiece.getX()].setChessPiece(null);
					destSquare.setChessPiece(piece);
					break;
				default:
					origSquare.setChessPiece(null);
					destSquare.setChessPiece(piece);
			}
		}
	}

	/**
	 * Fjerner alle brikker og renser brettet.
	 */
	public void removeAllPieces() {
		for (int y=0; y<squares.length; y++) {
			for (int x=0; x<squares[y].length; x++) {
				squares[y][x].setChessPiece(null);
				squares[y][x].setHilight(false);
				squares[y][x].setSelected(false);
			}
		}

		squareSelected = null;
	}

	/**
	 * Fjerner brikken som står ved det gitte punktet ved at brikketypen
	 * settes til 0.
	 * @return <code>true</code>, såfremt det er en brikke på den angitte ruten.
	 */
	private boolean removePiece(GuiSquare square) {

		int x = (int)square.getXPos();
		int y = (int)square.getYPos();

		if (square.getChessPiece() == null)
			return false;

		squares[y][x].setChessPiece(null);

		return true;
	}

	/**
	 * Henter ut GuiSquare fra det gitte punktet <code>p</code>.
	 * @param p Punktet på brettet.
	 * @return GuiSquare til det gitte punkt.
	 */
	private GuiSquare getSquare(BoardPoint p) {
		int x = (int)p.getX();
		int y = (int)p.getY();

		return squares[y][x];
	}


	/**
	 * Setter ut en angitt brikke på feltet <code>square</code>.
	 * @param ChessPiece Sjakkbrikke.
	 * @param GuiSquare Feltet hvor brikken skal plasseres.
	 */
	private void setPiece(ChessPiece piece, GuiSquare square) {
		piece.setPosition(square.getPoint());
        	square.setChessPiece(piece);
	}

	/**
	 * Knappelytter.i
	 * @param e Knappetrykket som har funnet sted.
	 */
	public void actionPerformed(ActionEvent e) {

		GuiSquare square;
		square = (GuiSquare)e.getSource();
        	BoardPoint p = square.getPoint();
        	if (squareSelected == null) {
			selectPiece(p);
        	} else {
			movePiece(p);
        	}
	}
}



