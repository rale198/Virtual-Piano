package POOP;

import java.util.ArrayList;

public class RecordList {

	private ArrayList<Note> symbols = new ArrayList<Note>();
	private ArrayList<Long> milis = new ArrayList<Long>();

	public synchronized void put(Note m, long t) {
		symbols.add(m);
		milis.add(t);
	}

	public synchronized Composition makeComposition() {
		Composition ret = new Composition();

		Chord helpChord = null;
		try {
			for (int i = 0; i < symbols.size() - 1; i++) {
				Note left = symbols.get(i);
				long lapseTime = milis.get(i + 1) - milis.get(i);
				if (lapseTime < 750 && lapseTime > 50) {
					if (helpChord != null) {
						helpChord.dodaj(left);
						ret.put(helpChord);
						helpChord = null;
						continue;
					}
					ret.put(left);
				} else if (lapseTime > 750) {
					lapseTime -= 750;
					if (helpChord != null) {
						helpChord.dodaj(left);
						ret.put(helpChord);
						helpChord = null;
						while (lapseTime > 0) {
							lapseTime -= 750;
							ret.put(new Pause(new Duration(8, 1)));
						}
						continue;
					}

					ret.put(left);
					while (lapseTime > 0) {
						lapseTime -= 750;
						ret.put(new Pause(new Duration(8, 1)));
					}
				} else {
					if (helpChord == null)
						helpChord = new Chord();
					helpChord.dodaj(left);

				}
			}

			if (helpChord != null) {
				helpChord.dodaj(symbols.get(symbols.size() - 1));
				ret.put(helpChord);
				helpChord = null;
			}
		} catch (GDuration g) {
		}
		;
		return ret;
	}

}
