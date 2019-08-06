package POOP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

public class FileWr implements FileFormatting {
	private BufferedWriter writer;

	public synchronized void writeFile(Composition comp, String pathname) {
		try {
			FileWriter f = new FileWriter(pathname);
			writer = new BufferedWriter(f);

			f.write(comp.writeTxt());
			f.close();
		} catch (

		IOException e) {
		}
		;
	}
}
