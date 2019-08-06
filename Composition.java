package POOP;

import java.awt.Canvas;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.swing.JFrame;

public class Composition {

	private ArrayList<MusicSymbol> lista = new ArrayList<MusicSymbol>();
	private MidiMap maps = new MidiMap();

	public Composition() {
	}

	public synchronized void loadComposition(String pathname, MidiMap mapa, Piano jfr) {
		try {

			BufferedReader br = new BufferedReader(new FileReader(new File(pathname)));
			Stream<String> s = br.lines();

			s.forEach(str -> {

				Pattern p = Pattern.compile("(\\[[^\\[\\]]*\\])|([^\\[\\]]*)");

				Matcher m = p.matcher(str);

				try {
					while (m.find()) {

						if (m.group(1) != null) {

							String g1 = m.group(1);
							String hlp = g1.substring(1, g1.length() - 1);
							String[] notes = hlp.split(" ");

							if (notes.length == 1) {
								Chord ch = new Chord();

								for (int i = 0; i < notes[0].length(); i++) {
									String tmp = "" + notes[0].charAt(i);
									Object[] key = new Object[] { " " };

									key = mapa.get(tmp);

									String key0 = (String) key[0]; // nota
									String key1 = (String) key[1]; // midiValue
									Byte midivi = Byte.parseByte(key1);
									boolean sharp = false;
									int idx = 1;
									if (key0.length() > 2) {
										sharp = true;
										idx = 2;
									}
									ch.dodaj(new Note(new Duration(4, 1), Byte.parseByte("" + key0.charAt(idx)),
											key0.charAt(0), sharp, midivi));
								}

								lista.add(ch);
							} else {
								for (String nota : notes) {

									for (int i = 0; i < nota.length(); i++) {
										String tmp = "" + nota.charAt(i);
										Object[] key = new Object[] { " " };

										key = mapa.get(tmp);

										String key0 = (String) key[0]; // nota
										String key1 = (String) key[1]; // midiValue
										Byte midivi = Byte.parseByte(key1);
										boolean sharp = false;
										int idx = 1;
										if (key0.length() > 2) {
											sharp = true;
											idx = 2;
										}
										lista.add(new Note(new Duration(8, 1), Byte.parseByte("" + key0.charAt(idx)),
												key0.charAt(0), sharp, midivi));
									}
								}
							}

						} else if (m.group(2) != null) {
							String g2 = m.group(2);

							for (int i = 0; i < g2.length(); i++) {
								String tmp = "" + g2.charAt(i);
								Object[] key = new Object[] { " " };

								if (tmp.equals(" ") == false && tmp.equals("|") == false) {
									key = mapa.get(tmp);
									
									if(key==null) throw new GBadFormatting("*** BAD CODES FOR SOME NOTE ***");
									String key0 = (String) key[0]; // nota
									String key1 = (String) key[1]; // midiValue
									Byte midivi = Byte.parseByte(key1);
									boolean sharp = false;
									int idx = 1;
									if (key0.length() > 2) {
										sharp = true;
										idx = 2;
									}
									lista.add(new Note(new Duration(4, 1), Byte.parseByte("" + key0.charAt(idx)),
											key0.charAt(0), sharp, midivi));

								} else {

									int im = (tmp.equals(" ")) ? 8 : 4;

									lista.add(new Pause(new Duration(im, 1)));

								}
							}
						}

					}
					
				} catch (GDuration g) {
					jfr.writeError(g.toString());
				}
				catch(GBadFormatting gb) {jfr.writeError(gb.toString());}
			});

		} catch (FileNotFoundException err) {
			jfr.writeError("*** File not found ***");
		}
	}

	@Override
	public String toString() {

		return lista.toString();
	}

	public synchronized MusicSymbol get(int idx) {
		return this.lista.get(idx);
	}

	public synchronized int size() {
		return lista.size();
	}

	public synchronized void put(MusicSymbol mus) {
		lista.add(mus);
	}

	public synchronized String writeTxt() {
		boolean helpFlg = false;
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < this.lista.size(); i++) {
			String[] str = lista.get(i).txt().split("\n");
			if (str.length == 1) {
				if (str[0].equals(" ")) {
					if (helpFlg == true) {
						helpFlg = false;
						s.append("]");
					}
					s.append(" ");
				} else if (str[0].equals("|")) {
					if (helpFlg == true) {
						helpFlg = false;
						s.append("]");
					}
					s.append("|");
				} else if (lista.get(i).getDur().getIm() == 4) {
					if (helpFlg == true) {
						helpFlg = false;
						s.append("]");
					}
					s.append(maps.getReverse(str[0]));
				} else if (helpFlg == false) {
					helpFlg = true;
					s.append("[");
					s.append(maps.getReverse(str[0]));
				} else {
					s.append(" " + maps.getReverse(str[0]));
					if (i == lista.size() - 1)
						s.append("]");
				}
			} else {
				if (helpFlg == true) {
					helpFlg = false;
					s.append("]");
				}
				s.append("[");
				
				for (int j = 0; j < str.length; j++)
					s.append(maps.getReverse(str[j]));

				s.append("]");
			}
		}
		return s.toString();
	}

}
