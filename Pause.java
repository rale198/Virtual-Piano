package POOP;

public class Pause extends MusicSymbol {

	public Pause(Duration d) {
		super(d);
	}
	
	@Override
	public String toString() {
		if(this.duz.getIm()==8) return " ";
		return "|";
	}

	@Override
	public String txt() {
		return this.toString();
	}
}
