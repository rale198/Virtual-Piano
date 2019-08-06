package POOP;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;

public class noteCanvas extends Canvas {

	private Composition composition = null;
	private boolean note = false;

	private MidiMap mapa = new MidiMap();

	public noteCanvas() {
	}

	public synchronized void setComposition(Composition composition) {

		this.composition = composition;
	}

	public synchronized void setLetters(boolean note) {
		this.note = note;

	}
	public synchronized void  writeError(String msg)
	{
		Graphics g=this.getGraphics();
		g.setFont(new Font("SansSerif",Font.BOLD,50));
		g.setColor(new Color(250,128,114));
		g.drawString(msg, (int)((getWidth()/4)-1), getHeight()/2-1);
	}
	private int tmpIdx = 0;

	@Override
	public void paint(Graphics g) {
		if (composition == null)
			return;

		int sir = getWidth() - 1;
		int vis = getHeight() - 1;
		int num = ((composition.size() - tmpIdx < 40) ? composition.size() - tmpIdx : 40);

		double oneNoteWidth = sir * 1. / num;
		drawMeasure(sir, vis, g, oneNoteWidth * 2);

		double x = 0., y = vis * 1. / 2;
		for (int i = tmpIdx; i < tmpIdx + num; i++) {
			MusicSymbol mus = composition.get(i);

			double oneNoteWidth2 = oneNoteWidth * ((mus.getDur().getIm() == 8) ? 1 : 2);
			Graphics2D g2 = (Graphics2D) g;
			double h = 40;
			double tmpY = y;
			if (mus instanceof Chord) {
				Chord ch = (Chord) mus;
				h += (ch.size() - 1) * 40;
				int offset = 0;
				if (ch.size() % 2 == 0) {
					offset = 20;
				}

				tmpY = tmpY - (ch.size() / 2) * 40 + offset;
			}

			g2.setColor(getRand());
			g2.setStroke(new BasicStroke(4));
			g2.drawRect((int) x, (int) tmpY, (int) oneNoteWidth2, (int) h);
			g2.setColor(oneNoteWidth2 > oneNoteWidth ? getRed() : getGreen());
			g2.fillRect((int) x, (int) tmpY, (int) oneNoteWidth2, (int) h);
			g2.setColor(letterColors());
			g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
			String[] strs = getStr(mus).split("\n");
			tmpY += 20;
			for (String sss : strs) {
				g2.drawString(sss, (int) (x + oneNoteWidth2 * 0.2), (int) (tmpY));
				tmpY += 20;
			}
			x += oneNoteWidth2;

		}

	}

	private Color letterColors() {
		return new Color(255, 250, 250);
	}

	private String getStr(MusicSymbol mus) {
		if (note == true)
			return mus + "";
		if ((mus instanceof Pause))
			return " ";
		
		String[] str=mus.toString().split("\n");
		StringBuilder ret=new StringBuilder();
		
		for(String strings:str)
			ret.append(mapa.getReverse(strings)+"\n");
		return ret.toString();
	}

	private Color getRed() {
		return new Color(214, 26, 60);
	}

	private Color getGreen() {
		return new Color(46, 139, 87);
	}

	private Color getRand() {
		int red = (int) ((0 + Math.random() * (254))) % 255;
		int green = (int) ((0 + Math.random() * (254))) % 255;
		int blue = (int) ((0 + Math.random() * (254))) % 255;

		return new Color(red, green, blue);
	}

	private synchronized void drawMeasure(int sir, int vis, Graphics g, double step) {
		double s = step;

		for (; s <= sir; s += step) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(112, 128, 144));
			g2.setStroke(new BasicStroke(7));
			g2.drawLine((int) s, (int) (vis * 0.80), (int) s, (int) (vis * 0.87));
		}
	}

	public synchronized void setIdx(int idx) {
		tmpIdx = idx;
	}

	public synchronized void incIdx() {
		tmpIdx++;
	}

	public synchronized void incIdxAutoPlay() {
		tmpIdx++;
		if (tmpIdx<composition.size()&&composition.get(tmpIdx) instanceof Pause)
			tmpIdx++;
	}

	public synchronized int getIdx() {
		return tmpIdx;
	}
}
