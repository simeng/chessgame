package yacm.engine.boardgame;

import java.io.Serializable;
/**
 * En abstrakt klasse for en runde av et brettspill. Dette objektet er en del av en lenketliste.
 * @author Morten L. Andersen
 */
public abstract class Move implements Serializable
{
	/**
	 * Forrige <code>Move</code>
	 */
	private Move previous;
	/**
	 * Neste <code>Move</code>
	 */
	private Move next;
	/**
	 * Trekknummeret.
	 */
	private int moveNumber;

	/**
	 * Ekseverer et trekk.
	 * @return <code>true</code> hvis alt gikk som det skulle.
	 */
	protected abstract boolean execute();

	/**
	 * Henter ut resultatet av trekket.
	 * @return Resultatet av trekket.
	 */
	public abstract int getResult();

	/**
	 * Setter rundenummeret.
	 * @param number Rundenummeret vi ønser å sette.
	 */
	protected void setMoveNumber(int number)
	{
		moveNumber = number;
	}

	/**
	 * Setter en peker til forrige <code>Move</code>.
	 * @param aMove En peker til forrige <code>Move</code>.
	 */
	protected void setPrevious(Move aMove)
	{
		previous = aMove;
	}

	/**
	 * Setter en peker til neste <code>Move</code>.
	 * @param aMove En peker til neste <code>Move</code>.
	 */
	protected void setNext(Move aMove)
	{
		next = aMove;
	}

	/**
	 * Henter en peker til forrige <code>Move</code>.
	 * @return Forrige <code>Move</move>
	 */
	protected Move getPrevious()
	{
		return previous;
	}

	/**
	 * Henter en peker til neste <code>Move</code>.
	 * @return Neste <code>Move</code>
	 */
	protected Move getNext()
	{
		return next;
	}

	/**
	 * Sjekker om det er et <code>Move</code> etter dette.
	 * @return <code>true</code> hvis det finnes et neste <code>Move</code> i listen.
	 */
	protected boolean hasNext()
	{
		return next != null;
	}

	/**
	 * Sjekker om det er et <code>Move</code> før dette.
	 * @return <code>true</code> hvis det finnes et forrige <code>Move</code> i listen.
	 */
	protected boolean hasPrevious()
	{
		return previous != null;
	}

	/**
	 * Tester om trekket er gjennomført.
	 * @return Er det det?
	 */
	public abstract boolean isExecuted();

	/**
	 * Tester om trekket er verifisert som et reelt trekk.
	 * @return Er det det?
	 */
	public abstract boolean isVerified();
	
	/**
	 * Verifiserer trekket.
	 * @return Statusmelding for trekket
	 */
	public abstract int verifie();
	
	/**
	 * Henter ut brikkens sluttposisjon.
	 * @return Sluttposisjon til brikken.
	 */
	public abstract BoardPoint getDest();
	
	/**
	 * Henter ut brikkens startposisjon.
	 * @return Startposisjon til brikken.
	 */
	public abstract BoardPoint getOrig();

	/**
	 * Finner frem til hvilken runde man har kommet til.
	 * @return Rundenummeret.
	 */
	public int getMoveNumber()
	{
		return moveNumber;
	}

	public String toString()
	{
		return "trekk nr. " + moveNumber;
	}
}
