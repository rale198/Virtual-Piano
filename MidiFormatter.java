package POOP;

import java.io.File;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

public class MidiFormatter implements FileFormatting {

	public synchronized void writeFile(Composition com, String pathname) {
		long actionTime = 1;
		try {
//****  Create a new MIDI sequence with 24 ticks per beat  ****
			Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ, 24);

//****  Obtain a MIDI track from the sequence  ****
			Track t = s.createTrack();

//****  General MIDI sysex -- turn on General MIDI sound set  ****
			byte[] b = { (byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7 };
			SysexMessage sm = new SysexMessage();
			sm.setMessage(b, 6);
			MidiEvent me = new MidiEvent(sm, (long) 0);
			t.add(me);

//****  set tempo (meta event)  ****
			MetaMessage mt = new MetaMessage();
			byte[] bt = { 0x02, (byte) 0x00, 0x00 };
			mt.setMessage(0x51, bt, 3);
			me = new MidiEvent(mt, (long) 0);
			t.add(me);

//****  set track name (meta event)  ****
			mt = new MetaMessage();
			String TrackName = new String("midifile track");
			mt.setMessage(0x03, TrackName.getBytes(), TrackName.length());
			me = new MidiEvent(mt, (long) 0);
			t.add(me);

//****  set omni on  ****
			ShortMessage mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7D, 0x00);
			me = new MidiEvent(mm, (long) 0);
			t.add(me);

//****  set poly on  ****
			mm = new ShortMessage();
			mm.setMessage(0xB0, 0x7F, 0x00);
			me = new MidiEvent(mm, (long) 0);
			t.add(me);

//****  set instrument to Piano  ****
			mm = new ShortMessage();
			mm.setMessage(0xC0, 0x00, 0x00);
			me = new MidiEvent(mm, (long) 0);
			t.add(me);

			for (int i = 0; i < com.size(); i++) {
				if (com.get(i) instanceof Pause) {
					actionTime += (com.get(i).getDur().getIm() == 4 ? 90 : 45);
				} else if (com.get(i) instanceof Chord) {
					Note helper = null;
					Chord ch = (Chord) com.get(i);
					for (int k = 0; k < ch.size(); k++) {
						helper = (Note) ch.get(k);

						mm = new ShortMessage();
						mm.setMessage(0x90, helper.getMidi(), 0x60); // menjao sa 40=>70 i 60=>100
						me = new MidiEvent(mm, actionTime);
						t.add(me);

					}
					actionTime += 50;
					mm = new ShortMessage();
					mm.setMessage(0x80, helper.getMidi(), 0x40);
					me = new MidiEvent(mm, actionTime);
					t.add(me);
				} else {
					Note n = (Note) com.get(i);
					mm = new ShortMessage();
					mm.setMessage(0x90, n.getMidi(), 0x64); // menjao sa 40=>70 i 60=>100
					me = new MidiEvent(mm, actionTime);
					t.add(me);

					actionTime += (com.get(i).getDur().getIm() == 4 ? 50 : 25);

					mm = new ShortMessage();
					mm.setMessage(0x80, n.getMidi(), 0x64);
					me = new MidiEvent(mm, actionTime);
					t.add(me);
				}
			}
//****  set end of track (meta event) 19 ticks later  ****
			mt = new MetaMessage();
			byte[] bet = {}; // empty array
			mt.setMessage(0x2F, bet, 0);
			me = new MidiEvent(mt, (long) 140);
			t.add(me);

//****  write the MIDI sequence to a MIDI file  ****
			File f = new File(pathname);
			MidiSystem.write(s, 1, f);
		} // try
		catch (Exception e) {
			System.out.println("Exception caught " + e.toString());
		} // catch
	}
}
