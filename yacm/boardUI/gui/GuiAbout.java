package yacm.boardUI.gui;

import yacm.engine.boardgame.chess.*;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.sound.midi.*;
import java.io.File;
import java.awt.Container;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.Thread;

/*
 * Sist endret av: $Author: simeng $
 */

/**
 * @author Simen Graaten
 * @version $Revision: 1.7 $
 */
class GuiAbout extends JDialog implements ActionListener, java.io.Serializable {
	private JFrame parent;
	private CreditBox creditBox;
	private boolean running;
    private Sequencer sequencer;
	private Sequence sequence;
	private JButton ok;


	/**
	 * Åpner et "About" vindu som lister opp kodeskribentene
	 * @param parent Eiren av vinduet
	 */
	public GuiAbout(JFrame parent) {
		super(parent, "Om \"Yet Another ChessMaster 2003 Platinum Edition\"", true);
		creditBox = new CreditBox();
		Container c = getContentPane();
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		this.setSize(new Dimension(200,300));
		this.setVisible(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		ok = new JButton("Tilbake til spillet");
		ok.addActionListener(this);

		try {
        	sequence = MidiSystem.getSequence(getClass().getResource("/yacm/boardUI/gui/tng.mid"));

 			sequencer = MidiSystem.getSequencer();

    	} catch (Exception e) {
    	}

		c.add(creditBox);
		c.add(ok);
	}

	/**
	 * Starter sekvensen om igjen.
	 */
	public void reset() {
		running = true;
		try {
        	sequencer.open();
       		sequencer.setSequence(sequence);
        	sequencer.start();
		} catch(Exception e) {
			
		}
		creditBox.reset();
	}

	/**
	 * Overvåker knappetrykk
	 * @param e Tingen som fant sted.

	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			running = false;

			try {
				sequencer.stop();
				sequencer.close();
			} catch(Exception ex) { }

			this.setVisible(false);
		}
	}

	/**
	 * Indre klasse som lager den fancy stjernefeltet og den rullende teksten.
	 */
	public class CreditBox extends JPanel implements Runnable, java.io.Serializable{
		class Star implements java.io.Serializable {
			public double angle;
			public int fakez;
			public int d;
		}

		private Thread t;
		private int timer = 0;
		private Star[] stars;
		private final int MAXWIDTH;
		private final int MAXHEIGHT;
		private final int CENTERX;
		private final int CENTERY;
		private final String credits[] = {
			"Chess - The Final Fronteir",
			"These are the programmers",
			"of the chessgame YACM",
			"Our continuing mission,",
			"to boldly play chess like",
			"no man has played chess",
			"before...",
			"",
			"Yet Another ChessMaster",
			"Laget av følgende individer:",
			"(i analfabetisk rekkefølge)",
			"",
			"Daniel Andreas Bach",
			"Kristian Berg",
			"Morten L. Andersen",
			"Simen J. M. Graaten",
			"Trond Smaavik",
			"",
			"All koden er vår.",
			"Vi har en bakdør for",
			"overvåking av eventuelle",
			"kodetyver.  Når vi finner deg",
			"kommer vi med avbiter....",
			"Da ligger kablene dine",
			"tynt an, da!",
			"",
			"",
			"Copyright Gruppe10",
			"AITeL@HiST 2003",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			""
			};

		public CreditBox() {
			this.setSize(200, 290);
			MAXWIDTH = (int)getSize().getWidth();
			MAXHEIGHT = (int)getSize().getHeight();
			CENTERX = MAXWIDTH/2;
			CENTERY = MAXHEIGHT/2;
			this.start();

			stars = new Star[100];

			for (int i=0; i<stars.length; i++) {
				stars[i] = new Star();
				stars[i].angle = (double)Math.random()*Math.PI*2;
				stars[i].d = (int)(Math.random()*MAXHEIGHT*2);
				stars[i].fakez = (int)(Math.random()*25);
			}

			running = true;
		}

		public void start() {
			if (t == null) {
				t = new Thread(this);
				t.setPriority(Thread.MIN_PRIORITY);
				t.start();
			}
		}

		public void run()
		{
			while (true) {
				if (running && Thread.currentThread() == t)
				{
					try {
						Thread.sleep(100);
					} catch(Exception e) { }
					timer++;
					repaint();
				}
			}
		}

		public void reset()
		{
			timer = 0;
		}

		public void update(Graphics g) {
		}

		public void paint(Graphics g) {

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, MAXWIDTH, MAXHEIGHT);

			g.setColor(Color.WHITE);
			for (int i=0; i<credits.length; i++) {
				int y = (MAXHEIGHT)-(timer-(i*20))%(credits.length*20);
				if (y < MAXHEIGHT+20 && y > -20)
					g.drawString(credits[i],10,y);
			}

			for (int i=0; i<stars.length; i++) {
				stars[i].fakez++;
				if (stars[i].fakez > 30) {
					stars[i].fakez = 0;
					stars[i].d = (int)(Math.random()*MAXHEIGHT*2);
				}

				int len = stars[i].fakez*stars[i].d/30;
				int dx = (int)(Math.cos(stars[i].angle)*len);
				int dy = (int)(Math.sin(stars[i].angle)*len);
				g.setColor(new Color(50+(int)((stars[i].fakez/30.0f)*200),50+(int)((stars[i].fakez/30.0f)*200),50+(int)((stars[i].fakez/30.0f)*200)));
				g.drawLine(CENTERX+dx,CENTERY+dy,CENTERX+dx,CENTERY+dy);
			}
		}
	}
}

