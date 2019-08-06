package POOP;

public class Note extends MusicSymbol {

	private byte octave;
	private char height;
	private boolean sharp;
	private byte midiValue;
	public Note(Duration d, byte octave, char h, boolean s, byte midi) {
		super(d);
		
		this.octave=octave;
		this.height=h;
		this.sharp=s;
		this.midiValue=midi;
	}
	public Note(Duration d) {
		super(d);
	}
	public byte getOctave() {
		return octave;
	}
	public char getHeight() {
		return height;
	}
	public String getSharp() {
		return (sharp==true)?"#":"";
	}
	
	public byte getMidi()
	{
		return midiValue;
	}
	
	public void setSharp() {
		sharp=true;
	}
	
	@Override
	public String toString() {
		return ""+height+((sharp==true)?"#":"")+octave;
	}
	@Override
	public String txt() {
		return this.toString();
	}

}
