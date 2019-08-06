package POOP;

@SuppressWarnings("serial")
public class GDuration extends Exception {

	public GDuration(String message) {
		super(message);
	}

	@Override
	public String toString() {
		return super.getMessage();
	}

}
