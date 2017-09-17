/*
 * @(#)$Id: ThoughtArray.java,v 1.1 2003/05/05 16:52:21 simeng Exp $
 *
 * Coppyright 2003 Gruppe 10 All rights reserved.
 */
package yacm.ai;

import yacm.engine.boardgame.BoardPoint;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * Siste endret av: $Author: simeng $
 */

/**
 * @author      Andreas Bach
 */
public class ThoughtArray {

	double poeng;
	ArrayList Flytt();
	ArrayList Brikker();
	Tankerekke PappaRekke;

	public Tankerekke(int nummer, Tankerekke PappaRekke) {
		this.PappaRekke = PappaRekke;
		this.nummer = nummer;
	}

	private void giTankerekkeFlerePoeng(double antallpoeng) {
		poeng += antallpoeng;
		PappaRekke.giTankerekkeFlerePoeng(antallpoeng);
	}

	private double finnPoeng() {
		return poeng;
	}

	private leggTilTrekk(ChessPiece brikke, BoardPoint flytteTil) {
		Flytt.add(flytteTil);
		Brikker.add(brikke);
	}


	//Jeg vet ikke hvorfor ai'en kanskje vil dette, men man vet jo aldri
	private fjerneSisteFlytt() {
		int i = Flytt.length();
		int j = Brikker.length();

		Flytt.del(i - 1);
		Brikker.del(i - 1);
		
	} 


}
