package POOP;

public class GBadFormatting extends Exception {

	public GBadFormatting(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}
}
