package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.Move;
import yacm.engine.boardgame.BoardPoint;
import yacm.engine.boardgame.Board;

import java.util.ArrayList;

/**
 * En instans av denne klassen vil inneholde alt som er verdt å vite om et
 * sjakktrekk. Klassen kapsler inn informasjonen som går mellom engine og
 * brukergrensesnittene.
 *
 * @version $Revision: 1.6 $
 * @author Morten L. Andersen
 * @author Andreas Bach
 */
public class ChessMove extends Move implements Constants
{

	/**
	 * Ikke utført, eller verifisert trekk.
	 */
	public static final int NOT_VERIFIED = 0;


	/**
	 * Verifisert at brikken kan flyttes.
	 */
	public static final int LEGAL_SELECTION = 1;

	/**
	 * Verifisert trekk, fra og til er lovlige valg.
	 */
	public static final int LEGAL_TO_EXECUTE = 2;

	/**
	 * Bondeforvandling skal skje, har ikke blitt utført ennå
	 */
	public static final int PENDING = 3;

	/**
	 * Trekk, og evt bondeforvandling har blitt utført. Klar for arkivering,
	 */
	public static final int EXECUTED = 4;

	/**
	 * Variabel som inneholder brettet man spiller på
	 */
	private ChessBoard board;

	/**
	 * Variabel som inneholder hvor brikken flyttes fra
	 */
	private BoardPoint orig;

	/**
	 * Variabel som inneholder hvor brikken skal flyttes til
	 */
	private BoardPoint dest;

	/**
	 * Variabel som inneholder brikken som skal flyttes
	 */
	private ChessPiece piece;

	/**
	 * Variabel som inneholder evt. brikke som har blitt tatt
	 */
	private ChessPiece casualty;

	/**
	 * Variabel som inneholder evt. hva en bonde skal forfremes til
	 */
	private ChessPiece promotion;

	/**
	 * Variabel som inneholder hvor et tårn kommer fra, ved rokkering
	 */
	private BoardPoint rookOrig;

	/**
	 * Variabel som inneholder hvor et tårn skal til, ved rokkering
	 */
	private BoardPoint rookDest;

	/**
	 * Variabel som inneholder hvilken tilstand trekket har
	 */
	private int verifiedState = NOT_VERIFIED;

	/**
	 * Variabel som inneholder resultatet av trekket, om bordet skal skifte status.
	 */
	private int result = NORMAL;

	/**
	 * Variabel som inneholder hvem som eier brikken
	 */
	private int owner;

	/**
	 * Variabel som inneholder hvilken type trekk som ble tatt
	 */
	private int outcome = 0;

	/**
	 * Variabel som inneholder hvorvidt det er flere brikker, av samme type, som kan ta samme brikke.
	 * Dette for loggingen sin del, da man ikke logger origin på alle trekk i følge standard.
	 */
	private boolean ambiguous = false;

	/**
	 * Oppretter et sjakk-flytt, på et spesifikt bord, samt hvor
	 * man flytter fra, og hvem som eier brikken
	 * @param aBoard Hvilket brett det blir gjort et flytt på
	 * @param orig Hvor flyttes det fra
	 * @param owner Hvilken spiller eier brikken
	 */
	public ChessMove(Board aBoard, BoardPoint orig, int owner)
	{
		board  = (ChessBoard) aBoard;
		this.orig = orig;
		this.owner = owner;
	}

	/**
	 * Denne metoden finner ut hvor brikken kommer fra
	 * @return Hvor brikken kommer fra
	 */
	public BoardPoint getOrig()
	{
		return orig;
	}

	/**
	 * Denne metoden finner ut hvor brikken skal flyttes
	 * @return Hvor brikken skal flyttes
	 */
	public BoardPoint getDest()
	{
		return dest;
	}

	/**
	 * Denne metoden returnerer hvilken type trekk som blir gjort
	 * @return Hvilken type trekk som har blitt tatt
	 */
	public int getOutcome()
	{
		return outcome;
	}

	/**
	 * Denne metoden returnerer resultatet av trekket, hvordan står det til med bordet nå
	 * @return Resultatet til trekket, hvilken state brettet har nå
	 */
	public int getResult()
	{
		return result;
	}

	/**
	 * Denne metoden returnerer hvem som eier brikken
	 * @return Hvem eier brikken
	 */
	public int getOwner()
	{
		return owner;
	}

	/**
	 * Denne metoden returnerer hvorvidt trekket er verifisert med brettet
	 * @return Er trekket verifisert?
	 */
	public int verifie()
	{
		switch(verifiedState)
		{
			case NOT_VERIFIED:
				ChessPiece aPiece = board.getPieceOnBoard(orig);

				if(aPiece != null && aPiece.getColor() == owner)
				{
					if(!board.getLegalMoves(aPiece).isEmpty())
					{
						piece = aPiece;
						verifiedState = LEGAL_SELECTION;
					}
				}
				break;
			case LEGAL_SELECTION:
				ArrayList legalMoves = board.getLegalMoves(piece);

				if(dest != null && legalMoves.indexOf(dest) != -1)
				{
					/*
					 * setter eventuell brikke som den slår ut
					 **/
					if(!dest.equals(board.getEnpassantCoordinat()))
					{
						casualty = board.getPieceOnBoard(dest);
					}else{

						int x = dest.getX();
						int y = dest.getY();
						casualty = board.getPieceOnBoard(x, (y == 2 ? 3 : 4));
					}

					if(piece instanceof King && Math.abs(orig.getX() - dest.getX()) == 2)
					{
						int rookOrigX = dest.getX() == 2 ? 0 : 7;
						int rookDestX = dest.getX() == 2 ? 3 : 5;
						rookOrig = new BoardPoint(rookOrigX, dest.getY());
						rookDest = new BoardPoint(rookDestX, dest.getY());
					}


					BoardPoint targetingDest[] = board.getPointingAtPosition(dest, board.getOpponentColor(owner), false);

					if(targetingDest != null)
					{
						for(int i = 0; i < targetingDest.length; i++)
						{
							ChessPiece ambiguousPiece = board.getPieceOnBoard(targetingDest[i]);
							if(!piece.equals(ambiguousPiece) && piece.getType() == ambiguousPiece.getType())
							{
								ambiguous = true;
								break;
							}
						}

					}

					verifiedState = LEGAL_TO_EXECUTE;
				}else{
					dest = null;
				}

				break;
			case LEGAL_TO_EXECUTE:
			case PENDING:
				if(outcome == PAWN_PROMOTION && promotion == null)
				{
					verifiedState = PENDING;
				}else{
					verifiedState = EXECUTED;
					result = board.getBoardState();
					MakeReadyForAddToHistory();
				}
		}
		return verifiedState;
	}

	/**
	 * Denne metoden gjør klar et trekk for å bli arkivert
	 */
	private void MakeReadyForAddToHistory()
	{
		piece = (ChessPiece) piece.copy();
		casualty = casualty != null ? (ChessPiece) casualty.copy() : null;
		promotion = promotion != null ? (ChessPiece) promotion.copy() : null;

	}

	/**
	 * Denne metoden returnerer hvorvidt et trekk er utført og ferdig eller ikke
	 * @return Hvorvidt flyttet er utført eller ei
	 */
	public boolean isExecuted()
	{
		return verifiedState == EXECUTED;
	}

	/**
	 * Denne metoden returnerer hvorvidt flyttet er verifisert for utførelse eller ikke
	 * @return Hvorvidt flyttet er verifisert eller ikke
	 */
	public boolean isVerified()
	{
		return verifiedState <= LEGAL_TO_EXECUTE;
	}

	/**
	 * Denne metoden returnerer brikken som skal flyttes, som objekt
	 * @return Brikken som skal flyttes
	 */
	public ChessPiece getPiece()
	{
		return piece;
	}

	/**
	 * Denne metoden returnerer den eventuelle brikken som ble slått ut
	 * @return evt. utslått brikke
	 */
	public ChessPiece getCasualty()
	{
		return casualty;
	}

	/**
	 * Denne metoden returnerer hva en bonde skal forvandles til (ved bondeforvandling)
	 * @return Brikken en bonde skal forvandles til
	 */
	public ChessPiece getPromotion()
	{
		return promotion;
	}

	/**
	 * Denne metoden returnerer hvor et tårn stod før rokkering
	 * @return Hvor tårnet stod før rokkeringen
	 */
	public BoardPoint getRookOrig()
	{
		return rookOrig;
	}

	/**
	 * Denne metoden returnerer hvor et tårn skal ved rokkering
	 * @return Hvor tårnet skal hen, ved rokkering
	 */
	public BoardPoint getRookDest()
	{
		return rookDest;
	}

	/**
	 * Er det flere brikker av samme type som har muligheten til å ta samme brikke
	 * @return Hvorvidt flere brikker, av samme type, kan ta en og samme brikke
	 */
	public boolean isAmbiguous()
	{
		return ambiguous;
	}

	/**
	 * Denne metoden setter hvor brikken skal
	 * @param coordinate Hvor brikken skal
	 * @return Hvorvidt noe gikk galt eller ikke
	 */
	protected boolean setDest(BoardPoint coordinate)
	{
		boolean noError = false;

		if(verifiedState == LEGAL_SELECTION)
		{
			dest = coordinate;
			if(verifie() == LEGAL_TO_EXECUTE)
			{
				noError = true;
			}
		}
		return noError;
	}

	/**
	 * Denne metoden setter at en brikke skal forvandles
	 * @param aPiece Brikken som skal forvandles
	 */
	protected void setPromotion(ChessPiece aPiece)
	{
		if(verifiedState == PENDING)
		{
			promotion = aPiece;
		}
		verifie();
		
	}

	/**
	 * Denne metoden utfører flyttet
	 * @return Gikk det bra?
	 */
	protected boolean execute()
	{
		boolean noError = false;
		if(verifiedState == LEGAL_TO_EXECUTE)
		{
			outcome = board.move(this);
			verifie();
			noError = true;
		}
		return noError;
	}
}
