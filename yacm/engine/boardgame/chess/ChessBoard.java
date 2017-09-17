/*
 * Coppyright 2003 Gruppe 10 All rights reserved.
 * @(#)$Id: ChessBoard.java,v 1.12 2003/05/07 00:52:52 mortenla Exp $
 */

package yacm.engine.boardgame.chess;

import yacm.engine.boardgame.Board;
import yacm.engine.boardgame.BoardPoint;
import yacm.engine.boardgame.Move;

import yacm.engine.TestTools;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * Siste endret av: $Author: mortenla $
 */

/**
 * Dette er det virtuelle sjakkbrettet og all logikken som ligger i selve brettet.
 *
 * @author	Morten L. Andersen
 * @author	Simen Graaten
 * @author 	Kristian Berg
 * @author	Trond Smaavik
 * @author	Andreas Bach
 * @version	$Revision: 1.12 $, $Date: 2003/05/07 00:52:52 $
 */
public class ChessBoard implements Board, Constants, java.io.Serializable
{
//--- Atributer ---------------------------------------------------
	/**
	 * Tilstanden sjakkbrettet er i.
	 */
	private int boardState = NORMAL;

	/**
	 * En tabell over brikkene. Denne for å gjøre det enklere
	 * å finne en spesifik brikke. I denne tabellen er alle brikkene, uavhengig av tilstand.
	 */
	private ChessPiece pieces[][] = new ChessPiece[2][16];

	/**
	 * Brikkene som står på bordet. De som er drept, blir fjernet fra denne tabellen.
	 */
	private ChessPiece board[][] = new ChessPiece[8][8];

	/**
	 * Denne vil inneholde lovlige trekk som fører til at
	 * man kan komme seg ut av sjakk.
	 */
	private ArrayList counterMoves = new ArrayList();

	/**
	 * Blir brukt til å undersøke om 50-trekksregelen oppfylles.
	 */
	private int numberOfMovesSinceLstKPM = 0;

	/**
	 * Går det an å gjøre en passant?
	 * Denne blir satt hvis siste trekk var et dobbelt bondeflytt.
	 */
	private BoardPoint enpassantCoordinat = null;

	/**
	 * Denne blir satt hvis siste trekk førte til at man skal
	 * gjøre en bondeforvandling.
	 */
	private ChessPiece pawnToBePromoted = null;

	/**
	 * Lagrer statusen til brettet for å kunne sjekke for
	 * 3-status regelen.
	 */
	private ArrayList states = new ArrayList();

//--- konstruktører ----------------------------------------------

	/**
	 * Hovedsaken med dette spillet. 
	 * Denne har egen initierings-metode.
	 */
	public ChessBoard()
	{
		initBoard();
	}

//--- public -----------------------------------------------------

	/**
	 * Returnerer statusen til brettet.
	 *
	 * @return Statusen til brettet
	 *
	 * @see yacm.engine.chess.Constants#NORMAL
	 * @see yacm.engine.chess.Constants#CHECK
	 * @see yacm.engine.chess.Constants#REMI
	 * @see yacm.engine.chess.Constants#PATT
	 * @see yacm.engine.chess.Constants#CHECKMATE
	 */
	public int getBoardState() { return boardState; }

	/**
	 * Denne metoden returner en tabell med alle brikkene,
	 * også de som er slått ut.
	 * @return Alle brikkene, uavhengig av tilstand.
	 */
	public ChessPiece[][] getPieces()
	{
		ChessPiece[][] tempPieces = new ChessPiece[2][16];

		for(int i = pieces.length - 1; i >= 0; i--)
		{
			for(int j = 0; j < pieces[i].length; j++)
			{
				tempPieces[i][j] = (ChessPiece) pieces[i][j].copy();
			}
		}
		return tempPieces;
	}

	/**
	 * Initialiserer sjakkbrettet. Setter opp standard brikkesett.
	 */
	public void initBoard()
	{
		initBoard(mkDefault());
	}

	/**
	 * Initialiserer sjakkbrettet. Setter opp standard brikkesett.
	 * @param initPieces	Brikker til å starte med.
	 */
	public void initBoard(ChessPiece initPieces[][])
	{
		boardState = NORMAL;
		pieces = initPieces != null ?  initPieces : new ChessPiece[2][16];

		board = new ChessPiece[8][8];
		counterMoves.clear();
		numberOfMovesSinceLstKPM = 0;
		enpassantCoordinat = null;
		states.clear();

		for(int color = WHITE; color <= BLACK; color++)
		{
			for(int type = ROOK; type <= PAWN_8; type++)
			{
				ChessPiece aPiece = getAlivePiece(type, color);
				if(aPiece != null)
				{
					setPieceOnBoard(aPiece);
				}
			}
		}
	}

	/**
	 * Flytter en brikke til en ny posisjon.
	 * @param position	Ny posisjon
	 * @return Status for brikkeflytting
	 */
	public int move(Move selectedMove)
	{
		ChessMove aMove = (ChessMove) selectedMove;

		int outcome = 0;

		if(aMove != null &&  aMove.isVerified() && !aMove.isExecuted())
		{
			/*
			 * Hvis spilleren som er i trekk var i sjakk så
			 * vet vi de eneste lovlige trekkene han kan gjøre er
			 * å få seg selv ut av sjakk
			 **/
			this.boardState = NORMAL;

			outcome = moveSelectedPieceOnBoard(aMove);

			if(outcome != PAWN_PROMOTION)
			{
				/*
				 * Dette vil generere evntuelle trekk som gjøre at fi
				 * vil ha muligeten til å komme seg ut av en eventuell sjakk
				 */
				if(genCounterMoves(aMove.getOwner()) != 0)
				{
					this.boardState = CHECK;
				}

				checkState(aMove.getOwner());
			}
		}
		return outcome;
	}

	/**
	 * Denne metoden brukes til å velge en brikke på brettet
	 * ut i fra posisjon. Den vil returnere en ArrayList med koordinater som brikken
	 * har lov til å flytte til.
	 * @param coordinate	Koordinat til (forhåpentligvis) en brikke
	 * @return Lovlige flytt
	 */
	public ArrayList selectPiece(Move selectedMove)
	{
		ChessMove aMove = (ChessMove) selectedMove;
		ArrayList legalMoves = null;

		/*
		 * Forsikkrer oss om at vi bare kan velge en brikke så lenge som
		 * at brettet er i en state som det er tilatt i.
		 * Dvs. NORMAL og CHECK
		 */
		if(boardState < REMI && pawnToBePromoted == null)
		{
			if(aMove.verifie() == ChessMove.LEGAL_SELECTION)
			{
				legalMoves = getLegalMoves(aMove.getPiece());
			}
		}
		return (legalMoves != null && !legalMoves.isEmpty()) ?  new ArrayList(legalMoves) : null;
	}

	/**
	 * Denne metoden utfører bondeforvandling.
	 * @param type	Hvilken type skal det forandres til
	 * @param selectedMove	Et valgt trekk
	 * @return <code>true</code> om alt gikk bra, <code>false</code> ellers.
	 */
	public boolean promotePawn(int type, Move selectedMove)
	{
		ChessMove aMove = (ChessMove) selectedMove;
		boolean noError = false;
		if(pawnToBePromoted != null && type < 8 && type != 4)
		{
			int color = pawnToBePromoted.getColor();

			for(int i = 0; i < pieces[color].length; i++)
			{
				if(pawnToBePromoted == pieces[color][i])
				{
					ChessPiece aPiece = mkPiece(type, color,new BoardPoint(pawnToBePromoted.getPosition()));
					aPiece.setMoved(true);
					pieces[color][i] = aPiece;
					setPieceOnBoard(aPiece);
					pawnToBePromoted = null;
					/*
					 * Dette vil generere eventuelle trekk som gjøre at fi
					 * vil ha muligeten til å komme seg ut av en eventuell sjakk
					 */
					this.counterMoves.clear();


					if(genCounterMoves(aMove.getOwner()) != 0)
					{
						this.boardState = CHECK;

					}
					checkState(color);

					aMove.setPromotion(aPiece);

					noError = true;
				}
			}
		}
		return noError;
	}

//--- protected --------------------------------------------------

	/**
	 * Denne metoden returnerer brikker som er i live som er av en bestemt type og farge
	 * @param type	Brikketype
	 * @param color	Brikkefarge
	 * @return En sjakkbrikke som oppfyller kravene til type og farge, og som er i live.
	 */
	protected ChessPiece getAlivePiece(int type, int color)
	{
		ChessPiece aPiece = getPiece(type, color);

		if(aPiece != null && !aPiece.isAlive())
		{
			aPiece = null;
		}
		return aPiece;
	}

	/**
	 * Returnerer en koordinat hvis det er lov å ta enpassant
	 * @return En ekstra koordinat man kan så dersom det er lov til å ta en passant.
	 */
	protected BoardPoint getEnpassantCoordinat() { return enpassantCoordinat; }

	/**
	 * Denne metoden returner posisjonene til de brikkene som holder en gitt brikke i sjakk.
	 * @param aPiece	Sjakkbrikken som det skal undersøkes om er i sjakk.
	 * @return En tabell med posisjoner til brikker som peker på den gitte brikken.
	 */
	protected BoardPoint[] getPointingAtPosition(ChessPiece aPiece)
	{
		return getPointingAtPosition(aPiece.getPosition(), aPiece.getColor(), false);
	}

	/**
	 * Denne metoden returner posisjonene til de brikkene som holder en bestemt koordinat i sjakk.
	 * @param x X-koordinat
	 * @param y Y-koordinat
	 * @param color Fargen på brikken
	 * @param transKing Verdi som sier som man skal se vidre bak kongen, eller stoppe ved den.
	 * @return En liste med posisjoner som holder den gitte posisjonen i sjakk.
	 */
	protected BoardPoint[] getPointingAtPosition(int x, int y, int  color, boolean transKing)
	{
		return getPointingAtPosition(new BoardPoint(x, y), color, transKing);
	}

	/**
	 * Denne metoden returnerer posisjonene til de brikkene som holder en bestemt koordinat i sjakk.
	 * @param position Posisjonen til brikken
	 * @param color Fargen til brikken
	 * @param transKing Verdi som sier som man skal se videre bak kongen eller stoppe ved den.
	 * @return En liste med posisjoner som holder den gitte posisjonen i sjakk.
	 */
	protected BoardPoint[] getPointingAtPosition(BoardPoint position, int  color, boolean transKing)
	{
		// Alle de mulige retningene
		final BoardPoint[] directionVectors = {
			new BoardPoint(0, 1, false),	// N
			new BoardPoint(1, 1, false),	// NØ
			new BoardPoint(1, 0, false),	// Ø
			new BoardPoint(1, -1, false),	// SØ
			new BoardPoint(0, -1, false),	// S
			new BoardPoint(-1, -1, false),	// SW
			new BoardPoint(-1, 0, false),	// W
			new BoardPoint(-1, 1, false),	// NW
		};
		final int opponentColor = getOpponentColor(color);

		ArrayList piecesTargetingPosition = new ArrayList();

		ChessPiece aPiece = null;

		// Må forsikre oss om at denne posisjonen eksisterer på brettet
		if(isValidCoordinate(position))
		{
			/*
			 * Letter opp eventuelle fi i aksene til punktet og legger så posisjonen til fi
			 * i ArrayListen hvis den holder posisjonen i sjakk.
			 */
			for(int i = 0; i < directionVectors.length; i++)
			{
				aPiece = findFirstPieceInDirection(position , directionVectors[i]);
                                if(aPiece != null)
				{
					if(aPiece.getColor() != opponentColor && aPiece.getType() == KING && transKing)
					{
						aPiece = findFirstPieceInDirection(aPiece.getPosition(), directionVectors[i]);
					}

					if(aPiece != null && aPiece.getColor() == opponentColor && isPointingOn(aPiece, position, transKing))
					{
						piecesTargetingPosition.add(new BoardPoint(aPiece.getPosition()));
					}
				}
			}

			/*
			 * Sjekker om Hestene til fi holder posisjonen i sjakk
			 * og eventuelt legger koordinatene i ArrayListen.
			 */
			ChessPiece aFoeKnight;
			for(int i = 0; i < 16; i++)
			{
				aFoeKnight = getAlivePiece(i, opponentColor);

				if(aFoeKnight instanceof Knight  && isPointingOn(aFoeKnight, position, NOT_TRANSPARENT))
				{
					piecesTargetingPosition.add(new BoardPoint(aFoeKnight.getPosition()));
				}
			}
		}

		BoardPoint temp[] = null;

		if(!piecesTargetingPosition.isEmpty())
		{
			temp = new BoardPoint[piecesTargetingPosition.size()];
			Iterator itr = piecesTargetingPosition.iterator();
			int i = 0;

			while(itr.hasNext())
			{
				temp[i++] = (BoardPoint) itr.next();
			}

		}
		return temp;
	}

	/**
	 * Søker i den retningen som retningsvektoren tilsier og returnerer den
	 * første brikken den finner.
	 * @param piece	En brikke det skal søkes fra.
	 * @param directionVector	Retningen det skal søkes i.
	 * @return Den første brikken i en gitt retning.
	 */
	protected ChessPiece findFirstPieceInDirection(ChessPiece piece ,BoardPoint directionVector)
	{
		return findFirstPieceInDirection(piece.getPosition(), directionVector);
	}

	/**
	 * Søker i den retningen som retningsvektoren tilsier og returnerer den
	 * første brikken den finner.
	 * @param from	Et punkt det skal søkes fra.
	 * @param directionVector	Retinigen det skal søkes i.
	 * @return Den første brikken i en gitt retning
	 */
	protected ChessPiece findFirstPieceInDirection(BoardPoint from ,BoardPoint directionVector)
	{
		ChessPiece aPiece = null;
		if(isValidCoordinate(from))
		{
			int x = from.getX();
			int y = from.getY();

			int dx = directionVector.getX();
			int dy = directionVector.getY();
			while(isValidCoordinate(x += dx, y += dy) && board[x][y] == null);
			aPiece = isValidCoordinate(x, y) ? board[x][y] : null;
		}
		return aPiece;
	}

	/**
	 * Denne metoden returnerer den brikken som står på valgte ruten på sjakkbrettet.
	 *
	 * @param	x	X-koordianten til en rute.
	 * @param	y	Y-koordianten til en rute.
	 * @return	Returnerer en kopi av den brikken som står på den angitte posisjonen, hvis det ikke
	 * 				står noe brikke på posisjonen blir <code>null</code> returnert.
	 */
	protected ChessPiece getStandingOnPosition(int x, int y)
	{
		return getPieceOnBoard(x, y);
	}
	/**
	 * Denne metoden returnerer den brikken som står på valgte ruten på sjakkbrettet.
	 *
	 * @param	position	En posisjon på brettet.
	 * @return	Returnerer en kopi av den brikken som står på den angitte posisjonen, hvis det ikke
	 * 				står noe brikke på posisjonen blir <code>null</code> returnert.
	 *
	 */
	protected ChessPiece getStandingOnPosition(BoardPoint coordinate)
	{
		return getPieceOnBoard(coordinate);
	}

	/**
	 * Denne metoden er for å sjekke om en instans av BoardPoint beskriver
	 * en gyldig lokasjon på sjakkbrettet.
	 *
	 * @param	coordinate	Et BoardPoint som skal sjekkes om er gyldig.
	 * @return	<code>true</code> om koordinaten er gyldig, <code>false</code> ellers.
	 */
	protected boolean isValidCoordinate(BoardPoint coordinate)
	{
		boolean isValid = false;
		if(coordinate != null && coordinate.isFixed())
		{
			isValid = isValidCoordinate(coordinate.getX(), coordinate.getY());
		}
		return isValid;
	}

	/**
	 * Sjekker om koordianten er innenfor brettet
	 * @param	x	X-koordinat
	 * @param	y	Y-koordinat
	 * @return	<code>true</code> om koordinaten er gyldig, <code>false</code> ellers.
	 */
	protected boolean isValidCoordinate(int x, int y)
	{
		return (x >= 0 && x < 8 && y >= 0 && y < 8);
	}

//--- private ----------------------------------------------------

	/**
	 * Denne metoden sjekker for sjakkmatt eller patt,
	 * hvis det inntreffer skifter den boardState.
	 * Må alltid kalles etter at genCounterMoves() eller boardState blir satt til CHECK.
	 *
	 * @param inPlay Spilleren som er i trekk.
	 */
	private void checkState(int inPlay)
	{
		int opponentColor = getOpponentColor(inPlay);
		ChessPiece theFoeKing = getPiece(KING, opponentColor);
		ArrayList legalMoves;
		boolean patt = true;

		if(checkTreeState(new ChessBoardState(enpassantCoordinat, pieces, opponentColor)))
		{
			boardState = REMI;

		}else{
			//Finner ut om fi har noen lovlige trekk
			for(int i = 0; i < pieces[opponentColor].length; i++)
			{
				ChessPiece aPiece = getAlivePiece(i, opponentColor);
				if(aPiece != null)
				{
					legalMoves = getLegalMoves(aPiece);

					if(legalMoves.size() > 0)
					{
						patt = false;
						break;
					}
				}
			}

			if(boardState == CHECK && patt)
			{
				boardState = CHECKMATE;
			}
			else if(boardState == NORMAL && patt)
			{
				boardState = PATT;
			}
		}
	}

	/**
	 * Denne metoden sjekker om man har tre like statuser i løpet av spillet
	 * @param aState Bordstatus
	 * @return <code>true</code> om tre like statuser har oppstått, <code>false</code> ellers.
	 */
	private boolean checkTreeState(ChessBoardState aState)
	{
                boolean treeState = false;
		int index = states.indexOf(aState);

		if(index == -1)
		{
			states.add(aState);
		}
		else if(((ChessBoardState)states.get(index)).addCounter() == 3)
		{
			treeState = true;
		}
		return treeState;
	}

	/**
	 * Finner retningsvektoren mellom to brikker om dette er mulig.
	 * @param APiece	En brikke.
	 * @param BPiece	En brikke.
	 * @return Om brikkene ligger på linje returneres retningen fra APiece til BPiece, ellers returneres <code>null</code>.
	 * @see yacm.engine.chess.ChessBoard#getDirectionVectorToPoint(BoardPoint pointOfIntersection, BoardPoint aPoint)
	 */
	private BoardPoint getDirectionVectorToPoint(ChessPiece APiece, ChessPiece BPiece)
	{
		return getDirectionVectorToPoint(APiece.getPosition(), BPiece.getPosition());
	}

	/**
	 * Denne metoden finner ut om et punkt er i en annet punkts akser
	 * Hvert punkt på brettet har 8 akser N, NØ, Ø, SØ, S, SV, V, NV
	 *
	 * Den returnerer vektoren som den brikken står på, eller null
	 * @param pointA Et punkt.
	 * @param pointB Et punkt.
	 * @return Om brikkene ligger på linje returneres retningen fra pointA til pointB, ellers returneres <code>null</code>.
	 **/
	private BoardPoint getDirectionVectorToPoint(BoardPoint pointA, BoardPoint pointB)
	{
		BoardPoint directionVector = null;

		//Konstanter for å gjøre det enklere å lese
		final int NORTH		= 0;
		final int NORTH_EAST	= 1;
		final int EAST		= 2;
		final int SOUTH_EAST	= 3;
		final int SOUTH		= 4;
		final int SOUTH_WEST	= 5;
		final int WEST		= 6;
		final int NORTH_WEST	= 7;
		final int NONE		= 8;
		if(isValidCoordinate(pointA) && isValidCoordinate(pointB))
		{

			// Alle de mulige retningene
			BoardPoint[] directionVectors = {
				new BoardPoint(0, 1, false),	// N
				new BoardPoint(1, 1, false),	// NØ
				new BoardPoint(1, 0, false),	// Ø
				new BoardPoint(1, -1, false),	// SØ
				new BoardPoint(0, -1, false),	// S
				new BoardPoint(-1, -1, false),	// SW
				new BoardPoint(-1, 0, false),	// W
				new BoardPoint(-1, 1, false),	// NW
				null				// null
			};

			int xSum = pointB.getX() - pointA.getX();
			int ySum = pointB.getY() - pointA.getY();
			int direction = NONE;

			if(xSum < 0){
				if(ySum < 0 && ySum == xSum) direction = SOUTH_WEST;
				else if(ySum == 0) direction = WEST;
				else if(ySum > 0 && ySum + xSum == 0)direction = NORTH_WEST;
			}
			else if(xSum == 0){
				if(ySum < 0) direction = SOUTH;
				else if(ySum > 0) direction = NORTH;
			}
			else{
				if(ySum < 0 && ySum + xSum == 0) direction = SOUTH_EAST;
				else if(ySum == 0) direction = EAST;
				else if(ySum >  0 && ySum == xSum)direction = NORTH_EAST;
			}

			directionVector = directionVectors[direction];
		}
		return  directionVector;
	}

	/**
	 * Denne metoden returnerer en ArrayList med koordinater som representerer lovlige trekk.
	 * @param	piece	En brikke man vil finne de lovlige trekkene til.
	 * @return En ArrayList med koordinater som representerer lovlige trekk til gitt brikke.
	 */
	protected ArrayList getLegalMoves(ChessPiece piece)
	{
		//Skaffer egen konge
		ChessPiece theKing = getPiece(KING, piece.getColor());

		//Finner ut om brikken står i en av aksene til sin egen konge
		BoardPoint directionVector = getDirectionVectorToPoint(theKing, piece);

		//Genererer alle koordianate som brikken har lov til å flyttet til.
		//Også koordinater som fører til at egen konge blir satt i sjakk vil bli returnert
		ArrayList coordinates = genCoordinates(piece);

		// Hvis man er i sjakk er de eneste lovlige trekkene de som
		// sørger for at man kommer seg ut av sjakken.
		// Dette blir kjørt hvis det er kongen vi skal flytte.
		if(boardState == CHECK && !this.counterMoves.isEmpty() && theKing != piece)
		{
			//Fjerner alle de trekken som brikken egentlig har lov til å flytte til,
			//men som ikke får kongen ut av sjakk.
			coordinates.retainAll(this.counterMoves);
		}

		/*
		 * Dette trenger bare å bli kjørt hvis brikken som skal flyttes
		 * står i directionVectoren til egen kongen. Kongen står heller
		 * ikke i sin egen directionVector.
		 **/
		if(directionVector != null)
		{
			// Finner den første ruten fra kongen som det står en brikke på
			ChessPiece aPiece = findFirstPieceInDirection(theKing, directionVector);

			//Hvis det er brikken vi jobber med som står på denne ruten må
			//vi finne ut om neste brikke er fi eller ikke.
			if(piece == aPiece)
			{
				// Finner neste rute det står en brikke på
				aPiece = findFirstPieceInDirection(piece, directionVector);

				/*
				 * Hvis denne brikken er fi så må vi sjekke om den kan nå kongen.
				 * Dette gjør vi ved å bruke isPointingOn()-methoden.
				 */
				if(aPiece != null && !piece.isSameTeam(aPiece)
					&& isPointingOn(aPiece, theKing.getPosition(), TRANSPARENT))
				{
					// Lager de kordinatene som er i fra kongen og til og med fibrikken
					// Tar ikke med koordinaten til brikken som står i mellom egen konge og fi
					ArrayList possibleLegale = genCoordinatesInDirection(theKing, directionVector);

					possibleLegale.addAll(genCoordinatesInDirection(piece, directionVector));
					possibleLegale.add(aPiece.getPosition());

					/*
					 * Ved bruke de genererte posisjonene
					 * fjerner vi alle de posisjonene i fra coordinates slik at
					 * vi sitter igjen med de lovlige trekkene til
					 * den aktuelle brikken
					 */
					coordinates.retainAll(possibleLegale);
				}
			}
		}
		return coordinates;
	}

	/**
	 * Returnerer motstanderens "farge" for en brikke
	 * @param aPiece En testbrikke
	 * @return Den motsatte fargen av testbrikkens. 0 for hvit og 1 for svart.
	 */
	protected int getOpponentColor(ChessPiece aPiece)
	{
		return getOpponentColor(aPiece.getColor());
	}

	/**
	 * Returnerer motstanderen "farge".
	 * @param color En testfarge
	 * @return Den motsatte fargen av testfargens. 0 for hvit og 1 for svart.
	 */
	protected int getOpponentColor(int color)
	{
		return color == WHITE ? BLACK : WHITE;
	}

	/**
	 * Returnerer den angitte brikken.
	 * @param type Brikkens type
	 * @param color Brikkens farge
	 * @return En brikke som passer beskrivelsen.
	 */
	protected ChessPiece getPiece(int type, int color)
	{
		ChessPiece aPiece = null;
		if(color == WHITE || color == BLACK)
		{
			if(type < pieces[color].length && type >= 0)
			{
				aPiece = pieces[color][type];
			}
		}
		return aPiece;
	}

	/**
	 * Denne metoden returnerer brikken som står på den angitte ruten
	 * @param coordinate Koordinat til ruten vi vil sjekke for brikker
	 * @return Brikken som står i ruten, om noen
	 */
	protected ChessPiece getPieceOnBoard(BoardPoint coordinate)
	{
		return isValidCoordinate(coordinate) ? board[coordinate.getX()][coordinate.getY()] : null;
	}

	/**
	 * Denne metoden returnerer brikken som står på den angitte ruten.
	 * @param x X-koordinat for en rute.
	 * @param y Y-koordinat for en rute.
	 * @return Brikken som står i ruten, om noen.
	 */
	protected ChessPiece getPieceOnBoard(int x, int y)
	{
		return isValidCoordinate(x, y) ? board[x][y] : null;
	}

	/**
	 * Denne metoden genrerer BoardPoint-objekt som representerer koordinater på
	 * et sjakkbrett en valgt brikke kan flytte til ut i fra sine primitive regler.
	 *
	 * Det tas altså ikke hensyn til at kongen eventuelt blir i sjakk hvis man flytter den aktulle
	 * brikken til en av disse kordinatene.
	 * @param aPiece En brikke man vil teste
	 * @return Koordinater dit man kan flytte
	 */
	private ArrayList genCoordinates(ChessPiece aPiece)
	{
		ArrayList coordinates = new ArrayList();
		ArrayList points = aPiece.getPossibleLegalMoves(false);

		BoardPoint aPoint = null;

		Iterator itr = points.iterator();

		while(itr.hasNext())
		{
			aPoint = (BoardPoint) itr.next();

			int dx = aPoint.getX();
			int dy = aPoint.getY();
			int x = aPiece.getPosition().getX();
			int y = aPiece.getPosition().getY();

			if(!aPoint.isFixed())
			{
				boolean last = false;
				while(!last && isValidCoordinate(x += dx, y += dy) && ( board[x][y] == null
					|| !board[x][y].isSameTeam(aPiece)))
				{
					coordinates.add(new BoardPoint(x, y));
					last = (board[x][y] != null);
				}


			}
			else if(isValidCoordinate(x += dx, y += dy) && (board[x][y] == null
				|| !board[x][y].isSameTeam(aPiece) ))
			{
				coordinates.add(new BoardPoint(x, y));
			}
		}
		return coordinates;
	}

	/**
	 * Lager brettkoordinater fra en brikke og i en bestemt retning
	 * @param aPiece Brikken man starter på.
	 * @param directionVectors Retningen man går i.
	 * @return Koordinater fra brikker i gitt retning.
	 */
	private ArrayList genCoordinatesInDirection(ChessPiece aPiece, BoardPoint directionVectors)
	{
		return genCoordinatesInDirection(aPiece.getPosition(), directionVectors);
	}

	/**
	 * Lager brettkoordinater fra et punkt og i en bestemt retning
	 * @param from Startpunkt
	 * @param directionVectors Retningen det skal laged koordinater i.
	 * @return Koordinater fra startpunktet i gitt retning.
	 */
	private ArrayList genCoordinatesInDirection(BoardPoint from, BoardPoint directionVectors)
	{
		ArrayList coordinates = new ArrayList();
		if(isValidCoordinate(from) && directionVectors != null && !directionVectors.isFixed())
		{
			int x = from.getX();
			int y = from.getY();

			int dx = directionVectors.getX();
			int dy = directionVectors.getY();

			while(isValidCoordinate(x += dx, y += dy) && board[x][y] == null)
			{
				coordinates.add(new BoardPoint(x, y));
			}
		}
		return coordinates;
	}

	/**
	 * Genererer mulige mottrekk som fører til at fi's konge ikke lenger står i sjakk.
 	 * @param inPlay Den spilleren som er i trekk.
	 * @return Et antall brikker som sjakker motstanderens konge.
	 */
	protected int genCounterMoves(int inPlay)
	{
		final int NOT_CHECK = 0;
		final int SINGLE_CHECK = 1;

		int result = NOT_CHECK;

		ChessPiece theFoeKing = getPiece(KING, getOpponentColor(inPlay));

		// Får ta i alle koordinaten som det står en brikke på som
		// sjakker motstanderens konge.
		BoardPoint[] coordinateCheckTheFoeKing = getPointingAtPosition(theFoeKing);

		this.counterMoves.clear();

		if(coordinateCheckTheFoeKing != null)
		{
			result = coordinateCheckTheFoeKing.length;

			if(result  == SINGLE_CHECK)
			{
				ChessPiece offendingPiece = getPieceOnBoard(coordinateCheckTheFoeKing[0]);
				counterMoves.add(offendingPiece.getPosition());

				BoardPoint directionVector = getDirectionVectorToPoint(offendingPiece, theFoeKing);
				counterMoves.addAll(genCoordinatesInDirection(offendingPiece, directionVector));
			}
		}
		return result;
	}

	/**
	 * Denne metoden finner ut om en brikke kan nå et felt ut i fra sine basisregler.
	 * Hvis transparent er true så vil ikke denne rutinen ta høyde for at det står noen
	 * i mellom denne brikken og den koordianten man tester mot.
	 * @param piece Brikken man vil teste
	 * @param coordinate Koordinaten man ønsker å nå.
	 * @param transparent skal det tas hensyn til om det står brikker i vegen?
	 * @return <code>true</code> hvis ønsket koordinat kan nås med gitt brikke, <code>false</code> ellers.
	 */
	private boolean isPointingOn(ChessPiece piece, BoardPoint coordinate, boolean transparent)
	{
		boolean pointingOn = false;
		BoardPoint directionVector = getDirectionVectorToPoint(piece.getPosition(), coordinate);

		/*
		 * Sjekker om brikken ikke er en konge
		 */
		if(piece != getPiece(KING, piece.getColor()))
		{
			ArrayList possibleLegalMoves = piece.getPossibleLegalMoves(true);

			Iterator itr = possibleLegalMoves.iterator();
			while(itr.hasNext() && !pointingOn)
			{
				BoardPoint movmentVector = (BoardPoint) itr.next();

				if(movmentVector.isFixed())
				{
					int x = piece.getPosition().getX() +  movmentVector.getX();
					int y = piece.getPosition().getY() +  movmentVector.getY();

					pointingOn = coordinate.equals(new BoardPoint(x, y));
				}

				/*
				 * poitningOn vil få verdien true så lenge brikken har en movmentVector som er
				 * lik directionVectoren.
				 *
				 * Hvis methoden ble kalt med transparent == false så vil også det sjekkes om
				 * det står noe i mellom brikken og den ruten vi tester imot
				 * hvis det gjør det så vil pointingOn bli satt til false;
				 */
				else if(directionVector != null
					&& (pointingOn = directionVector.equals(movmentVector)) && !transparent)
				{
					/*
					 * Vi inverserer retningsvektoren for å sjekker om første brikke fra
					 * koordiant er piece
					 */
					int x = directionVector.getX();
					int y = directionVector.getY();

					BoardPoint invertedDirectionVactor = new BoardPoint(x * -1, y * -1, false);
					ChessPiece aPiece  = findFirstPieceInDirection(coordinate, invertedDirectionVactor);

					pointingOn = piece.equals(aPiece);
				}
			}
		}
		/*
		 * Hvis vi sjekker om en konge holder en annen rute i sjakk
		 * blir dette kjørt.
		 * Grunnen til at vi ikke kan bruke den generelle testrutinen over
		 * er at vi får en evig løkke når vi kjører genCounterMoves
		 * og begge kongene er i vektoren for den ruten vi sjekker i mot.
		 */
		else if(directionVector != null)
		{
			int x = piece.getPosition().getX();
			int y = piece.getPosition().getY();

			int dx = directionVector.getX();
			int dy = directionVector.getY();

			pointingOn = coordinate.equals(new BoardPoint(x + dx, y + dy));
		}

		return pointingOn;
	}

	/**
	 * Denne metoden genererer alle brikkene og initialiserer den til
	 * default posisjon.
	 * @return Alle brikkene ferdig initiert
	 */
	private ChessPiece[][] mkDefault()
	{
		ChessPiece tempPieces[][] = new ChessPiece[2][16];

		// Oppretter alle brikkene og gir dem en posisjon.
		int x = 0;
		int y = 0;
		int pawnRow = 1;

		for(int color = WHITE; color <= BLACK; color++)
		{
			for(int type = ROOK; type <= PAWN_8; type++)
			{
				x = type < PAWN  ? type : type - PAWN;
				y = type < PAWN ? y : pawnRow;

				tempPieces[color][type] = mkPiece(type, color, new BoardPoint(x, y));
			}
			y = 7;
			pawnRow = 6;
		}

		return tempPieces;
	}

	/**
	 * Med dennne metoden oppretter man en brikke.
	 * @param type Typen til brikken
	 * @param color Fargen brikken skal ha
	 * @param position Hvor brikken skal stå
	 * @return Brikken, ferdig laget
	 */
	private ChessPiece mkPiece(int type, int color, BoardPoint position)
	{
		ChessPiece aPiece = null;

		switch (type)
		{
			case KING:
				aPiece = new King(color, position, this);
				break;
			case QUEEN:
				aPiece = new Queen(color, position);
				break;
			case ROOK_KING:
			case ROOK_QUEEN:
				aPiece = new Rook(color, position);
				break;
			case BISHOP_KING:
			case BISHOP_QUEEN:
				aPiece = new Bishop(color, position);
				break;
			case KNIGHT_KING:
			case KNIGHT_QUEEN:
				aPiece = new Knight(color, position);
				break;
			case PAWN_1:
			case PAWN_2:
			case PAWN_3:
			case PAWN_4:
			case PAWN_5:
			case PAWN_6:
			case PAWN_7:
			case PAWN_8:
				aPiece = new Pawn(color, position, this);
				break;
		}
		return aPiece;
	}

	/**
	 * Flytter den valget brikken på brettet.
	 * Sørger også for at eventuell brikke som står på målet får status "ikke i live",
	 * og at brikken som er flyttet får lagret den nye posisjonen den er flyttet til
	 * @param aMove Et flytt
	 * @return Hvilken type flytt dette var.
	 *			0 - Vanlig flytt
	 *			1 - Bondeforvandling
	 *			2 - Kort rokade
	 *			3 - Lang rokade
	 */
	private int moveSelectedPieceOnBoard(ChessMove aMove)
	{
		int outcome = 0;
		BoardPoint aEnpassantCoordinat = null;
		ChessPiece aPiece = aMove.getPiece();

		int x = aMove.getDest().getX();
		int y = aMove.getDest().getY();
		ChessPiece oldPiece = null;

		if(aMove.getPiece() instanceof Pawn)
		{
			// Støtte for passant
			if(!aPiece.isMoved() && Math.abs(y - aPiece.getPosition().getY()) == 2)
			{
				aEnpassantCoordinat = new BoardPoint(x, (y == 3 ? 2 : 5));
			}
			else if((new BoardPoint(x,y)).equals(enpassantCoordinat))
			{
				oldPiece = getPieceOnBoard(x, (y == 2 ? 3 : 4));
				board[x][(y == 2 ? 3 : 4)] = null;
				outcome = ENPASSANT;
			}
			// Støtte for bondeforvandling
			else if( y == 7 || y == 0)
			{
				pawnToBePromoted = aPiece;
				outcome = PAWN_PROMOTION;
			}
		}
		// Støtte for rokade
		else if(aPiece instanceof King && Math.abs(x - aPiece.getPosition().getX()) == 2)
		{
			ChessPiece aRook = null;
			int rookXcoordinate = 0;

			switch (x - aPiece.getPosition().getX())
			{
				case 2:
					aRook = getPiece(ROOK_KING, aPiece.getColor());
					rookXcoordinate = 5;
					outcome = ROKADE_KING;
					break;
				case -2:
					aRook = getPiece(ROOK_QUEEN, aPiece.getColor());
					rookXcoordinate = 3;
					outcome = ROKADE_QUEEN;
					break;
			}

			setPieceOnBoard(aRook, rookXcoordinate, y);
			aRook.setMoved(true);
		}

		enpassantCoordinat = aEnpassantCoordinat;

		oldPiece = setPieceOnBoard(aPiece, x, y);
		aPiece.setMoved(true);

		if(oldPiece != null)
		{
			oldPiece.setAlive(false);
			states.clear();
		}

		if(oldPiece != null || aPiece instanceof Pawn)
		{
			this.numberOfMovesSinceLstKPM = 0;
		}
		else if((++this.numberOfMovesSinceLstKPM) == 50)
		{
			this.boardState = REMI;
		}
		return outcome;
	}

	/**
	 * Setter en brikke til en posisjon
	 * @param aPiece En brikke som skal flyttes
	 * @param coordinate Der brikken skal stå
	 * @return Returnerer brikken
	 */
	private ChessPiece setPieceOnBoard(ChessPiece aPiece, BoardPoint coordinate)
	{
		ChessPiece oldPiece = null;

		if(isValidCoordinate(coordinate))
		{
			int x = coordinate.getX();
			int y = coordinate.getY();
			oldPiece = setPieceOnBoard(aPiece, x, y);
		}

		return  oldPiece;
	}

	/**
	 * Setter en brikke til posisjon og returnerer eventuell
	 * brikke som sto der fra før
	 * @param aPiece En brikke som skal flyttes
	 * @param x X-koordinat
	 * @param y Y-koordinat
	 * @return En brikke som evt stod der.
	 */
	private ChessPiece setPieceOnBoard(ChessPiece aPiece, int x, int y)
	{
		ChessPiece oldPiece = null;

		if(isValidCoordinate(x,y))
		{
			if(isValidCoordinate(aPiece.getPosition()))
			{
				int oldX = aPiece.getPosition().getX();
				int oldY = aPiece.getPosition().getY();
				if(board[oldX][oldY] == aPiece)
				{
					board[oldX][oldY] = null;
				}
			}

			oldPiece = board[x][y];
			board[x][y] = aPiece;
			aPiece.setPosition(new BoardPoint(x,y));
		}
		return oldPiece;
	}

	/**
	 * Setter en brikke på sjakkbrettet ut i fra posisjonen som
	 * den sier den selv har.
	 * @param aPiece En brikke som skal settes på brettet
	 * @return <code>true</code> om alt gikk bra, <code>false</code> ellers.
	 */
	private boolean setPieceOnBoard(ChessPiece aPiece)
	{
		boolean noError = false;

		if(aPiece != null && isValidCoordinate(aPiece.getPosition()))
		{
			int x = aPiece.getPosition().getX();
			int y = aPiece.getPosition().getY();
			board[x][y] = aPiece;
			noError = true;
		}
		return noError;
	}

//--- test metoder -----------------------------------------------

	public void printBoard()
	{
		System.out.println("  |---|---|---|---|---|---|---|---|");
		for(int i = board[0].length - 1; i >= 0 ; i--)
		{
			System.out.print(i + " ");
			for(int j = 0; j < board.length; j++)
			{
				if(board[j][i] != null)
				{
					System.out.print("| " + board[j][i] +" ");
				}else
				{
					System.out.print("|   ");
				}

			}
			System.out.print("|\n");
			System.out.println("  |---|---|---|---|---|---|---|---|");
		}
		System.out.println("    0   1   2   3   4   5   6   7");
	}


	/*
	 * Testmethode for private boolean isValidCoordinate(int x, int y)
	 **/
	private static void test1()
	{
		ChessBoard board = new ChessBoard();

		String testVariables[] = {"kordinat"};
		int testDataSet[] = {-1, 0, 4, 7, 8};

		boolean expRes[] = {
			false, false, false, false, false,
			false, true, true, true, false,
			false, true, true, true, false,
			false, true, true, true, false,
			false, false, false, false, false
		};
		String testResult[][] = new String[expRes.length][3];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			for(int j = 0; j < testDataSet.length; j++)
			{
				testResult[setNo][0] = "(" + testDataSet[i] + "," + testDataSet[j] + ")";
				testResult[setNo][1] = "" + expRes[setNo];
				testResult[setNo][2] = "" + board.isValidCoordinate(testDataSet[i], testDataSet[j]);
				setNo++;
			}
		}
		TestTools.printTestResult("private boolean isValidCoordinate(int x, int y)", testVariables, testResult);

	}

	/*
	 * Testmethode for private boolean isValidCoordinate(BoardPoint coordinate)
	 **/
	private static void test2()
	{
		ChessBoard board = new ChessBoard();

		String testVariables[] = {"Kordinat"};
		BoardPoint testDataSet[] = {
			new BoardPoint(-1, -1), new BoardPoint(-1, 0),
			new BoardPoint(-1, 4), new BoardPoint(-1, 7),
			new BoardPoint(-1, 8), new BoardPoint(0, -1),
			new BoardPoint(0, 0), new BoardPoint(0, 4),
			new BoardPoint(0, 7), new BoardPoint(0, 8),
			new BoardPoint(4, -1), new BoardPoint(4, 0),
			new BoardPoint(4, 4), new BoardPoint(4, 7),
			new BoardPoint(4, 8), new BoardPoint(7, -1),
			new BoardPoint(7, 0), new BoardPoint(7, 4),
			new BoardPoint(7, 7), new BoardPoint(7, 8),
			new BoardPoint(8, -1), new BoardPoint(8, 0),
			new BoardPoint(8, 4), new BoardPoint(8, 7),
			new BoardPoint(8, 8), new BoardPoint(-1, -1, false),
			new BoardPoint(-1, 0, false), new BoardPoint(-1, 4, false),
			new BoardPoint(-1, 7, false), new BoardPoint(-1, 8, false),
			new BoardPoint(0, -1, false), new BoardPoint(0, 0, false),
			new BoardPoint(0, 4, false), new BoardPoint(0, 7, false),
			new BoardPoint(0, 8, false), new BoardPoint(4, -1, false),
			new BoardPoint(4, 0, false), new BoardPoint(4, 4, false),
			new BoardPoint(4, 7, false), new BoardPoint(4, 8, false),
			new BoardPoint(7, -1, false), new BoardPoint(7, 0, false),
			new BoardPoint(7, 4, false), new BoardPoint(7, 7, false),
			new BoardPoint(7, 8, false), new BoardPoint(8, -1, false),
			new BoardPoint(8, 0, false), new BoardPoint(8, 4, false),
			new BoardPoint(8, 7, false), new BoardPoint(8, 8, false)
		};

		boolean expRes[] = {
			false, false, false, false, false,
			false, true, true, true, false,
			false, true, true, true, false,
			false, true, true, true, false,
			false, false, false, false, false,
			false, false, false, false, false,
			false, false, false, false, false,
			false, false, false, false, false,
			false, false, false, false, false,
			false, false, false, false, false
		};
		String testResult[][] = new String[expRes.length][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			testResult[setNo][0] = "" + testDataSet[i];
			testResult[setNo][1] = "" + expRes[setNo];
			testResult[setNo][2] = "" + board.isValidCoordinate(testDataSet[i]);
			setNo++;
		}
		TestTools.printTestResult("private boolean isValidCoordinate(BoardPoint coordinate)", testVariables, testResult);
	}

	/*
	 * Testmethode for private ChessPiece getPiece(int type, int color)
	 **/
	private static void test3()
	{
		ChessBoard board = new ChessBoard();

		String testVariables[] = {"int color", "int type"};
		int testDataSet[][] = {
			{WHITE, BLACK},
			{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}
		};

		String expRes[] = {
			"rook", "knight", "bishop", "queen",
			"king", "bishop", "knight", "rook",
			"pawn", "pawn", "pawn", "pawn",
			"pawn", "pawn", "pawn", "pawn"
		};

		String testResult[][] = new String[expRes.length * 2][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			for(int j = 0; j < testDataSet[1].length; j++)
			{
				ChessPiece aPiece = board.getPiece(testDataSet[1][j], testDataSet[0][i]);
				testResult[setNo][0] = "" + testDataSet[0][i];
				testResult[setNo][1] = "" + testDataSet[1][j];
				testResult[setNo][2] = "" + expRes[j] + " : " + testDataSet[0][i];
				testResult[setNo][3] = "" + aPiece.getChessName() + " : " + aPiece.getColor();
				setNo++;
			}
		}

		TestTools.printTestResult("private ChessPiece getPiece(int type, int color)", testVariables, testResult);
	}

	/*
	 * Testmethode for protected int getOpponentColor(int color)
	 **/
	private static void test4()
	{
		ChessBoard board = new ChessBoard();

		String testVariables[] = {"color"};
		int testDataSet[] = {WHITE, BLACK};
		int expRes[] = {BLACK, WHITE};

		String testResult[][] = new String[expRes.length][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			testResult[setNo][0] = "" + testDataSet[i];
			testResult[setNo][1] = "" + expRes[i];
			testResult[setNo][2] = "" + board.getOpponentColor(i);
			setNo++;
		}

		TestTools.printTestResult("private int getOpponentColor(int color)", testVariables, testResult);
	}

	/*
	 * Testmethode for protected int getOpponentColor(ChessPiece aPiece)
	 **/
	private static void test5()
	{
		ChessBoard board = new ChessBoard();

		String testVariables[] = {"color"};
		int testDataSet[] = {WHITE, BLACK};
		int expRes[] = {BLACK, WHITE};

		String testResult[][] = new String[expRes.length][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			testResult[setNo][0] = "" + testDataSet[i];
			testResult[setNo][1] = "" + expRes[i];
			testResult[setNo][2] = "" + board.getOpponentColor(new Bishop(testDataSet[i], null));
			setNo++;
		}

		TestTools.printTestResult("private int getOpponentColor(ChessPiece aPiece)", testVariables, testResult);
	}

	/*
	 * Testmethode for protected ChessPiece getPieceOnBoard(int x, int y)
	 **/
	private static void test6()
	{
		ChessBoard board = new ChessBoard();

		String testVariables[] = {"x kordinat ", "y kordinat"};
		int testDataSet[] = {-1,0,1,2,3,4,5,6,7,8};
		String expRes[] = {
			"null", "null", "null", "null", "null", "null", "null", "null", "null", "null",
			"null", "rook", "pawn", "null", "null", "null", "null", "pawn", "rook", "null",
			"null", "knight", "pawn", "null", "null", "null", "null", "pawn", "knight", "null",
			"null", "bishop", "pawn", "null", "null", "null", "null", "pawn", "bishop", "null",
			"null", "quieen", "pawn", "null", "null", "null", "null", "pawn", "quieen", "null",
			"null", "king", "pawn", "null", "null", "null", "null", "pawn", "king", "null",
			"null", "bishop", "pawn", "null", "null", "null", "null", "pawn", "bishop", "null",
			"null", "knight", "pawn", "null", "null", "null", "null", "pawn", "knight", "null",
			"null", "rook", "pawn", "null", "null", "null", "null", "pawn", "rook", "null",
			"null", "null", "null", "null", "null", "null", "null", "null", "null", "null"
		};
		String testResult[][] = new String[expRes.length][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			for(int j = 0; j < testDataSet.length; j++)
			{

				testResult[setNo][0] = "" + testDataSet[i];
				testResult[setNo][1] = "" + testDataSet[j];

				testResult[setNo][2] = "" + expRes[setNo];

				ChessPiece aPiece = board.getPieceOnBoard(testDataSet[i], testDataSet[j]);
				testResult[setNo][3] = "" + (aPiece != null ? aPiece.getChessName(): null);
				setNo++;
			}
		}
		TestTools.printTestResult("private ChessPiece getPieceOnBoard(int x, int y)", testVariables, testResult);
	}

	/*
	 * Testmethode for private ChessPiece setPieceOnBoard(ChessPiece aPiece, int x, int y)
	 */
	private static void test7()
	{
		ChessBoard board = new ChessBoard();
		board.initBoard(null);
		board.printBoard();

		String testVariables[] = {"Kordinat"};
		int testDataSet[] = {-1,0,1,2,3,4,5,6,7,8};
		String expRes[] = {
			"null", "null", "null", "null", "null", "null", "null", "null", "null", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "king", "king", "king", "king", "king", "king", "king", "king", "null",
			"null", "null", "null", "null", "null", "null", "null", "null", "null", "null",
		};
		String testResult[][] = new String[expRes.length][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < testDataSet.length; i++)
		{
			for(int j = 0; j < testDataSet.length; j++)
			{

				testResult[setNo][0] = "(" + testDataSet[i] + "," + testDataSet[j] + ")";
				testResult[setNo][1] = "" + expRes[setNo];

				ChessPiece aPiece = board.mkPiece(KING,WHITE,new BoardPoint(0,0));
				board.setPieceOnBoard(aPiece,testDataSet[i], testDataSet[j]);

				aPiece = board.getPieceOnBoard(testDataSet[i], testDataSet[j]);
				testResult[setNo][2] = "" + (aPiece != null ? aPiece.getChessName(): null);
				setNo++;
			}
		}
		TestTools.printTestResult("private ChessPiece setPieceOnBoard(ChessPiece aPiece, int x, int y)", testVariables, testResult);
		board.printBoard();
	}

	/*
	 * Testmethode for genCoordinatesInDirection(BoardPoint from, BoardPoint directionVectors)
	 */
	private static void test8()
	{
		ChessBoard board = new ChessBoard();
		board.initBoard(null);

		String testVariables[] = {"fra", "dirVect"};

		BoardPoint[] pos = {
			new BoardPoint(-1,-1), new BoardPoint(0,0),
			new BoardPoint(3,3), new BoardPoint(4,7)
		};

		BoardPoint[] dirVect = {
			new BoardPoint(-1,-1,false), new BoardPoint(-1,0,false),
			new BoardPoint(-1,1,false), new BoardPoint(0,-1,false),
			new BoardPoint(0,1,false), new BoardPoint(1,-1,false),
			new BoardPoint(1,0,false),new BoardPoint(1,1,false)
		};

		String expRes[] = {
			"null","null","null","null","null","null","null","null",
			"null","null","null","null","(0,1)->(0,7)","null","(1,0)->(7,0)","(1,1)->(7,7)",
			"(2,2)->(0,0)","(2,3)->(0,3)","(2,4)->(0,6)","(3,2)->(3,0)","(3,4)->(3,7)","(4,2)->(6,0)","(4,3)->(7,3)",
			"(4,4)->(7,7)","(3,6)->(0,3)","(3,7)->(0,7)","null","(4,6)->(4,0)","null","(5,6)->(7,4)","(5,7)->(7,7)", "null"
		};

		String testResult[][] = new String[expRes.length][testVariables.length + 2];

		int setNo = 0;
		for(int i = 0; i < pos.length; i++)
		{
			for(int j = 0; j < dirVect.length; j++)
			{
				testResult[setNo][0] = "(" + pos[i].getX() + "," + pos[i].getY() + ")";
				testResult[setNo][1] = "(" + dirVect[j].getX() + "," + dirVect[j].getY() + ")";
				testResult[setNo][2] = expRes[setNo];

				ArrayList coords = board.genCoordinatesInDirection(pos[i], dirVect[j]);
				testResult[setNo][3] = (coords.isEmpty()) ? "null" : "";
				if(coords != null) {
					Iterator itr = coords.iterator();
					while(itr.hasNext())	{
						BoardPoint aPoint = (BoardPoint) itr.next();
						testResult[setNo][3] += (aPoint != null ?  "(" + aPoint.getX() + "," + aPoint.getY() + ")" : "null");
						if(itr.hasNext()) testResult[setNo][3] +=  " ";
					}
				}
				setNo++;
			}
		}
		TestTools.printTestResult("private ArrayList genCoordinatesInDirection(BoardPoint from, BoardPoint directionVectors)",
			testVariables, testResult);
	}

	/*
	 * Testmetode for ChessPiece getAlivePiece(int type, int color)
	 */
	private static void test9()
	{
		ChessBoard board = new ChessBoard();

		ChessPiece[][] layout = new ChessPiece[2][16];
		layout[0][0] = new Rook(WHITE, new BoardPoint(4,0));
		layout[0][1] = new Knight(WHITE, new BoardPoint(3,1));
		layout[0][2] = new Bishop(WHITE, new BoardPoint(2,5));
		layout[0][3] = new Queen(WHITE, new BoardPoint(2,1));
		layout[1][0] = new Rook(BLACK, new BoardPoint(3,3));
		layout[1][1] = new Knight(BLACK, new BoardPoint(0,2));
		layout[1][8] = new Pawn(BLACK, new BoardPoint(1,4),board);
		board.initBoard(layout);

		String[] testVars = {"Type", "Color"};

		int[] types = {ROOK, KNIGHT, BISHOP, QUEEN, KING, PAWN};
		int[] colors = {WHITE, BLACK};

		String[] expRes = {
			"Rook, white", "Rook, black",
			"Knight, white", "Knight, black",
			"Bishop, white", "Null",
			"Queen, white", "Null",
			"Null", "Null",
			"Null", "Pawn, black"
		};

		String[][] testResult = new String[expRes.length][testVars.length + 2];

		int setNo = 0;
		for(int i = 0; i < types.length; i++) {
			for(int j = 0; j < colors.length; j++) {
				testResult[setNo][0] = Integer.toString(types[i]);
				testResult[setNo][1] = (colors[j] == 0) ? "White" : "Black";
				testResult[setNo][2] = expRes[setNo];
				ChessPiece aPiece = board.getAlivePiece(types[i], colors[j]);
				testResult[setNo][3] = (aPiece == null) ? "Null" : "";
				if(aPiece != null) {
					testResult[setNo][3] = aPiece.getChessName() + " ," + ((aPiece.getColor() == 0) ? "white" : "black");
				}
				setNo++;
			}
		}

		TestTools.printTestResult("protected ChessPiece getAlivePiece(int type, int color)", testVars, testResult);
		System.out.println("Rook = 0, Knight = 1, Bishop = 2, Queen = 3, King = 4, Pawn = 8.");
	}

	/*
	 * Testmethode for BoardPoint getDirectionVectorToPoint(BoardPoint pointA, BoardPoint pointB)
	 */
	private static void test10()
	{
		ChessBoard board = new ChessBoard();
		board.initBoard(null);
		String[] testVars = {"from", "to"};

		BoardPoint[] from = {
			new BoardPoint(0,0),
			new BoardPoint(0,3),
			new BoardPoint(3,3),
			new BoardPoint(4,6)
		};

		BoardPoint[] to = {
			new BoardPoint(-1,-1),
			new BoardPoint(0,0),
			new BoardPoint(0,6),
			new BoardPoint(6,4),
			new BoardPoint(7,7)
		};

		String[] expRes = {
			"n/a","n/a","0,1","n/a","1,1",
			"n/a","0,-1","0,1","n/a","n/a",
			"n/a","1,-1","-1,1","n/a","1,1",
			"n/a","n/a","-1,0","1,-1","n/a"
		};

		String[][] testResult = new String[expRes.length][testVars.length + 2];
		int setNo = 0;

		for(int i = 0; i < from.length; i++)
		{
			for(int j = 0; j < to.length; j++)
			{
				String aVector = "n/a";
				BoardPoint vector = board.getDirectionVectorToPoint(from[i], to[j]);
				if(vector != null)
				{
					aVector = vector.getX() + "," + vector.getY();
				}

				testResult[setNo][0] = "(" + from[i].getX() + "," + from[i].getY() + ")";
				testResult[setNo][1] = "(" + to[j].getX() + "," + to[j].getY() + ")";
				testResult[setNo][2] = expRes[setNo];
				testResult[setNo][3] = aVector;
				setNo++;
			}
		}
		TestTools.printTestResult("BoardPoint getDirectionVectorToPoint(BoardPoint pointOfIntersection, BoardPoint aPoint)",testVars,testResult);
	}

	/*
	 * Tester methoden private boolean isPointingOn(ChessPiece piece, BoardPoint coordinate, boolean transparent)
	 */
	private static void test11()
	{
		ChessBoard board = new ChessBoard();

		ChessPiece[][] layout = new ChessPiece[2][16];
		layout[0][0] = new Rook(WHITE,new BoardPoint(3,3));
		layout[0][1] = new Knight(WHITE,new BoardPoint(3,3));
		layout[0][2] = new Bishop(WHITE,new BoardPoint(3,3));
		layout[0][3] = new Queen(WHITE,new BoardPoint(3,3));
		layout[0][4] = new King(WHITE,new BoardPoint(3,3), board);
		layout[0][6] = new Rook(WHITE,new BoardPoint(1,3));
		layout[0][8] = new Pawn(WHITE, new BoardPoint(3,3), board);
		layout[1][8] = new Pawn(BLACK,new BoardPoint(3,3), board);
		board.initBoard(layout);
		board.board[3][3] = null;

		String[] testVars = {"Sjakkbrikke", "Koordinat", "Transp."};

		ChessPiece[] testPieces = new ChessPiece[7];
		testPieces[0] = board.getPiece(ROOK,WHITE);
		testPieces[1] = board.getPiece(KNIGHT,WHITE);
		testPieces[2] = board.getPiece(BISHOP,WHITE);
		testPieces[3] = board.getPiece(QUEEN,WHITE);
		testPieces[4] = board.getPiece(KING,WHITE);
		testPieces[5] = board.getPiece(PAWN,WHITE);
		testPieces[6] = board.getPiece(PAWN, BLACK);

		BoardPoint[] coords = {
			new BoardPoint(-1,-1),
			new BoardPoint(0,0),
			new BoardPoint(0,3),
			new BoardPoint(3,2),
			new BoardPoint(4,2),
			new BoardPoint(4,3),
			new BoardPoint(4,5)
		};

		boolean[] transp = {true, false};

		boolean[] expRes = {
			//true
			false,false,true,true,false,true,false,
			false,false,false,false,false,false,true,
			false,true,false,false,true,false,false,
			false,true,true,true,true,true,false,
			false,false,false,true,true,true,false,
			false,false,false,false,false,false,false,
			false,false,false,false,true,false,false,
			//false
			false,false,false,true,false,true,false,
			false,false,false,false,false,false,true,
			false,true,false,false,true,false,false,
			false,true,false,true,true,true,false,
			false,false,false,true,true,true,false,
			false,false,false,false,false,false,false,
			false,false,false,false,true,false,false
		};

		String[][] testResult = new String[expRes.length][testVars.length + 2];
		int setNo = 0;
		for(int i = 0; i < transp.length; i++) {
			for(int j = 0; j < testPieces.length; j++) {
				board.setPieceOnBoard(testPieces[j]);
				for(int k = 0; k < coords.length; k++) {
					testResult[setNo][0] = testPieces[j].getChessName()
						+ ", " + testPieces[j].getPosition().getX()
						+ "," + testPieces[j].getPosition().getY();

					testResult[setNo][1] = coords[k].getX() + "," + coords[k].getY();
					testResult[setNo][2] = Boolean.toString(transp[i]);
					testResult[setNo][3] = Boolean.toString(expRes[setNo]);
					testResult[setNo][4] = Boolean.toString(board.isPointingOn(testPieces[j],coords[k],transp[i]));
					setNo++;
				}
			}
		}

		TestTools.printTestResult("private boolean isPointingOn(ChessPiece piece, BoardPoint coordinate, boolean transparent)|transparent = false",
			testVars, testResult);
	}

	/*
	 * Tester methoden protected ChessPiece findFirstPieceInDirection(BoardPoint from ,BoardPoint directionVector)
	 */
	private static void test12()
	{
		ChessBoard board = new ChessBoard();
		board.initBoard(null);

		board.setPieceOnBoard(new Queen(WHITE, new BoardPoint(1,3)));
		board.setPieceOnBoard(new Rook(WHITE, new BoardPoint(2,3)));
		board.setPieceOnBoard(new Bishop(WHITE, new BoardPoint(4,4)));
		board.setPieceOnBoard(new Knight(WHITE, new BoardPoint(5,3)));


		board.printBoard();

		String[] testVars = {"Fra", "Retningsvektor"};

		BoardPoint[] from = {
			new BoardPoint(-1,-1),
			new BoardPoint(0,0),
			new BoardPoint(0,3),
			new BoardPoint(3,3)
		};

		BoardPoint[] dirVect = {
			new BoardPoint(-1,-1,false), new BoardPoint(-1,0,false),
			new BoardPoint(-1,1,false), new BoardPoint(0,-1,false),
			new BoardPoint(0,1,false), new BoardPoint(1,-1,false),
			new BoardPoint(1,0,false),new BoardPoint(1,1,false)
		};

		String[] expRes = {
			"null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","Bishop",
			"null","null","null","null","null","null","Queen","null",
			"null","Rook","null","null","null","null","Knight","Bishop"
		};

		String[][] testResult = new String[expRes.length][testVars.length + 2];
		int setNo = 0;

		for(int i = 0; i < from.length; i++) {
			for(int j = 0; j < dirVect.length; j++) {
				testResult[setNo][0] = from[i].getX() + "," + from[i].getY();
				testResult[setNo][1] = dirVect[j].getX() + "," + dirVect[j].getY();
				testResult[setNo][2] = expRes[setNo];
				ChessPiece foundPiece = board.findFirstPieceInDirection(from[i],dirVect[j]);
				testResult[setNo][3] = foundPiece != null ? foundPiece.getChessName() : "null";
				setNo++;
			}
		}
		TestTools.printTestResult("protected ChessPiece findFirstPieceInDirection(BoardPoint from ,BoardPoint directionVector)",testVars,testResult);
	}

	/*
	 * Tester methoden protected BoardPoint[] getPointingAtPosition(BoardPoint position, int  color, boolean transKing)
	 */
	private static void test13()
	{
		ChessBoard board = new ChessBoard();
		ChessPiece[][] layout = new ChessPiece[2][16];

		layout[0][0] = new Rook(WHITE, new BoardPoint(4,0));
		layout[0][1] = new Knight(WHITE, new BoardPoint(3,1));
		layout[0][2] = new Bishop(WHITE, new BoardPoint(2,5));
		layout[0][3] = new Queen(WHITE, new BoardPoint(2,1));
		layout[0][6] = new Knight(WHITE, new BoardPoint(6,7));
		layout[1][0] = new Rook(BLACK, new BoardPoint(3,3));
		layout[1][1] = new Knight(BLACK, new BoardPoint(0,2));
		layout[1][6] = new Knight(BLACK, new BoardPoint(7,7));
		layout[1][8] = new King(BLACK, new BoardPoint(1,4),board);
		board.initBoard(layout);

		board.printBoard();

		String[] testVars = {"Position", "Color", "Transp."};

		BoardPoint[] pos = {
			new BoardPoint(-1,-1),
			new BoardPoint(0,0),
			new BoardPoint(0,3),
			new BoardPoint(4,3)
		};

		int[] color = {BLACK, WHITE};
		boolean[] transp = {true, false};

		String[] expRes = {
			//true
			"Null", "Null",
			"Rook(4,0)","Null",
			"Queen(2,1) Bishop(2,5)","Rook(3,3)",
			"Rook(4,0) Queen(2,1) Bishop(2,5) Knight(3,1)","Rook(3,3)",
			//false
			"Null", "Null",
			"Rook(4,0)","Null",
			"Queen(2,1)","Rook(3,3)",
			"Rook(4,0) Queen(2,1) Bishop(2,5) Knight(3,1)","Rook(3,3)"
		};

		String[][] testResult = new String[expRes.length][testVars.length + 2];
		int setNo = 0;
		for(int i = 0; i < transp.length; i++) {
			for(int j = 0; j < pos.length; j++) {
				for(int k = 0; k < color.length; k++) {
					testResult[setNo][0] = pos[j].getX() + "," + pos[j].getY();
					testResult[setNo][1] = color[k] == BLACK ? "Black" : "White";
					testResult[setNo][2] = Boolean.toString(transp[i]);
					testResult[setNo][3] = expRes[setNo];
					BoardPoint[] points = board.getPointingAtPosition(pos[j],color[k], transp[i]);
					testResult[setNo][4] = points == null ? "null" : "";
					if(points != null) {
						for(int l = 0; l < points.length; l++) {
							testResult[setNo][4] += board.getPieceOnBoard(points[l]).getChessName() + "(" + points[l].getX() + "," + points[l].getY() + ")";
						}
					}
					setNo++;
				}
			}
		}
		TestTools.printTestResult("protected BoardPoint[] getPointingAtPosition(BoardPoint position, int  color, boolean transKing)", testVars, testResult);
	}


	/*
	 * Tester methoden public 
	 */
	private static void test14()
	{
		ChessBoard board = new ChessBoard();
		ChessPiece[][] layout = new ChessPiece[2][16];

		layout[0][0] = new Rook(WHITE, new BoardPoint(4,0));
		layout[0][1] = new Knight(WHITE, new BoardPoint(3,1));
		layout[0][2] = new Bishop(WHITE, new BoardPoint(2,5));
		layout[0][3] = new Queen(WHITE, new BoardPoint(2,1));
		layout[0][6] = new Knight(WHITE, new BoardPoint(6,7));
		layout[1][0] = new Rook(BLACK, new BoardPoint(3,3));
		layout[1][1] = new Knight(BLACK, new BoardPoint(0,2));
		layout[1][6] = new Knight(BLACK, new BoardPoint(7,7));
		layout[1][8] = new King(BLACK, new BoardPoint(1,4),board);
		board.initBoard(layout);

		board.printBoard();

		String[] testVars = {"Position", "Color", "Transp."};

		BoardPoint[] pos = {
			new BoardPoint(-1,-1),
			new BoardPoint(0,0),
			new BoardPoint(0,3),
			new BoardPoint(4,3)
		};

		int[] color = {BLACK, WHITE};
		boolean[] transp = {true, false};

		String[] expRes = {
			//true
			"Null", "Null",
			"Rook(4,0)","Null",
			"Queen(2,1) Bishop(2,5)","Rook(3,3)",
			"Rook(4,0) Queen(2,1) Bishop(2,5) Knight(3,1)","Rook(3,3)",
			//false
			"Null", "Null",
			"Rook(4,0)","Null",
			"Queen(2,1)","Rook(3,3)",
			"Rook(4,0) Queen(2,1) Bishop(2,5) Knight(3,1)","Rook(3,3)"
		};

		String[][] testResult = new String[expRes.length][testVars.length + 2];
		int setNo = 0;
		for(int i = 0; i < transp.length; i++) {
			for(int j = 0; j < pos.length; j++) {
				for(int k = 0; k < color.length; k++) {
					testResult[setNo][0] = pos[j].getX() + "," + pos[j].getY();
					testResult[setNo][1] = color[k] == BLACK ? "Black" : "White";
					testResult[setNo][2] = Boolean.toString(transp[i]);
					testResult[setNo][3] = expRes[setNo];
					BoardPoint[] points = board.getPointingAtPosition(pos[j],color[k], transp[i]);
					testResult[setNo][4] = points == null ? "null" : "";
					if(points != null) {
						for(int l = 0; l < points.length; l++) {
							testResult[setNo][4] += board.getPieceOnBoard(points[l]).getChessName() + "(" + points[l].getX() + "," + points[l].getY() + ")";
						}
					}
					setNo++;
				}
			}
		}
		TestTools.printTestResult("protected BoardPoint[] getPointingAtPosition(BoardPoint position, int  color, boolean transKing)", testVars, testResult);
	}


	/**
	 * Testmethode for klassen. gjør masse fancy greier.
	 * @param args En tabell av kommandolinjeargumenter. Eneste gyldige argument er heltall fra 0 til 12.
	 */
	public static void main (String[] args)
	{
		if(args != null && args.length > 0)
		{
			switch(Integer.parseInt(args[0].trim()))
			{
				case 1:
					test1();
					break;
				case 2:
					test2();
					break;
				case 3:
					test3();
					break;
				case 4:
					test4();
					break;
				case 5:
					test5();
					break;
				case 6:
					test6();
					break;
				case 7:
					test7();
					break;
				case 8:
					test8();
					break;
				case 9:
					test9();
					break;
				case 10:
					test10();
					break;
				case 11:
					test11();
					break;
				case 12:
					test12();
					break;
				case 13:
					test12();
					break;

			}
		}
	}
}


