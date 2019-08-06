package POOP;

public abstract class MusicSymbol {

	protected Duration duz;
	public MusicSymbol(Duration d) {this.duz=d;}
	
	public synchronized Duration getDur() {return duz;}
	public abstract String txt();

}
